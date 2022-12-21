package com.exercise.passgen.controllers;

import com.exercise.passgen.exceptions.IncorrectPasswordLengthException;
import com.exercise.passgen.exceptions.NoCaseException;
import com.exercise.passgen.exceptions.TooManyPasswordsAtOnceException;
import com.exercise.passgen.models.schemas.*;
import com.exercise.passgen.services.PasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("password")
@RequiredArgsConstructor
public class PasswordController {
    private final PasswordService passwordService;

    @PostMapping("/generate")
    public PasswordGenerationResponseDTO generatePasswords(@RequestBody PasswordGenerationRequestDTO request)
            throws IncorrectPasswordLengthException, NoCaseException, TooManyPasswordsAtOnceException, NoSuchAlgorithmException {
        List<PasswordDTO> passwords = passwordService.generatePasswords(request);
        List<PasswordDTO> duplicates = passwordService.persistUniquePasswords(passwords);

        return PasswordGenerationResponseDTO.builder()
                .passwords(passwords.stream().map(PasswordDTO::getPassword).collect(Collectors.toList()))
                .duplicates(duplicates.stream().map(PasswordDTO::getPassword).collect(Collectors.toList()))
                .complexity(passwords.get(0).getComplexity())
                .build();
    }

    @PostMapping("/complexity")
    public PasswordDTO checkComplexity(@RequestBody String password)
            throws IncorrectPasswordLengthException, NoSuchAlgorithmException {
        PasswordDTO out = passwordService.getPasswordDTO(password);

        if (out == null) {
            out = PasswordDTO.builder()
                    .password(password)
                    .complexity(passwordService.getComplexity(password))
                    .generationDateTime(null)
                    .build();
        }

        return out;
    }

    @DeleteMapping("")
    public PasswordDTO deletePassword(@RequestBody String password)
            throws IncorrectPasswordLengthException, NoSuchAlgorithmException {
        PasswordDTO out = passwordService.deletePassword(password);

        if (out == null) {
            out = PasswordDTO.builder()
                    .password(password)
                    .complexity(passwordService.getComplexity(password))
                    .generationDateTime(null)
                    .build();
        }

        return out;
    }
}
