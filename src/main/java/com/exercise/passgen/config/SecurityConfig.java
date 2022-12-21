package com.exercise.passgen.config;

import com.exercise.passgen.security.SearchHashGenerator;
import com.exercise.passgen.security.SimpleMD5SearchHashGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {
    private final PasswordEncoder passwordEncoder;
    private final SearchHashGenerator searchHashGenerator;

    public SecurityConfig() {
        this.passwordEncoder = Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
        this.searchHashGenerator = new SimpleMD5SearchHashGenerator();
    }

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }

    @Bean
    public SearchHashGenerator getSearchHashGenerator() {
        return searchHashGenerator;
    }
}
