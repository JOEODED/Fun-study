package com.Funstudy.backend.controller;

import com.Funstudy.backend.model.*;
import com.Funstudy.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    @Autowired private CourseRepository courseRepository;
    @Autowired private AssignmentRepository assignmentRepository;
    @Autowired private StudyTaskRepository studyTaskRepository;
    @Autowired private UserRepository userRepository;

    private User getUserFromHeader(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @GetMapping("/courses")
    public ResponseEntity<?> searchCourses(@RequestParam String keyword,
                                            @RequestHeader("X-User-Email") String email) {
        try {
            User user = getUserFromHeader(email);
            List<Course> results = courseRepository.findByUserAndCourseNameContainingIgnoreCase(user, keyword);
            results.forEach(c -> c.getUser().setPassword(null));
            return ResponseEntity.ok(results);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/assignments")
    public ResponseEntity<?> searchAssignments(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestHeader("X-User-Email") String email) {
        try {
            User user = getUserFromHeader(email);
            List<Assignment> results;
            if (keyword != null && !keyword.isBlank()) {
                results = assignmentRepository.findByUserAndTitleContainingIgnoreCase(user, keyword);
            } else if (status != null && !status.isBlank()) {
                results = assignmentRepository.findByUserAndStatus(user, status);
            } else {
                results = assignmentRepository.findByUser(user);
            }
            results.forEach(a -> a.getUser().setPassword(null));
            return ResponseEntity.ok(results);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/tasks")
    public ResponseEntity<?> filterTasks(@RequestParam String status,
                                          @RequestHeader("X-User-Email") String email) {
        try {
            User user = getUserFromHeader(email);
            List<StudyTask> results = studyTaskRepository.findByUserAndStatus(user, status);
            results.forEach(t -> t.getUser().setPassword(null));
            return ResponseEntity.ok(results);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}