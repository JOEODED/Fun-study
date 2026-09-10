package com.Funstudy.backend.repository;

import com.Funstudy.backend.model.StudySession;
import com.Funstudy.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudySessionRepository extends JpaRepository<StudySession, Long> {
    List<StudySession> findByUser(User user);
}