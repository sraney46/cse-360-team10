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

public class ControllerForgottenPassword {

  private static Alert alertPasswordError = new Alert(AlertType.INFORMATION);
  private static Alert success = new Alert(AlertType.INFORMATION);

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

  protected static void doChangePassword() {
    String password_1 = ViewForgottenPassword.text_Password1.getText();
    String password_2 = ViewForgottenPassword.text_Password2.getText();
    String username = ViewForgottenPassword.theUser.getUserName();

    if (!password_1.equals(password_2)) {
      alertPasswordError.setContentText("Passwords do not match. Try again.");
      alertPasswordError.showAndWait();
      return;
    }
    if (password_1.isEmpty() || password_2.isEmpty()) {
      alertPasswordError.setContentText("Passwords can not be empty. Try again.");
      alertPasswordError.showAndWait();
      return;

    } else {
      theDatabase.updatePassword(username, password_1);
      ViewForgottenPassword.theUser.setPassword(password_1);
      theDatabase.clearOneTimePassword(username);
      success.setContentText("Your password has been changed.");
      Optional<ButtonType> result = success.showAndWait();
      if (result.isPresent() && result.get() == ButtonType.OK) {
        guiUserLogin.ViewUserLogin.displayUserLogin(ViewForgottenPassword.theStage);
      }

    }

  }

  protected static void performQuit() {

    System.exit(0);
  }
}
