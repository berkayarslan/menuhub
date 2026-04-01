package com.menuhub.api.submission;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "menu_submissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuSubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long restaurantId;
    private String sourceType;

    @Column(columnDefinition = "TEXT")
    private String rawText;

    private String status;

    private LocalDateTime createdAt;
}