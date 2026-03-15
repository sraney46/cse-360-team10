package guiUserLogin;

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
import javafx.scene.shape.Line;
import javafx.scene.paint.Color;

/*******
 * <p>
 * Title: GUIStartupPage Class.
 * </p>
 * 
 * <p>
 * Description: The Java/FX-based System Startup Page.
 * </p>
 * 
 * <p>
 * Copyright: Lynn Robert Carter © 2025
 * </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00 2025-04-20 Initial version
 * 
 */

public class ViewUserLogin {

  /*-********************************************************************************************
  
  Attributes
  
   *********************************************************************************************/

  // These are the application values required by the user interface
  private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH * 0.5;
  private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

  private static Label label_ApplicationTitle = new Label("Welcome!");

  // This set is for all subsequent starts of the system
  // private static Label label_OperationalStartTitle = new Label("Log In or
  // Invited User Account Setup ");

  // private static Label label_LogInInsrtuctions = new Label("Enter your user
  // name and password and "+
  // "then click on the LogIn button");
  private static Label label_Username = new Label("Username");
  private static Label label_Password = new Label("Password");
  protected static Alert alertUsernamePasswordError = new Alert(AlertType.INFORMATION);

  // private User user;
  protected static TextField text_Username = new TextField();
  protected static PasswordField text_Password = new PasswordField();
  private static Button button_Login = new Button("Log In");

  // private static Label label_AccountSetupInsrtuctions = new Label("No account?
  // "+
  // "Enter your invitation code and click on the Account Setup button");
  private static Label label_AccountSetupInsrtuctions = new Label("Invitation Code");
  private static TextField text_Invitation = new TextField();
  private static Button button_SetupAccount = new Button("Setup Account");

  private static Button button_Quit = new Button("Quit");

  private static Stage theStage;				
  private static Pane theRootPane;		
  
  /** An object for the user login scene. By default null since it does not appear by default */
  public static Scene theUserLoginScene = null;
  private static ViewUserLogin theView = null; // private static guiUserLogin.ControllerUserLogin theController;

  /*-********************************************************************************************
  
  Constructor
  
   *********************************************************************************************/

  public static void displayUserLogin(Stage ps) {

    // Establish the references to the GUI. There is no current user yet.
    theStage = ps;

    // If not yet established, populate the static aspects of the GUI
    if (theView == null)
      theView = new ViewUserLogin();

    // Populate the dynamic aspects of the GUI with the data from the user and the
    // current
    // state of the system.
    text_Username.setText(""); // Reset the username and password from the last use
    text_Password.setText("");
    text_Invitation.setText(""); // Same for the invitation code

    // Set the title for the window, display the page, and wait for the Admin to do
    // something
    theStage.setTitle("");
    theStage.setScene(theUserLoginScene);
    theStage.show();
  }

  /**********
   * <p>
   * Method: ViewUserLoginPage()
   * </p>
   * 
   * <p>
   * Description: This method is called when the application first starts. It must
   * handle
   * two cases: 1) when no has been established and 2) when one or more users have
   * been
   * established.
   * 
   * If there are no users in the database, this means that the person starting
   * the system jmust
   * be an administrator, so a special GUI is provided to allow this Admin to set
   * a username and
   * password.
   * 
   * If there is at least one user, then a different display is shown for existing
   * users to login
   * and for potential new users to provide an invitation code and if it is valid,
   * they are taken
   * to a page where they can specify a username and password.
   * </p>
   * 
   * @param ps      specifies the JavaFX Stage to be used for this GUI and it's
   *                methods
   * 
   * @param theRoot specifies the JavaFX Pane to be used for this GUI and it's
   *                methods
   * 
   * @param db      specifies the Database to be used by this GUI and it's methods
   * 
   */
  private ViewUserLogin() {

    // Create the Pane for the list of widgets and the Scene for the window
    theRootPane = new Pane();
    theUserLoginScene = new Scene(theRootPane, width, height);

    theUserLoginScene.getStylesheets().add(
        getClass().getResource("/applicationMain/application.css").toExternalForm());

    // Populate the window with the title and other common widgets and set their
    // static state
    setupLabelUI(label_ApplicationTitle, "Arial", 32, width, Pos.CENTER, 0, 10);

    // setupLabelUI(label_OperationalStartTitle, "Arial", 24, width, Pos.CENTER, 0,
    // 60);

    // Existing user log in portion of the page

    label_Username.getStyleClass().add("sub-label");
    setupLabelUI(label_Username, "Arial", 18, width, Pos.BASELINE_LEFT, 50, 90);

    // Establish the text input operand field for the username
    setupTextUI(text_Username, "Arial", 18, 300, Pos.BASELINE_LEFT, 50, 120, true);
    text_Username.setPromptText("");

    label_Password.getStyleClass().add("sub-label");
    setupLabelUI(label_Password, "Arial", 18, width, Pos.BASELINE_LEFT, 50, 170);

    // Establish the text input operand field for the password
    setupTextUI(text_Password, "Arial", 18, 300, Pos.BASELINE_LEFT, 50, 200, true);
    text_Password.setPromptText("");

    // Set up the Log In button
    setupButtonUI(button_Login, "Dialog", 18, 300, Pos.BASELINE_LEFT, 50, 255);
    button_Login.setOnAction((_) -> {
      ControllerUserLogin.doLogin(theStage);
    });

    alertUsernamePasswordError.setTitle("Invalid username/password!");
    alertUsernamePasswordError.setHeaderText(null);

    // The invitation to setup an account portion of the page
    label_AccountSetupInsrtuctions.getStyleClass().add("sub-label");
    setupLabelUI(label_AccountSetupInsrtuctions, "Arial", 18, width, Pos.BASELINE_LEFT, 50, 310);

    // Establish the text input operand field for the password
    setupTextUI(text_Invitation, "Arial", 18, 300, Pos.BASELINE_LEFT, 50, 340, true);

    // Set up the setup button
    setupButtonUI(button_SetupAccount, "Dialog", 18, 300, Pos.BASELINE_LEFT, 50, 400);
    button_SetupAccount.setOnAction((_) -> {
      System.out.println("**** Calling doSetupAccount");
      ControllerUserLogin.doSetupAccount(theStage, text_Invitation.getText());
    });

    // Set up the Quit button
    setupButtonUI(button_Quit, "Dialog", 18, 100, Pos.CENTER, 150, 520);
    button_Quit.setOnAction((_) -> {
      ControllerUserLogin.performQuit();
    });

    // theRootPane.getChildren().clear();

    theRootPane.getChildren().addAll(
        label_ApplicationTitle,
        label_Username, label_Password, label_AccountSetupInsrtuctions, text_Username,
        button_Login, text_Password, text_Invitation, button_SetupAccount,
        button_Quit);
  }

  /*-********************************************************************************************
  
  Helper methods to reduce code length
  
   *********************************************************************************************/

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
