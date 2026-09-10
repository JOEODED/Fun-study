package com.Funstudy.backend.repository;

import com.Funstudy.backend.model.StudyTask;
import com.Funstudy.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudyTaskRepository extends JpaRepository<StudyTask, Long> {
    List<StudyTask> findByUser(User user);
    List<StudyTask> findByUserAndStatus(User user, String status);
}
