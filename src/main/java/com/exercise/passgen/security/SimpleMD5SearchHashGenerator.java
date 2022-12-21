package com.exercise.passgen.security;

import com.exercise.passgen.exceptions.SearchHashGenerationFailureException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SimpleMD5SearchHashGenerator implements SearchHashGenerator {
    @Override
    public byte[] generateSearchHash(String text) throws SearchHashGenerationFailureException {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            return messageDigest.digest(text.substring(0, text.length()/3).getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new SearchHashGenerationFailureException(e.getMessage());
        }
    }
}
