package org.kailas.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordChecker {

    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MIN_NUMERIC_COUNT = 1;
    private static final int MIN_SYMBOL_COUNT = 1;
    private static final int MIN_UPPERCASE_COUNT = 1;
    private static final int MIN_LOWERCASE_COUNT = 1;
    private static final Pattern NUMERIC_PATTERN = Pattern.compile("[0-9]");
    static String symbols = "[!@#\\$%\\^&\\*\\(\\)_\\+\\-=\\[\\]\\{\\}\\|,\\.<>\\/\\?\"';:]+";
    private static final Pattern SYMBOL_PATTERN= Pattern.compile(symbols);


    private static final Pattern UPPERCASE_PATTERN = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile("[a-z]");

    public static boolean isValid(String password) {
        if (password.length() < MIN_PASSWORD_LENGTH) {
            return false;
        }

        int numericCount = 0;
        int symbolCount = 0;
        int uppercaseCount = 0;
        int lowercaseCount = 0;

        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);
            Matcher numericMatcher = NUMERIC_PATTERN.matcher(String.valueOf(c));
            Matcher symbolMatcher = SYMBOL_PATTERN.matcher(String.valueOf(c));
            Matcher uppercaseMatcher = UPPERCASE_PATTERN.matcher(String.valueOf(c));
            Matcher lowercaseMatcher = LOWERCASE_PATTERN.matcher(String.valueOf(c));

            if (numericMatcher.matches()) {
                numericCount++;
            }  if (symbolMatcher.matches()) {
                symbolCount++;
            }  if (uppercaseMatcher.matches()) {
                uppercaseCount++;
            }  if (lowercaseMatcher.matches()) {
                lowercaseCount++;
            }
        }

        return numericCount >= MIN_NUMERIC_COUNT
                && symbolCount >= MIN_SYMBOL_COUNT
                && uppercaseCount >= MIN_UPPERCASE_COUNT
                && lowercaseCount >= MIN_LOWERCASE_COUNT;
    }
}
