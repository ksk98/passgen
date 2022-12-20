package com.exercise.passgen.models.schemas;

import com.exercise.passgen.enums.Complexity;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class PasswordDTO {
    private String password;
    private Complexity complexity;
    private LocalDateTime generationDateTime;
}
