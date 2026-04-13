package com.menuhub.api.submission;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/submissions")
public class AdminSubmissionController {

    private final AdminSubmissionService adminSubmissionService;

    public AdminSubmissionController(AdminSubmissionService adminSubmissionService) {
        this.adminSubmissionService = adminSubmissionService;
    }

    @GetMapping
    public List<AdminSubmissionListItem> list() {
        return adminSubmissionService.listSubmissions();
    }

    @PostMapping("/{id}/approve")
    public MenuSubmission approve(@PathVariable Long id) {
        return adminSubmissionService.approve(id);
    }

    @PostMapping("/{id}/reject")
    public MenuSubmission reject(@PathVariable Long id) {
        return adminSubmissionService.reject(id);
    }
}