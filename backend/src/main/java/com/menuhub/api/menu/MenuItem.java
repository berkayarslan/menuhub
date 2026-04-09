package com.menuhub.api.menu;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.menuhub.api.restaurant.Restaurant;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "menu_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String category;
    private String name;
    private String descriptionText;
    private Double priceAmount;
    private String currency;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastApprovedAt;

    private Long createdBySubmissionId;
    private Long lastUpdatedBySubmissionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    @JsonIgnore
    private Restaurant restaurant;

    @PrePersist
    public void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}