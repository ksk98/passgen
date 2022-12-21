package com.exercise.passgen.security;

import com.exercise.passgen.exceptions.SearchHashGenerationFailureException;

public interface SearchHashGenerator {
    byte[] generateSearchHash(String text) throws SearchHashGenerationFailureException;
}
