
package guiForgttenPassword;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import database.Database;
import entityClasses.User;
import guiUserLogin.ViewUserLogin;

/*******
 * <p>
 * Title: ViewForgottenPassword Class.
 * </p>
 * 
 * <p>
 * Description: The ViewForgottenPassword Page is used to enable a potential
 * user with
 * an One Time Password
 * to change thier password after they have specified the one time password on
 * the standard login
 * page.
 * </p>
 * 
 * <p>
 * Copyright: Jonathan Stark © 2026
 * </p>
 * 
 * @author Jonathan Stark
 * 
 * @version 1.00 2026-02-05 Initial version
 * 
 */

public class ViewForgottenPassword {

  /*-********************************************************************************************
  
  Attributes
  
  */

  // These are the application values required by the user interface

  /**
   * The width for the Forgotten Password window, based on a fraction of main
   * window width.
   */
  private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH * 0.5;

  /**
   * The height for the Forgotten Password window, based on a fraction of main
   * window height.
   */
  private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT * 0.75;

  // This is a simple GUI password change Page, very similar to the pages
  // throughout the program.
  // The only real
  // difference is in this case we also know the user, since it was are
  // referencing the user
  // from the login

  /** The label indicating the purpose of the page: changing the password. */
  protected static Label label_PasswordChange = new Label("Change Password");

  /** The primary password input field. */
  protected static PasswordField text_Password1 = new PasswordField();

  /** The confirmation password input field. */
  protected static PasswordField text_Password2 = new PasswordField();

  /** The button that triggers the password change logic. */
  protected static Button button_ChangePassword = new Button("Change Password"); // This alert is used should the user
                                                                                 // enter two passwords that do not
                                                                                 // match

  // Another alert is used when the password change was successful

  /** Alert displayed when password validation fails (e.g., mismatch). */
  protected static Alert alertPasswordError = new Alert(AlertType.INFORMATION);

  /** Alert displayed when the password has been successfully updated. */
  protected static Alert success = new Alert(AlertType.INFORMATION);

  /** Button to exit the application. */
  protected static Button button_Quit = new Button("Quit");

  // These attributes are used to configure the page and populate it with this
  // user's information

  /** The singleton instance of this view. */
  private static ViewForgottenPassword theView;

  /** Reference to the system database for password updates. */
  private static Database theDatabase = applicationMain.FoundationsMain.database;

  /** The Stage provided by JavaFX for displaying this scene. */
  protected static Stage theStage;

  /** The container holding all GUI components for this page. */
  private static Pane theRootPane;

  /** The user whose password is currently being updated. */
  protected static User theUser;

  /**
   * Access to the User Update page's GUI Widgets
   */
  public static Scene theForgottenPassScene = null;

  /*-********************************************************************************************
  
  Constructors
  
  */

  /**********
   * <p>
   * Method: displayForgotPass(Stage ps, User user)
   * </p>
   * 
   * <p>
   * Description: This method is the single entry point from outside this package
   * to cause the ViewForgottenPassword page to be displayed.
   * 
   * It first sets up very shared attributes so we don't have to pass parameters.
   * 
   * It then checks to see if the page has been setup. If not, it instantiates the
   * class,
   * initializes all the static aspects of the GUI widgets (e.g., location on the
   * page, font,
   * size, and any methods to be performed).
   * 
   * After the instantiation, the code then populates the elements that change
   * based on the user
   * and the system's current state. It then sets the Scene onto the stage, and
   * makes it visible
   * to the user.
   * 
   * @param ps   specifies the JavaFX Stage to be used for this GUI and it's
   *             methods
   * 
   * @param user specifies the user's invitation code for this GUI and it's
   *             methods
   * 
   */
  public static void displayForgotPass(Stage ps, User user) {
    // This is the only way some component of the system can cause a Forgotten
    // Password
    // page to
    // appear. The first time, the class is created and initialized. Every
    // subsequent call it
    // is reused with only the elements that differ being initialized.

    // Establish the references to the GUI and the current user
    theStage = ps; // Save the reference to the Stage for the rest of this package
    theRootPane = new Pane();
    theUser = user;
    if (theView == null)
      theView = new ViewForgottenPassword();

    text_Password1.setText("");
    text_Password2.setText("");

    // Place all of the established GUI elements into the pane
    theRootPane.getChildren().clear();
    theRootPane.getChildren().addAll(text_Password1, text_Password2, button_ChangePassword, button_Quit);

    // Set the title for the window, display the page, and wait for the Admin to do
    // something
    theStage.setTitle("");
    theStage.setScene(theForgottenPassScene);
    theStage.show();
  }

  /**********
   * <p>
   * Constructor: ViewForgottenPassword()
   * </p>
   * 
   * <p>
   * Description: This constructor is called just once, the first time a Password
   * needs to be
   * changed. It establishes all of the common GUI widgets for the page so they
   * are only
   * created once and reused when needed.
   * 
   * 
   * 
   */
  private ViewForgottenPassword() {

    // Create the Pane for the list of widgets and the Scene for the window
    theRootPane = new Pane();
    theForgottenPassScene = new Scene(theRootPane, width, height);

    // Styles the page.
    theForgottenPassScene.getStylesheets().add(
        getClass().getResource("/applicationMain/application.css").toExternalForm());

    // Establish the text input operand field for the password
    setupTextUI(text_Password1, "Arial", 18, 300, Pos.CENTER, 50, 175, true);
    text_Password1.setPromptText("Enter the Password");

    // Establish the text input operand field to confirm the password
    setupTextUI(text_Password2, "Arial", 18, 300, Pos.CENTER, 50, 225, true);
    text_Password2.setPromptText("Enter the Password Again");

    // Makes the password change
    setupButtonUI(button_ChangePassword, "Dialog", 18, 100, Pos.CENTER, 200, 310);
    button_ChangePassword.setOnAction((_) -> {
      ControllerForgottenPassword.doChangePassword();
    });

    setupButtonUI(button_Quit, "Arial", 18, 100, Pos.CENTER, 55, 310);
    button_Quit.setOnAction((_) -> {
      ControllerForgottenPassword.performQuit();
    });

    theRootPane.getChildren().addAll(text_Password1, text_Password2, button_ChangePassword);
  } /*-********************************************************************************************
  
  Helper methods to reduce code length
  
   */

  /**********
   * Private local method to initialize the standard fields for a label
   */

  private void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, double y) {
    l.setFont(Font.font(ff, f));
    l.setMinWidth(w);
    l.setAlignment(p);
    l.setLayoutX(x);
    l.setLayoutY(y);
  }

  /**********
   * Private local method to initialize the standard fields for a button
   * 
   * @param b  The Button object to be initialized
   * @param ff The font to be used
   * @param f  The size of the font to be used
   * @param w  The width of the Button
   * @param p  The alignment (e.g. left, centered, or right)
   * @param x  The location from the left edge (x axis)
   * @param y  The location from the top (y axis)
   */
  private void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, double y) {
    b.setFont(Font.font(ff, f));
    b.setMinWidth(w);
    b.setAlignment(p);
    b.setLayoutX(x);
    b.setLayoutY(y);
  }

  /**********
   * Private local method to initialize the standard fields for a text field
   */
  private void setupTextUI(TextField t, String ff, double f, double w, Pos p, double x, double y, boolean e) {
    t.setFont(Font.font(ff, f));
    t.setMinWidth(w);
    t.setMaxWidth(w);
    t.setAlignment(p);
    t.setLayoutX(x);
    t.setLayoutY(y);
    t.setEditable(e);
  }
}
