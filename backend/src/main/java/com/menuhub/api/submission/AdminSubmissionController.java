package com.menuhub.api.submission;

import com.menuhub.api.menu.MenuItem;
import com.menuhub.api.menu.MenuItemRepository;
import com.menuhub.api.restaurant.Restaurant;
import com.menuhub.api.restaurant.RestaurantRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

@RestController
@RequestMapping("/api/admin/submissions")
public class AdminSubmissionController {

    private final MenuSubmissionRepository submissionRepository;
    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;
    private final AdminSubmissionService adminSubmissionService;

    public AdminSubmissionController(
                    MenuSubmissionRepository submissionRepository,
                    RestaurantRepository restaurantRepository,
                    MenuItemRepository menuItemRepository,
                    AdminSubmissionService adminSubmissionService
    ) {
        this.submissionRepository = submissionRepository;
        this.restaurantRepository = restaurantRepository;
        this.menuItemRepository = menuItemRepository;
        this.adminSubmissionService = adminSubmissionService;
    }

    @GetMapping
    public List<AdminSubmissionListItem> list() {
        return adminSubmissionService.listSubmissions();
    }

    @PostMapping("/{id}/approve")
    public MenuSubmission approve(@PathVariable Long id) {
        MenuSubmission submission = submissionRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Submission not found"));

        // If already approved, return as-is (idempotent)
        if ("APPROVED".equals(submission.getStatus())) {
            return submission;
        }

        Restaurant restaurant = restaurantRepository.findById(submission.getRestaurantId())
                        .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        // If transitioning from REJECTED, delete old items first to prevent duplicates
        if ("REJECTED".equals(submission.getStatus())) {
            List<MenuItem> oldItems = menuItemRepository.findByRestaurantId(restaurant.getId());
            menuItemRepository.deleteAll(oldItems);
        }

        List<MenuItem> parsedItems = parseRawText(submission.getRawText(), restaurant);
        menuItemRepository.saveAll(parsedItems);

        submission.setStatus("APPROVED");
        submission.setApprovedAt(java.time.LocalDateTime.now());
        submission.setRejectedAt(null); // Clear rejected timestamp if re-approving
        return submissionRepository.save(submission);
    }

    @PostMapping("/{id}/reject")
    public MenuSubmission reject(@PathVariable Long id) {
        MenuSubmission submission = submissionRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Submission not found"));

        // If already rejected, return as-is (idempotent)
        if ("REJECTED".equals(submission.getStatus())) {
            return submission;
        }

        submission.setStatus("REJECTED");
        submission.setRejectedAt(java.time.LocalDateTime.now());
        submission.setApprovedAt(null); // Clear approved timestamp if re-rejecting
        return submissionRepository.save(submission);
    }

    private List<MenuItem> parseRawText(String rawText, Restaurant restaurant) {
        List<String> lines = rawText.lines()
                        .map(String::trim)
                        .filter(line -> !line.isBlank())
                        .toList();

        return lines.stream()
                        .map(line -> toMenuItem(line, restaurant))
                        .filter(Objects::nonNull)
                        .toList();
    }

    private MenuItem toMenuItem(String line, Restaurant restaurant) {
        String[] parts = line.split("\\|");

        if (parts.length >= 3) {
            String category = parts[0].trim();
            String name = parts[1].trim();
            String pricePart = parts[2].trim();

            java.util.regex.Pattern pricePattern =
                            java.util.regex.Pattern.compile("(\\d+[\\.,]?\\d*)\\s*([A-Za-z₺]+)?");
            java.util.regex.Matcher matcher = pricePattern.matcher(pricePart);

            if (!matcher.find()) {
                return null;
            }

            double price = Double.parseDouble(matcher.group(1).replace(",", "."));
            String currency = matcher.group(2) != null
                            ? matcher.group(2).replace("₺", "TRY").trim().toUpperCase()
                            : "TRY";

            // Check for duplicate (same restaurant + category + name)
            var existingOpt = menuItemRepository.findByRestaurantIdAndCategoryAndName(
                    restaurant.getId(),
                    category,
                    capitalize(name)
            );

            if (existingOpt.isPresent()) {
                // UPDATE existing item
                MenuItem existing = existingOpt.get();
                existing.setPriceAmount(price);
                existing.setCurrency(currency);
                existing.setLastApprovedAt(java.time.LocalDateTime.now());
                existing.setLastUpdatedBySubmissionId(null); // Will be set by caller
                return existing;
            }

            // CREATE new item
            return MenuItem.builder()
                            .restaurant(restaurant)
                            .category(category.isBlank() ? "Diğer" : category)
                            .name(capitalize(name))
                            .descriptionText(null)
                            .priceAmount(price)
                            .currency(currency)
                            .build();
        }

        String normalized = line.replace("₺", " TL").replace("TRY", " TL").trim();

        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(
                        "^(.*?)(\\d+[\\.,]?\\d*)\\s*(TL)?$",
                        java.util.regex.Pattern.CASE_INSENSITIVE
        );
        java.util.regex.Matcher matcher = pattern.matcher(normalized);

        if (!matcher.find()) {
            return null;
        }

        String name = matcher.group(1).trim();
        String priceRaw = matcher.group(2).replace(",", ".");
        double price = Double.parseDouble(priceRaw);

        if (name.isBlank()) {
            return null;
        }

        // Check for duplicate
        var existingOpt = menuItemRepository.findByRestaurantIdAndCategoryAndName(
                restaurant.getId(),
                "Katkıdan eklendi",
                capitalize(name)
        );

        if (existingOpt.isPresent()) {
            MenuItem existing = existingOpt.get();
            existing.setPriceAmount(price);
            existing.setCurrency("TRY");
            existing.setLastApprovedAt(java.time.LocalDateTime.now());
            return existing;
        }

        return MenuItem.builder()
                        .restaurant(restaurant)
                        .category("Katkıdan eklendi")
                        .name(capitalize(name))
                        .descriptionText(null)
                        .priceAmount(price)
                        .currency("TRY")
                        .build();
    }

    private String capitalize(String text) {
        if (text == null || text.isBlank()) return text;
        return text.substring(0, 1).toUpperCase(new Locale("tr", "TR")) + text.substring(1);
    }
}