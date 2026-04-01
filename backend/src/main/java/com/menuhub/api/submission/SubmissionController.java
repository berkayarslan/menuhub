package com.menuhub.api.submission;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/submissions")
public class SubmissionController {

    private final MenuSubmissionRepository repository;

    public SubmissionController(MenuSubmissionRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public MenuSubmission create(@Valid @RequestBody CreateSubmissionRequest request) {
        MenuSubmission submission = MenuSubmission.builder()
                        .restaurantId(request.restaurantId())
                        .sourceType(request.sourceType())
                        .rawText(request.rawText())
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
}
