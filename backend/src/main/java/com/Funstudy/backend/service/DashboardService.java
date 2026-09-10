package com.Funstudy.backend.service;

import com.Funstudy.backend.dto.DashboardSummary;
import com.Funstudy.backend.model.Assignment;
import com.Funstudy.backend.model.StudySession;
import com.Funstudy.backend.model.User;
import com.Funstudy.backend.repository.AssignmentRepository;
import com.Funstudy.backend.repository.CourseRepository;
import com.Funstudy.backend.repository.StudySessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.TextStyle;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class DashboardService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private StudySessionRepository sessionRepository;

    public DashboardSummary buildSummary(User user) {
        long totalCourses = courseRepository.findByUser(user).size();

        List<Assignment> assignments = assignmentRepository.findByUser(user);
        long pending = assignments.stream()
                .filter(a -> "PENDING".equalsIgnoreCase(a.getStatus()))
                .count();
        long completed = assignments.stream()
                .filter(a -> "COMPLETED".equalsIgnoreCase(a.getStatus()))
                .count();

        List<StudySession> sessions = sessionRepository.findByUser(user);
        int totalMinutes = sessions.stream()
                .mapToInt(StudySession::getDurationMinutes)
                .sum();

        Map<String, Integer> weekly = new LinkedHashMap<>();
        for (StudySession s : sessions) {
            String day = s.getCompletedAt().getDayOfWeek()
                    .getDisplayName(TextStyle.SHORT, Locale.ENGLISH); // "Mon", "Tue"...
            weekly.merge(day, s.getDurationMinutes(), Integer::sum);
        }

        return new DashboardSummary(totalCourses, pending, completed, totalMinutes, weekly);
    }
}