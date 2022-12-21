package com.exercise.passgen.models.schemas;

import com.exercise.passgen.enums.Complexity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordDTO {
    private String password;
    private Complexity complexity;
    private LocalDateTime generationDateTime;
}
