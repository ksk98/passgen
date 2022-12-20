package com.exercise.passgen.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class TooManyPasswordsAtOnceException extends Exception {
    public TooManyPasswordsAtOnceException(String message) {
        super(message);
    }
}
