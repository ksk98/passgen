package com.exercise.passgen.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public abstract class MD5Digester {
    public static byte[] digest(String text) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        return messageDigest.digest(text.getBytes(StandardCharsets.UTF_8));
    }

    public static byte[] getSearchHash(String text) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        return messageDigest.digest(text.substring(0, text.length()/3).getBytes(StandardCharsets.UTF_8));
    }
}
