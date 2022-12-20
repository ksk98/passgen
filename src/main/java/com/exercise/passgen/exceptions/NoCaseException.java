package com.exercise.passgen.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class NoCaseException extends Exception {
    public NoCaseException(String message) {
        super(message);
    }
}
