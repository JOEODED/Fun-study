package com.Funstudy.backend.controller;

import com.Funstudy.backend.model.StudySession;
import com.Funstudy.backend.model.User;
import com.Funstudy.backend.repository.UserRepository;
import com.Funstudy.backend.service.StudySessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sessions")
public class StudySessionController {

    @Autowired
    private StudySessionService sessionService;

    @Autowired
    private UserRepository userRepository;

    private User getUserFromHeader(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public static class SessionRequest {
        private int durationMinutes;
        private Long courseId;

        public int getDurationMinutes() { return durationMinutes; }
        public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }

        public Long getCourseId() { return courseId; }
        public void setCourseId(Long courseId) { this.courseId = courseId; }
    }

    @PostMapping
    public ResponseEntity<?> logSession(@RequestBody SessionRequest request, @RequestHeader("X-User-Email") String email) {
        try {
            User user = getUserFromHeader(email);
            StudySession session = sessionService.logSession(request.getDurationMinutes(), request.getCourseId(), user);
            session.getUser().setPassword(null);
            return ResponseEntity.status(HttpStatus.CREATED).body(session);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllSessions(@RequestHeader("X-User-Email") String email) {
        try {
            User user = getUserFromHeader(email);
            List<StudySession> sessions = sessionService.getSessionsForUser(user);
            sessions.forEach(s -> s.getUser().setPassword(null));
            return ResponseEntity.ok(sessions);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/total-minutes")
    public ResponseEntity<?> getTotalMinutes(@RequestHeader("X-User-Email") String email) {
        try {
            User user = getUserFromHeader(email);
            int totalMinutes = sessionService.getTotalStudyMinutes(user);
            return ResponseEntity.ok(Map.of("totalMinutes", totalMinutes));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}