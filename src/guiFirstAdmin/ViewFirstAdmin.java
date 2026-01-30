package guiFirstAdmin;

import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.Scene;


/*******
 * <p> Title: ViewFirstAdmin Class</p>
 * 
 * <p> Description: The FirstAdmin Page View.  This class is used to require the very first user of
 * the system to specify an Admin Username and Password when there is no database for the
 * application.  This avoids the common weakness practice of hard coding credentials into the 
 * application.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00		2025-08-15 Initial version
 *  
 */

public class ViewFirstAdmin {

	/*-********************************************************************************************

	Attributes

	 */

	// These are the application values required by the user interface
	
	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

	// These are the widget attributes for the GUI
	
	// The GUI informs the user about the purpose of this page, provides three text inputs fields
	// for the user to specify a username for this account and two copies of the password to be
	// used (they must match), a button to request that the account be established, and a quit
	// but to abort the action and stop the application.
	private static Label label_ApplicationTitle = new Label("Admin Startup Page");
	private static Label label_TitleLine1 = 
			new Label(" You are the first user.");
	
	private static Label label_TitleLine2 = 
			new Label("You must be an administrator.");
	
	protected static Label label_PasswordsDoNotMatch = new Label();
	protected static TextField text_AdminUsername = new TextField();
	protected static PasswordField text_AdminPassword1 = new PasswordField();
	protected static PasswordField text_AdminPassword2 = new PasswordField();
	private static Button button_AdminSetup = new Button("Setup Admin Account");

	// This alert is used should the user enter two passwords that do not match
	protected static Alert alertUsernamePasswordError = new Alert(AlertType.INFORMATION);

	// This button allow the user to abort creating the first admin account and terminate
	private static Button button_Quit = new Button("Quit");

	// These attributes are used to configure the page and populate it with this user's information
	protected static Stage theStage;	
	private static Pane theRootPane;
	private static Scene theFirstAdminScene = null;
	private static final int theRole = 1;		// Admin: 1; Role1: 2; Role2: 3
		
	
	/*-********************************************************************************************

	Constructor

	 */
	
	/**********
	 * <p> Method: public displayFirstAdmin(Stage ps) </p>
	 * 
	 * <p> Description: This method is called when the application first starts. It create an
	 * an instance of the View class.  
	 * 
	 * NOTE: As described below, this code does not implement MVC using the singleton pattern used
	 * by most of the pages as the code is written this way because we know with certainty that it
	 * will only be called once.  For this reason, we directly call the private class constructor.
	 * 
	 * @param ps specifies the JavaFX Stage to be used for this GUI and it's methods
	 */
	public static void displayFirstAdmin(Stage ps) {
		
		// Establish the references to the GUI.  There is no user yet.
		theStage = ps;			// Establish a reference to the JavaFX Stage
		
		// This page is only called once so there is no need to save the reference to it
		new ViewFirstAdmin();	// Create an instance of the class
		
		// Populate the dynamic aspects of the GUI with the data from the user and the current
		// state of the system.
		applicationMain.FoundationsMain.activeHomePage = theRole;	// 1: Admin; 2: Role1; 3 Roles2

		// Set the title for the window, display the page, and wait for the Admin to do something
		theStage.setTitle("");	
		theStage.setScene(theFirstAdminScene);
		theStage.show();
	}

	/**********
	 * <p> Method: private ViewFirstAdmin() </p>
	 * 
	 * <p> Description: This constructor is called when the application first starts. It must 
	 * handle when no user has been established.  It is never called again, either during the first
	 * run of the program after reboot or during normal operations of the program.  (This page is
	 * only used when there is no database for the application or there is a database, but there
	 * no users in the database.
	 * 
	 * If there is no database or there is a database, but there are no users, it means that the
	 * person starting the system is an administrator, so a special GUI is provided to allow this
	 * Admin to create an account by setting a username and password.
	 * 
	 * If there is at least one user, this page is not called.  Since this is only used one when
	 * the system is being set up, this MVC code does not use a singleton protocol.  For this
	 * reason, do not use this as a typical MVC pattern.</p>
	 * 
	 * This is the View of the Model, View, Controller architectural pattern.  Due to how it is
	 * called, it does not follow the usual singleton implementation pattern as explained above.
	 * 
	 * The view sets up the window and all of the Graphical User Interface (GUI) widgets.  Once
	 * they are all set, the window is displayed to the user, the View returns to the starting main
	 * method, and that execution thread ends.  The JavaFX thread continues to "execute" waiting
	 * for the user to interact with the GUI.  Based on which widget is used, changes to the
	 * display are made and/or a Controller method is called to perform some action.
	 * 
	 */
	private ViewFirstAdmin() {

		// Create the Pane for the list of widgets and the Scene for the window
		theRootPane = new Pane();
		theFirstAdminScene = new Scene(theRootPane, width, height);
		
		// ADD THIS LINE to apply your global stylesheet
	    theFirstAdminScene.getStylesheets().add(
	        getClass().getResource("/applicationMain/application.css").toExternalForm()
	    );

		// Label theScene with the name of the system startup screen
		setupLabelUI(label_ApplicationTitle, "Arial", 32, width, Pos.CENTER, 0, 10);

		// Label to display the welcome message for the first user
		setupLabelUI(label_TitleLine1, "Arial", 24, width, Pos.CENTER, 0, 70);

		// Label to display the welcome message for the first user
		setupLabelUI(label_TitleLine2, "Arial", 18, width, Pos.CENTER, 0, 100);

		// Establish the text input operand field for the Admin username
		setupTextUI(text_AdminUsername, "Arial", 18, 300, Pos.BASELINE_LEFT, 50, 160, 
				true);
		text_AdminUsername.setPromptText("Enter Admin Username");
		text_AdminUsername.textProperty().addListener((_, _, _) 
				-> {ControllerFirstAdmin.setAdminUsername(); });

		// Establish the text input operand field for the password
		setupTextUI(text_AdminPassword1, "Arial", 18, 300, Pos.BASELINE_LEFT, 50, 210, 
				true);
		text_AdminPassword1.setPromptText("Enter Admin Password");
		text_AdminPassword1.textProperty().addListener((_, _, _)
				-> {ControllerFirstAdmin.setAdminPassword1(); });

		// Establish the text input operand field for the password
		setupTextUI(text_AdminPassword2, "Arial", 18, 300, Pos.BASELINE_LEFT, 50, 260, 
				true);
		text_AdminPassword2.setPromptText("Enter Admin Password Again");
		text_AdminPassword2.textProperty().addListener((_, _, _) 
				-> {ControllerFirstAdmin.setAdminPassword2(); });

		// Set up the Log In button
		setupButtonUI(button_AdminSetup, "Dialog", 18, 200, Pos.CENTER, 475, 210);
		button_AdminSetup.setOnAction((_) -> {
			ControllerFirstAdmin.doSetupAdmin(theStage,1); 
			});

		// Label to display the Passwords do not match error message
		setupLabelUI(label_PasswordsDoNotMatch, "Arial", 18, width, Pos.CENTER, 0, 300);

		setupButtonUI(button_Quit, "Dialog", 18, 250, Pos.CENTER, 300, 520);
		button_Quit.setOnAction((_) -> {ControllerFirstAdmin.performQuit(); });

		// Place all of the just-initialized GUI elements into the pane
		theRootPane.getChildren().addAll(label_ApplicationTitle, label_TitleLine1,
				label_TitleLine2, text_AdminUsername, text_AdminPassword1, 
				text_AdminPassword2, button_AdminSetup, label_PasswordsDoNotMatch,
				button_Quit);
	}
	
	
	/*-********************************************************************************************

	Helper methods to reduce code length

	 */
	
	/**********
	 * Private local method to initialize the standard fields for a label
	 * 
	 * @param l		The Label object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The width of the Label
	 * @param p		The alignment (e.g. left, centered, or right)
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */

	private void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, double y){
		l.setFont(Font.font(ff, f));
		l.setMinWidth(w);
		l.setAlignment(p);
		l.setLayoutX(x);
		l.setLayoutY(y);		
	}


	/**********
	 * Private local method to initialize the standard fields for a button
	 * 
	 * @param b		The Button object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The width of the Button
	 * @param p		The alignment (e.g. left, centered, or right)
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
	private void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, double y){
		b.setFont(Font.font(ff, f));
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);		
	}

	
	/**********
	 * Private local method to initialize the standard fields for a text field
	 * 
	 * @param t		The TextField object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The width of the Button
	 * @param p		The alignment (e.g. left, centered, or right)
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 * @param e		The flag (Boolean) that specifies if this field is editable
	 */
	private void setupTextUI(TextField t, String ff, double f, double w, Pos p, double x, double y, 
			boolean e){
		t.setFont(Font.font(ff, f));
		t.setMinWidth(w);
		t.setMaxWidth(w);
		t.setAlignment(p);
		t.setLayoutX(x);
		t.setLayoutY(y);		
		t.setEditable(e);
	}	
}