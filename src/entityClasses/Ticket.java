package entityClasses;

/**********
 * <p>Title: Ticket Class</p>
 *
 * <p>Description: This class defines a Ticket object for the discussion forum.
 * Each Ticket contains content authored by a student, organized by category,
 * and tracked by a unique ID and timestamp. This class provides getters,
 * setters, and input validation for all attributes.</p>
 */
public class Ticket extends Post {

    /**********************************************************************************************
     * Constructors
     **********************************************************************************************/

    /**********
     * <p>Constructor: Ticket()</p>
     *
     * <p>Description: Default constructor. Creates an empty Ticket object.
     * Used when building a Ticket from database retrieval before setting fields individually.</p>
     */
    public Ticket() {}

    /**********
     * <p>Constructor: Ticket(String author, String content, String category)</p>
     *
     * <p>Description: Creates a new Ticket with the provided author, content, and category.
     * Timestamp is set automatically to the current system time.
     * TicketID is left as default (0) until assigned by the database.</p>
     *
     * @param author   the identification of the student creating the Ticket
     * @param title  the title of the ticket
     * @param content  the body text of the Ticket
     */
    public Ticket(int author, String title, String content) {
    	this.setAuthor(author);
    	this.setTitle(title);
    	this.setContent(content);
    	this.setCategory("Open");
    	this.setTimestamp(System.currentTimeMillis());
    }
}
