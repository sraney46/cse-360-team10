
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
 * Title: ViewNewAccount Class.
 * </p>
 * 
 * <p>
 * Description: The ViewNewAccount Page is used to enable a potential user with
 * an invitation
 * code to establish an account after they have specified an invitation code on
 * the standard login
 * page.
 * </p>
 * 
 * <p>
 * Copyright: Lynn Robert Carter © 2025
 * </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00 2025-08-19 Initial version
 * 
 */

public class ViewForgottenPassword {

  /*-********************************************************************************************
  
  Attributes
  
  */

  // These are the application values required by the user interface

  private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH * 0.5;
  private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT * 0.75;

  // This is a simple GUI login Page, very similar to the FirstAdmin login page.
  // The only real
  // difference is in this case we also know an email address, since it was used
  // to send the
  // invitation to the potential user.
  private static Label label_ApplicationTitle = new Label("Foundation Application Account Setup Page");
  protected static Label label_NewUserCreation = new Label("Change Password");
  protected static PasswordField text_Password1 = new PasswordField();
  protected static PasswordField text_Password2 = new PasswordField();
  protected static Button button_UserSetup = new Button("Change Password");
  protected static TextField text_Invitation = new TextField();
  // This alert is used should the user enter two passwords that do not match
  protected static Alert alertUsernamePasswordError = new Alert(AlertType.INFORMATION);

  protected static Button button_Quit = new Button("Quit");

  // These attributes are used to configure the page and populate it with this
  // user's information
  private static ViewForgottenPassword theView; // Is instantiation of the class needed?

  // Reference for the in-memory database so this package has access
  private static Database theDatabase = applicationMain.FoundationsMain.database;

  protected static Stage theStage; // The Stage that JavaFX has established for us
  private static Pane theRootPane; // The Pane that holds all the GUI widgets
  protected static User theUser; // The current logged in User

  protected static String theInvitationCode; // The invitation code links to an email address
                                             // and a role for this user
  protected static String emailAddress; // Established here for use by the controller
  protected static String theRole; // Established here for use by the controller
  protected static boolean checkValidTimer; // Established here for use by the controller
  public static Scene theNewAccountScene = null; // Access to the User Update page's GUI Widgets

  /*-********************************************************************************************
  
  Constructors
  
  */

  /**********
   * <p>
   * Method: displayNewAccount(Stage ps, String ic)
   * </p>
   * 
   * <p>
   * Description: This method is the single entry point from outside this package
   * to cause
   * the NewAccount page to be displayed.
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
   * @param ps specifies the JavaFX Stage to be used for this GUI and it's methods
   * 
   * @param ic specifies the user's invitation code for this GUI and it's methods
   * 
   */
  public static void displayNewAccount(Stage ps,User user) {
    // This is the only way some component of the system can cause a New User
    // Account page to
    // appear. The first time, the class is created and initialized. Every
    // subsequent call it
    // is reused with only the elements that differ being initialized.

    // Establish the references to the GUI and the current user
    theStage = ps; // Save the reference to the Stage for the rest of this package
    theRootPane = new Pane();
    theUser=user;
    if (theView == null)
      theView = new ViewForgottenPassword();

    text_Password1.setText(""); // appear for a new user
    text_Password2.setText("");

    // Fetch the role for this user

    // Validate the timer on the user code to check for expiration
    // checkValidTimer = theDatabase.validateUserCodeTime(theInvitationCode);

    // if (theRole.length() == 0 || !checkValidTimer) {// If there is an issue with
    // the invitation code, display a
    // alertInvitationCodeIsInvalid.showAndWait(); // dialog box saying that are
    // when it it
    // return; // acknowledged, return so the proper code can be entered
    // }

    // Place all of the established GUI elements into the pane
    theRootPane.getChildren().clear();
    theRootPane.getChildren().addAll(label_NewUserCreation,
        text_Password1, text_Password2, button_UserSetup, button_Quit);

    // Set the title for the window, display the page, and wait for the Admin to do
    // something
    theStage.setTitle("");
    theStage.setScene(theNewAccountScene);
    theStage.show();
  }

  /**********
   * <p>
   * Constructor: ViewNewAccount()
   * </p>
   * 
   * <p>
   * Description: This constructor is called just once, the first time a new
   * account needs to
   * be created. It establishes all of the common GUI widgets for the page so they
   * are only
   * created once and reused when needed.
   * 
   * The do
   * 
   */
  private ViewForgottenPassword() {

    // Create the Pane for the list of widgets and the Scene for the window
    theRootPane = new Pane();
    theNewAccountScene = new Scene(theRootPane, width, height);

    theNewAccountScene.getStylesheets().add(
        getClass().getResource("/applicationMain/application.css").toExternalForm());

    // Label to display the welcome message for the new user
    setupLabelUI(label_NewUserCreation, "Arial", 32, width, Pos.CENTER, 0, 50);

    // Establish the text input operand field for the password
    setupTextUI(text_Password1, "Arial", 18, 300, Pos.CENTER, 50, 175, true);
    text_Password1.setPromptText("Enter the Password");

    // Establish the text input operand field to confirm the password
    setupTextUI(text_Password2, "Arial", 18, 300, Pos.CENTER, 50, 225, true);
    text_Password2.setPromptText("Enter the Password Again");

    // Set up the account creation and login
    setupButtonUI(button_UserSetup, "Dialog", 18, 100, Pos.CENTER, 200, 310);
    button_UserSetup.setOnAction((_) -> {
      ControllerForgottenPassword.doChangePassword();
    });

    setupButtonUI(button_Quit, "Arial", 18, 100, Pos.CENTER, 55, 310);
    button_Quit.setOnAction((_) -> {
      ControllerForgottenPassword.performQuit();
    });

    theRootPane.getChildren().addAll(label_NewUserCreation, text_Password1, text_Password2, button_UserSetup);
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
