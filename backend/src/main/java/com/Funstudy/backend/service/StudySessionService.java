package com.Funstudy.backend.service;

import com.Funstudy.backend.model.Course;
import com.Funstudy.backend.model.StudySession;
import com.Funstudy.backend.model.User;
import com.Funstudy.backend.repository.CourseRepository;
import com.Funstudy.backend.repository.StudySessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StudySessionService {

    @Autowired
    private StudySessionRepository sessionRepository;

    @Autowired
    private CourseRepository courseRepository;

    public StudySession logSession(int durationMinutes, Long courseId, User user) {
        StudySession session = new StudySession();
        session.setDurationMinutes(durationMinutes);
        session.setCompletedAt(LocalDateTime.now());
        session.setUser(user);

        if (courseId != null) {
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new IllegalArgumentException("Course not found"));
            session.setCourse(course);
        }

        return sessionRepository.save(session);
    }

    public List<StudySession> getSessionsForUser(User user) {
        return sessionRepository.findByUser(user);
    }

    public int getTotalStudyMinutes(User user) {
        return sessionRepository.findByUser(user)
                .stream()
                .mapToInt(StudySession::getDurationMinutes)
                .sum();
    }
}