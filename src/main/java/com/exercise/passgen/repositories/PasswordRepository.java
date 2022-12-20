package com.exercise.passgen.repositories;

import com.exercise.passgen.models.entities.PasswordEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordRepository extends JpaRepository<PasswordEntity, Long> {
    boolean existsByPasswordHash(String passwordHash);
}
