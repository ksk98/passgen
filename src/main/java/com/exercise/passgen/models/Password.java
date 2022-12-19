package com.exercise.passgen.models;

import com.exercise.passgen.enums.Complexity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Password {
    @Id
    private Long id;
    private String passwordHash;
    private Complexity complexity;
    private LocalDateTime createdOn;
}
