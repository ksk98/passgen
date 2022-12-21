package com.exercise.passgen.repositories;

import com.exercise.passgen.models.entities.PasswordEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PasswordRepository extends JpaRepository<PasswordEntity, Long> {
    boolean existsByPasswordHash(String passwordHash);
    PasswordEntity findByPasswordHash(String passwordHash);
    PasswordEntity deletePasswordByPasswordHash(String passwordHash);
    List<PasswordEntity> findAllBySearchHash(byte[] searchHash);
}
