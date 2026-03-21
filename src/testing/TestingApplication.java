package testing;

import java.util.List;

//import guiDiscussionForum.PostList;
import entityClasses.Post;
import validation.ValidationResult;

/*******
 * <p>
 * Title: HW2Application Class
 * </p>
 *
 * <p>
 * Description: Standalone HW2 application demonstrating CRUD and input
 * validation for Post, Reply, PostCollection, and ReplyCollection. Runs test
 * cases
 * </p>
 *
 * 
 * @author Jonathan Stark
 * @version 1.00 2026-02-22 HW2 Standalone application
 *
 */

/*****
 * <p>
 * Method: TestingApplication()
 * </p>
 * 
 * <p>
 * Description: This method initiates the running of the tests by calling runTests().
 * </p>
 * 
 */
// Starts the testing application by calling runTests().
public class TestingApplication {

  /**
   * Main entry point.
   */
  public static void main(String[] args) {
    runTests();

  }

  /*****
   * <p>
   * Method: runTests()
   * </p>
   * 
   * <p>
   * Description: Runs all of the tests.
   * </p>
   * 
   */
  // Runs all of the tests.
  public static void runTests() {
    PostTests tests = new PostTests();
    tests.runAll();
  }

}
