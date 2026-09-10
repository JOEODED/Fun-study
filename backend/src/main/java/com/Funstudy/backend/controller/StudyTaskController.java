package com.Funstudy.backend.controller;

import com.Funstudy.backend.model.StudyTask;
import com.Funstudy.backend.model.User;
import com.Funstudy.backend.repository.UserRepository;
import com.Funstudy.backend.service.StudyTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class StudyTaskController {

    @Autowired
    private StudyTaskService taskService;

    @Autowired
    private UserRepository userRepository;

    private User getUserFromHeader(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody StudyTask task, @RequestHeader("X-User-Email") String email) {
        try {
            User user = getUserFromHeader(email);
            StudyTask created = taskService.createTask(task, user);
            created.getUser().setPassword(null);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllTasks(@RequestParam(required = false) String status,
                                          @RequestHeader("X-User-Email") String email) {
        try {
            User user = getUserFromHeader(email);
            List<StudyTask> tasks = (status != null)
                    ? taskService.getTasksByStatus(user, status)
                    : taskService.getTasksForUser(user);
            tasks.forEach(t -> t.getUser().setPassword(null));
            return ResponseEntity.ok(tasks);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTask(@PathVariable Long id, @RequestHeader("X-User-Email") String email) {
        try {
            User user = getUserFromHeader(email);
            StudyTask task = taskService.getTaskById(id, user);
            task.getUser().setPassword(null);
            return ResponseEntity.ok(task);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Long id, @RequestBody StudyTask task,
                                         @RequestHeader("X-User-Email") String email) {
        try {
            User user = getUserFromHeader(email);
            StudyTask updated = taskService.updateTask(id, task, user);
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
            StudyTask updated = taskService.markAsCompleted(id, user);
            updated.getUser().setPassword(null);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id, @RequestHeader("X-User-Email") String email) {
        try {
            User user = getUserFromHeader(email);
            taskService.deleteTask(id, user);
            return ResponseEntity.ok("Task deleted successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}