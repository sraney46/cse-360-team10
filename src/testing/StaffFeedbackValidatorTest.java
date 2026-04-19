package testing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import entityClasses.StaffFeedbackValidator;

/**
 * <p>Class: StaffFeedbackValidatorTest</p>
 *
 * <p>Description: Unit tests for the StaffFeedbackValidator class. Tests cover
 * all positive and negative cases for validateEmailMessage() and
 * containsInappropriateContent() to ensure the Staff Review and Feedback Hub
 * validation logic behaves correctly under all expected conditions.</p>
 */
public class StaffFeedbackValidatorTest {

    // Shared validator instance used across all tests
    private StaffFeedbackValidator validator;

    /**
     * <p>Method: setUp()</p>
     *
     * <p>Description: Initializes a fresh StaffFeedbackValidator before each test
     * to ensure test isolation.</p>
     */
    @BeforeEach
    public void setUp() {
        validator = new StaffFeedbackValidator();
    }

    // -----------------------------------------------------------------------
    // validateEmailMessage() tests
    // -----------------------------------------------------------------------

    /**
     * Null input should return a validation error.
     * A null message must never be passed to the email system.
     */
    @Test
    public void testValidateEmailMessage_Null() {
        assertEquals("Email body cannot be null.", validator.validateEmailMessage(null));
    }

    /**
     * Empty string input should return a validation error.
     * An empty message provides no feedback value.
     */
    @Test
    public void testValidateEmailMessage_Empty() {
        assertEquals("Email body cannot be empty.", validator.validateEmailMessage(""));
    }

    /**
     * Whitespace-only input should return a validation error.
     * A message with only spaces or tabs is not meaningful feedback.
     */
    @Test
    public void testValidateEmailMessage_WhitespaceOnly() {
        assertEquals("Email body cannot be whitespace only.", validator.validateEmailMessage("   "));
    }

    /**
     * A valid message should pass validation and return an empty string.
     * This is the happy path — a properly formed message should be allowed through.
     */
    @Test
    public void testValidateEmailMessage_Valid() {
        assertEquals("", validator.validateEmailMessage("Hi user6, please keep your posts focused on course content."));
    }

    // -----------------------------------------------------------------------
    // containsInappropriateContent() tests
    // -----------------------------------------------------------------------

    /**
     * Clean text should not be flagged.
     * Normal discussion content should pass through without issue.
     */
    @Test
    public void testContainsInappropriateContent_CleanText() {
        assertFalse(validator.containsInappropriateContent("This is a normal discussion post."));
    }

    /**
     * Text containing an inappropriate word should be flagged.
     * Uses an actual word from INAPPROPRIATE_WORDS to ensure the check fires correctly.
     */
    @Test
    public void testContainsInappropriateContent_InappropriateWord() {
        assertTrue(validator.containsInappropriateContent("This post is complete crap and should be removed."));
    }

    /**
     * Empty string should not be flagged.
     * There is no content to evaluate so it should return false.
     */
    @Test
    public void testContainsInappropriateContent_EmptyString() {
        assertFalse(validator.containsInappropriateContent(""));
    }

    /**
     * Null input should not be flagged.
     * The method must handle null gracefully without throwing an exception.
     */
    @Test
    public void testContainsInappropriateContent_Null() {
        assertFalse(validator.containsInappropriateContent(null));
    }

    /**
     * Mixed case inappropriate word should still be flagged.
     * The check must be case-insensitive to catch all capitalization variants.
     */
    @Test
    public void testContainsInappropriateContent_MixedCase() {
        assertTrue(validator.containsInappropriateContent("This is completely STUPID behavior."));
    }

    /**
     * A word that merely contains an inappropriate substring should NOT be flagged.
     * Whole-word matching via \b must prevent false positives on partial matches.
     */
    @Test
    public void testContainsInappropriateContent_SubstringNotFlagged() {
        assertFalse(validator.containsInappropriateContent("I will assess the situation carefully."));
    }
}