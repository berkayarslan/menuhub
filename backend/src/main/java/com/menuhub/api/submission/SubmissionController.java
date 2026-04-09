package com.menuhub.api.submission;

import com.menuhub.api.restaurant.RestaurantRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/submissions")
public class SubmissionController {

    private final MenuSubmissionRepository repository;
    private final RestaurantRepository restaurantRepository;

    public SubmissionController(MenuSubmissionRepository repository, RestaurantRepository restaurantRepository) {
        this.repository = repository;
        this.restaurantRepository = restaurantRepository;
    }

    @PostMapping
    public MenuSubmission create(@Valid @RequestBody CreateSubmissionRequest request) {
        // Validate restaurant exists
        restaurantRepository.findById(request.restaurantId())
                        .orElseThrow(() -> new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Restaurant not found with id: " + request.restaurantId()
                        ));

        String normalizedRawText = normalizeRawText(request);

        if (normalizedRawText.isBlank()) {
            throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Submission boş olamaz. rawText veya items gönderilmelidir."
            );
        }

        MenuSubmission submission = MenuSubmission.builder()
                        .restaurantId(request.restaurantId())
                        .sourceType(normalizeSourceType(request.sourceType()))
                        .rawText(normalizedRawText)
                        .status("PENDING_REVIEW")
                        .createdAt(java.time.LocalDateTime.now())
                        .build();

        return repository.save(submission);
    }

    @PostMapping("/ocr-preview")
    public Object preview(@RequestBody String rawText) {
        return java.util.Map.of(
                        "status", "PREVIEW_READY",
                        "rawText", rawText,
                        "items", java.util.List.of(
                                        java.util.Map.of("category", "Tahmini", "name", "Adana Kebap", "price", 340, "currency", "TRY"),
                                        java.util.Map.of("category", "Tahmini", "name", "Ayran", "price", 40, "currency", "TRY")
                        )
        );
    }

    private String normalizeRawText(CreateSubmissionRequest request) {
        if (request.items() != null && !request.items().isEmpty()) {
            return request.items().stream()
                            .filter(Objects::nonNull)
                            .map(this::toRawLine)
                            .filter(line -> !line.isBlank())
                            .collect(Collectors.joining("\n"));
        }

        if (request.rawText() != null && !request.rawText().isBlank()) {
            return request.rawText().lines()
                            .map(String::trim)
                            .filter(line -> !line.isBlank())
                            .collect(Collectors.joining("\n"));
        }

        return "";
    }

    private String toRawLine(SubmissionMenuRowRequest item) {
        String category = item.category() == null || item.category().isBlank() ? "Diğer" : item.category().trim();
        String name = item.name() == null ? "" : item.name().trim();
        String currency = normalizeCurrency(item.currency());

        if (name.isBlank()) {
            return "";
        }

        String price = item.priceAmount().stripTrailingZeros().toPlainString();
        return "%s | %s | %s %s".formatted(category, name, price, currency);
    }

    private String normalizeCurrency(String currency) {
        if (currency == null || currency.isBlank()) {
            return "TRY";
        }
        return currency.trim().toUpperCase(Locale.ROOT).replace("₺", "TRY");
    }

    private String normalizeSourceType(String sourceType) {
        return sourceType == null || sourceType.isBlank()
                        ? "STRUCTURED_FORM"
                        : sourceType.trim().toUpperCase(Locale.ROOT);
    }
}