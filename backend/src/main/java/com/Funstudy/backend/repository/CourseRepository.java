package com.Funstudy.backend.repository;

import com.Funstudy.backend.model.Course;
import com.Funstudy.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByUser(User user);
    List<Course> findByUserAndCourseCodeContainingIgnoreCase(User user, String keyword);
}