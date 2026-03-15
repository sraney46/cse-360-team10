package entityClasses;

/**********
 * <p>Title: Reply Class</p>
 *
 * <p>Description: This class defines a Reply object for the discussion forum.
 * Each reply is associated with a specific post via postID, tracks the author
 * and their role, and includes the reply content and timestamp. This class
 * provides getters, setters, and input validation for all attributes.</p>
 */
public class Reply {

    /**********************************************************************************************
     * Attributes
     **********************************************************************************************/

    /** Unique identifier for the reply, assigned by the database */
    private int replyID;

    /** The ID of the post this reply belongs to */
    private int postID;

    /** The username of the user who created the reply */
    private String author;

    /** The role of the author (e.g., "Admin", "Staff", "Student") */
    private String authorRole;

    /** The body content of the reply */
    private String content;

    /** The time the reply was created, stored as a Unix timestamp in milliseconds */
    private long timestamp;
    
    /** Maximum allowed character length for reply body content */
    private static final int MAX_LENGTH = 300;

    /**********************************************************************************************
     * Constructors
     **********************************************************************************************/

    /**********
     * <p>Constructor: Reply()</p>
     *
     * <p>Description: Default constructor. Creates an empty Reply object.
     * Used when building a Reply from database retrieval before setting fields individually.</p>
     */
    public Reply() {}

    /**********
     * <p>Constructor: Reply(int postID, String author, String authorRole, String content)</p>
     *
     * <p>Description: Creates a new Reply with the provided postID, author, authorRole,
     * and content. Timestamp is set automatically to the current system time.
     * ReplyID is left as default (0) until assigned by the database.</p>
     *
     * @param postID     the ID of the post this reply is responding to
     * @param author     the username of the user creating the reply
     * @param authorRole the role of the author (e.g., "Admin", "Staff", "Student")
     * @param content    the body text of the reply
     */
    public Reply(int postID, String author, String authorRole, String content) {
        this.postID = postID;
        this.author = author;
        this.authorRole = authorRole;
        this.content = content;
        this.timestamp = System.currentTimeMillis();
    }

    /**********************************************************************************************
     * Getters
     **********************************************************************************************/

    /**********
     * <p>Method: getReplyID()</p>
     * @return the unique ID of the reply
     */
    public int getReplyID() { return replyID; }

    /**********
     * <p>Method: getPostID()</p>
     * @return the ID of the post this reply belongs to
     */
    public int getPostID() { return postID; }

    /**********
     * <p>Method: getAuthor()</p>
     * @return the username of the reply's author
     */
    public String getAuthor() { return author; }

    /**********
     * <p>Method: getAuthorRole()</p>
     * @return the role of the reply's author
     */
    public String getAuthorRole() { return authorRole; }

    /**********
     * <p>Method: getContent()</p>
     * @return the body content of the reply
     */
    public String getContent() { return content; }

    /**********
     * <p>Method: getTimestamp()</p>
     * @return the Unix timestamp of when the reply was created
     */
    public long getTimestamp() { return timestamp; }

    /**********************************************************************************************
     * Setters
     **********************************************************************************************/

    /**********
     * <p>Method: setReplyID(int replyID)</p>
     * @param replyID the database-assigned ID to set
     */
    public void setReplyID(int replyID) { this.replyID = replyID; }

    /**********
     * <p>Method: setPostID(int postID)</p>
     * @param postID the ID of the post this reply belongs to
     */
    public void setPostID(int postID) { this.postID = postID; }

    /**********
     * <p>Method: setAuthor(String author)</p>
     * @param author the username to set as the author
     */
    public void setAuthor(String author) { this.author = author; }

    /**********
     * <p>Method: setAuthorRole(String authorRole)</p>
     * @param authorRole the role to assign to the author
     */
    public void setAuthorRole(String authorRole) { this.authorRole = authorRole; }

    /**********
     * <p>Method: setContent(String content)</p>
     * @param content the new body content to set
     */
    public void setContent(String content) { this.content = content; }

    /**********
     * <p>Method: setTimestamp(long timestamp)</p>
     * @param timestamp the Unix timestamp to set
     */
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    /**********************************************************************************************
     * Validation
     **********************************************************************************************/

    /**********
     * <p>Method: checkValidation()</p>
     *
     * <p>Description: Validates the reply's content field and returns a helpful error
     * message if validation fails. Checks that the body is not null, not empty,
     * not whitespace-only, and does not exceed the maximum allowed length.
     * Returns an empty string if the reply is valid.</p>
     *
     * @return a String describing the validation error, or empty string if valid
     */
    public String checkValidation() {
        if (content == null)
            return "Reply body cannot be null.";
        if (content.isEmpty())
            return "Reply body cannot be empty.";
        if (content.trim().isEmpty())
            return "Reply body cannot be whitespace only.";
        if (content.length() > MAX_LENGTH)
            return "Reply body cannot exceed " + MAX_LENGTH + " characters.";
        return "";
    }
}