package com.exercise.passgen.PasswordService;

import com.exercise.passgen.repositories.PasswordRepository;
import com.exercise.passgen.services.PasswordService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PasswordPersistenceTests {
    @Mock
    PasswordRepository passwordRepository;

    @InjectMocks
    PasswordService passwordService;

    @Test
    public void passwordPersistence() {

    }
}
