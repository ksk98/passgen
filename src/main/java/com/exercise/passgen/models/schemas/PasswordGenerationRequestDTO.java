package com.exercise.passgen.models.schemas;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PasswordGenerationRequestDTO {
    private int length, amount;
    private boolean lowerCase, upperCase, specialCase;
}
