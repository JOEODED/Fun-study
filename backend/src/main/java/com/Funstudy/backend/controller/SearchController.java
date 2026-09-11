package com.Funstudy.backend.controller;

import com.Funstudy.backend.model.*;
import com.Funstudy.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    @Autowired private CourseRepository courseRepository;
    @Autowired private AssignmentRepository assignmentRepository;
    @Autowired private StudyTaskRepository taskRepository;
    @Autowired private UserRepository userRepository;

    private User getUserFromHeader(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @GetMapping("/courses")
    public ResponseEntity<?> searchCourses(@RequestParam(required = false) String keyword,
                                            @RequestHeader("X-User-Email") String email) {
        User user = getUserFromHeader(email);
        List<Course> courses = (keyword != null)
                ? courseRepository.findByUserAndCourseCodeContainingIgnoreCase(user, keyword)
                : courseRepository.findByUser(user);
        courses.forEach(c -> c.getUser().setPassword(null));
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/assignments")
    public ResponseEntity<?> filterAssignments(@RequestParam(required = false) String status,
                                                @RequestHeader("X-User-Email") String email) {
        User user = getUserFromHeader(email);
        List<Assignment> assignments = (status != null)
                ? assignmentRepository.findByUserAndStatus(user, status)
                : assignmentRepository.findByUser(user);
        assignments.forEach(a -> a.getUser().setPassword(null));
        return ResponseEntity.ok(assignments);
    }

    @GetMapping("/tasks")
    public ResponseEntity<?> filterTasks(@RequestParam(required = false) String status,
                                          @RequestHeader("X-User-Email") String email) {
        User user = getUserFromHeader(email);
        List<StudyTask> tasks = (status != null)
                ? taskRepository.findByUserAndStatus(user, status)
                : taskRepository.findByUser(user);
        tasks.forEach(t -> t.getUser().setPassword(null));
        return ResponseEntity.ok(tasks);
    }
}