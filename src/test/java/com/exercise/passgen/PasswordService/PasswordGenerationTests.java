package com.exercise.passgen.PasswordService;

import com.exercise.passgen.enums.Complexity;
import com.exercise.passgen.exceptions.IncorrectPasswordLengthException;
import com.exercise.passgen.exceptions.NoCaseException;
import com.exercise.passgen.exceptions.TooManyPasswordsAtOnceException;
import com.exercise.passgen.models.schemas.PasswordDTO;
import com.exercise.passgen.services.PasswordService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@RunWith(MockitoJUnitRunner.class)
public class PasswordGenerationTests {
    @InjectMocks
    PasswordService passwordService;

    @Test
    public void generationExceptions() {
        assertThrows(
                IncorrectPasswordLengthException.class,
                () -> passwordService.generatePasswords(
                        PasswordService.MIN_CHARACTERS-1, true, false, false, 1)
        );

        assertThrows(
                IncorrectPasswordLengthException.class,
                () -> passwordService.generatePasswords(
                        PasswordService.MAX_CHARACTERS+1, true, false, false, 1)
        );

        assertThrows(
                NoCaseException.class,
                () -> passwordService.generatePasswords(
                        PasswordService.MIN_CHARACTERS, false, false, false, 1)
        );

        assertThrows(
                TooManyPasswordsAtOnceException.class,
                () -> passwordService.generatePasswords(
                        PasswordService.MIN_CHARACTERS, true, false, false, PasswordService.MAX_PASSWORDS_AT_ONCE+1)
        );
    }

    @Test
    public void generateAndValidateUltraComplexity() throws IncorrectPasswordLengthException, NoCaseException, TooManyPasswordsAtOnceException {
        // 16+ characters
        generateAndAssert(Complexity.ULTRA, 17, true, true, true);
        generateAndAssert(Complexity.HIGH, 16, true, true, true);

        // Special case
        generateAndAssert(Complexity.MEDIUM, 17, true, true, false);

        // Lower and upper case
        generateAndAssert(Complexity.LOW, 17, false, true, false);
        generateAndAssert(Complexity.LOW, 17, true, false, false);
    }

    @Test
    public void generateAndValidateHighComplexity() throws IncorrectPasswordLengthException, NoCaseException, TooManyPasswordsAtOnceException {
        // 8+ characters
        generateAndAssert(Complexity.HIGH, 9, true, true, true);
        generateAndAssert(Complexity.MEDIUM, 8, true, true, true);

        // Special case
        generateAndAssert(Complexity.MEDIUM, 9, true, true, false);

        // Lower and upper case
        generateAndAssert(Complexity.LOW, 9, false, true, true);
        generateAndAssert(Complexity.LOW, 9, true, false, true);
    }

    @Test
    public void generateAndValidateMediumComplexity() throws IncorrectPasswordLengthException, NoCaseException, TooManyPasswordsAtOnceException {
        // 5+ characters
        generateAndAssert(Complexity.MEDIUM, 6, true, true, false);
        generateAndAssert(Complexity.LOW, 5, true, true, false);

        // Special case doesn't change anything for this length
        generateAndAssert(Complexity.MEDIUM, 6, true, true, true);

        // Lower and upper case
        generateAndAssert(Complexity.LOW, 6, false, true, false);
        generateAndAssert(Complexity.LOW, 6, true, false, false);
    }

    @Test
    public void generateAndValidateLowComplexity() throws IncorrectPasswordLengthException, NoCaseException, TooManyPasswordsAtOnceException {
        // Up to 5 characters, but more won't change anything for only lower case or only upper case
        generateAndAssert(Complexity.LOW, PasswordService.MIN_CHARACTERS, true, false, false);
        generateAndAssert(Complexity.LOW, 5, true, false, false);

        // Special case doesn't change anything for this length and character case
        generateAndAssert(Complexity.LOW, PasswordService.MIN_CHARACTERS, true, false, true);
        generateAndAssert(Complexity.LOW, 5, true, false, true);
    }

    private void generateAndAssert(Complexity expected, int length, boolean lowerCase, boolean upperCase, boolean specialCase)
            throws IncorrectPasswordLengthException, NoCaseException, TooManyPasswordsAtOnceException {
        List<PasswordDTO> passwords = passwordService.generatePasswords(length, lowerCase, upperCase, specialCase, 1);

        for (PasswordDTO password: passwords)
            assertEquals(expected, passwordService.getComplexity(password.getPassword()));
    }
}
