package com.exercise.passgen.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class UndeterminablePasswordComplexityException extends Exception {
    public UndeterminablePasswordComplexityException(String message) {
        super(message);
    }
}
