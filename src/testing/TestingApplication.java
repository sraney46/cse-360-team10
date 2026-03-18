package testing;

import java.util.List;

import guiDiscussionForum.PostList;
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

public class TestingApplication {

  /**
   * Main entry point.
   */
  public static void main(String[] args) {
    runTests();

  }

  /**
   * Run all automated test cases.
   */
  public static void runTests() {
    PostTests tests = new PostTests();
    tests.runAll();
  }

}
