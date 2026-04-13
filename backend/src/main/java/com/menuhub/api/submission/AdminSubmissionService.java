package com.menuhub.api.submission;

import com.menuhub.api.menu.MenuItem;
import com.menuhub.api.menu.MenuItemRepository;
import com.menuhub.api.restaurant.Restaurant;
import com.menuhub.api.restaurant.RestaurantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AdminSubmissionService {

    private final MenuSubmissionRepository submissionRepository;
    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;

    public AdminSubmissionService(
            MenuSubmissionRepository submissionRepository,
            RestaurantRepository restaurantRepository,
            MenuItemRepository menuItemRepository
    ) {
        this.submissionRepository = submissionRepository;
        this.restaurantRepository = restaurantRepository;
        this.menuItemRepository = menuItemRepository;
    }

    public List<AdminSubmissionListItem> listSubmissions() {
        List<MenuSubmission> submissions = submissionRepository.findAll().stream()
                .sorted(
                        Comparator
                                .comparing((MenuSubmission s) -> !"PENDING_REVIEW".equals(s.getStatus()))
                                .thenComparing(MenuSubmission::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder()))
                                .thenComparing(MenuSubmission::getId, Comparator.reverseOrder())
                )
                .toList();

        Set<Long> restaurantIds = submissions.stream()
                .map(MenuSubmission::getRestaurantId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<Long, String> restaurantNameMap = restaurantRepository.findAllById(restaurantIds).stream()
                .collect(Collectors.toMap(Restaurant::getId, Restaurant::getName));

        return submissions.stream()
                .map(submission -> new AdminSubmissionListItem(
                        submission.getId(),
                        submission.getRestaurantId(),
                        restaurantNameMap.getOrDefault(submission.getRestaurantId(), "Bilinmeyen Restoran"),
                        submission.getSourceType(),
                        submission.getRawText(),
                        submission.getStatus(),
                        submission.getCreatedAt(),
                        submission.getApprovedAt(),
                        submission.getRejectedAt()
                ))
                .toList();
    }

    @Transactional
    public MenuSubmission approve(Long id) {
        MenuSubmission submission = submissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Submission not found"));

        if ("APPROVED".equals(submission.getStatus())) {
            return submission;
        }

        Restaurant restaurant = restaurantRepository.findById(submission.getRestaurantId())
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        if ("REJECTED".equals(submission.getStatus())) {
            List<MenuItem> oldItems = menuItemRepository.findByRestaurantId(restaurant.getId());
            menuItemRepository.deleteAll(oldItems);
        }

        java.time.LocalDateTime approvalTime = java.time.LocalDateTime.now();
        java.time.LocalDateTime contributionTime = submission.getCreatedAt() != null
                ? submission.getCreatedAt()
                : approvalTime;
        List<MenuItem> parsedItems = parseRawText(
                submission.getRawText(),
                restaurant,
                submission.getId(),
                contributionTime,
                approvalTime
        );
        menuItemRepository.saveAll(parsedItems);

        submission.setStatus("APPROVED");
        submission.setApprovedAt(approvalTime);
        submission.setRejectedAt(null);
        return submissionRepository.save(submission);
    }

    @Transactional
    public MenuSubmission reject(Long id) {
        MenuSubmission submission = submissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Submission not found"));

        if ("REJECTED".equals(submission.getStatus())) {
            return submission;
        }

        submission.setStatus("REJECTED");
        submission.setRejectedAt(java.time.LocalDateTime.now());
        submission.setApprovedAt(null);
        return submissionRepository.save(submission);
    }

    private List<MenuItem> parseRawText(
            String rawText,
            Restaurant restaurant,
            Long submissionId,
            java.time.LocalDateTime contributionTime,
            java.time.LocalDateTime approvalTime
    ) {
        List<String> lines = rawText.lines()
                .map(String::trim)
                .filter(line -> !line.isBlank())
                .toList();

        return lines.stream()
                .map(line -> toMenuItem(line, restaurant, submissionId, contributionTime, approvalTime))
                .filter(Objects::nonNull)
                .toList();
    }

    private MenuItem toMenuItem(
            String line,
            Restaurant restaurant,
            Long submissionId,
            java.time.LocalDateTime contributionTime,
            java.time.LocalDateTime approvalTime
    ) {
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

            var existingOpt = menuItemRepository.findByRestaurantIdAndCategoryAndName(
                    restaurant.getId(),
                    category,
                    capitalize(name)
            );

            if (existingOpt.isPresent()) {
                MenuItem existing = existingOpt.get();
                existing.setPriceAmount(price);
                existing.setCurrency(currency);
                existing.setLastApprovedAt(approvalTime);
                existing.setLastUpdatedBySubmissionId(submissionId);
                return existing;
            }

            return MenuItem.builder()
                    .restaurant(restaurant)
                    .category(category.isBlank() ? "Diğer" : category)
                    .name(capitalize(name))
                    .descriptionText(null)
                    .priceAmount(price)
                    .currency(currency)
                    .createdAt(contributionTime)
                    .updatedAt(contributionTime)
                    .lastApprovedAt(approvalTime)
                    .createdBySubmissionId(submissionId)
                    .lastUpdatedBySubmissionId(submissionId)
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

        var existingOpt = menuItemRepository.findByRestaurantIdAndCategoryAndName(
                restaurant.getId(),
                "Katkıdan eklendi",
                capitalize(name)
        );

        if (existingOpt.isPresent()) {
            MenuItem existing = existingOpt.get();
            existing.setPriceAmount(price);
            existing.setCurrency("TRY");
            existing.setLastApprovedAt(approvalTime);
            existing.setLastUpdatedBySubmissionId(submissionId);
            return existing;
        }

        return MenuItem.builder()
                .restaurant(restaurant)
                .category("Katkıdan eklendi")
                .name(capitalize(name))
                .descriptionText(null)
                .priceAmount(price)
                .currency("TRY")
                .createdAt(contributionTime)
                .updatedAt(contributionTime)
                .lastApprovedAt(approvalTime)
                .createdBySubmissionId(submissionId)
                .lastUpdatedBySubmissionId(submissionId)
                .build();
    }

    private String capitalize(String text) {
        if (text == null || text.isBlank()) {
            return text;
        }
        return text.substring(0, 1).toUpperCase(new Locale("tr", "TR")) + text.substring(1);
    }
}

