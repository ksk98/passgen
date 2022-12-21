package com.exercise.passgen.Services;

import com.exercise.passgen.enums.Complexity;
import com.exercise.passgen.exceptions.SearchHashGenerationFailureException;
import com.exercise.passgen.models.schemas.PasswordDTO;
import com.exercise.passgen.repositories.PasswordRepository;
import com.exercise.passgen.services.PasswordService;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class PasswordPersistenceTests {

    @Autowired
    PasswordRepository passwordRepository;

    @Autowired
    PasswordService passwordService;

    @AfterEach
    public void cleanupDB() {
        passwordRepository.deleteAll();
    }

    @Test
    public void SuccessfulPasswordBatchPersistence() throws SearchHashGenerationFailureException {
        // First, create and persist the password batch
        List<PasswordDTO> passwords = new ArrayList<>(3);

        passwords.add(PasswordDTO.builder()
                .password("example")
                .complexity(Complexity.LOW)
                .generationDateTime(LocalDateTime.of(2020, 4, 15, 5, 5))
                .build());
        passwords.add(PasswordDTO.builder()
                .password("example2")
                .complexity(Complexity.MEDIUM)
                .generationDateTime(LocalDateTime.of(2021, 4, 15, 5, 5))
                .build());
        passwords.add(PasswordDTO.builder()
                .password("example3")
                .complexity(Complexity.HIGH)
                .generationDateTime(LocalDateTime.of(2022, 4, 15, 5, 5))
                .build());

        passwordService.persistUniquePasswords(passwords);

        // Check that every password was persisted and assert values in the entity
        for (PasswordDTO passwordDTO: passwords) {
            PasswordDTO fromService = passwordService.getPasswordDTO(passwordDTO.getPassword());

            assertNotNull(fromService);
            assertEquals(passwordDTO.getPassword(), fromService.getPassword());
            assertEquals(passwordDTO.getComplexity(), fromService.getComplexity());
            assertEquals(passwordDTO.getGenerationDateTime(), fromService.getGenerationDateTime());
        }

        // Perform deletion of passwords and check if they were deleted and returned properly
        for (PasswordDTO passwordDTO: passwords) {
            PasswordDTO fromService = passwordService.deletePassword(passwordDTO.getPassword());

            assertNotNull(fromService);
            assertEquals(passwordDTO.getPassword(), fromService.getPassword());
            assertEquals(passwordDTO.getComplexity(), fromService.getComplexity());
            assertEquals(passwordDTO.getGenerationDateTime(), fromService.getGenerationDateTime());

            fromService = passwordService.getPasswordDTO(passwordDTO.getPassword());
            assertNull(fromService);
        }
    }

    @Test
    public void SuccessfulPasswordPersistenceIgnoreDuplicatesInBatch() throws SearchHashGenerationFailureException {
        // First, create and persist the password batch
        List<PasswordDTO> passwords = new ArrayList<>(3);

        passwords.add(PasswordDTO.builder()
                .password("example")
                .complexity(Complexity.LOW)
                .generationDateTime(LocalDateTime.of(2020, 4, 15, 5, 5))
                .build());
        passwords.add(PasswordDTO.builder()
                .password("example2")
                .complexity(Complexity.MEDIUM)
                .generationDateTime(LocalDateTime.of(2021, 4, 15, 5, 5))
                .build());
        passwords.add(PasswordDTO.builder()
                .password("example")
                .complexity(Complexity.HIGH)
                .generationDateTime(LocalDateTime.of(2022, 4, 15, 5, 5))
                .build());

        List<PasswordDTO> duplicates = passwordService.persistUniquePasswords(passwords);
        assertEquals(1, duplicates.size());

        // Assert that duplicates were properly returned and not persisted
        PasswordDTO duplicate = duplicates.get(0);
        assertEquals("example", duplicate.getPassword());
        assertEquals(Complexity.HIGH, duplicate.getComplexity());
        assertEquals(LocalDateTime.of(2022, 4, 15, 5, 5), duplicate.getGenerationDateTime());

        PasswordDTO fromService = passwordService.getPasswordDTO("example");
        assertEquals("example", fromService.getPassword());
        assertEquals(Complexity.LOW, fromService.getComplexity());
        assertEquals(LocalDateTime.of(2020, 4, 15, 5, 5), fromService.getGenerationDateTime());
    }


}
