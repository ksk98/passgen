package com.exercise.passgen.services;

import com.exercise.passgen.enums.Complexity;
import com.exercise.passgen.exceptions.IncorrectPasswordLengthException;
import com.exercise.passgen.exceptions.NoCaseException;
import com.exercise.passgen.exceptions.TooManyPasswordsAtOnceException;
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

    private static final int minCharacters = 3, maxCharacters = 32, maxPasswordsAtOnce = 1000;

    /**
     * Returns complexity of a given password.<br>
     * Categories go as follows:<br>
     * <ul>
     *     <li>LOW - up to 5 characters, no special case, only lower or only upper case</li>
     *     <li>MEDIUM - at least 5 characters, no special case, lower or upper case</li>
     *     <li>HIGH - at least 8 characters, at least 1 special case, both lower and upper case</li>
     *     <li>ULTRA - at least 16 characters, at least 1 special case, both lower and upper case</li>
     * </ul>
     * @param password String value of a given password
     * @return Complexity value
     * @throws IncorrectPasswordLengthException if length was less than {@value PasswordService#minCharacters}
     * and more than {@value PasswordService#maxCharacters}
     */
    public Complexity getComplexity(String password) throws IncorrectPasswordLengthException {
        int length = password.length();
        checkLengthBetweenMinMax(length);

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

            // From my understanding medium complexity passwords do not contain ONLY lower or ONLY upper case
            // So this still lands inside this IF statement
            if (length > 5) return Complexity.MEDIUM;
        }

        return Complexity.LOW;
    }

    /**
     * Generate a batch of passwords in form of a list.
     * @param length length of generated passwords, (between {@value PasswordService#minCharacters} and {@value PasswordService#maxCharacters})
     * @param lowerCase if true, passwords will contain lowercase letters
     * @param upperCase if true, passwords will contain uppercase letters
     * @param specialCase if true, passwords will contain special characters
     * @param amount amount of generated passwords (max {@value PasswordService#maxPasswordsAtOnce})
     * @return list of generated passwords
     * @throws IncorrectPasswordLengthException when length is not between {@value PasswordService#minCharacters} and {@value PasswordService#maxCharacters}
     * @throws NoCaseException when all case flags are false
     * @throws TooManyPasswordsAtOnceException when amount exceeds {@value PasswordService#maxPasswordsAtOnce}
     */
    private List<String> generatePasswords(int length, boolean lowerCase, boolean upperCase, boolean specialCase, int amount)
            throws IncorrectPasswordLengthException, NoCaseException, TooManyPasswordsAtOnceException {
        checkLengthBetweenMinMax(length);

        if (!lowerCase && !upperCase && !specialCase)
            throw new NoCaseException();

        if (amount > maxPasswordsAtOnce)
            throw new TooManyPasswordsAtOnceException();

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

    private void checkLengthBetweenMinMax(int length) throws IncorrectPasswordLengthException {
        if (length < minCharacters || length > maxCharacters)
            throw new IncorrectPasswordLengthException();
    }
}
