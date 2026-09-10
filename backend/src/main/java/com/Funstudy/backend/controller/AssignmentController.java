package com.Funstudy.backend.controller;

import com.Funstudy.backend.model.Assignment;
import com.Funstudy.backend.model.User;
import com.Funstudy.backend.repository.UserRepository;
import com.Funstudy.backend.service.AssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assignments")
public class AssignmentController {

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private UserRepository userRepository;

    private User getUserFromHeader(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @PostMapping
    public ResponseEntity<?> createAssignment(@RequestBody Assignment assignment,
                                               @RequestParam Long courseId,
                                               @RequestHeader("X-User-Email") String email) {
        try {
            User user = getUserFromHeader(email);
            Assignment created = assignmentService.createAssignment(assignment, courseId, user);
            created.getUser().setPassword(null);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllAssignments(@RequestParam(required = false) String status,
                                                @RequestHeader("X-User-Email") String email) {
        try {
            User user = getUserFromHeader(email);
            List<Assignment> assignments = (status != null)
                    ? assignmentService.getAssignmentsByStatus(user, status)
                    : assignmentService.getAssignmentsForUser(user);
            assignments.forEach(a -> a.getUser().setPassword(null));
            return ResponseEntity.ok(assignments);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAssignment(@PathVariable Long id, @RequestHeader("X-User-Email") String email) {
        try {
            User user = getUserFromHeader(email);
            Assignment assignment = assignmentService.getAssignmentById(id, user);
            assignment.getUser().setPassword(null);
            return ResponseEntity.ok(assignment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAssignment(@PathVariable Long id, @RequestBody Assignment assignment,
                                               @RequestHeader("X-User-Email") String email) {
        try {
            User user = getUserFromHeader(email);
            Assignment updated = assignmentService.updateAssignment(id, assignment, user);
            updated.getUser().setPassword(null);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<?> markCompleted(@PathVariable Long id, @RequestHeader("X-User-Email") String email) {
        try {
            User user = getUserFromHeader(email);
            Assignment updated = assignmentService.markAsCompleted(id, user);
            updated.getUser().setPassword(null);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAssignment(@PathVariable Long id, @RequestHeader("X-User-Email") String email) {
        try {
            User user = getUserFromHeader(email);
            assignmentService.deleteAssignment(id, user);
            return ResponseEntity.ok("Assignment deleted successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
