package com.Funstudy.backend.service;

import com.Funstudy.backend.model.Assignment;
import com.Funstudy.backend.model.Course;
import com.Funstudy.backend.model.User;
import com.Funstudy.backend.repository.AssignmentRepository;
import com.Funstudy.backend.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssignmentService {

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private CourseRepository courseRepository;

    public Assignment createAssignment(Assignment assignment, Long courseId, User user) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        if (!course.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You do not have access to this course");
        }

        assignment.setCourse(course);
        assignment.setUser(user);
        if (assignment.getStatus() == null) {
            assignment.setStatus("PENDING");
        }
        return assignmentRepository.save(assignment);
    }

    public List<Assignment> getAssignmentsForUser(User user) {
        return assignmentRepository.findByUser(user);
    }

    public List<Assignment> getAssignmentsByStatus(User user, String status) {
        return assignmentRepository.findByUserAndStatus(user, status);
    }

    public Assignment getAssignmentById(Long id, User user) {
        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found"));

        if (!assignment.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You do not have access to this assignment");
        }
        return assignment;
    }

    public Assignment updateAssignment(Long id, Assignment updatedData, User user) {
        Assignment assignment = getAssignmentById(id, user);

        assignment.setTitle(updatedData.getTitle());
        assignment.setDescription(updatedData.getDescription());
        assignment.setDeadline(updatedData.getDeadline());
        assignment.setPriority(updatedData.getPriority());
        assignment.setStatus(updatedData.getStatus());

        return assignmentRepository.save(assignment);
    }

    public Assignment markAsCompleted(Long id, User user) {
        Assignment assignment = getAssignmentById(id, user);
        assignment.setStatus("COMPLETED");
        return assignmentRepository.save(assignment);
    }

    public void deleteAssignment(Long id, User user) {
        Assignment assignment = getAssignmentById(id, user);
        assignmentRepository.delete(assignment);
    }
}
