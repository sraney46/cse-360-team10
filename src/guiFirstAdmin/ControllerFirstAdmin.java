package guiFirstAdmin;

import java.sql.SQLException;
import database.Database;
import entityClasses.User;
import guiUserLogin.ViewUserLogin;
import guiUserUpdate.ViewUserUpdate;
import javafx.stage.Stage;
import validation.PasswordValidator;
import validation.ValidationResult;

/*******
 * <p>
 * Title: ControllerFirstAdmin Class.
 * </p>
 * 
 * <p>
 * Description: ControllerFirstAdmin class provides the controller actions based
 * on the user's
 * use of the JavaFX GUI widgets defined by the View class.
 * 
 * This page contains a number of buttons that have not yet been implemented.
 * WHhen those buttons
 * are pressed, an alert pops up to tell the user that the function associated
 * with the button has
 * not been implemented. Also, be aware that What has been implemented may not
 * work the way the
 * final product requires and there maybe defects in this code.
 * 
 * The class has been written assuming that the View or the Model are the only
 * class methods that
 * can invoke these methods. This is why each has been declared at "protected".
 * Do not change any
 * of these methods to public.
 * </p>
 * 
 * <p>
 * Copyright: Lynn Robert Carter © 2025
 * </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00 2025-08-17 Initial version
 * @version 1.01 2026-01-19 User name input validation controller logic added
 * 
 */

public class ControllerFirstAdmin {
  /*-********************************************************************************************
  
  The controller attributes for this page
  
  This controller is not a class that gets instantiated.  Rather, it is a collection of protected
  static methods that can be called by the View (which is a singleton instantiated object) and 
  the Model is often just a stub, or will be a singleton instantiated object.
  
  */

  private static String adminUsername = "";
  private static String adminPassword1 = "";
  private static String adminPassword2 = "";
  protected static Database theDatabase = applicationMain.FoundationsMain.database;

  /*-********************************************************************************************
  
  The User Interface Actions for this page
  
  */

  /**
   * Default constructor is not used.
   */
  public ControllerFirstAdmin() {
  }

  /**********
   * <p>
   * Method: setAdminUsername()
   * </p>
   * 
   * <p>
   * Description: This method is called when the user adds text to the username
   * field in the
   * View. A private local copy of what was last entered is kept here.
   * </p>
   * 
   */
  protected static void setAdminUsername() {
    adminUsername = ViewFirstAdmin.text_AdminUsername.getText();
  }

  /**********
   * <p>
   * Method: setAdminPassword1()
   * </p>
   * 
   * <p>
   * Description: This method is called when the user adds text to the password 1
   * field in
   * the View. A private local copy of what was last entered is kept here.
   * </p>
   * 
   */
  protected static void setAdminPassword1() {
    adminPassword1 = ViewFirstAdmin.text_AdminPassword1.getText();
    ViewFirstAdmin.label_PasswordsDoNotMatch.setText("");
  }

  /**********
   * <p>
   * Method: setAdminPassword2()
   * </p>
   * 
   * <p>
   * Description: This method is called when the user adds text to the password 2
   * field in
   * the View. A private local copy of what was last entered is kept here.
   * </p>
   * 
   */
  protected static void setAdminPassword2() {
    adminPassword2 = ViewFirstAdmin.text_AdminPassword2.getText();
    ViewFirstAdmin.label_PasswordsDoNotMatch.setText("");
  }

  /**********
   * <p>
   * Method: doSetupAdmin()
   * </p>
   * 
   * <p>
   * Description: This method is called when the user presses the button to set up
   * the Admin
   * account. It start by trying to establish a new user and placing that user
   * into the
   * database. If that is successful, we proceed to the login page.
   * </p>
   * 
   */
  protected static void doSetupAdmin(Stage ps, int r) {

    // Make sure user name is valid
    String checkUser = ModelFirstAdmin.checkForValidUserName(adminUsername);
    if (checkUser.compareTo("") != 0) {
      ViewFirstAdmin.text_AdminUsername.setText("");
      ViewFirstAdmin.label_PasswordsDoNotMatch.setText(
          checkUser + "\nThe username is invalid. Please try again!");
    }
    
    // Make sure password boxes are not empty
    else if (adminPassword1.compareTo("") == 0) {
      ViewFirstAdmin.label_PasswordsDoNotMatch.setText(
          "Please Enter a password");
    } else {
    	
	// check password fulfills requirements
	PasswordValidator validator = new PasswordValidator();
    ValidationResult result = validator.validate(adminPassword1);

    if (!result.isValid()) {
    	ViewFirstAdmin.text_AdminPassword1.setText("");
    	ViewFirstAdmin.text_AdminPassword2.setText("");
        ViewFirstAdmin.alertUsernamePasswordError.setContentText(result.getMessage());
        ViewFirstAdmin.alertUsernamePasswordError.showAndWait();
        return;
    }
    	
	  // Make sure the two passwords are the same
	  if (adminPassword1.compareTo(adminPassword2) == 0) {
	    // Create the passwords and proceed to the user home page
	    User user = new User(adminUsername, adminPassword1, "", "", "", "", "", "", true, false,
	        false, false);
	    try {
	      // Create a new User object with admin role and register in the database
	      theDatabase.register(user);
	    } catch (SQLException e) {
	      System.err.println("*** ERROR *** Database error trying to register a user: " +
	          e.getMessage());
	      e.printStackTrace();
	      System.exit(0);
	    }
	
	    // User was established in the database, so navigate to back to the login Page
	    guiUserLogin.ViewUserLogin.displayUserLogin(ViewFirstAdmin.theStage);
	  } else {
	    // The two passwords are NOT the same, so clear the passwords, explain the
	    // passwords
	    // must be the same, and clear the message as soon as the first character is
	    // typed.
	    ViewFirstAdmin.text_AdminPassword1.setText("");
	    ViewFirstAdmin.text_AdminPassword2.setText("");
	    ViewFirstAdmin.label_PasswordsDoNotMatch.setText(
	        "The two passwords must match. Please try again!");
	  }
	}

  }

  /**********
   * <p>
   * Method: performQuit()
   * </p>
   * 
   * <p>
   * Description: This method terminates the execution of the program. It leaves
   * the
   * database in a state where the normal login page will be displayed when the
   * application is
   * restarted.
   * </p>
   * 
   */
  protected static void performQuit() {
    System.out.println("Perform Quit");
    System.exit(0);
  }
}
