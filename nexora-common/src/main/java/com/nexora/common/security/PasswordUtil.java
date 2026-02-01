package com.nexora.common.security;

import java.util.regex.Pattern;

/**
 * Password validation and strength assessment utility.
 *
 * <p>Provides password validation according to common security policies:
 * <ul>
 *   <li>Minimum 8 characters, maximum 128 characters</li>
 *   <li>At least one uppercase letter</li>
 *   <li>At least one lowercase letter</li>
 *   *   <li>At least one digit</li>
 *   *   <li>At least one special character</li>
 * </ul>
 *
 * <p>Usage:
 * <pre>
 * // Validate password (throws exception if invalid)
 * PasswordUtil.validate("MyPassword123!");
 *
 * // Check if password is valid (returns boolean)
 * if (PasswordUtil.isValid("MyPassword123!")) {
 *     // password is valid
 * }
 *
 * // Get password strength (0-4)
 * int strength = PasswordUtil.getStrength("MyPassword123!");
 * </pre>
 *
 * @author sujie
 */
public final class PasswordUtil {

    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 128;
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile("[a-z]");
    private static final Pattern DIGIT_PATTERN = Pattern.compile("[0-9]");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]");

    private PasswordUtil() {
    }

    /**
     * Validates password according to security policy.
     *
     * @param password the password to validate
     * @throws IllegalArgumentException if password is invalid
     */
    public static void validate(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password cannot be blank");
        }
        if (password.length() < MIN_LENGTH) {
            throw new IllegalArgumentException(
                "Password must be at least " + MIN_LENGTH + " characters long");
        }
        if (password.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                "Password must not exceed " + MAX_LENGTH + " characters");
        }
        if (!UPPERCASE_PATTERN.matcher(password).find()) {
            throw new IllegalArgumentException(
                "Password must contain at least one uppercase letter");
        }
        if (!LOWERCASE_PATTERN.matcher(password).find()) {
            throw new IllegalArgumentException(
                "Password must contain at least one lowercase letter");
        }
        if (!DIGIT_PATTERN.matcher(password).find()) {
            throw new IllegalArgumentException(
                "Password must contain at least one digit");
        }
        if (!SPECIAL_CHAR_PATTERN.matcher(password).find()) {
            throw new IllegalArgumentException(
                "Password must contain at least one special character");
        }
    }

    /**
     * Checks if password meets minimum requirements.
     *
     * @param password the password to check
     * @return true if password is valid
     */
    public static boolean isValid(String password) {
        try {
            validate(password);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Gets password strength score (0-4).
     *
     * <p>Strength criteria:
     * <ul>
     *   <li>0: Too short (less than 8 characters)</li>
     *   *   <li>1: Weak (meets minimum requirements)</li>
     *   *   <li>2: Fair (12+ characters, multiple character types)</li>
     *   *   <li>3: Good (all character types present, 14+ characters)</li>
     *   *   <li>4: Strong (all character types present, 16+ characters)</li>
     * </ul>
     *
     * @param password the password to check
     * @return strength score from 0 (weak) to 4 (strong)
     */
    public static int getStrength(String password) {
        if (password == null || password.length() < MIN_LENGTH) {
            return 0;
        }

        int score = 0;
        if (password.length() >= 12) score++;
        if (password.length() >= 14) score++;
        if (password.length() >= 16) score++;

        if (UPPERCASE_PATTERN.matcher(password).find()) score++;
        if (LOWERCASE_PATTERN.matcher(password).find()) score++;
        if (DIGIT_PATTERN.matcher(password).find()) score++;
        if (SPECIAL_CHAR_PATTERN.matcher(password).find()) score++;

        return Math.min(score, 4);
    }

    /**
     * Get strength description.
     *
     * @param strength the strength score (0-4)
     * @return strength description
     */
    public static String getStrengthDescription(int strength) {
        return switch (strength) {
            case 0 -> "TOO_WEAK";
            case 1 -> "WEAK";
            case 2 -> "FAIR";
            case 3 -> "GOOD";
            case 4 -> "STRONG";
            default -> "UNKNOWN";
        };
    }

    /**
     * Generate a random password with specified strength.
     *
     * @param strength desired strength level (1-4)
     * @return generated password
     * @throws IllegalArgumentException if strength is not 1-4
     */
    public static String generatePassword(int strength) {
        if (strength < 1 || strength > 4) {
            throw new IllegalArgumentException("Strength must be between 1 and 4");
        }

        int length = switch (strength) {
            case 1 -> MIN_LENGTH;
            case 2 -> 12;
            case 3 -> 14;
            case 4 -> 16;
            default -> MIN_LENGTH;
        };

        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = upper.toLowerCase();
        String digits = "0123456789";
        String special = "!@#$%^&*()_+-=[]{}|;:,.<>?";

        StringBuilder pool = new StringBuilder();
        pool.append(upper).append(lower).append(digits).append(special);

        java.util.Random random = new java.util.Random();
        StringBuilder password = new StringBuilder(length);

        // Ensure at least one character from each category for strength 2-4
        if (strength >= 1) {
            password.append(lower.charAt(random.nextInt(lower.length())));
        }
        if (strength >= 2) {
            password.append(upper.charAt(random.nextInt(upper.length())));
            password.append(digits.charAt(random.nextInt(digits.length())));
        }
        if (strength >= 3) {
            password.append(special.charAt(random.nextInt(special.length())));
        }
        if (strength >= 4) {
            password.append(special.charAt(random.nextInt(special.length())));
        }

        // Fill the rest with random characters from all pools
        while (password.length() < length) {
            password.append(pool.charAt(random.nextInt(pool.length())));
        }

        // Shuffle the password
        return shuffleString(password.toString());
    }

    /**
     * Generate a random password with good strength (level 3).
     *
     * @return generated password
     */
    public static String generatePassword() {
        return generatePassword(3);
    }

    /**
     * Shuffle string characters randomly.
     */
    private static String shuffleString(String input) {
        char[] characters = input.toCharArray();
        java.util.Random random = new java.util.Random();

        for (int i = characters.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = characters[i];
            characters[i] = characters[j];
            characters[j] = temp;
        }

        return new String(characters);
    }

    /**
     * Password strength levels.
     */
    public enum Strength {
        TOO_WEAK(0, "Too Weak"),
        WEAK(1, "Weak"),
        FAIR(2, "Fair"),
        GOOD(3, "Good"),
        STRONG(4, "Strong");

        private final int level;
        private final String description;

        Strength(int level, String description) {
            this.level = level;
            this.description = description;
        }

        public int getLevel() {
            return level;
        }

        public String getDescription() {
            return description;
        }
    }
}
