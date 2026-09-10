package com.Funstudy.backend.service;

import com.Funstudy.backend.model.StudyTask;
import com.Funstudy.backend.model.User;
import com.Funstudy.backend.repository.StudyTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudyTaskService {

    @Autowired
    private StudyTaskRepository taskRepository;

    public StudyTask createTask(StudyTask task, User user) {
        task.setUser(user);
        if (task.getStatus() == null) {
            task.setStatus("PENDING");
        }
        return taskRepository.save(task);
    }

    public List<StudyTask> getTasksForUser(User user) {
        return taskRepository.findByUser(user);
    }

    public List<StudyTask> getTasksByStatus(User user, String status) {
        return taskRepository.findByUserAndStatus(user, status);
    }

    public StudyTask getTaskById(Long id, User user) {
        StudyTask task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        if (!task.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You do not have access to this task");
        }
        return task;
    }

    public StudyTask updateTask(Long id, StudyTask updatedData, User user) {
        StudyTask task = getTaskById(id, user);

        task.setTitle(updatedData.getTitle());
        task.setDescription(updatedData.getDescription());
        task.setDeadline(updatedData.getDeadline());
        task.setPriority(updatedData.getPriority());
        task.setStatus(updatedData.getStatus());

        return taskRepository.save(task);
    }

    public StudyTask markAsCompleted(Long id, User user) {
        StudyTask task = getTaskById(id, user);
        task.setStatus("COMPLETED");
        return taskRepository.save(task);
    }

    public void deleteTask(Long id, User user) {
        StudyTask task = getTaskById(id, user);
        taskRepository.delete(task);
    }
}