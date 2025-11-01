package com.sprawler.common.validators.nric;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NricValidator  implements ConstraintValidator<ValidNric, String> {
    @Override
    public boolean isValid(String nric, ConstraintValidatorContext context) {
        if (nric == null || !nric.matches("^[STFG]\\d{7}[A-Z]$")) {
            return false;
        }
        char[] icArray = nric.toUpperCase().toCharArray();
        int[] weights = {2, 7, 6, 5, 4, 3, 2};
        int offset = (icArray[0] == 'T' || icArray[0] == 'G') ? 4 : 0;
        int sum = 0;

        for (int i = 1; i <= 7; i++) {
            sum += (icArray[i] - '0') * weights[i - 1];
        }
        sum += offset;
        int remainder = sum % 11;

        char[] st = {'J', 'Z', 'I', 'H', 'G', 'F', 'E', 'D', 'C', 'B', 'A'};
        char[] fg = {'X', 'W', 'U', 'T', 'R', 'Q', 'P', 'N', 'M', 'L', 'K'};

        char expectedLastChar = (icArray[0] == 'S' || icArray[0] == 'T')
                ? st[remainder]
                : fg[remainder];

        return icArray[8] == expectedLastChar;
    }
}
