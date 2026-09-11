package com.Funstudy.backend.controller;

import com.Funstudy.backend.model.Assignment;
import com.Funstudy.backend.model.Course;
import com.Funstudy.backend.model.User;
import com.Funstudy.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired private CourseRepository courseRepository;
    @Autowired private AssignmentRepository assignmentRepository;
    @Autowired private StudyTaskRepository taskRepository;
    @Autowired private StudySessionRepository sessionRepository;
    @Autowired private UserRepository userRepository;

    private User getUserFromHeader(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @GetMapping
    public ResponseEntity<?> getDashboard(@RequestHeader("X-User-Email") String email) {
        try {
            User user = getUserFromHeader(email);

            List<Course> courses = courseRepository.findByUser(user);
            List<Assignment> assignments = assignmentRepository.findByUser(user);

            long pending = assignments.stream().filter(a -> "PENDING".equals(a.getStatus())).count();
            long completed = assignments.stream().filter(a -> "COMPLETED".equals(a.getStatus())).count();

            long upcomingDeadlines = assignments.stream()
                    .filter(a -> "PENDING".equals(a.getStatus()))
                    .filter(a -> a.getDeadline() != null && !a.getDeadline().isBefore(LocalDate.now()))
                    .filter(a -> a.getDeadline().isBefore(LocalDate.now().plusDays(7)))
                    .count();

            int totalStudyMinutes = sessionRepository.findByUser(user)
                    .stream().mapToInt(s -> s.getDurationMinutes()).sum();

            double progress = (assignments.isEmpty()) ? 0 :
                    Math.round(((double) completed / assignments.size()) * 100.0);

            Map<String, Object> stats = new HashMap<>();
            stats.put("totalCourses", courses.size());
            stats.put("pendingAssignments", pending);
            stats.put("completedAssignments", completed);
            stats.put("upcomingDeadlines", upcomingDeadlines);
            stats.put("totalStudyHours", Math.round((totalStudyMinutes / 60.0) * 10.0) / 10.0);
            stats.put("overallProgress", progress);

            return ResponseEntity.ok(stats);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}