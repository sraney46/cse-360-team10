package testing;

import java.util.List;

import database.Database;
import guiDiscussionForum.ModelDiscussionForum;
import entityClasses.Post;
import entityClasses.Reply;
import validation.ValidationResult;
import java.sql.SQLException;

/*******
 * <p>
 * Title: PostTests Class
 * </p>
 *
 * <p>
 * Description: Test cases for Post and Reply CRUD, input validation, and
 * collection subset operations. Tests cover positive and negative scenarios
 * with helpful error message verification. Runs independently to verify
 * requirements.
 * </p>
 *
 *
 * 
 * @author Jonathan Stark
 * @version 1.00 2026-02-22 HW2 Test cases
 *
 */

public class PostTests {

  private int passed;
  private int failed;
  private boolean isValid;
  private static database.Database theDatabase = applicationMain.FoundationsMain.database;


  /*****
   * <p>
   * Method: PostTests()
   * </p>
   * 
   * <p>
   * Description: This constructor is used to establish PostTest objects.
   * </p>
   */
  // Constructor to initialize a PostTests object that tracks the passes and fails for each test run.
  public PostTests() {
    this.passed = 0;
    this.failed = 0;
  }
  
  /*****
   * <p>
   * Method: clearTestData()
   * </p>
   *
   * <p>
   * Description: This method clears all data from all database tables
   * and resets the auto-increment ID counters back to 1 before tests are run,
   * ensuring a clean state for each test execution.
   * </p>
   */
  // Deletes all rows from all tables and resets ID counters to 1 so tests
  // are repeatable and not affected by stale data or climbing primary key values.
  private void clearTestData() {
      try {
          theDatabase.getConnection().createStatement().executeUpdate("DELETE FROM replyDB");
          theDatabase.getConnection().createStatement().executeUpdate("DELETE FROM postDB");
          theDatabase.getConnection().createStatement().executeUpdate("DELETE FROM InvitationCodes");
          theDatabase.getConnection().createStatement().executeUpdate("DELETE FROM userDB");
          theDatabase.getConnection().createStatement().executeUpdate("ALTER TABLE replyDB ALTER COLUMN replyID RESTART WITH 1");
          theDatabase.getConnection().createStatement().executeUpdate("ALTER TABLE postDB ALTER COLUMN postID RESTART WITH 1");
          theDatabase.getConnection().createStatement().executeUpdate("ALTER TABLE userDB ALTER COLUMN id RESTART WITH 1");
          System.out.println("   Test data cleared.");
      } catch (SQLException e) {
          System.out.println("  Failed to clear test data: " + e.getMessage());
      }
  }

  /*****
   * <p>
   * Method: void assertTrue(boolean condition, String testName, String message)
   * </p>
   * 
   * <p>
   * Description: This method prints the result and details of a test.
   * </p>
   * 
   * @param condition is the pass/fail result of the test.
   * 
   * @param testName is the name of the test.
   * 
   * @param message contains the content of the print statement. 
   * 
   */
  // Prints the result and details of a test
  private void assertTrue(boolean condition, String testName, String message) {
    if (condition) {
      passed++;
      System.out.println("  [PASS] " + testName + (message != null ? ": " + message : ""));
    } else {
      failed++;
      System.out.println("  [FAIL] " + testName + ": " + (message != null ? message : "expected true"));
    }
  }

  /*****
   * <p>
   * Method: void assertFalse(boolean condition, String testName, String message)
   * </p>
   * 
   * <p>
   * Description: This sets the result of a test to false, then calls assertTrue to print a failed test message.
   * </p>
   * 
   * @param condition is the pass/fail result of the test.
   * 
   * @param testName is the name of the test.
   * 
   * @param message contains the content of the print statement. 
   * 
   */
  // This sets the result of a test to false, then calls assertTrue to print a failed test message.
  private void assertFalse(boolean condition, String testName, String message) {
    assertTrue(!condition, testName, message);
  }

  
  /*****
   * <p>
   * Method: void assertNotNull(Object obj, String testName, String message)
   * </p>
   * 
   * <p>
   * Description: This method is used to show that a searched object (Post/Reply) was found.
   * </p>
   * 
   * @param obj is the object (Post/Reply) searched for.
   * 
   * @param testName is the name of the test.
   * 
   * @param message contains the content of the print statement. 
   * 
   */
  // This method is used to show that a searched object (Post/Reply) was found.
  private void assertNotNull(Object obj, String testName, String message) {
    assertTrue(obj != null, testName, message);
  }

  /*****
   * <p>
   * Method: void assertNull(Object obj, String testName, String message)
   * </p>
   * 
   * <p>
   * Description: This method is used to show that a searched object (Post/Reply) was not found.
   * </p>
   * 
   * @param obj is the object (Post/Reply) searched for.
   * 
   * @param testName is the name of the test.
   * 
   * @param message contains the content of the print statement. 
   * 
   */
  // This method is used to show that a searched object (Post/Reply) was not found.
  private void assertNull(Object obj, String testName, String message) {
    assertTrue(obj == null, testName, message);
  }

  /*****
   * <p>
   * Method: void assertEquals(Object obj, String testName, String message)
   * </p>
   * 
   * <p>
   * Description: This method is used to show that an object is what we expect it to be.
   * </p>
   * 
   * @param expected is the object is the expected object.
   * 
   * @param actual is the actual object found.
   * 
   * @param testName is the name of the test.
   * 
   * @param message contains the content of the print statement. 
   * 
   */
  // This method is used to show that a searched object (postID) was found.
  private void assertEquals(Object expected, Object actual, String testName) {
    boolean eq = (expected == null && actual == null) || (expected != null && expected.equals(actual));
    assertTrue(eq, testName, "expected=" + expected + ", actual=" + actual);
  }


  /*****
   * <p>
   * Method: runPostTests()
   * </p>
   * 
   * <p>
   * Description: This method runs all test cases for Post CRUD and validation.
   * </p>
   */
  // This method runs all test cases for Post CRUD and validation.
  
  public void runPostTests() {
    System.out.println("\n ---Post CRUD and Validation Tests---");
    ModelDiscussionForum pc = new ModelDiscussionForum();
    isValid=false;

   

    // Positive: Create valid post
    entityClasses.Post p1 = new entityClasses.Post();
    p1.setAuthor("s1");
    p1.setTitle("Title");
    p1.setContent("The test");
    p1.setCategory("HW");
    p1.setTimestamp(System.currentTimeMillis());
    if(pc.addPost(p1)){
      isValid=true;
      assertTrue(isValid, "Post Create - valid input", "Post created successfully with id " + p1.getPostID() + ".");
    }

    // negative: Create blank post
    entityClasses.Post p2 = new entityClasses.Post();
    p2.setAuthor("");
    p2.setTitle("");
    p2.setContent("");
    p2.setCategory("");
    p2.setTimestamp(System.currentTimeMillis());
    if(!pc.addPost(p2)){
      isValid=false;
      assertFalse(isValid, "Post Create - invalid input", "Post created unsuccessfully " + "requied fileds are blank.");
    }


    // Positive: Read post
    Post read = pc.getPostByID(p1.getPostID());
    assertNotNull(read, "Post Read - found by id", null);
    assertEquals("s1", read.getAuthor(), "Post Read - author");
    assertEquals("HW", read.getCategory(), "Post Read - title");

    // Positive: Update post
    read.setAuthor("Jonathan");
    if(pc.updatePost(read)){
      isValid=true;
          assertTrue(isValid, "Post Update - valid", "Post " + read.getPostID() + " updated successfully.");

    }
    Post updated =pc.getPostByID(p1.getPostID());
    assertEquals("Jonathan", updated.getAuthor(), "Post Update - author updated");

     // Negative: Update post
    read.setAuthor("");
    if(!pc.updatePost(read)){
      isValid=false;
          assertFalse(isValid, "Post not updateded","Post " + read.getPostID() + " updated unsuccessfully.");

    }

    /// Empty Title Test   
    Post emptyTitle = new Post();
    emptyTitle.setAuthor("s1");
    emptyTitle.setTitle("");
    emptyTitle.setContent("hello");
    emptyTitle.setCategory("HW");
    emptyTitle.setTimestamp(System.currentTimeMillis());

    boolean createdEmptyTitle = pc.addPost(emptyTitle);
    System.out.println("  empty Tile -> created=" + createdEmptyTitle
    	    + ", validation='" + emptyTitle.checkValidation() + "'");
    assertFalse(createdEmptyTitle, "Post Create - empty title", emptyTitle.checkValidation());
    
 // Empty Body Post Test
    Post emptyBody = new Post();
    emptyBody.setAuthor("s1");
    emptyBody.setTitle("Title");
    emptyBody.setContent("");
    emptyBody.setCategory("HW");
    emptyBody.setTimestamp(System.currentTimeMillis());

    boolean createdEmptyBody = pc.addPost(emptyBody);
    System.out.println("  empty body -> created=" + createdEmptyBody
            + ", validation='" + emptyBody.checkValidation() + "'");
    assertFalse(createdEmptyBody, "Post Create - empty body", emptyBody.checkValidation());
    
    // Null Body Test
    
    Post nullBody = new Post();
    nullBody.setAuthor("s1");
    nullBody.setTitle("Title");
    nullBody.setContent(null);
    nullBody.setCategory("HW");
    nullBody.setTimestamp(System.currentTimeMillis());

    boolean createdNullBody = pc.addPost(nullBody);
    System.out.println("  Debug null body -> created=" + createdNullBody
            + ", validation='" + nullBody.checkValidation() + "'");
    assertFalse(createdNullBody, "Post Create - null body", nullBody.checkValidation());
    
    // Max Length Body Test
    
    Post maxBody = new Post();
    maxBody.setAuthor("s1");
    maxBody.setTitle("Title");
    maxBody.setContent("1a1B2c3D4e5F6g7H8i9J0k1L2m3N4o5P6q7R8s9T0u1V2w3X4y5Z6a1B2c3D4e5F6g7H8i9J0k1L2m3N4o5P6q7R8s9T0u1V2w3X4");
    maxBody.setCategory("HW");
    maxBody.setTimestamp(System.currentTimeMillis());

    boolean createdMaxBody = pc.addPost(maxBody);
    System.out.println("  max body -> created=" + createdMaxBody
            + ", validation='" + maxBody.checkValidation() + "1a1B2c3D4e5F6g7H8i9J0k1L2m3N4o5P6q7R8s9T0u1V2w3X4y5Z6a1B2c3D4e5F6g7H8i9J0k1L2m3N4o5P6q7R8s9T0u1V2w3X4");
    assertFalse(createdMaxBody, "Post Create - max body", maxBody.checkValidation());

    // Special character Test
    
    Post special = new Post();
    special.setAuthor("@");
    special.setTitle("Title");
    special.setContent("Hello");
    special.setCategory("HW");
    special.setTimestamp(System.currentTimeMillis());

    boolean createdSpecial = pc.addPost(special);
    System.out.println("  max body -> created=" + createdSpecial
            + ", validation='" + special.checkValidation() + "@");
    assertFalse(createdSpecial, "Post Create - special character", special.checkValidation());

    
    // Negative: Create with empty author
    Post bad = new Post();
    bad.setAuthor("");
    bad.setTitle("Title");
    bad.setContent("The test");
    bad.setCategory("HW");
    bad.setTimestamp(System.currentTimeMillis());

    boolean createdBad = pc.addPost(bad);
    System.out.println("   empty author -> created=" + createdBad
            + ", validation='" + bad.checkValidation() + "'");
    assertFalse(createdBad, "Post Create - empty author", bad.checkValidation());

    // Negative: Create with null author
    Post nullAuthor = new Post();
    nullAuthor.setAuthor(null);
    nullAuthor.setTitle("Title");
    nullAuthor.setContent("The test");
    nullAuthor.setCategory("HW");
    nullAuthor.setTimestamp(System.currentTimeMillis());

    boolean createdNullAuthor= pc.addPost(nullAuthor);
    System.out.println("  null author -> created=" + createdNullAuthor 
            + ", validation='" + nullAuthor.checkValidation() + "'");
    assertFalse(createdNullAuthor, "Post Create - null author", nullAuthor.checkValidation());

    
    // Negative: Read non-existent
    Post notFound = pc.getPostByID(999);
    assertNull(notFound, "Post Read - non-existent id", null);

    // Positive: Delete existing
    boolean delete = pc.hardDeletePost(1);
    if(delete){
      isValid=true;
      assertTrue(isValid, "Post Delete - valid", "post deleted successfully");

    }
        assertNull(pc.getPostByID(1), "Post Delete - verify removed", null);

    
  }


  /*****
   * <p>
   * Method: runReplyTests()
   * </p>
   * 
   * <p>
   * Description: This method runs all test cases for Reply CRUD and validation.
   * </p>
   */
  // This method runs all test cases for Reply CRUD and validation.
  public void runReplyTests() {
    
    System.out.println("\n---Reply CRUD and Validation Tests---");
    ModelDiscussionForum rc = new ModelDiscussionForum();
   
     entityClasses.Post p1 = new entityClasses.Post();
    p1.setAuthor("s1");
    p1.setTitle("Title");
    p1.setContent("The test");
    p1.setCategory("HW");
    p1.setTimestamp(System.currentTimeMillis());
    
    rc.addPost(p1);
   

   
    // Positive: Create valid reply
    
    entityClasses.Reply r1 = new Reply();
    r1.setAuthor("ta1");
    r1.setAuthorRole("admin");
    r1.setContent("This is a reply");
    r1.setTimestamp(System.currentTimeMillis());
    boolean createReply= rc.addReply(r1,2);
    if(createReply){
      isValid=true;
      assertTrue(isValid, "Reply Create - valid input", "Post reply created successfully for post "+ r1.getPostID() + " Reply ID=" + r1.getReplyID() + ".");
    }

    // Positive: Read reply
    int idToFind = r1.getReplyID();
    Reply read = rc.getReplyByID(idToFind);
    assertNotNull(read, "Reply Read - found", null);
    assertEquals(2, read.getPostID(), "Reply Read - postId");
    assertEquals("ta1", read.getAuthor(), "Reply Read - author");

    // Positive: Update reply
    read.setContent("Updated: This is updated");
    if(rc.updateReply(read)){
      isValid=true;
          assertTrue(isValid, "Reply Update - valid", "reply content Updated.");
              assertEquals("Updated: This is updated", read.getContent(), "Reply Read - Content");


    }

    // Negative: Create with invalid postId 
     entityClasses.Reply r2 = new Reply();
    r2.setAuthor("ta1");
    r2.setAuthorRole("admin");
    r2.setContent("This is a reply");
    r2.setTimestamp(System.currentTimeMillis());
    boolean createInReply= rc.addReply(r2,1);
    if(!createInReply){
      isValid=false;
      assertFalse(isValid, "Reply Create - invalid input", "Post reply created unsuccessfully for post "+ r1.getPostID() + " PostID does not exist.");
    }

    // Positive: Delete
    if(rc.deleteReply(2,1)){
      isValid=true;
      assertTrue(isValid, "Reply Delete - valid", "Reply deleted");

    }
    
  }


 
  /*****
   * <p>
   * Method: runSubsetTests()
   * </p>
   * 
   * <p>
   * Description: This method runs all tests and prints a summary.
   * </p>
   */
  // This method runs all tests and prints a summary.
  public void runAll() {
theDatabase = new database.Database();
    try {
        theDatabase.connectToDatabase();
        applicationMain.FoundationsMain.database = theDatabase; 
        clearTestData();
        System.out.println("   Database Connection Established.");
    } catch (Exception e) {
        System.out.println("  Database failed to connect.");
    }
    System.out.println("CRUD Test Suite - Post, Reply");
    runPostTests();
    runReplyTests();
   
    System.out.println("\n--- Summary ---");
    System.out.println("  Passed: " + passed);
    System.out.println("  Failed: " + failed);
    System.out.println("  Total:  " + (passed + failed));
    if (failed == 0) {
      System.out.println("\nAll tests PASSED.");
    } else {
      System.out.println("\n" + failed + " test(s) FAILED.");
    }
  }


  /*****
   * <p>
   * Method: getPassed()
   * </p>
   * 
   * <p>
   * Description: This getter method gets the count of passed tests.
   * </p>
   * 
   * @return the count of passed tests.
   */
  // This getter method gets the count of passed tests.
  public int getPassed() {
    return passed;
  }

  /*****
   * <p>
   * Method: getFailed()
   * </p>
   * 
   * <p>
   * Description: This getter method gets the count of failed tests.
   * </p>
   * 
   * @return the number of failed tests.
   */
  // This getter method gets the count of failed tests.
  public int getFailed() {
    return failed;
  }
}
