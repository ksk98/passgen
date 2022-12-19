package com.exercise.passgen.PasswordService;

import com.exercise.passgen.enums.Complexity;
import com.exercise.passgen.exceptions.IncorrectPasswordLengthException;
import com.exercise.passgen.services.PasswordService;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PasswordValidationTests {
    @InjectMocks
    PasswordService passwordService;

    @Test
    public void validateUltraComplexity() throws IncorrectPasswordLengthException {
        // 16+ characters
        assertEquals(Complexity.ULTRA, passwordService.getComplexity("$AaAa$AaAa$AaAa$AaAa"));
        assertEquals(Complexity.HIGH, passwordService.getComplexity("$AaAa$AaAa$AaAa$"));

        // Special case
        assertEquals(Complexity.MEDIUM, passwordService.getComplexity("SAaAaSAaAaSAaAaSAaAa"));

        // Lower and upper case
        assertEquals(Complexity.LOW, passwordService.getComplexity("$AAAA$AAAA$AAAA$AAAA"));
        assertEquals(Complexity.LOW, passwordService.getComplexity("$aaaa$aaaa$aaaa$aaaa"));
    }

    @Test
    public void validateHighComplexity() throws IncorrectPasswordLengthException {
        // 8+ characters
        assertEquals(Complexity.HIGH, passwordService.getComplexity("$AaAa$AaAa"));
        assertEquals(Complexity.MEDIUM, passwordService.getComplexity("$AaAa$A"));

        // Special case
        assertEquals(Complexity.MEDIUM, passwordService.getComplexity("SAaAaSAaAa"));

        // Lower and upper case
        assertEquals(Complexity.LOW, passwordService.getComplexity("$AAAA$AAAA"));
        assertEquals(Complexity.LOW, passwordService.getComplexity("$aaaa$aaaa"));
    }

    @Test
    public void validateMediumComplexity() throws IncorrectPasswordLengthException {
        // 5+ characters
        assertEquals(Complexity.MEDIUM, passwordService.getComplexity("SAaAaS"));
        assertEquals(Complexity.LOW, passwordService.getComplexity("SAaAa"));

        // Lower and upper case
        assertEquals(Complexity.LOW, passwordService.getComplexity("SAAAAS"));
        assertEquals(Complexity.LOW, passwordService.getComplexity("saaaas"));
    }
}
