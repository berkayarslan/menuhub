package com.menuhub.api.submission;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuSubmissionRepository extends JpaRepository<MenuSubmission, Long> {
    List<MenuSubmission> findAllByOrderByIdDesc();
}