package com.exercise.passgen.services;

import com.exercise.passgen.enums.Complexity;
import com.exercise.passgen.exceptions.IncorrectPasswordLengthException;
import com.exercise.passgen.exceptions.NoCaseException;
import com.exercise.passgen.exceptions.TooManyPasswordsAtOnceException;
import com.exercise.passgen.models.schemas.PasswordDTO;
import com.exercise.passgen.models.entities.PasswordEntity;
import com.exercise.passgen.models.schemas.PasswordGenerationRequestDTO;
import com.exercise.passgen.repositories.PasswordRepository;
import com.exercise.passgen.util.MD5Digester;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class PasswordService {
    private static final char[] LOWER = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    private static final char[] UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private static final char[] SPECIAL = "!@#$%&*()_+-=[]|,./?><".toCharArray();

    public static final int MIN_CHARACTERS = 3, MAX_CHARACTERS = 32, MAX_PASSWORDS_AT_ONCE = 1000;

    private final PasswordRepository passwordRepository;
    private final PasswordEncoder passwordEncoder;

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
     * @throws IncorrectPasswordLengthException if length was less than {@value PasswordService#MIN_CHARACTERS}
     * and more than {@value PasswordService#MAX_CHARACTERS}
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
     * Wrapper method for {@link PasswordService#generatePasswords(int, boolean, boolean, boolean, int)}.<br>
     * <b>THIS METHOD DOES NOT PERSIST THE GENERATED PASSWORDS!</b>
     * @param request request containing all arguments for {@link PasswordService#generatePasswords(int, boolean, boolean, boolean, int)}
     * @return list of generated password DTO's
     * @throws IncorrectPasswordLengthException when length is not between {@value PasswordService#MIN_CHARACTERS} and {@value PasswordService#MAX_CHARACTERS}
     * @throws NoCaseException when all case flags are false
     * @throws TooManyPasswordsAtOnceException when amount exceeds {@value PasswordService#MAX_PASSWORDS_AT_ONCE}
     */
    public List<PasswordDTO> generatePasswords(PasswordGenerationRequestDTO request)
            throws IncorrectPasswordLengthException, NoCaseException, TooManyPasswordsAtOnceException {
        return generatePasswords(request.getLength(), request.isLowerCase(), request.isUpperCase(), request.isUpperCase(), request.getAmount());
    }

    /**
     * Generates a batch of password DTO's packed in a list.<br><b>THIS METHOD DOES NOT PERSIST THE GENERATED PASSWORDS!</b>
     * @param length length of generated passwords, (between {@value PasswordService#MIN_CHARACTERS} and {@value PasswordService#MAX_CHARACTERS})
     * @param lowerCase if true, passwords will contain lowercase letters
     * @param upperCase if true, passwords will contain uppercase letters
     * @param specialCase if true, passwords will contain special characters
     * @param amount amount of generated passwords (max {@value PasswordService#MAX_PASSWORDS_AT_ONCE})
     * @return list of generated password DTO's
     * @throws IncorrectPasswordLengthException when length is not between {@value PasswordService#MIN_CHARACTERS} and {@value PasswordService#MAX_CHARACTERS}
     * @throws NoCaseException when all case flags are false
     * @throws TooManyPasswordsAtOnceException when amount exceeds {@value PasswordService#MAX_PASSWORDS_AT_ONCE}
     */
    public List<PasswordDTO> generatePasswords(int length, boolean lowerCase, boolean upperCase, boolean specialCase, int amount)
            throws IncorrectPasswordLengthException, NoCaseException, TooManyPasswordsAtOnceException {
        checkLengthBetweenMinMax(length);

        if (!lowerCase && !upperCase && !specialCase)
            throw new NoCaseException("At least one case must be selected.");

        if (amount > MAX_PASSWORDS_AT_ONCE)
            throw new TooManyPasswordsAtOnceException("Cannot request more than " + MAX_PASSWORDS_AT_ONCE + " to be generated at once.");

        List<PasswordDTO> out = new ArrayList<>(amount);

        StringBuilder stringBuilder = new StringBuilder();
        if (lowerCase) stringBuilder.append(LOWER);
        if (upperCase) stringBuilder.append(UPPER);
        if (specialCase) stringBuilder.append(SPECIAL);
        char[] characters = stringBuilder.toString().toCharArray();

        // This will be the same for the whole batch and will be computed along the first generated password
        Complexity complexity = null;

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
                password[indexes.get(j)] = characters[random.nextInt(characters.length)];

            if (complexity == null)
                complexity = getComplexity(new String(password));

            out.add(PasswordDTO.builder()
                    .password(new String(password))
                    .complexity(complexity)
                    .generationDateTime(LocalDateTime.now())
                    .build());
        }

        return out;
    }

    /**
     * Looks for an entity that contains a password hash matching the given password and returns a DTO version of found
     * entity.
     * @param password unhashed password string
     * @return DTO version of entity associated with a given password or null
     * @throws NoSuchAlgorithmException when MD5 used to compute searchHash is unavailable
     */
    public PasswordDTO getPasswordDTO(String password) throws NoSuchAlgorithmException {
        List<PasswordEntity> possibleMatches = passwordRepository.findAllBySearchHash(MD5Digester.getSearchHash(password));

        for (PasswordEntity entity: possibleMatches) {
            if (passwordEncoder.matches(password, entity.getPasswordHash())) {
                return PasswordDTO.builder()
                        .password(password)
                        .complexity(entity.getComplexity())
                        .generationDateTime(entity.getGenerationDateTime())
                        .build();
            }
        }

        return null;
    }

    /**
     * Deletes an entity associated with a given password and returns its DTO version.
     * @param password unhashed password string
     * @return DTO version of entity associated with a given password or null
     * @throws NoSuchAlgorithmException when MD5 used to compute searchHash is unavailable
     */
    public PasswordDTO deletePassword(String password) throws NoSuchAlgorithmException {
        List<PasswordEntity> possibleMatches = passwordRepository.findAllBySearchHash(MD5Digester.getSearchHash(password));

        for (PasswordEntity entity: possibleMatches) {
            if (passwordEncoder.matches(password, entity.getPasswordHash())) {
                passwordRepository.delete(entity);
                return PasswordDTO.builder()
                        .password(password)
                        .complexity(entity.getComplexity())
                        .generationDateTime(entity.getGenerationDateTime())
                        .build();
            }
        }

        return null;
    }

    /**
     * Persists a given iterable of password DTO's.
     * @return list of duplicates that were not re-added
     */
    public List<PasswordDTO> persistUniquePasswords(List<PasswordDTO> passwords) throws NoSuchAlgorithmException {
        List<PasswordDTO> out = new LinkedList<>();
        List<PasswordEntity> in = new LinkedList<>();

        for (PasswordDTO password: passwords) {
            String passwordHash = passwordEncoder.encode(password.getPassword());
            if (passwordRepository.existsByPasswordHash(passwordHash)) {
                out.add(password);
            } else {
                in.add(PasswordEntity.builder()
                        .complexity(password.getComplexity())
                        .passwordHash(passwordHash)
                        .searchHash(MD5Digester.getSearchHash(password.getPassword()))
                        .generationDateTime(password.getGenerationDateTime())
                        .build());
            }
        }

        passwordRepository.saveAll(in);
        return out;
    }

    /**
     * Checks if a given length is between {@value PasswordService#MIN_CHARACTERS} and {@value PasswordService#MAX_CHARACTERS}.
     * @param length length of a password
     */
    private void checkLengthBetweenMinMax(int length) throws IncorrectPasswordLengthException {
        if (length < MIN_CHARACTERS || length > MAX_CHARACTERS)
            throw new IncorrectPasswordLengthException("Password length must be between " + MIN_CHARACTERS + " and " + MAX_CHARACTERS + ".");
    }
}
