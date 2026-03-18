package validation;

import java.util.Set;

/*******
 * <p> Title: PasswordValidator Class. </p>
 * 
 * <p> Description: The password validation class used for validating password requirements
 * when created, and/or entered.</p>
 * 
 * <p> Copyright: James Waldrop @ 2026 </p>
 * 
 * @author James Waldrop
 * 
 * @version 1.00		2026-02-18 Initial version
 *  
 */
public class PasswordValidator {

	/**********
     * <p>Constructor: PasswordValidator()</p>
     *
     * <p>Description: In the hopes of shutting up java doc errors,
     * this constructor shall exist!</p>
     */
	public PasswordValidator() {}
	
    // ---- Configuration ----
    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 64;

    private static final String SPECIAL_CHARS = "!@#$%^&*()-_=+[]{}|;:'\",.<>?/";

    // Commonly used and/or weak passwords to explicitly reject
    private static final Set<String> BLACKLIST = Set.of(
        "password",
        "qwerty",
        "admin"
    );

    /**
     * Validate a password using an FSM.
     *
     * @param password the password to validate
     * @return ValidationResult indicating pass/fail and reason
     */
    public ValidationResult validate(String password) {

        // ---- Basic checks ----
        if (password == null || password.isEmpty()) {
            return new ValidationResult(false, "Password is required.");
        }

        if (password.length() < MIN_LENGTH) {
            return new ValidationResult(false, "Password is too short.");
        }

        if (password.length() > MAX_LENGTH) {
            return new ValidationResult(false, "Password is too long.");
        }

        // ---- Blacklist check (case-insensitive) ----
        if (BLACKLIST.contains(password.toLowerCase())) {
            return new ValidationResult(false, "Password is too common.");
        }

        // ---- FSM flags ----
        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;

        // ---- FSM character processing ----
        for (char c : password.toCharArray()) {

            if (Character.isUpperCase(c)) {
                hasUpper = true;
            } 
            else if (Character.isLowerCase(c)) {
                hasLower = true;
            } 
            else if (Character.isDigit(c)) {
                hasDigit = true;
            } 
            else if (SPECIAL_CHARS.indexOf(c) >= 0) {
                hasSpecial = true;
            } 
            else {
                // Reject unexpected or unsafe characters
                return new ValidationResult(false, "Password contains invalid characters.");
            }
        }

        // ---- Final FSM acceptance ----
        if (!hasUpper) {
            return new ValidationResult(false, "Password must contain an uppercase letter.");
        }

        if (!hasLower) {
            return new ValidationResult(false, "Password must contain a lowercase letter.");
        }

        if (!hasDigit) {
            return new ValidationResult(false, "Password must contain a digit.");
        }

        if (!hasSpecial) {
            return new ValidationResult(false, "Password must contain a special character.");
        }

        return new ValidationResult(true, "Password accepted.");
    }
}
