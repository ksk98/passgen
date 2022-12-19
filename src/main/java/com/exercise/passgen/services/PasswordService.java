package com.exercise.passgen.services;

import com.exercise.passgen.enums.Complexity;
import com.exercise.passgen.exceptions.IncorrectPasswordLengthException;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class PasswordService {
    private static final char[] LOWER = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    private static final char[] UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private static final char[] SPECIAL = "!@#$%&*()_+-=[]|,./?><".toCharArray();

    private static final int minCharacters = 3, maxCharacters = 32;

    public Complexity getComplexity(String password) throws IncorrectPasswordLengthException {
        int length = password.length();
        checkLength(length);

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

    private List<String> generatePasswords(int length, boolean lowerCase, boolean upperCase, boolean specialCase, int amount)
            throws IncorrectPasswordLengthException {
        checkLength(length);
        List<String> out = new LinkedList<>();

        StringBuilder stringBuilder = new StringBuilder();
        if (lowerCase) stringBuilder.append(LOWER);
        if (upperCase) stringBuilder.append(UPPER);
        if (specialCase) stringBuilder.append(SPECIAL);
        char[] characters = stringBuilder.toString().toCharArray();

        // We will have to ensure that at least 1 character of every specified case will be present
        // A "sure case" is a character from one of those cases that will be randomly inserted at the start of the process
        // After all sure cases are inserted, the rest of the password is built randomly
        for (int i = 0; i < amount; i++) {
            Random random = new SecureRandom();
            int sureCaseInsertIndex = 0;

            // Prepare shuffled list of password character indexes
            List<Integer> indexes = IntStream.range(0, length).boxed().collect(Collectors.toList());
            Collections.shuffle(indexes, random);

            char[] password = new char[length];

            // Insert sure cases
            if (lowerCase) {
                password[indexes.get(sureCaseInsertIndex)] = LOWER[random.nextInt(LOWER.length)];
                sureCaseInsertIndex += 1;
            }
            if (upperCase) {
                password[indexes.get(sureCaseInsertIndex)] = UPPER[random.nextInt(UPPER.length)];
                sureCaseInsertIndex += 1;
            }
            if (specialCase) {
                password[indexes.get(sureCaseInsertIndex)] = SPECIAL[random.nextInt(SPECIAL.length)];
                sureCaseInsertIndex += 1;
            }

            // Randomize the rest
            for (int j = sureCaseInsertIndex; j < length; j++)
                password[j] = characters[random.nextInt(characters.length)];

            out.add(new String(password));
        }

        return out;
    }

    private void checkLength(int length) throws IncorrectPasswordLengthException {
        if (length < minCharacters || length > maxCharacters)
            throw new IncorrectPasswordLengthException();
    }
}
