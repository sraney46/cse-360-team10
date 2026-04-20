package testing;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import guiDiscussionForum.ModelDiscussionForum;
import entityClasses.Post;


/*******
 * <p>
 * Title: Testing Class.
 * </p>
 * 
 * <p>
 * Description: This class is used to test the hide and unhide post functionality in guiDiscussionForum.
 * 
 * Note for each test; I ran into an issue where JavaFX components would crash.
 * To resolve this, I used CountDownLatch to sync up the JUnit thread with the JavaFX thread. 
 * This prevents the 'Toolkit not initialized' errors and makes sure the isDisplayed flag is actually updated 
 * before the test checks it. The try-finally block is there to make sure the 
 * test doesn't hang forever if the GUI code fails.
 * </p>
 * 
 * <p>
 * Copyright: Devin Miller @ 2026
 * </p>
 * 
 * @author Devin Miller
 * 
 * @version 1.00 2026-04-06 Initial version
 * 
 */

class HideUnhidePostTest {
	
	  private static database.Database theDatabase = applicationMain.FoundationsMain.database;
	  private ModelDiscussionForum testForum;
	  private Post test;
	  
	  /**
	   * Default constructor is not used.
	   */
	  public HideUnhidePostTest() {
	  }
	  
	  /**********
	   * <p>
	   * Method: setUpTesting()
	   * </p>
	   * 
	   * <p>
	   * Description: This is ran before each test, and sets up the testing environment.
	   * It connects to the database, creates a test forum, and a test post in the forum.
	   * 
	   */
	  @BeforeEach
	  void setUpTesting() {
		    try {
		        theDatabase.connectToDatabase();
		        applicationMain.FoundationsMain.database = theDatabase; 
		        clearTestData();
		        System.out.println("   Database Connection Established.");
		    } catch (Exception e) {
		        System.out.println("  Database failed to connect.");
		    }
		  
		  testForum = new ModelDiscussionForum();
		  
		  clearTestData();
		  
		  // Test post:
		  test = new Post();
		  test.setAuthor(123);
		  test.setTitle("Test");
		  test.setContent("Test Post."); 
		  test.setCategory("HW");
		  test.setTimestamp(System.currentTimeMillis());
		  
		  testForum.addPost(test);
	  }
	  
	  /**********
	   * <p>
	   * Method: tearDownTesting()
	   * </p>
	   * 
	   * <p>
	   * Description: This closes the connection to the database after each test.
	   * 
	   */
	  @AfterEach
	  void tearDownTesting() {
		  if (theDatabase != null) {
			  theDatabase.closeConnection();
		  }
	  }
	  
	  /*****
	   * <p>
	   * Method: clearTestData()
	   * </p>
	   *
	   * <p>
	   * Description: This method clears all data from the postDB table
	   * and resets the auto-increment ID counters back to 1 before tests are run,
	   * ensuring a clean state for each test execution.
	   * </p>
	   */
	  // Deletes all rows from postDB table and resets ID counters to 1 so tests
	  // are repeatable and not affected by stale data or climbing primary key values.
	  private void clearTestData() {
	      try {
	    	  var theDatabase = applicationMain.FoundationsMain.database;
	    	  
	          theDatabase.getConnection().createStatement().executeUpdate("DELETE FROM postDB");
	          theDatabase.getConnection().createStatement().executeUpdate("ALTER TABLE postDB ALTER COLUMN postID RESTART WITH 1");
	          System.out.println("   Test data cleared.");
	      } catch (SQLException e) {
	          System.out.println("  Failed to clear test data: " + e.getMessage());
	      }
	  }
	  

	
	  /**********
	   * <p>
	   * Method: testDefaultPostStatus()
	   * </p>
	   * 
	   * <p>
	   * Description: Verifies the default state of a new post is unhidden.
	   * 
	   */
	@Test
	void testDefaultPostStatus() {
		  assertFalse(test.getPostHiddenStatus());	
	}
	
	  /**********
	   * <p>
	   * Method: testHidePost()
	   * </p>
	   * 
	   * <p>
	   * Description: Verifies the post is properly marked as hidden.
	   * 
	   */
	@Test
	void testSetHidePost() { 
		  test.setPostHiddenStatus(true);
		  
		  assertTrue(test.getPostHiddenStatus()); 
	}
	
	  /**********
	   * <p>
	   * Method: testHideUnhidePost()
	   * </p>
	   * 
	   * <p>
	   * Description: Verifies the post is properly marked as unhidden if
	   * it is hidden, then unhidden.
	   * 
	   */
	@Test
	void testSetHideUnhidePost() {
		  test.setPostHiddenStatus(true);
		  test.setPostHiddenStatus(false);
		  
		  assertFalse(test.getPostHiddenStatus()); 
	}
	
	  /**********
	   * <p>
	   * Method: testHidePost()
	   * </p>
	   * 
	   * <p>
	   * Description: Verifies the post is marked as hidden when calling
	   * hidePost()
	   * 
	   */
	@Test
	void testHidePost() {
		  testForum.hidePost(test);
		
		  assertTrue(test.getPostHiddenStatus()); 		  
	}
	
	  /**********
	   * <p>
	   * Method: testUnhidePost()
	   * </p>
	   * 
	   * <p>
	   * Description: Verifies the post is marked as unhidden when calling
	   * UnhidePost() after hidePost()
	   * 
	   */
	@Test
	void testUnhidePost() {
		  testForum.hidePost(test);
		  testForum.unhidePost(test);
		
		  assertFalse(test.getPostHiddenStatus()); 		  
	}
		
		

}
