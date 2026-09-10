package com.Funstudy.backend.repository;

import com.Funstudy.backend.model.Assignment;
import com.Funstudy.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findByUser(User user);
    List<Assignment> findByUserAndStatus(User user, String status);
}