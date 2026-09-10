package com.Funstudy.backend.controller;

import com.Funstudy.backend.model.Grade;
import com.Funstudy.backend.model.User;
import com.Funstudy.backend.repository.UserRepository;
import com.Funstudy.backend.service.GradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/grades")
public class GradeController {

    @Autowired
    private GradeService gradeService;

    @Autowired
    private UserRepository userRepository;

    private User getUserFromHeader(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public static class GradeRequest {
        private Long courseId;
        private double assignmentScore;
        private double testScore;
        private double examScore;

        public Long getCourseId() { return courseId; }
        public void setCourseId(Long courseId) { this.courseId = courseId; }

        public double getAssignmentScore() { return assignmentScore; }
        public void setAssignmentScore(double assignmentScore) { this.assignmentScore = assignmentScore; }

        public double getTestScore() { return testScore; }
        public void setTestScore(double testScore) { this.testScore = testScore; }

        public double getExamScore() { return examScore; }
        public void setExamScore(double examScore) { this.examScore = examScore; }
    }

    @PostMapping
    public ResponseEntity<?> addGrade(@RequestBody GradeRequest request, @RequestHeader("X-User-Email") String email) {
        try {
            User user = getUserFromHeader(email);
            Grade grade = gradeService.addGrade(
                    request.getCourseId(),
                    request.getAssignmentScore(),
                    request.getTestScore(),
                    request.getExamScore(),
                    user
            );
            grade.getUser().setPassword(null);
            return ResponseEntity.status(HttpStatus.CREATED).body(grade);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllGrades(@RequestHeader("X-User-Email") String email) {
        try {
            User user = getUserFromHeader(email);
            List<Grade> grades = gradeService.getGradesForUser(user);
            grades.forEach(g -> g.getUser().setPassword(null));
            return ResponseEntity.ok(grades);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/gpa")
    public ResponseEntity<?> getGPA(@RequestHeader("X-User-Email") String email) {
        try {
            User user = getUserFromHeader(email);
            double gpa = gradeService.calculateGPA(user);
            return ResponseEntity.ok(Map.of("gpa", gpa));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}