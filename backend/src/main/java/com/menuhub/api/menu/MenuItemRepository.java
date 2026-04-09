package com.menuhub.api.menu;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    List<MenuItem> findByRestaurantId(Long restaurantId);

    // Find duplicate items (same restaurant + category + name)
    Optional<MenuItem> findByRestaurantIdAndCategoryAndName(
            Long restaurantId,
            String category,
            String name
    );

    // Find items by submission
    List<MenuItem> findByCreatedBySubmissionId(Long submissionId);
    List<MenuItem> findByLastUpdatedBySubmissionId(Long submissionId);
}