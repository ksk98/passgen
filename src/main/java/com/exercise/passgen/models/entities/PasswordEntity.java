package com.exercise.passgen.models.entities;

import com.exercise.passgen.enums.Complexity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class PasswordEntity {
    @Id
    private Long id;
    @Column(unique = true)
    private String passwordHash;
    private Complexity complexity;
    private LocalDateTime generationDateTime;
}
