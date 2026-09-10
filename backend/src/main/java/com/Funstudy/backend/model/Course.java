package com.Funstudy.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String courseCode;

    @Column(nullable = false)
    private String courseName;

    private String lecturer;

    private int creditUnits;

    private String semester;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Course() {}

    public Course(String courseCode, String courseName, String lecturer, int creditUnits, String semester, User user) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.lecturer = lecturer;
        this.creditUnits = creditUnits;
        this.semester = semester;
        this.user = user;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public String getLecturer() { return lecturer; }
    public void setLecturer(String lecturer) { this.lecturer = lecturer; }

    public int getCreditUnits() { return creditUnits; }
    public void setCreditUnits(int creditUnits) { this.creditUnits = creditUnits; }

    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
    

