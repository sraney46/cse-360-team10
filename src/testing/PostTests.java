package testing;

import java.util.List;

import database.Database;
import guiDiscussionForum.ModelDiscussionForum;
import entityClasses.Post;
import entityClasses.Reply;
import validation.ValidationResult;

/*******
 * <p>
 * Title: HW2Tests Class
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
    p1.setContent("The test");
    p1.setCategory("HW");
    p1.setTimestamp(System.currentTimeMillis());
    if(pc.addPost(p1)){
      isValid=true;
      assertTrue(isValid, "Post Create - valid input", "Post created successfully with id " + p1.getPostID() + ".");
    }

    // Positive: Read post
    Post read = pc.getPostByID(1);
    assertNotNull(read, "Post Read - found by id", null);
    assertEquals("s1", read.getAuthor(), "Post Read - author");
    assertEquals("HW", read.getCategory(), "Post Read - title");

    // Positive: Update post
    read.setAuthor("Jonathan");
    if(pc.updatePost(read)){
          assertTrue(isValid, "Post Update - valid", "Post " + read.getPostID() + " updated successfully.");

    }
    Post updated =pc.getPostByID(1);
    assertEquals("Jonathan", updated.getAuthor(), "Post Update - author updated");

    // Positive: GetAllPosts
//    List<Post> all = pc.getAllPosts();
//    assertTrue(all.size() == 1, "Post GetAllPosts - count", "size=" + all.size());
/**
    // Positive: Subset by search
    pc.create(new Post(0, "ta1", "What is polymorphism?", "Explain polymorphism in Java.", System.currentTimeMillis()));
    List<Post> subset = pc.getSubset("debugger");
    assertTrue(subset.size() == 1, "Post Subset search - found", "size=" + subset.size());
    subset = pc.getSubset("polymorphism");
    assertTrue(subset.size() == 1, "Post Subset search - polymorphism", "size=" + subset.size());
**/
    // Negative: Create with null author
    Post bad = new Post();
    bad.setAuthor(null);
     p1.setContent("The test");
    p1.setCategory("HW");
    p1.setTimestamp(System.currentTimeMillis());
    if (pc.addPost(bad)){
      isValid= false;
          assertFalse(isValid, "Post Create - null author", "Post created successfully with id "+ bad.getPostID() + ".");

    }
        assertTrue(isValid, "Post Create - error for null author", bad.checkValidation());

/**
    // Negative: Create with empty title
    bad.setAuthor("user");
    bad.setTitle("");
    bad.setContent("Content");
    vr = pc.create(bad);
    assertFalse(vr.isValid(), "Post Create - empty title", vr.getMessage());
    assertTrue(vr.getMessage().contains("Title"), "Post Create - error for empty title", vr.getMessage());

    // Negative: Create with empty content
    bad.setTitle("Title");
    bad.setContent("");
    vr = pc.create(bad);
    assertFalse(vr.isValid(), "Post Create - empty content", vr.getMessage());

    // Negative: Read non-existent
    Post notFound = pc.read(999);
    assertNull(notFound, "Post Read - non-existent id", null);

    // Negative: Delete non-existent
    vr = pc.delete(999);
    assertFalse(vr.isValid(), "Post Delete - non-existent", vr.getMessage());
    assertTrue(vr.getMessage().contains("not found"), "Post Delete - error", vr.getMessage());

    // Positive: Delete existing
    vr = pc.delete(1);
    assertTrue(vr.isValid(), "Post Delete - valid", vr.getMessage());
    assertNull(pc.read(1), "Post Delete - verify removed", null);
    **/
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
    /**
    System.out.println("\n---Reply CRUD and Validation Tests---");
    ReplyCollection rc = new ReplyCollection();
    PostCollection pc = new PostCollection();

    // Create a post first for postId
    Post p = new Post();
    p.setAuthor("student1");
    p.setTitle("Question");
    p.setContent("Content");
    pc.create(p);

    // Positive: Create valid reply
    Reply r1 = new Reply();
    r1.setPostId(1);
    r1.setAuthor("ta1");
    r1.setContent("You can set breakpoints by double-clicking in the left margin of the editor.");
    r1.setTimestamp(System.currentTimeMillis());
    ValidationResult vr = rc.create(r1);
    assertTrue(vr.isValid(), "Reply Create - valid input", vr.getMessage());
    assertEquals(1, rc.size(), "Reply Create - size");

    // Positive: Read reply
    Reply read = rc.read(1);
    assertNotNull(read, "Reply Read - found", null);
    assertEquals(1, read.getPostId(), "Reply Read - postId");
    assertEquals("ta1", read.getAuthor(), "Reply Read - author");

    // Positive: Update reply
    read.setContent("Updated: Double-click in the left margin to set breakpoints.");
    vr = rc.update(read);
    assertTrue(vr.isValid(), "Reply Update - valid", vr.getMessage());

    // Positive: GetAllReplies
    List<Reply> all = rc.getAllReplies();
    assertTrue(all.size() == 1, "Reply GetAllReplies - count", "size=" + all.size());

    // Positive: Subset by postId
    rc.create(new Reply(0, 1, "instructor", "Great question!", System.currentTimeMillis()));
    List<Reply> byPost = rc.getSubset(1);
    assertTrue(byPost.size() == 2, "Reply Subset by postId - count", "size=" + byPost.size());

    // Positive: Subset by search
    List<Reply> bySearch = rc.getSubset("breakpoints");
    assertTrue(bySearch.size() >= 1, "Reply Subset search - found", "size=" + bySearch.size());

    // Negative: Create with invalid postId (0)
    Reply bad = new Reply();
    bad.setPostId(0);
    bad.setAuthor("user");
    bad.setContent("Content");
    vr = rc.create(bad);
    assertFalse(vr.isValid(), "Reply Create - invalid postId 0", vr.getMessage());
    assertTrue(vr.getMessage().contains("Post ID"), "Reply Create - error", vr.getMessage());

    // Negative: Create with empty content
    bad.setPostId(1);
    bad.setContent("");
    vr = rc.create(bad);
    assertFalse(vr.isValid(), "Reply Create - empty content", vr.getMessage());

    // Negative: Read non-existent
    Reply notFound = rc.read(999);
    assertNull(notFound, "Reply Read - non-existent", null);

    // Positive: Delete
    vr = rc.delete(1);
    assertTrue(vr.isValid(), "Reply Delete - valid", vr.getMessage());
    **/
  }


  /*****
   * <p>
   * Method: runSubsetTests()
   * </p>
   * 
   * <p>
   * Description: This method runs all test cases for empty and large subset behavior.
   * </p>
   */
  // This method runs all test cases for empty and large subset behavior.
  public void runSubsetTests() {
    /**
    System.out.println("\n---Subset Tests---");
    PostCollection pc = new PostCollection();
    ReplyCollection rc = new ReplyCollection();

    // Empty subset - search with no matches
    List<Post> empty = pc.getSubset("nonexistentxyz");
    assertTrue(empty.isEmpty(), "Post Subset - empty when no match", "size=" + empty.size());

    // Empty subset - empty search term
    List<Post> emptySearch = pc.getSubset("");
    assertTrue(emptySearch.isEmpty(), "Post Subset - empty for empty search term", null);

    // Empty subset - null search term
    List<Post> nullSearch = pc.getSubset(null);
    assertTrue(nullSearch.isEmpty(), "Post Subset - empty for null search", null);

    // Reply subset for non-existent postId returns empty
    List<Reply> emptyReplies = rc.getSubset(999);
    assertTrue(emptyReplies.isEmpty(), "Reply Subset - empty for non-existent postId", null);
    **/
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
        
        System.out.println("   Database Connection Established.");
    } catch (Exception e) {
        System.out.println("  Database failed to connect.");
    }
    System.out.println("HW2 Test Suite - Post, Reply, PostCollection, ReplyCollection");
    runPostTests();
    runReplyTests();
    runSubsetTests();
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
   */
  // This getter method gets the count of failed tests.
  public int getFailed() {
    return failed;
  }
}
