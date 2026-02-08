package guiForgttenPassword;

import java.sql.SQLException;

import database.Database;
import entityClasses.User;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import guiFirstAdmin.ModelFirstAdmin;
import guiUserLogin.ViewUserLogin;
import javafx.scene.control.Dialog;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.util.Optional;

/*******
 * <p>
 * Title: ControllerForgottenPassword Class.
 * </p>
 * 
 * <p>
 * Description: ControllerForgottenPassword class provides the controller
 * actions based
 * on the user's
 * use of the JavaFX GUI widgets defined by the View class.
 * 
 * This page contains buttons
 * WHhen those buttons that
 * are pressed, an alert pops up to tell the user that the change password
 * function associated
 * with the button has been done.
 * The class has been written assuming that the View or the Model are the only
 * class methods that
 * can invoke these methods. This is why each has been declared at "protected".
 * Do not change any
 * of these methods to public.
 * </p>
 * 
 * <p>
 * Copyright: Jonathan Stark © 2026
 * </p>
 * 
 * @author Jonathan Stark
 *
 * @version 1.00 2026-02-04 Initial version
 * 
 */

public class ControllerForgottenPassword {

  /*-********************************************************************************************
  
  The User Interface Actions for this page
  
  This controller is not a class that gets instantiated.  Rather, it is a collection of protected
  static methods that can be called by the View (which is a singleton instantiated object) and 
  the Model is often just a stub, or will be a singleton instantiated object.
  
  */

  /**
   * Default constructor is not used.
   */
  public ControllerForgottenPassword() {
  }

  private static Database theDatabase = applicationMain.FoundationsMain.database;

  /**********
   * <p>
   * Method: doChangePassword()
   * </p>
   * 
   * <p>
   * Description: This method is called when the user adds text to the password
   * field in the
   * View.
   * </p>
   * 
   */

  protected static void doChangePassword() {
    // sets the password from the user input and username from the Initial login
    // page
    String password_1 = ViewForgottenPassword.text_Password1.getText();
    String password_2 = ViewForgottenPassword.text_Password2.getText();
    String username = ViewForgottenPassword.theUser.getUserName();

    // Checks to see if the passwords are the same and not empty
    if (!password_1.equals(password_2)) {
      ViewForgottenPassword.alertPasswordError.setContentText("Passwords do not match. Try again.");
      ViewForgottenPassword.alertPasswordError.showAndWait();
      return;
    }
    if (password_1.isEmpty() || password_2.isEmpty()) {
      ViewForgottenPassword.alertPasswordError.setContentText("Passwords can not be empty. Try again.");
      ViewForgottenPassword.alertPasswordError.showAndWait();
      return;

      // changes the users password
    } else {
      theDatabase.updatePassword(username, password_1);
      ViewForgottenPassword.theUser.setPassword(password_1);
      theDatabase.clearOneTimePassword(username);
      ViewForgottenPassword.success.setContentText("Your password has been changed.");
      Optional<ButtonType> result = ViewForgottenPassword.success.showAndWait();
      if (result.isPresent() && result.get() == ButtonType.OK) {
        guiUserLogin.ViewUserLogin.displayUserLogin(ViewForgottenPassword.theStage);
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

    // exits the program
    System.exit(0);
  }
}
