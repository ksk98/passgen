package com.exercise.passgen.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class SearchHashGenerationFailureException extends Exception {
    public SearchHashGenerationFailureException(String message) {
        super(message);
    }
}
