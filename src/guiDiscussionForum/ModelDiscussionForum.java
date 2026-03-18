package guiDiscussionForum;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import entityClasses.Post;
import entityClasses.Reply;

/**********
 * <p>Title: ModelDiscussionForum Class</p>
 *
 * <p>Description: This class serves as the Model layer for the Discussion Forum
 * in the MVC architecture. It handles all CRUD operations for both Post and Reply
 * objects, interacting with the postDB and replyDB tables of the H2 database.
 * The shared database connection is accessed via the static reference from
 * FoundationsMain, keeping all connection management centralized in the Database class.</p>
 */
public class ModelDiscussionForum {

    /**********************************************************************************************
     * Attributes
     **********************************************************************************************/

    // Reference for the in-memory database so this package has access
    private static database.Database theDatabase = applicationMain.FoundationsMain.database;

    /**********************************************************************************************
     * Constructor
     **********************************************************************************************/

    /**********
     * <p>Constructor: ModelDiscussionForum()</p>
     *
     * <p>Description: Default constructor. The database connection is accessed
     * via the shared static database reference from FoundationsMain.</p>
     */
    public ModelDiscussionForum() {}

    /**********************************************************************************************
     * POST - CREATE
     **********************************************************************************************/

    /**********
     * <p>Method: addPost(Post post)</p>
     *
     * <p>Description: Inserts a new Post object into the postDB table.
     * Validates the post before attempting insertion and returns false
     * with a printed error if validation fails. On successful insert,
     * the database-generated postID is retrieved and stamped back onto
     * the Post object.</p>
     *
     * @param post the Post object to insert into the database
     * @return true if the post was successfully inserted, false otherwise
     */
    public boolean addPost(Post post) {
        String error = post.checkValidation();
        if (!error.isEmpty()) {
            System.out.println("*** ERROR *** Cannot add post: " + error);
            return false;
        }
        String query = "INSERT INTO postDB (author, content, category, timestamp) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = theDatabase.getConnection()
                .prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, post.getAuthor());
            pstmt.setString(2, post.getContent());
            pstmt.setString(3, post.getCategory());
            pstmt.setLong(4, post.getTimestamp());
            pstmt.executeUpdate();

            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                post.setPostID(generatedKeys.getInt(1));
            }

            return true;
        } catch (SQLException e) {
            System.out.println("*** ERROR *** Failed to add post: " + e.getMessage());
            return false;
        }
    }

    /**********************************************************************************************
     * POST - READ
     **********************************************************************************************/

   /**********
     * <p>Method: getPostByID(int id)</p>
     *
     * <p>Description: Retrieves post matching the desired id and returns the post object.</p>
     *
     * @param postID the ID of the post to grab.
     * @return a post object of desired post, or null if an error occurs.
     */
  public Post getPostByID(int id){
     List<Post> postList = new ArrayList<>();
        String query = "SELECT * FROM postDB WHERE postID = ?";
        try (PreparedStatement pstmt = theDatabase.getConnection().prepareStatement(query)) {  
      pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()) {
                return buildPostFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.out.println("*** ERROR *** Failed to retrieve posts by ID: " + e.getMessage());    
    }
    return null;

  }

    /**********
     * <p>Method: getAllPosts()</p>
     *
     * <p>Description: Retrieves all posts from the postDB table and returns them
     * as a list of Post objects.</p>
     *
     * @return a List of all Post objects in the database, or null if an error occurs
     */
    public List<Post> getAllPosts() {
        List<Post> postList = new ArrayList<>();
        String query = "SELECT * FROM postDB";
        try (PreparedStatement pstmt = theDatabase.getConnection().prepareStatement(query)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                postList.add(buildPostFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.out.println("*** ERROR *** Failed to retrieve posts: " + e.getMessage());
            return null;
        }
        return postList;
    }

    /**********
     * <p>Method: getPostsByCategory(String category)</p>
     *
     * <p>Description: Retrieves a subset of posts filtered by category
     * (e.g., "General", "Homework", "Lectures").</p>
     *
     * @param category the category to filter posts by
     * @return a List of Post objects matching the category, or null if an error occurs
     */
    public List<Post> getPostsByCategory(String category) {
        List<Post> postList = new ArrayList<>();
        String query = "SELECT * FROM postDB WHERE category = ?";
        try (PreparedStatement pstmt = theDatabase.getConnection().prepareStatement(query)) {
            pstmt.setString(1, category);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                postList.add(buildPostFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.out.println("*** ERROR *** Failed to retrieve posts by category: " + e.getMessage());
            return null;
        }
        return postList;
    } 

    /**********************************************************************************************
     * POST - UPDATE
     **********************************************************************************************/

    /**********
     * <p>Method: updatePost(Post post)</p>
     *
     * <p>Description: Updates the content and category of an existing post in the postDB table.
     * Matches the record by postID. Validates the post before attempting the update.</p>
     *
     * @param post the Post object containing the updated values and the postID to match
     * @return true if the update was successful, false otherwise
     */
    public boolean updatePost(Post post) {
        String error = post.checkValidation();
        if (!error.isEmpty()) {
            System.out.println("*** ERROR *** Cannot update post: " + error);
            return false;
        }
        String query = "UPDATE postDB SET content = ?, category = ?, author = ? WHERE postID = ? ";
        try (PreparedStatement pstmt = theDatabase.getConnection().prepareStatement(query)) {
            pstmt.setString(1, post.getContent());
            pstmt.setString(2, post.getCategory());
            pstmt.setString(3,post.getAuthor());
            pstmt.setInt(4, post.getPostID());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("*** ERROR *** Failed to update post: " + e.getMessage());
            return false;
        }
    }

    /**********************************************************************************************
     * POST - DELETE
     **********************************************************************************************/

    /**********
     * <p>Method: deletePost(int postID)</p>
     *
     * <p>Description: Deletes a post from the postDB table by its postID.
     * The "Are you sure?" confirmation is handled by the GUI before this method is called.</p>
     *
     * @param postID the ID of the post to delete
     * @return true if the deletion was successful, false otherwise
     */
    public boolean deletePost(int postID) {
        String query = "DELETE FROM postDB WHERE postID = ?";
        try (PreparedStatement pstmt = theDatabase.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, postID);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("*** ERROR *** Failed to delete post: " + e.getMessage());
            return false;
        }
    }

    /**********************************************************************************************
     * REPLY - CREATE
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
            System.out.println("*** ERROR *** Cannot add reply: " + error);
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
     * REPLY - READ
     **********************************************************************************************/

    /**********
     * <p>Method: getAllReplies()</p>
     *
     * <p>Description: Retrieves all replies from the replyDB table.</p>
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

    /**********
     * <p>Method: getRepliesByPost(int postID)</p>
     *
     * <p>Description: Retrieves all replies associated with a specific post.
     * Primary method used by the GUI when a user clicks on a post.</p>
     *
     * @param postID the ID of the post whose replies should be retrieved
     * @return a List of Reply objects for the given postID, or null if an error occurs
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
     * <p>Description: Retrieves replies for a specific post filtered by author role.
     * Useful for showing only Staff replies on a given post.</p>
     *
     * @param postID the ID of the post to filter by
     * @param role   the author role to filter by
     * @return a List of Reply objects matching both postID and authorRole, or null if an error occurs
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
     * <p>Method: buildPostFromResultSet(ResultSet rs)</p>
     *
     * <p>Description: Private helper that constructs a Post object from the current
     * row of a ResultSet, avoiding duplicate field mapping code.</p>
     *
     * @param rs the ResultSet positioned at the row to convert
     * @return a Post object populated with values from the current row
     * @throws SQLException if any column cannot be retrieved
     */
    private Post buildPostFromResultSet(ResultSet rs) throws SQLException {
        Post post = new Post();
        post.setPostID(rs.getInt("postID"));
        post.setAuthor(rs.getString("author"));
        post.setContent(rs.getString("content"));
        post.setCategory(rs.getString("category"));
        post.setTimestamp(rs.getLong("timestamp"));
        return post;
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
        reply.setAuthor(rs.getString("author"));
        reply.setAuthorRole(rs.getString("authorRole"));
        reply.setContent(rs.getString("content"));
        reply.setTimestamp(rs.getLong("timestamp"));
        return reply;
    }
}
