package com.exercise.passgen.services;

import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class PasswordRegexService {
    private final Pattern patternLowerCase = Pattern.compile("^[a-z]+$");

    public boolean hasLowerCase(String password) {

    }

    public boolean hasUpperCase(String password) {

    }

    public boolean hasSpecialCase(String password) {

    }
}
