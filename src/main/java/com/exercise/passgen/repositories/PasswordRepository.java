package com.exercise.passgen.repositories;

import com.exercise.passgen.services.PasswordService;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordRepository extends JpaRepository<Long, PasswordService> {

}
