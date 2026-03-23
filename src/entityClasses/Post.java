package entityClasses;

/**********
 * <p>Title: Post Class</p>
 *
 * <p>Description: This class defines a Post object for the discussion forum.
 * Each post contains content authored by a student, organized by category,
 * and tracked by a unique ID and timestamp. This class provides getters,
 * setters, and input validation for all attributes.</p>
 */
public class Post {

    /**********************************************************************************************
     * Attributes
     **********************************************************************************************/

    /** Unique identifier for the post, assigned by the database */
    private int postID;

    /** The username of the student who created the post */
    private String author;
    
    private String title;

    /** The body content of the post */
    private String content;

    /** The category of the post (e.g., "General", "Homework", "Lectures") */
    private String category;

    /** The time the post was created, stored as a Unix timestamp in milliseconds */
    private long timestamp;
    
    /** Maximum allowed character length for post body content */
    private static final int MAX_LENGTH = 100;

    private static final String SPECIAL_CHARS = "!@#$%^&*[]{}|;:<>?/";


    /**********************************************************************************************
     * Constructors
     **********************************************************************************************/

    /**********
     * <p>Constructor: Post()</p>
     *
     * <p>Description: Default constructor. Creates an empty Post object.
     * Used when building a Post from database retrieval before setting fields individually.</p>
     */
    public Post() {}

    /**********
     * <p>Constructor: Post(String author, String content, String category)</p>
     *
     * <p>Description: Creates a new Post with the provided author, content, and category.
     * Timestamp is set automatically to the current system time.
     * PostID is left as default (0) until assigned by the database.</p>
     *
     * @param author   the username of the student creating the post
     * @param content  the body text of the post
     * @param category the category the post belongs to
     */
    public Post(String author, String title, String content, String category) {
        this.author = author;
        this.title = title;
        this.content = content;
        this.category = category;
        this.timestamp = System.currentTimeMillis();
    }

    /**********************************************************************************************
     * Getters
     **********************************************************************************************/

    /**********
     * <p>Method: getPostID()</p>
     * @return the unique ID of the post
     */
    public int getPostID() { return postID; }

    /**********
     * <p>Method: getAuthor()</p>
     * @return the username of the post's author
     */
    public String getAuthor() { return author; }
    
    /**********
     * <p>Method: getTitle()</p>
     * @return the title of the post
     */
    public String getTitle() { return title; }

    /**********
     * <p>Method: getContent()</p>
     * @return the body content of the post
     */
    public String getContent() { return content; }

    /**********
     * <p>Method: getCategory()</p>
     * @return the category of the post
     */
    public String getCategory() { return category; }

    /**********
     * <p>Method: getTimestamp()</p>
     * @return the Unix timestamp of when the post was created
     */
    public long getTimestamp() { return timestamp; }

    /**********************************************************************************************
     * Setters
     **********************************************************************************************/

    /**********
     * <p>Method: setPostID(int postID)</p>
     * @param postID the database-assigned ID to set
     */
    public void setPostID(int postID) { this.postID = postID; }

    /**********
     * <p>Method: setAuthor(String author)</p>
     * @param author the username to set as the author
     */
    public void setAuthor(String author) { this.author = author; }
    
    /**********
     * <p>Method: setTitle(String title)</p>
     * @param author the username to set as the author
     */
    public void setTitle(String title) { this.title = title; }

    /**********
     * <p>Method: setContent(String content)</p>
     * @param content the new body content to set
     */
    public void setContent(String content) { this.content = content; }

    /**********
     * <p>Method: setCategory(String category)</p>
     * @param category the category to assign to the post
     */
    public void setCategory(String category) { this.category = category; }

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
     * <p>Description: Validates the post's content field and returns a helpful error
     * message if validation fails. Checks that the body is not null, not empty,
     * not whitespace-only, and does not exceed the maximum allowed length.
     * Returns an empty string if the post is valid.</p>
     *
     * @return a String describing the validation error, or empty string if valid
     */
	public String checkValidation() {
	    if (author == null) return "Post author cannot be null.";
	    if (title == null) return "Post title cannot be null."; // Added title null check
	    if (content == null) return "Post body cannot be null.";
	    if (category == null) return "Post category cannot be null.";
	
	    if (author.isEmpty() && title.isEmpty() && content.isEmpty() && category.isEmpty()) {
	        return "All fields can not be empty.";
	    }
	
	    if (author.isEmpty()) return "Post author cannot be empty.";
	    if (title.isEmpty()) return "Post title cannot be empty.";
	    if (content.isEmpty()) return "Post body cannot be empty.";
	    
	    if (content.trim().isEmpty()) return "Post body cannot be whitespace only.";
	    if (content.length() > MAX_LENGTH) {
	        return "Post body cannot exceed " + MAX_LENGTH + " characters.";
	    }
	
	    boolean hasSpecial = false;
	    for (char c : author.toCharArray()) {
	        if (SPECIAL_CHARS.indexOf(c) >= 0) {
	            hasSpecial = true;
	            break; 
	      }
	    }
	    
	    if (hasSpecial) {
	        return "Post author and/or category cannot be special character.";
	    }
	
	    return "";
	}
}
