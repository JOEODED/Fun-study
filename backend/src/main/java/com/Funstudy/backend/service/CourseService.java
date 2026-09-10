package com.Funstudy.backend.service;

import com.Funstudy.backend.model.Course;
import com.Funstudy.backend.model.User;
import com.Funstudy.backend.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    public Course createCourse(Course course, User user) {
        course.setUser(user);
        return courseRepository.save(course);
    }

    public List<Course> getCoursesForUser(User user) {
        return courseRepository.findByUser(user);
    }

    public Course getCourseById(Long id, User user) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        if (!course.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You do not have access to this course");
        }
        return course;
    }

    public Course updateCourse(Long id, Course updatedData, User user) {
        Course course = getCourseById(id, user); // reuses ownership check above

        course.setCourseCode(updatedData.getCourseCode());
        course.setCourseName(updatedData.getCourseName());
        course.setLecturer(updatedData.getLecturer());
        course.setCreditUnits(updatedData.getCreditUnits());
        course.setSemester(updatedData.getSemester());

        return courseRepository.save(course);
    }

    public void deleteCourse(Long id, User user) {
        Course course = getCourseById(id, user); // reuses ownership check above
        courseRepository.delete(course);
    }
}