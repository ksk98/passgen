package com.exercise.passgen.models.schemas;

import com.exercise.passgen.enums.Complexity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordGenerationResponseDTO {
    private List<String> passwords, duplicates;
    private Complexity complexity;
}
