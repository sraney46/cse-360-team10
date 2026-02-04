package guiMultipleRoleDispatch;

import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import database.Database;
import entityClasses.User;

/*******
 * <p> Title: GUIMultipleRoleDispatchPage Class. </p>
 * 
 * <p> Description: The Java/FX-based Admin Update Page.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00		2025-08-20 Initial version
 * @version 1.02		2026-02-03 UI Design Refresh
 *  
 */

public class ViewMultipleRoleDispatch {

	/*-****************************************************************************************

		Attributes

	 */

	// These are the application values required by the user interface

	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;


	// These are the widget attributes for the GUI. There are 3 areas for this GUI.

	// GUI Area 1: It informs the user about the purpose of this page and whose account is being
	// used. There is no button to allow this user to update the account settings.
	private static Label label_PageTitle = new Label("Multiple Role Dispatch Page");
	private static Label label_UserDetails = new Label();
	
	// This is a separator and it is used to partition the GUI for various tasks
	private static Line line_Separator1 = new Line(20, 95, width-20, 95);

	// GUI Area 2: This area consists of a label to ask the user what roles to place, a ComboBox 
	// so the user can select the role, and a button to perform that role.
	private static Label label_WhichRole = new Label("Which role do you wish to play:");
	protected static ComboBox <String> combobox_SelectRole = new ComboBox <String>();
	private static Button button_PerformRole = new Button("Perform Role");		
	
	// This is a separator and it is used to partition the GUI for various tasks
	private static Line line_Separator4 = new Line(20, 525, width-20,525);

	// GUI Area 3: This is last of the GUI areas.  It is used for quitting the application and
	// logging out.
	private static Button button_Logout = new Button("Logout");
	private static Button button_Quit = new Button("Quit");

	// This is the end of the GUI objects for the page.
	
	// These attributes are used to configure the page and populate it with this user's information

	private static ViewMultipleRoleDispatch theView;	// Used to determine if instantiation of
														// the class is needed


	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	protected static Stage theStage;			// The Stage that JavaFX has established for us
	private static Pane theRootPane;			// The Pane that holds all the GUI widgets 
	protected static User theUser;				// The current user of the application

	private static Scene theMultipleRoleDispatchScene = null;	


	/*-********************************************************************************************

	Constructor

	 */

	public static void displayMultipleRoleDispatch(Stage ps, User user) {
		
		// Establish the references to the GUI and the current user
		theStage = ps;
		theUser = user;
		
		if (theView == null) theView = new ViewMultipleRoleDispatch();

		List<String> list = new ArrayList<String>();	// Create a new list empty list
		theDatabase.getUserAccountDetails(theUser.getUserName());
		
		label_UserDetails.setText("User: " + theUser.getUserName() + "   Select which role");
		setupLabelUI(label_UserDetails, "Arial", 20, width, Pos.CENTER, 0, 50);

		label_UserDetails.setText("User: " + theUser.getUserName());
		setupLabelUI(label_UserDetails, "Arial", 20, width, Pos.BASELINE_LEFT, 20, 55);		
		
		combobox_SelectRole.setPromptText("Select Role");

		System.out.println("*** Getting multiple role details for user: " + theUser.getUserName());
		list = new ArrayList<String>();
		
		if (theDatabase.getCurrentAdminRole()) list.add("Admin");
		if (theDatabase.getCurrentNewRole1()) list.add("Role1");
		if (theDatabase.getCurrentNewRole2()) list.add("Role2");
		combobox_SelectRole.setItems(FXCollections.observableArrayList(list));
		
	

		
		// Set the title for the window, display the page, and wait for the Admin to do something
		theStage.setTitle("");	
		theStage.setScene(theMultipleRoleDispatchScene);		// Set this page onto the stage
		theStage.show();										// Display it to the user
	}


	/**********
	 * <p> Method: ViewMultipleRoleDispatch() </p>
	 * 
	 * <p> Description: This method initializes all the elements of the graphical user interface.
	 * This method determines the location, size, font, color, and change and event handlers for
	 * each GUI object. </p>
	 * 
	 * @param ps specifies the JavaFX Stage to be used for this GUI and it's methods
	 * 
	 * @param user specifies the User for this GUI and it's methods
	 * 
	 */
	private ViewMultipleRoleDispatch() {

		// Create the Pane for the list of widgets and the Scene for the window
		theRootPane = new Pane();
		theMultipleRoleDispatchScene = new Scene(theRootPane, width, height);
		
		theMultipleRoleDispatchScene.getStylesheets().add(
				getClass().getResource("/applicationMain/application.css").toExternalForm()
	    );

		// Populate the window with the title and other common widgets and set their static state
		
		// GUI Area 1
		setupLabelUI(label_PageTitle, "Arial", 28, width, Pos.CENTER, 0, 5);
		

		// GUI Area 2
		setupLabelUI(label_WhichRole, "Arial", 20, 200, Pos.BASELINE_LEFT, 20, 110);
		label_WhichRole.getStyleClass().add("sub-label");

		setupComboBoxUI(combobox_SelectRole, "Dialog", 16, 100, 305, 105);
		combobox_SelectRole.getStyleClass().add("default-combo-box");
		
		
		
		// Initially disable and style the button as red/grayed
		button_PerformRole.setDisable(true);
		button_PerformRole.setStyle("-fx-background-color: #dc3545; -fx-opacity: 0.5;");

		// Listen for selection changes
		combobox_SelectRole.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
		    if (newVal != null) {
		        // Role selected - enable button
		        button_PerformRole.setDisable(false);
		        button_PerformRole.setStyle(""); // Reset to default style
		    } else {
		        // No role selected - disable button
		        button_PerformRole.setDisable(true);
		        button_PerformRole.setStyle("-fx-background-color: #dc3545; -fx-opacity: 0.5;");
		    }
		});

		setupButtonUI(button_PerformRole, "Dialog", 16, 100, Pos.CENTER, 495, 105);
		button_PerformRole.setOnAction((_) -> 
		{guiMultipleRoleDispatch.ControllerMultipleRoleDispatch.performRole(); });

		
		// GUI Area 3		
		setupButtonUI(button_Logout, "Dialog", 18, 250, Pos.CENTER, 20, 540);
		button_Logout.setOnAction((_) -> 
		{guiMultipleRoleDispatch.ControllerMultipleRoleDispatch.performLogout(); });

		setupButtonUI(button_Quit, "Dialog", 18, 250, Pos.CENTER, 300, 540);
		button_Quit.setOnAction((_) -> 
		{guiMultipleRoleDispatch.ControllerMultipleRoleDispatch.performQuit(); });
		
		// This is the end of the GUI Widgets for the page

		// Place all of the just-initialized GUI elements into the pane
		theRootPane.getChildren().addAll(
				label_PageTitle,
				label_UserDetails,
				line_Separator1,
				label_WhichRole,
				combobox_SelectRole,
				button_PerformRole,
				line_Separator4, 
				button_Logout,
				button_Quit);
	}


	/*-********************************************************************************************

		Helper methods to reduce code length

	 */

	/**********
	 * Private local method to initialize the standard fields for a label
	 */

	private static void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, double y){
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
	private static void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, double y){
		b.setFont(Font.font(ff, f));
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);		
	}


	/**********
	 * Private local method to initialize the standard fields for a ComboBox
	 * 
	 * @param c		The ComboBox object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The width of the ComboBox
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
	private void setupComboBoxUI(ComboBox <String> c, String ff, double f, double w, double x, 
			double y){
		c.setStyle("-fx-font: " + f + " " + ff + ";");
		c.setMinWidth(w);
		c.setLayoutX(x);
		c.setLayoutY(y);
	}
}
