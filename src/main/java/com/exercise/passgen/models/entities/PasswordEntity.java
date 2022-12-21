package com.exercise.passgen.models.entities;

import com.exercise.passgen.enums.Complexity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class PasswordEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private byte[] searchHash;
    @Column(unique = true)
    private String passwordHash;
    private Complexity complexity;
    private LocalDateTime generationDateTime;
}
