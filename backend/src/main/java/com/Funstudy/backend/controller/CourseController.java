package com.Funstudy.backend.controller;

import com.Funstudy.backend.model.Course;
import com.Funstudy.backend.model.User;
import com.Funstudy.backend.repository.UserRepository;
import com.Funstudy.backend.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private UserRepository userRepository;

    // TEMPORARY: until we build real login sessions (Phase 6), we pass the user's email
    // as a header so we know "who" is making the request. This is not secure yet -
    // we'll replace it with a proper token in the next phase.
    private User getUserFromHeader(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @PostMapping
    public ResponseEntity<?> createCourse(@RequestBody Course course, @RequestHeader("X-User-Email") String email) {
        try {
            User user = getUserFromHeader(email);
            Course created = courseService.createCourse(course, user);
            created.getUser().setPassword(null);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllCourses(@RequestHeader("X-User-Email") String email) {
        try {
            User user = getUserFromHeader(email);
            List<Course> courses = courseService.getCoursesForUser(user);
            courses.forEach(c -> c.getUser().setPassword(null));
            return ResponseEntity.ok(courses);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCourse(@PathVariable Long id, @RequestHeader("X-User-Email") String email) {
        try {
            User user = getUserFromHeader(email);
            Course course = courseService.getCourseById(id, user);
            course.getUser().setPassword(null);
            return ResponseEntity.ok(course);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCourse(@PathVariable Long id, @RequestBody Course course, @RequestHeader("X-User-Email") String email) {
        try {
            User user = getUserFromHeader(email);
            Course updated = courseService.updateCourse(id, course, user);
            updated.getUser().setPassword(null);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCourse(@PathVariable Long id, @RequestHeader("X-User-Email") String email) {
        try {
            User user = getUserFromHeader(email);
            courseService.deleteCourse(id, user);
            return ResponseEntity.ok("Course deleted successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}