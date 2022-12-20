package com.exercise.passgen.models.schemas;

import com.exercise.passgen.enums.Complexity;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class PasswordGenerationResponseDTO {
    private List<String> passwords, duplicates;
    private Complexity complexity;
}
