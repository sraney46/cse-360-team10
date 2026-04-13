package guiTicketForum;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import entityClasses.Ticket;
import entityClasses.Reply;

/**********
 * <p>Title: ModelTicketForum Class</p>
 *
 * <p>Description: This class serves as the Model layer for the Discussion Forum
 * in the MVC architecture. It handles all CRUD operations for both Ticket and Reply
 * objects, interacting with the ticketDB and ticketreplyDB tables of the H2 database.
 * The shared database connection is accessed via the static reference from
 * FoundationsMain, keeping all connection management centralized in the Database class.</p>
 */
public class ModelTicketForum {

    /**********************************************************************************************
     * Attributes
     **********************************************************************************************/

    // Reference for the in-memory database so this package has access
    private static database.Database theDatabase = applicationMain.FoundationsMain.database;

    /**********************************************************************************************
     * Constructor
     **********************************************************************************************/

    /**********
     * <p>Constructor: ModelTicketForum()</p>
     *
     * <p>Description: Default constructor. The database connection is accessed
     * via the shared static database reference from FoundationsMain.</p>
     */
    public ModelTicketForum() {}

    /**********************************************************************************************
     * Ticket - CREATE
     **********************************************************************************************/

    /**********
     * <p>Method: addTicket(Ticket Ticket)</p>
     *
     * <p>Description: Inserts a new Ticket object into the ticketDB table.
     * Validates the Ticket before attempting insertion and returns false
     * with a printed error if validation fails. On successful insert,
     * the database-generated postID is retrieved and stamped back onto
     * the Ticket object.</p>
     *
     * @param Ticket the Ticket object to insert into the database
     * @return true if the Ticket was successfully inserted, false otherwise
     */
    public boolean addTicket(Ticket Ticket) {
        String error = Ticket.checkValidation();
        if (!error.isEmpty()) {
            System.out.println("*** ERROR *** Cannot add ticket: " + error);
            return false;
        }
        String query = "INSERT INTO ticketDB (author, title, content, category, timestamp) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = theDatabase.getConnection()
                .prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, Ticket.getAuthor());
            pstmt.setString(2, Ticket.getTitle());
            pstmt.setString(3, Ticket.getContent());
            pstmt.setString(4, Ticket.getCategory());
            pstmt.setLong(5, Ticket.getTimestamp());
            pstmt.executeUpdate();

            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                Ticket.setPostID(generatedKeys.getInt(1));
            }

            return true;
        } catch (SQLException e) {
            System.out.println("*** ERROR *** Failed to add ticket: " + e.getMessage());
            return false;
        }
    }

    /**********************************************************************************************
     * Ticket - READ
     **********************************************************************************************/

   /**********
     * <p>Method: getTicketByID(int id)</p>
     *
     * <p>Description: Retrieves Ticket matching the desired id and returns the Ticket object.</p>
     *
     * @param id the ID of the Ticket to grab.
     * @return a Ticket object of desired Ticket, or null if an error occurs.
     */
  public Ticket getTicketByID(int id){
     List<Ticket> TicketList = new ArrayList<>();
        String query = "SELECT * FROM ticketDB WHERE postID = ?";
        try (PreparedStatement pstmt = theDatabase.getConnection().prepareStatement(query)) {  
      pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()) {
                return buildTicketFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.out.println("*** ERROR *** Failed to retrieve Tickets by ID: " + e.getMessage());    
    }
    return null;

  }

    /**********
     * <p>Method: getAllTickets()</p>
     *
     * <p>Description: Retrieves all Tickets from the ticketDB table and returns them
     * as a list of Ticket objects.</p>
     * 
     * @param constraints is a list of arguments to add to the query
     * 
     * <p>In addition, the method can take a list of constraints. But that list CANNOT
     * use integers, it must be strings.</p>
     *
     * @return a List of all Ticket objects in the database, or null if an error occurs
     * and if there is a filter for the Tickets, will only return the Tickets that pass
     * the filter.
     */
    public List<Ticket> getAllTickets(List<String> constraints) {
        List<Ticket> TicketList = new ArrayList<>();
        String query = "SELECT * FROM ticketDB";
        
        //We need this in two places, so let's init here...
        List<String> rightHandArgs = new ArrayList<>();
        
        //If there is a list of constraints, then we need to add args to the query
        if(constraints != null && constraints.size() > 0)
        {
        	//Track if we need an AND
        	int stringCounter = 0;
        	query += " WHERE ";
	        //We need an array for right hand args, which will get saved in the list parser
	        for(String str : constraints)
	        {  	
	        	//Extract the left and right hand side of the args, where they are used
	        	//in two different places
	        	String left = str.substring(0, str.indexOf(" "));
	        	
	        	//Get everything to the right of the left hand as a substring
	        	String leftSub = str.substring(str.indexOf(" ") + 1, str.length());
	        	
	        	//Extract operator from the statement
	        	String operator = leftSub.substring(0, leftSub.indexOf(" "));
	        	
	        	//Now extract the right hand side and add them to a list
	        	String right = leftSub.substring(leftSub.indexOf(" ") + 1, leftSub.length());
	        	rightHandArgs.add(right);
	        	
	        	//Finally, build the statement. Append AND if it's another filter
	        	if(stringCounter > 0) query += " AND ";
	        	query += left + " " + operator + " ?";
	        	stringCounter++;
	        }
        }
        try (PreparedStatement pstmt = theDatabase.getConnection().prepareStatement(query)) {
        	//If there is a list of constraints, let's loop through the list
        	if(constraints != null)
	        	for(int i = 1; i <= constraints.size(); i++)
	        		pstmt.setString(i, rightHandArgs.get(i - 1));
        	
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                TicketList.add(buildTicketFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.out.println("*** ERROR *** Failed to retrieve Tickets: " + e.getMessage());
            return null;
        }
        return TicketList;
    }

    /**********************************************************************************************
     * Ticket - UPDATE
     **********************************************************************************************/

    /**********
     * <p>Method: updateTicket(Ticket Ticket)</p>
     *
     * <p>Description: Updates the content and category of an existing Ticket in the ticketDB table.
     * Matches the record by postID. Validates the Ticket before attempting the update.</p>
     *
     * @param Ticket the Ticket object containing the updated values and the postID to match
     * @return true if the update was successful, false otherwise
     */
    public boolean updateTicket(Ticket Ticket) {
        String error = Ticket.checkValidation();
        if (!error.isEmpty()) {
            System.out.println("*** ERROR *** Cannot update ticket: " + error);
            return false;
        }
        String query = "UPDATE ticketDB SET title = ?, content = ?, category = ?, author = ? WHERE postID = ? ";
        try (PreparedStatement pstmt = theDatabase.getConnection().prepareStatement(query)) {
        	pstmt.setString(1, Ticket.getTitle());
        	pstmt.setString(2, Ticket.getContent());
            pstmt.setString(3, Ticket.getCategory());
            pstmt.setInt(4,Ticket.getAuthor());
            pstmt.setInt(5, Ticket.getPostID());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("*** ERROR *** Failed to update ticket: " + e.getMessage());
            return false;
        }
    }

    /**********************************************************************************************
     * Ticket - DELETE
     **********************************************************************************************/

    /**********
     * <p>Method: hardDeleteTicket(int postID)</p>
     *
     * <p>Description: Permanently removes a Ticket from the ticketDB table by its postID.
     * This method is intended for use in testing only. The GUI should use softDeleteTicket()
     * which marks the Ticket as deleted instead of removing it, allowing replies to
     * remain visible. The "Are you sure?" confirmation is handled by the caller
     * before this method is called.</p>
     *
     * @param postID the ID of the Ticket to permanently delete
     * @return true if the deletion was successful, false otherwise
     */
    public boolean hardDeleteTicket(int postID) {
        String query = "DELETE FROM ticketDB WHERE postID = ?";
        try (PreparedStatement pstmt = theDatabase.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, postID);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("*** ERROR *** Failed to delete ticket: " + e.getMessage());
            return false;
        }
    }

    /**********
     * <p>Method: softDeleteTicket(int postID)</p>
     *
     * <p>Description: Soft deletes a Ticket by overwriting its content, title, and author
     * with placeholder deleted values instead of removing the row from the database.
     * This allows any replies attached to the Ticket to remain visible in the GUI,
     * where a message indicating the original Ticket was deleted will be shown.
     * This is the method the GUI should use for deletion. For permanent removal,
     * use hardDeleteTicket() which is intended for testing only.</p>
     *
     * @param postID the ID of the Ticket to soft delete
     * @return true if the soft deletion was successful, false otherwise
     */
    public boolean softDeleteTicket(int postID) {
        String query = "UPDATE ticketDB SET content = 'This Ticket has been deleted.', title = '[Deleted]', author = '[deleted]' WHERE postID = ?";
        try (PreparedStatement pstmt = theDatabase.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, postID);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("*** ERROR *** Failed to delete ticket: " + e.getMessage());
            return false;
        }
    }

    /**********************************************************************************************
     * REPLY - CREATE
     **********************************************************************************************/

    /**********
     * <p>Method: addReply(Reply reply, int id)</p>
     *
     * <p>Description: Inserts a new Reply object into the ticketreplyDB table.
     * Validates the reply before attempting insertion and returns false
     * with a printed error if validation fails. Verifies that the specified
     * postID exists in the database before inserting. On successful insert,
     * the database-generated replyID and the provided postID are stamped
     * back onto the Reply object.</p>
     *
     * @param reply the Reply object to insert into the database
     * @param id the postID that this reply belongs to
     * @return true if the reply was successfully inserted, false otherwise
     */
    public boolean addReply(Reply reply, int id) {
        String error = reply.checkValidation();
        if (!error.isEmpty()) {
            System.out.println("*** ERROR *** Cannot add reply: " + error);
            return false;
        }
        
        // Check if Ticket exists
        String checkTicketQuery = "SELECT postID FROM ticketDB WHERE postID = ?";
        try (PreparedStatement checkStmt = theDatabase.getConnection().prepareStatement(checkTicketQuery)) {
            checkStmt.setInt(1, id);
            ResultSet rs = checkStmt.executeQuery();
            if (!rs.next()) {
                System.out.println("*** ERROR *** Cannot add reply: Ticket ID " + id + " does not exist.");
                return false;
            }
        } catch (SQLException e) {
            System.out.println("*** ERROR *** Database error during Ticket existence check: " + e.getMessage());
            return false;
        }

        // Let database handle ID auto increment
        String query = "INSERT INTO ticketreplyDB (postID, author, content, timestamp) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = theDatabase.getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, id);
            pstmt.setInt(2, reply.getAuthor());
            pstmt.setString(3, reply.getContent());
            pstmt.setLong(4, reply.getTimestamp());
            pstmt.executeUpdate();
            
            // Stamp the generated ID back onto the reply object
            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                reply.setReplyID(generatedKeys.getInt(1));
            }
            reply.setPostID(id);
            
            return true;
        } catch (SQLException e) {
            System.out.println("*** ERROR *** Failed to add reply: " + e.getMessage());
            return false;
        }
    }

    /**********************************************************************************************
     * REPLY - READ
     **********************************************************************************************/

  /**********
     * <p>Method: getTicketByID(int id)</p>
     *
     * <p>Description: Retrieves Ticket matching the desired id and returns the Ticket object.</p>
     *
     * @param id the ID of the Ticket to grab.
     * @return a Ticket object of desired Ticket, or null if an error occurs.
     */
  public Reply getReplyByID(int id){
        List<Reply> replyList = new ArrayList<>();
        String query = "SELECT * FROM ticketreplyDB WHERE replyID = ?";
        try (PreparedStatement pstmt = theDatabase.getConnection().prepareStatement(query)) {  
      pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()) {
                return buildReplyFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.out.println("*** ERROR *** Failed to retrieve Tickets by ID: " + e.getMessage());    
    }
    return null;

  }  

    /**********
     * <p>Method: getAllReplies()</p>
     *
     * <p>Description: Retrieves all replies from the ticketreplyDB table.</p>
     *
     * @return a List of all Reply objects in the database, or null if an error occurs
     */
    public List<Reply> getAllReplies() {
        List<Reply> replyList = new ArrayList<>();
        String query = "SELECT * FROM ticketreplyDB";
        try (PreparedStatement pstmt = theDatabase.getConnection().prepareStatement(query)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                replyList.add(buildReplyFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.out.println("*** ERROR *** Failed to retrieve replies: " + e.getMessage());
            return null;
        }
        return replyList;
    }

    /**********
     * <p>Method: getRepliesByTicket(int postID)</p>
     *
     * <p>Description: Retrieves all replies associated with a specific Ticket.
     * Primary method used by the GUI when a user clicks on a Ticket.</p>
     *
     * @param postID the ID of the Ticket whose replies should be retrieved
     * @return a List of Reply objects for the given postID, or null if an error occurs
     */
    public List<Reply> getRepliesByTicket(int postID) {
        List<Reply> replyList = new ArrayList<>();
        String query = "SELECT * FROM ticketreplyDB WHERE postID = ?";
        try (PreparedStatement pstmt = theDatabase.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, postID);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                replyList.add(buildReplyFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.out.println("*** ERROR *** Failed to retrieve replies by ticket: " + e.getMessage());
            return null;
        }
        return replyList;
    }

    /**********
     * <p>Method: getRepliesByTicketAndRole(int postID, String role)</p>
     *
     * <p>Description: Retrieves replies for a specific Ticket filtered by author role.
     * Useful for showing only Staff replies on a given Ticket.</p>
     *
     * @param postID the ID of the Ticket to filter by
     * @param role   the author role to filter by
     * @return a List of Reply objects matching both postID, or null if an error occurs
     */
    public List<Reply> getRepliesByTicketAndRole(int postID, String role) {
        List<Reply> replyList = new ArrayList<>();
        String query = "SELECT * FROM ticketreplyDB WHERE postID = ?";
        try (PreparedStatement pstmt = theDatabase.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, postID);
            pstmt.setString(2, role);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
            	if(theDatabase.getUserAsObject(rs.getInt("id")).getRoleString() == role)
            		replyList.add(buildReplyFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.out.println("*** ERROR *** Failed to retrieve replies by Ticket and role: " + e.getMessage());
            return null;
        }
        return replyList;
    }
  /**********************************************************************************************
     * REPLY - UPDATE
     **********************************************************************************************/

    /**********
     * <p>Method: updateReply(Reply reply)</p>
     *
     * <p>Description: Updates the content, author, and authorRole of an existing reply
     * in the ticketreplyDB table. Matches the record by both postID and replyID. Validates
     * the reply before attempting the update and returns false if validation fails.
     * Also returns false if no matching reply is found in the database.</p>
     *
     * @param reply the Reply object containing the updated values, postID, and replyID to match
     * @return true if the update was successful, false otherwise
     */
    public boolean updateReply(Reply reply) {
    String error = reply.checkValidation(); 
    if (!error.isEmpty()) {
        System.out.println("*** ERROR *** Cannot update reply: " + error);
        return false;
    }

    String query = "UPDATE ticketreplyDB SET content = ?, author = ?, " +
                   "WHERE postID = ? AND replyID = ?";

    try (PreparedStatement pstmt = theDatabase.getConnection().prepareStatement(query)) {
        pstmt.setString(1, reply.getContent());
        pstmt.setInt(2, reply.getAuthor());
        pstmt.setInt(4, reply.getPostID());
        pstmt.setInt(5, reply.getReplyID());

        int rowsAffected = pstmt.executeUpdate();
        
        if (rowsAffected == 0) {
            System.out.println("*** ERROR *** No reply found with postID " + 
                               reply.getPostID() + " and ReplyID " + reply.getReplyID());
            return false;
        }
        
        return true;
    } catch (SQLException e) {
        System.out.println("*** ERROR *** Failed to update reply: " + e.getMessage());
        return false;
    }
    
   }
    
    


   /**********
    * <p>Method: markTicketAsRead(String userName, int postID)</p>
    *
    * <p>Description: Marks a Ticket as read for a specific user by inserting a row
    * into the TicketReadStatus table. Uses MERGE so duplicate reads do not cause
    * errors. Called when a user clicks on a Ticket in the scroll pane.</p>
    *
    * @param userName the username of the user who read the Ticket
    * @param postID the ID of the Ticket that was read
    * @return true if the operation was successful, false otherwise
    */
   public boolean markTicketAsRead(String userName, int postID) {
       String query = "MERGE INTO TicketReadStatus (userName, postID) VALUES (?, ?)";
       try (PreparedStatement pstmt = theDatabase.getConnection().prepareStatement(query)) {
           pstmt.setString(1, userName);
           pstmt.setInt(2, postID);
           pstmt.executeUpdate();
           return true;
       } catch (SQLException e) {
           System.out.println("*** ERROR *** Failed to mark Ticket as read: " + e.getMessage());
           return false;
       }
   }

   /**********
    * <p>Method: isTicketRead(String userName, int postID)</p>
    *
    * <p>Description: Checks whether a specific user has read a specific Ticket by
    * looking for a matching row in the TicketReadStatus table. The absence of a row
    * means the Ticket is unread. Called when building each Ticket row in the scroll pane.</p>
    *
    * @param userName the username of the user to check
    * @param postID the ID of the Ticket to check
    * @return true if the user has read the Ticket, false otherwise
    */
   public boolean isTicketRead(String userName, int postID) {
       String query = "SELECT 1 FROM TicketReadStatus WHERE userName = ? AND postID = ?";
       try (PreparedStatement pstmt = theDatabase.getConnection().prepareStatement(query)) {
           pstmt.setString(1, userName);
           pstmt.setInt(2, postID);
           ResultSet rs = pstmt.executeQuery();
           return rs.next();
       } catch (SQLException e) {
           System.out.println("*** ERROR *** Failed to check read status: " + e.getMessage());
           return false;
       }
   }

   /**********
    * <p>Method: markTicketAsUnread(int postID, String exceptUserName)</p>
    *
    * <p>Description: Marks a Ticket as unread for all users except the specified user
    * by deleting their rows from the TicketReadStatus table. Called when a new reply
    * is added to a Ticket so that all other users are notified of new activity.</p>
    *
    * @param postID the ID of the Ticket to mark as unread
    * @param exceptUserName the username of the user who should remain marked as read
    * @return true if the operation was successful, false otherwise
    */
   public boolean markTicketAsUnread(int postID, String exceptUserName) {
       String query = "DELETE FROM TicketReadStatus WHERE postID = ? AND userName != ?";
       try (PreparedStatement pstmt = theDatabase.getConnection().prepareStatement(query)) {
           pstmt.setInt(1, postID);
           pstmt.setString(2, exceptUserName);
           pstmt.executeUpdate();
           return true;
       } catch (SQLException e) {
           System.out.println("*** ERROR *** Failed to mark Ticket as unread: " + e.getMessage());
           return false;
       }
   }

    /**********************************************************************************************
     * Helper Methods
     **********************************************************************************************/

    /**********
     * <p>Method: buildTicketFromResultSet(ResultSet rs)</p>
     *
     * <p>Description: Private helper that constructs a Ticket object from the current
     * row of a ResultSet, avoiding duplicate field mapping code.</p>
     *
     * @param rs the ResultSet positioned at the row to convert
     * @return a Ticket object populated with values from the current row
     * @throws SQLException if any column cannot be retrieved
     */
    private Ticket buildTicketFromResultSet(ResultSet rs) throws SQLException {
        Ticket Ticket = new Ticket();
        Ticket.setPostID(rs.getInt("postID"));
        Ticket.setAuthor(rs.getInt("author"));
        Ticket.setTitle(rs.getString("title"));
        Ticket.setContent(rs.getString("content"));
        Ticket.setCategory(rs.getString("category"));
        Ticket.setTimestamp(rs.getLong("timestamp"));
        return Ticket;
    }
  /**********************************************************************************************
 * REPLY - DELETE
 **********************************************************************************************/

/**********
 * <p>Method: deleteReply(int postID, int replyID)</p>
 *
 * <p>Description: Deletes a specific reply from the ticketreplyDB table using its 
 * postID and replyID. The "Are you sure?" confirmation is handled by the 
 * GUI before this method is called.</p>
 *
 * @param postID the ID of the Ticket the reply belongs to
 * @param replyID the ID of the specific reply to delete
 * @return true if the deletion was successful, false otherwise
 */
public boolean deleteReply(int postID, int replyID) {
    String query = "DELETE FROM ticketreplyDB WHERE postID = ? AND replyID = ?";
    
    try (PreparedStatement pstmt = theDatabase.getConnection().prepareStatement(query)) {
        pstmt.setInt(1, postID);
        pstmt.setInt(2, replyID);
        
        int rowsAffected = pstmt.executeUpdate();
        
        return rowsAffected > 0;
    } catch (SQLException e) {
        System.out.println("*** ERROR *** Failed to delete reply: " + e.getMessage());
        return false;
    }
}

    /**********
     * <p>Method: buildReplyFromResultSet(ResultSet rs)</p>
     *
     * <p>Description: Private helper that constructs a Reply object from the current
     * row of a ResultSet, avoiding duplicate field mapping code.</p>
     *
     * @param rs the ResultSet positioned at the row to convert
     * @return a Reply object populated with values from the current row
     * @throws SQLException if any column cannot be retrieved
     */
    private Reply buildReplyFromResultSet(ResultSet rs) throws SQLException {
        Reply reply = new Reply();
        reply.setReplyID(rs.getInt("replyID"));
        reply.setPostID(rs.getInt("postID"));
        reply.setAuthor(rs.getInt("author"));
        reply.setContent(rs.getString("content"));
        reply.setTimestamp(rs.getLong("timestamp"));
        return reply;
    }
}
