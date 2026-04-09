package com.menuhub.api.submission;

import com.menuhub.api.restaurant.Restaurant;
import com.menuhub.api.restaurant.RestaurantRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AdminSubmissionService {

    private final MenuSubmissionRepository submissionRepository;
    private final RestaurantRepository restaurantRepository;

    public AdminSubmissionService(
                    MenuSubmissionRepository submissionRepository,
                    RestaurantRepository restaurantRepository
    ) {
        this.submissionRepository = submissionRepository;
        this.restaurantRepository = restaurantRepository;
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
}