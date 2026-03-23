package validation;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


class PasswordValidatorTest {

    @Test
    void commonPasswordRejected() {
        PasswordValidator v = new PasswordValidator();
        assertFalse(v.validate("password").isValid());
    }

    @Test
    void strongPasswordAccepted() {
        PasswordValidator v = new PasswordValidator();
        assertTrue(v.validate("Str0ng!Password").isValid());
    }
}