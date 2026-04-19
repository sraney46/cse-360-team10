package testing;
/**
 * Represents one reply relationship in the discussion system prototype.
 * <p>
 * A record captures the replying student and the student whose post or reply
 * was answered. This simple class is intentionally small so the participation
 * analysis logic can be tested independently from the JavaFX UI and database.
 */
public class ReplyRecord {
    private final String replierUsername;
    private final String targetUsername;

    /**
     * Creates a reply record.
     *
     * @param replierUsername the student who authored the reply
     * @param targetUsername the student whose content was answered
     */
    public ReplyRecord(String replierUsername, String targetUsername) {
        this.replierUsername = replierUsername;
        this.targetUsername = targetUsername;
    }

    /**
     * @return the username of the student who created the reply
     */
    public String getReplierUsername() {
        return replierUsername;
    }

    /**
     * @return the username of the student whose content was answered
     */
    public String getTargetUsername() {
        return targetUsername;
    }
}
