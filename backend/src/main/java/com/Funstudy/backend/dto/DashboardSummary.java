package com.Funstudy.backend.dto;

import java.util.Map;

public class DashboardSummary {
    private long totalCourses;
    private long pendingAssignments;
    private long completedAssignments;
    private int totalStudyMinutes;
    private Map<String, Integer> weeklyStudyMinutes; // e.g. "Mon" -> 45

    public DashboardSummary(long totalCourses, long pendingAssignments, long completedAssignments,
                             int totalStudyMinutes, Map<String, Integer> weeklyStudyMinutes) {
        this.totalCourses = totalCourses;
        this.pendingAssignments = pendingAssignments;
        this.completedAssignments = completedAssignments;
        this.totalStudyMinutes = totalStudyMinutes;
        this.weeklyStudyMinutes = weeklyStudyMinutes;
    }

    public long getTotalCourses() { return totalCourses; }
    public long getPendingAssignments() { return pendingAssignments; }
    public long getCompletedAssignments() { return completedAssignments; }
    public int getTotalStudyMinutes() { return totalStudyMinutes; }
    public Map<String, Integer> getWeeklyStudyMinutes() { return weeklyStudyMinutes; }
}