package com.menuhub.api.submission;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/submissions")
public class SubmissionController {

    private final SubmissionService submissionService;

    public SubmissionController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @PostMapping
    public MenuSubmission create(@Valid @RequestBody CreateSubmissionRequest request) {
        return submissionService.create(request);
    }

    @PostMapping("/ocr-preview")
    public Object preview(@RequestBody String rawText) {
        return submissionService.preview(rawText);
    }
}