package com.Funstudy.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "study_sessions")
public class StudySession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int durationMinutes;

    private LocalDateTime completedAt;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course; // optional - session might not be tied to a specific course

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public StudySession() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}