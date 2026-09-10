package com.Funstudy.backend.repository;

import com.Funstudy.backend.model.Grade;
import com.Funstudy.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GradeRepository extends JpaRepository<Grade, Long> {
    List<Grade> findByUser(User user);
}