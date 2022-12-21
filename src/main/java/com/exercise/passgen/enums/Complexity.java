package com.exercise.passgen.enums;

import com.exercise.passgen.PasswordRules;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Complexity {
    ULTRA(17, true, true),
    HIGH(9, true, true),
    MEDIUM(6, true, false),
    LOW(PasswordRules.MIN_CHARACTERS, false, false),
    ;

    public final int MINIMUM_CHARACTERS;
    public final boolean REQUIRES_LOWER_AND_UPPER, REQUIRES_SPECIAL;

    public boolean matchesCriteria(int length, boolean lowerCase, boolean upperCase, boolean specialCase) {
        return length >= MINIMUM_CHARACTERS &&
                fulfillsLowerUpper(lowerCase, upperCase) &&
                fulfillsSpecial(specialCase);
    }

    private boolean fulfillsLowerUpper(boolean lowerCase, boolean upperCase) {
        return !REQUIRES_LOWER_AND_UPPER || (lowerCase && upperCase);
    }

    private boolean fulfillsSpecial(boolean specialCase) {
        return !REQUIRES_SPECIAL || specialCase;
    }
}
