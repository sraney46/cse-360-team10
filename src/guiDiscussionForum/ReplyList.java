package guiDiscussionForum;

//import guiDiscussionForum.Reply;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import entityClasses.Reply;

/**********
 * <p>Title: ReplyList Class</p>
 *
 * <p>Description: This class handles all CRUD operations for Reply objects
 * in the replyDB table of the H2 database. It also supports retrieving
 * subsets of replies filtered by postID or author role. The shared database
 * connection is accessed via the static reference from FoundationsMain,
 * keeping all connection management centralized in the Database class.</p>
 */
public class ReplyList {

    /**********************************************************************************************
     * Attributes
     **********************************************************************************************/

    // Reference for the in-memory database so this package has access
    private static database.Database theDatabase = applicationMain.FoundationsMain.database;

    /**********************************************************************************************
     * Constructor
     **********************************************************************************************/

    /**********
     * <p>Constructor: ReplyList()</p>
     *
     * <p>Description: Default constructor. The database connection is accessed
     * via the shared static database reference from FoundationsMain.</p>
     */
    public ReplyList() {}

    /**********************************************************************************************
     * CREATE
     **********************************************************************************************/

    /**********
     * <p>Method: addReply(Reply reply)</p>
     *
     * <p>Description: Inserts a new Reply object into the replyDB table.
     * Validates the reply before attempting insertion and returns false
     * with a printed error if validation fails.</p>
     *
     * @param reply the Reply object to insert into the database
     * @return true if the reply was successfully inserted, false otherwise
     */
    public boolean addReply(Reply reply) {
    	String error = reply.checkValidation();
    	if (!error.isEmpty()) {
    	    System.out.println("*** ERROR *** Cannot add post: " + error);
    	    return false;
    	}
        String query = "INSERT INTO replyDB (postID, author, authorRole, content, timestamp) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = theDatabase.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, reply.getPostID());
            pstmt.setString(2, reply.getAuthor());
            pstmt.setString(3, reply.getAuthorRole());
            pstmt.setString(4, reply.getContent());
            pstmt.setLong(5, reply.getTimestamp());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("*** ERROR *** Failed to add reply: " + e.getMessage());
            return false;
        }
    }

    /**********************************************************************************************
     * READ - Full List
     **********************************************************************************************/

    /**********
     * <p>Method: getAllReplies()</p>
     *
     * <p>Description: Retrieves all replies from the replyDB table and returns them
     * as a list of Reply objects. Each row in the database becomes one Reply object
     * with its fields populated from the corresponding columns.</p>
     *
     * @return a List of all Reply objects in the database, or null if an error occurs
     */
    public List<Reply> getAllReplies() {
        List<Reply> replyList = new ArrayList<>();
        String query = "SELECT * FROM replyDB";
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

    /**********************************************************************************************
     * READ - Subsets
     **********************************************************************************************/

    /**********
     * <p>Method: getRepliesByPost(int postID)</p>
     *
     * <p>Description: Retrieves all replies associated with a specific post.
     * This is the primary method used by the GUI to load replies when a user
     * clicks on a post in the left panel.</p>
     *
     * @param postID the ID of the post whose replies should be retrieved
     * @return a List of Reply objects associated with the given postID, or null if an error occurs
     */
    public List<Reply> getRepliesByPost(int postID) {
        List<Reply> replyList = new ArrayList<>();
        String query = "SELECT * FROM replyDB WHERE postID = ?";
        try (PreparedStatement pstmt = theDatabase.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, postID);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                replyList.add(buildReplyFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.out.println("*** ERROR *** Failed to retrieve replies by post: " + e.getMessage());
            return null;
        }
        return replyList;
    }


    /**********
     * <p>Method: getRepliesByPostAndRole(int postID, String role)</p>
     *
     * <p>Description: Retrieves a subset of replies for a specific post, further filtered
     * by author role. This combines both filters so the GUI can show, for example,
     * only Staff replies on a specific post without fetching unnecessary data.</p>
     *
     * @param postID the ID of the post to filter by
     * @param role   the author role to filter by
     * @return a List of Reply objects matching both the postID and authorRole, or null if an error occurs
     */
    public List<Reply> getRepliesByPostAndRole(int postID, String role) {
        List<Reply> replyList = new ArrayList<>();
        String query = "SELECT * FROM replyDB WHERE postID = ? AND authorRole = ?";
        try (PreparedStatement pstmt = theDatabase.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, postID);
            pstmt.setString(2, role);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                replyList.add(buildReplyFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.out.println("*** ERROR *** Failed to retrieve replies by post and role: " + e.getMessage());
            return null;
        }
        return replyList;
    }

   

    /**********************************************************************************************
     * Helper Methods
     **********************************************************************************************/

    /**********
     * <p>Method: buildReplyFromResultSet(ResultSet rs)</p>
     *
     * <p>Description: A private helper method that constructs a Reply object from the
     * current row of a ResultSet. Centralizing this logic avoids duplicating the
     * field mapping code across every retrieval method.</p>
     *
     * @param rs the ResultSet positioned at the row to convert
     * @return a Reply object populated with values from the current row
     * @throws SQLException if any column cannot be retrieved from the ResultSet
     */
    private Reply buildReplyFromResultSet(ResultSet rs) throws SQLException {
        Reply reply = new Reply();
        reply.setReplyID(rs.getInt("replyID"));
        reply.setPostID(rs.getInt("postID"));
        reply.setAuthor(rs.getString("author"));
        reply.setAuthorRole(rs.getString("authorRole"));
        reply.setContent(rs.getString("content"));
        reply.setTimestamp(rs.getLong("timestamp"));
        return reply;
    }
}