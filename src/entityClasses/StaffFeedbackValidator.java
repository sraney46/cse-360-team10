package entityClasses;

/**
 * <p>Class: StaffFeedbackValidator</p>
 *
 * <p>Description: Provides core validation methods for the Staff Review and Feedback Hub.
 * These methods are used to validate private messages sent by staff members and to
 * detect inappropriate content in posts, replies, and messages before they are
 * processed or delivered.</p>
 */
public class StaffFeedbackValidator {

    // Hardcoded list of inappropriate words to flag in content.
    // In a production system this list would be loaded from a configurable source.
	private static final String[] INAPPROPRIATE_WORDS = {
		"ass", "asshole", "bastard", "bitch", 
		"bullshit", "crap", "damn", "dick", "douche", "freaking", 
		"fuck", "fucked", "fucker", "fucking", "hell", "idiot", "jackass",
		"jerk", "loser", "moron", "piss", "prick", "pussy", "retard", "shit", 
		"shitty", "slut", "stupid", "suck", "twat", "wanker", "whore"
	};

    /**
     * <p>Method: validateEmailMessage(String message)</p>
     *
     * <p>Description: Validates the content of a private email message before it is sent
     * by a staff member. Ensures the message is not null, not empty, and not
     * whitespace-only. Returns an empty string if the message is valid.</p>
     *
     * @param message the message content entered by the staff member
     * @return a String describing the validation error, or empty string if valid
     */
    public String validateEmailMessage(String message) {
        // Null check — prevents null pointer exceptions downstream
        if (message == null) return "Email body cannot be null.";

        // Empty check — a message with no content has no feedback value
        if (message.isEmpty()) return "Email body cannot be empty.";

        // Whitespace check — whitespace-only messages are not meaningful feedback
        if (message.trim().isEmpty()) return "Email body cannot be whitespace only.";

        // Message passed all checks
        return "";
    }

    /**
     * <p>Method: containsInappropriateContent(String text)</p>
     *
     * <p>Description: Checks whether the provided text contains any inappropriate words
     * from the hardcoded list. The check is case-insensitive to catch variations
     * in capitalization. Returns true if inappropriate content is detected.</p>
     *
     * @param text the text to be checked for inappropriate content
     * @return true if inappropriate content is found, false otherwise
     */
    public boolean containsInappropriateContent(String text) {
        if (text == null || text.isEmpty()) return false;

        String lowerText = text.toLowerCase();

        for (String word : INAPPROPRIATE_WORDS) {
            // \b ensures we match whole words only, not substrings
            String pattern = "\\b" + word.toLowerCase() + "\\b";
            if (lowerText.matches(".*" + pattern + ".*")) {
                return true;
            }
        }

        return false;
    }
}
