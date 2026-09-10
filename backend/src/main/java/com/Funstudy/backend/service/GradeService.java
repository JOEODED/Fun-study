package com.Funstudy.backend.service;

import com.Funstudy.backend.model.Course;
import com.Funstudy.backend.model.Grade;
import com.Funstudy.backend.model.User;
import com.Funstudy.backend.repository.CourseRepository;
import com.Funstudy.backend.repository.GradeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GradeService {

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private CourseRepository courseRepository;

    public Grade addGrade(Long courseId, double assignmentScore, double testScore, double examScore, User user) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        if (!course.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You do not have access to this course");
        }

        Grade grade = new Grade();
        grade.setCourse(course);
        grade.setUser(user);
        grade.setAssignmentScore(assignmentScore);
        grade.setTestScore(testScore);
        grade.setExamScore(examScore);

        double total = assignmentScore + testScore + examScore;
        grade.setTotalScore(total);

        String letter = calculateLetterGrade(total);
        grade.setLetterGrade(letter);
        grade.setGradePoint(letterToGradePoint(letter));

        return gradeRepository.save(grade);
    }

    private String calculateLetterGrade(double total) {
        if (total >= 70) return "A";
        if (total >= 60) return "B";
        if (total >= 50) return "C";
        if (total >= 45) return "D";
        return "F";
    }

    private double letterToGradePoint(String letter) {
        return switch (letter) {
            case "A" -> 5.0;
            case "B" -> 4.0;
            case "C" -> 3.0;
            case "D" -> 2.0;
            default -> 0.0;
        };
    }

    public List<Grade> getGradesForUser(User user) {
        return gradeRepository.findByUser(user);
    }

    public double calculateGPA(User user) {
        List<Grade> grades = gradeRepository.findByUser(user);

        double totalPoints = 0;
        int totalUnits = 0;

        for (Grade grade : grades) {
            int units = grade.getCourse().getCreditUnits();
            totalPoints += grade.getGradePoint() * units;
            totalUnits += units;
        }

        if (totalUnits == 0) return 0.0;
        return Math.round((totalPoints / totalUnits) * 100.0) / 100.0;
    }
}