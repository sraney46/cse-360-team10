package guiDiscussionForum;

//import guiDiscussionForum.Post;
import java.sql.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import entityClasses.Post;

/**********
 * <p>Title: PostList Class</p>
 *
 * <p>Description: This class handles all CRUD operations for Post objects
 * in the postDB table of the H2 database. It also supports retrieving
 * subsets of posts filtered by category or keyword. The shared database
 * connection is accessed via the static reference from FoundationsMain,
 * keeping all connection management centralized in the Database class.</p>
 */
public class PostList {

    /**********************************************************************************************
     * Attributes
     **********************************************************************************************/

    // Reference for the in-memory database so this package has access
    private static database.Database theDatabase = applicationMain.FoundationsMain.database;

    /**********************************************************************************************
     * Constructor
     **********************************************************************************************/

    /**********
     * <p>Constructor: PostList()</p>
     *
     * <p>Description: Default constructor. The database connection is accessed
     * via the shared static database reference from FoundationsMain.</p>
     */
    public PostList() {}

    /**********************************************************************************************
     * CREATE
     **********************************************************************************************/

    /**********
     * <p>Method: addPost(Post post)</p>
     *
     * <p>Description: Inserts a new Post object into the postDB table.
     * Validates the post before attempting insertion and returns false
     * with a printed error if validation fails.</p>
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
        try (PreparedStatement pstmt = theDatabase.getConnection().prepareStatement(query)) {
            pstmt.setString(1, post.getAuthor());
            pstmt.setString(2, post.getContent());
            pstmt.setString(3, post.getCategory());
            pstmt.setLong(4, post.getTimestamp());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("*** ERROR *** Failed to add post: " + e.getMessage());
            return false;
        }
    }

    /**********************************************************************************************
     * READ - Full List
     **********************************************************************************************/

    /**********
     * <p>Method: getAllPosts()</p>
     *
     * <p>Description: Retrieves all posts from the postDB table and returns them
     * as a list of Post objects. Each row in the database becomes one Post object
     * with its fields populated from the corresponding columns.</p>
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

    /**********************************************************************************************
     * READ - Subsets
     **********************************************************************************************/

    /**********
     * <p>Method: getPostsByCategory(String category)</p>
     *
     * <p>Description: Retrieves a subset of posts from the postDB table that match
     * the specified category. This supports the category filter feature in the GUI
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
     * UPDATE
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
    	    System.out.println("*** ERROR *** Cannot add post: " + error);
    	    return false;
    	}
        String query = "UPDATE postDB SET content = ?, category = ? WHERE postID = ?";
        try (PreparedStatement pstmt = theDatabase.getConnection().prepareStatement(query)) {
            pstmt.setString(1, post.getContent());
            pstmt.setString(2, post.getCategory());
            pstmt.setInt(3, post.getPostID());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("*** ERROR *** Failed to update post: " + e.getMessage());
            return false;
        }
    }

    /**********************************************************************************************
     * DELETE
     **********************************************************************************************/

    /**********
     * <p>Method: deletePost(int postID)</p>
     *
     * <p>Description: Deletes a post from the postDB table by its postID.
     * The "Are you sure?" confirmation is handled by the GUI before this method is called,
     * so this method performs the deletion directly.</p>
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
     * Helper Methods
     **********************************************************************************************/

    /**********
     * <p>Method: buildPostFromResultSet(ResultSet rs)</p>
     *
     * <p>Description: A private helper method that constructs a Post object from the
     * current row of a ResultSet. Centralizing this logic avoids duplicating the
     * field mapping code across every retrieval method.</p>
     *
     * @param rs the ResultSet positioned at the row to convert
     * @return a Post object populated with values from the current row
     * @throws SQLException if any column cannot be retrieved from the ResultSet
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
}