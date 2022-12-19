package com.exercise.passgen.services;

import com.exercise.passgen.enums.Complexity;
import com.exercise.passgen.exceptions.IncorrectPasswordLengthException;
import org.springframework.stereotype.Service;

@Service
public class PasswordService {
    public Complexity getComplexity(String password) throws IncorrectPasswordLengthException {
        int length = password.length();

        if (length < 3 || length > 32)
            throw new IncorrectPasswordLengthException();

        boolean hasLowerCase = false,
                hasUpperCase = false,
                hasSpecialCase = false;

        for (char c: password.toCharArray()) {
            if (Character.isLetter(c)) {
                if (Character.isLowerCase(c)) hasLowerCase = true;
                else if (Character.isUpperCase(c)) hasUpperCase = true;
            } else if (!Character.isDigit(c) && !Character.isSpaceChar(c)) hasSpecialCase = true;
        }

        if (hasLowerCase && hasUpperCase) {
            if (hasSpecialCase) {
                if (length > 16) return Complexity.ULTRA;
                if (length > 8) return Complexity.HIGH;
            }

            if (length > 5) return Complexity.MEDIUM;
        }

        return Complexity.LOW;
    }
}
