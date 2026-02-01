package guiAdminHome;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import database.Database;
import entityClasses.User;
import guiUserUpdate.ViewUserUpdate;

/*******
 * <p> Title: GUIAdminHomePage Class. </p>
 * 
 * <p> Description: The Java/FX-based Admin Home Page.  This class provides the JavaFX GUI widgets
 * that enable an admin to perform admin functions.  This page contains a number of buttons that
 * have not yet been implemented.  What has been implemented may not work the way the final product
 * requires and there maybe defects in this code.
 * 
 * The class has been written using a singleton design pattern and is the View portion of the 
 * Model, View, Controller pattern.  The pattern is designed that the all accesses to this page and
 * its functions starts by invoking the static method displayAdminHome.  No other method should 
 * attempt to instantiate this class as that is controlled by displayAdminHome.  It ensure that
 * only one instance of class is instantiated and that one is properly configured for each use.  
 * 
 * Please note that this implementation is not appropriate for concurrent systems with multiple
 * users. This Baeldung article provides insight into the issues: 
 *           https://www.baeldung.com/java-singleton</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00		2025-08-17 Initial version
 *  
 */

public class ViewAdminHome {
	
	/*-*******************************************************************************************

	Attributes
	
	*/
	
	// These are the application values required by the user interface
	
	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

	
	// These are the widget attributes for the GUI. There are 5 areas for this GUI.
	
	// GUI Area 1: It informs the user about the purpose of this page, whose account is being used,
	// and a button to allow this user to update the account settings
	protected static Label label_PageTitle = new Label();
	protected static Label label_UserDetails = new Label();
	protected static Button button_UpdateThisUser = new Button("Account Update");

	// This is a separator and it is used to partition the GUI for various tasks
	private static Line line_Separator1 = new Line(20, 95, width-20, 95);

	// GUI Area 2: This area is used to provide status of the system.  This basic foundational code
	// does not have much current status information to display.
	protected static Label label_NumberOfInvitations = 
			new Label("Number of Oustanding Invitations: x");
	protected static Label label_NumberOfUsers = new Label("Number of Users: x");
	
	// This is a separator and it is used to partition the GUI for various tasks
	private static Line line_Separator2 = new Line(20, 165, width-20, 165);
	
	// GUI Area 3: This is the first of two areas provided the admin with a set of action buttons
	// that can be used to perform the tasks allocated to the admin role.  This part is about
	// inviting potential new users to establish an account and what role that user will have.
	protected static Label label_Invitations = new Label("Send An Invitation");
	protected static Label label_InvitationEmailAddress = new Label("Email Address");
	protected static TextField text_InvitationEmailAddress = new TextField();
	protected static ComboBox <String> combobox_SelectRole = new ComboBox <String>();
	protected static String [] roles = {"Admin", "Role1", "Role2"};
	protected static Button button_SendInvitation = new Button("Send Invitation");
	protected static Alert alertEmailError = new Alert(AlertType.INFORMATION);
	protected static Alert alertEmailSent = new Alert(AlertType.INFORMATION);
	
	// This is a separator and it is used to partition the GUI for various tasks
	private static Line line_Separator3 = new Line(20, 255, width-20, 255);
	
	// GUI Area 4: This is the second of the two action item areas.  This provides a set of other
	// admin buttons to use to perform other roles.  Many of these buttons are just stubs and an
	// alert pops up to inform the admin of this fact.
	protected static Button button_ManageInvitations = new Button("Manage Invitations");
	
	//Properties and GUI related to One-time passwords
	protected static Button button_SetOnetimePassword = new Button("Set a One-Time Password");
	protected static Alert alertOneTimePassword = new Alert(AlertType.INFORMATION);
	
	//Properties and GUI related to deleting accounts
	protected static Button button_DeleteUser = new Button("Delete a User");
	protected static Alert alertDeleteUser = new Alert(AlertType.WARNING,"",ButtonType.OK,ButtonType.CANCEL);

	//To-do, remove this button and reorganize the UI flow
	protected static Button button_ListUsers = new Button("List All Users");
	protected static Button button_AddRemoveRoles = new Button("Add/Remove Roles");
	protected static Alert alertNotImplemented = new Alert(AlertType.INFORMATION);

	// This is a separator and it is used to partition the GUI for various tasks
	private static Line line_Separator4 = new Line(20, 525, width-20,525);

	// GUI Area 5: This is last of the GUI areas.  It is used for quitting the application, logging
	// out, and on other pages a return is provided so the user can return to a previous page when
	// the actions on that page are complete.  Be advised that in most cases in this code, the 
	// return is to a fixed page as opposed to the actual page that invoked the pages.
	protected static Button button_Logout = new Button("Logout");
	protected static Button button_Quit = new Button("Quit");

	// This is the end of the GUI objects for the page.
	
	// These attributes are used to configure the page and populate it with this user's information
	private static ViewAdminHome theView;		// Used to determine if instantiation of the class
												// is needed

	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;
	
	protected static Stage theStage;			// The Stage that JavaFX has established for us
	private static Pane theRootPane;			// The Pane that holds all the GUI widgets 
	protected static User theUser;				// The current logged in User

	private static Scene theAdminHomeScene;		// The shared Scene each invocation populates
	private static final int theRole = 1;		// Admin: 1; Role1: 2; Role2: 3
	
	//User list members
	protected static TableView<User> usersList = new TableView<>();

	/*-*******************************************************************************************

	Constructors
	
	*/

	/**********
	 * <p> Method: displayAdminHome(Stage ps, User user) </p>
	 * 
	 * <p> Description: This method is the single entry point from outside this package to cause
	 * the Admin Home page to be displayed.
	 * 
	 * It first sets up every shared attributes so we don't have to pass parameters.
	 * 
	 * It then checks to see if the page has been setup.  If not, it instantiates the class, 
	 * initializes all the static aspects of the GIUI widgets (e.g., location on the page, font,
	 * size, and any methods to be performed).
	 * 
	 * After the instantiation, the code then populates the elements that change based on the user
	 * and the system's current state.  It then sets the Scene onto the stage, and makes it visible
	 * to the user.
	 * 
	 * @param ps specifies the JavaFX Stage to be used for this GUI and it's methods
	 * 
	 * @param user specifies the User for this GUI and it's methods
	 * 
	 */
	public static void displayAdminHome(Stage ps, User user) {
		
		// Establish the references to the GUI and the current user
		theStage = ps;
		theUser = user;
		
		// If not yet established, populate the static aspects of the GUI
		if (theView == null) theView = new ViewAdminHome();		// Instantiate singleton if needed
		
		if (label_UserDetails != null) {
			label_UserDetails.setText("User: " + theUser.getUserName());
	    }
		
		System.out.println("DEBUG: Logging in as: " + user.getUserName());
		
		// Populate the dynamic aspects of the GUI with the data from the user and the current
		// state of the system.
		theDatabase.getUserAccountDetails(user.getUserName());		// Fetch this user's data
		applicationMain.FoundationsMain.activeHomePage = theRole;	// Set this as the active Home																	// UserUpdate page

		// Set the role for potential users to the default (No role selected)
		combobox_SelectRole.getSelectionModel().select(0);
			
		//Update the users list, here
		refreshUsersList();
		
		// Set the title for the window, display the page, and wait for the Admin to do something
		theStage.setTitle("");
		theStage.setScene(theAdminHomeScene);						// Set this page onto the stage
		theStage.show();											// Display it to the user
	}
	
	/**********
	 * <p> Method: GUIAdminHomePage() </p>
	 * 
	 * <p> Description: This method initializes all the elements of the graphical user interface.
	 * This method determines the location, size, font, color, and change and event handlers for
	 * each GUI object.
	 * 
	 * This is a singleton and is only performed once.  Subsequent uses fill in the changeable
	 * fields using the displayAdminHome method.</p>
	 * 
	 */
	private ViewAdminHome() {

		// Create the Pane for the list of widgets and the Scene for the window
		theRootPane = new Pane();
		theAdminHomeScene = new Scene(theRootPane, width, height);
		
		theAdminHomeScene.getStylesheets().add(
		        getClass().getResource("/applicationMain/application.css").toExternalForm()
	    );
	
		// Populate the window with the title and other common widgets and set their static state
		
		// GUI Area 1
		label_PageTitle.setText("Admin Home Page");
		setupLabelUI(label_PageTitle, "Arial", 28, width, Pos.CENTER, 0, 5);

		label_UserDetails.setText("User: " + theUser.getUserName());
		setupLabelUI(label_UserDetails, "Arial", 20, width, Pos.BASELINE_LEFT, 20, 55);
		
		setupButtonUI(button_UpdateThisUser, "Dialog", 18, 170, Pos.CENTER, 610, 45);
		button_UpdateThisUser.setOnAction((_) -> 
				{ViewUserUpdate.displayUserUpdate(theStage, theUser);});
			
		// GUI Area 2
		setupLabelUI(label_NumberOfInvitations, "Arial", 20, 200, Pos.BASELINE_LEFT, 20, 105);
		label_NumberOfInvitations.getStyleClass().add("sub-label");
		label_NumberOfInvitations.setText("Number of outstanding invitations: " + 
				theDatabase.getNumberOfInvitations());
	
		setupLabelUI(label_NumberOfUsers, "Arial", 20, 200, Pos.BASELINE_LEFT, 20, 135);
		label_NumberOfUsers.getStyleClass().add("sub-label");
		label_NumberOfUsers.setText("Number of users: " + 
				theDatabase.getNumberOfUsers());
	
		// GUI Area 3
		setupLabelUI(label_Invitations, "Arial", 20, width, Pos.BASELINE_LEFT, 20, 175);
		label_Invitations.getStyleClass().add("sub-label");
	
		setupLabelUI(label_InvitationEmailAddress, "Arial", 16, width, Pos.BASELINE_LEFT,
		20, 210);
		label_InvitationEmailAddress.getStyleClass().add("sub-label");
	
		setupTextUI(text_InvitationEmailAddress, "Arial", 16, 360, Pos.BASELINE_LEFT,
		150, 205, true);
	
		setupComboBoxUI(combobox_SelectRole, "Dialog", 16, 90, 520, 205);
	
		List<String> list = new ArrayList<String>();	// Create a new list empty list of the
		for (int i = 0; i < roles.length; i++) {		// roles this code currently supports
			list.add(roles[i]);
		}
		combobox_SelectRole.setItems(FXCollections.observableArrayList(list));
		combobox_SelectRole.getSelectionModel().select(0);
		alertEmailSent.setTitle("Invitation");
		alertEmailSent.setHeaderText("Invitation was sent");

		setupButtonUI(button_SendInvitation, "Dialog", 16, 150, Pos.CENTER, 630, 205);
		button_SendInvitation.setOnAction((_) -> {ControllerAdminHome.performInvitation(); });
	
		// GUI Area 4
		setupButtonUI(button_ManageInvitations, "Dialog", 16, 250, Pos.CENTER, 20, 270);
		button_ManageInvitations.setOnAction((_) -> 
			{ControllerAdminHome.manageInvitations(); });
	
		setupButtonUI(button_SetOnetimePassword, "Dialog", 16, 250, Pos.CENTER, 20, 320);
		button_SetOnetimePassword.setOnAction((_) -> 
			{ControllerAdminHome.setOnetimePassword(); });		
		alertOneTimePassword.setTitle("One-time password generated");

		setupButtonUI(button_DeleteUser, "Dialog", 16, 250, Pos.CENTER, 20, 370);
		button_DeleteUser.setOnAction((_) -> {ControllerAdminHome.deleteUser(); });
		alertDeleteUser.setTitle("Confirm");

		setupButtonUI(button_ListUsers, "Dialog", 16, 250, Pos.CENTER, 20, 420);
		button_ListUsers.setOnAction((_) -> {ControllerAdminHome.listUsers(); });

		setupButtonUI(button_AddRemoveRoles, "Dialog", 16, 250, Pos.CENTER, 20, 470);
		button_AddRemoveRoles.setOnAction((_) -> {ControllerAdminHome.addRemoveRoles(); });
		
		// GUI Area 5
		setupButtonUI(button_Logout, "Dialog", 18, 250, Pos.CENTER, 20, 540);
		button_Logout.setOnAction((_) -> {ControllerAdminHome.performLogout(); });
    
		setupButtonUI(button_Quit, "Dialog", 18, 250, Pos.CENTER, 300, 540);
		button_Quit.setOnAction((_) -> {ControllerAdminHome.performQuit(); });
		
		//Establish user list in the Admin home
		setupTableViewUI(usersList, "Dialog", 12, 480, 300, 268, 240);
		
		usersList.getSelectionModel().selectedItemProperty().addListener((selection) -> {
		    if (selection != null) {
		    	String user = usersList.getSelectionModel().getSelectedItem().getUserName();
		    	User selectUser = null;
		    	List<String> adminsAmount = theDatabase.getUserListAdmin();	
		    	if(!user.isEmpty()) selectUser = theDatabase.getUserAsObject(user);
		    	
		    	// Check if a user is selected. If so and this user is an admin, and they are
		    	// an admin, allow the one-time password button, but disallow deletion/
		    	// If the selected user is the same as the current one, only allow the delete button
		    	// If there is more than 1 admin. Otherwise, just reveal both buttons
		    	if(selectUser != null) {
		    		if(selectUser.getAdminRole() && !user.equals(theUser.getUserName())) {
		    			button_DeleteUser.setVisible(false);
		    			button_SetOnetimePassword.setVisible(true);
		    		}
		    		else if(selectUser.getAdminRole() && user.equals(theUser.getUserName())){
		    			if(adminsAmount.size() > 1) 
		    				button_DeleteUser.setVisible(true);
		    			else 
		    				button_DeleteUser.setVisible(false);
		    			button_SetOnetimePassword.setVisible(false);
		    		}
		    		else {
		    			button_DeleteUser.setVisible(true);
		    			button_SetOnetimePassword.setVisible(true);
		    		}
		    	}
		    }
		});

		// This is the end of the GUI initialization code
		
		// Place all of the widget items into the Root Pane's list of children
		theRootPane.getChildren().addAll(
			label_PageTitle, label_UserDetails, button_UpdateThisUser, line_Separator1,
    		label_NumberOfInvitations, label_NumberOfUsers,
    		line_Separator2,
    		label_Invitations, 
    		label_InvitationEmailAddress, text_InvitationEmailAddress,
    		combobox_SelectRole, button_SendInvitation, line_Separator3,
    		usersList,
    		button_ManageInvitations,
    		button_SetOnetimePassword,
    		button_DeleteUser,
    		button_ListUsers,
    		button_AddRemoveRoles,
    		line_Separator4, 
    		button_Logout,
    		button_Quit
    		);
		
		// With theRootPane set up with the common widgets, it is up to displayAdminHome to show
		// that Pane to the user after the dynamic elements of the widgets have been updated.
	}

	/*-*******************************************************************************************

	Helper methods used to minimizes the number of lines of code needed above
	
	*/

	/**********
	 * Private local method to initialize the standard fields for a label
	 * 
	 * @param l		The Label object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The width of the Button
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
	 * Private local method to initialize the standard fields for a text input field
	 * 
	 * @param b		The TextField object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The width of the Button
	 * @param p		The alignment (e.g. left, centered, or right)
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 * @param e		Is this TextField user editable?
	 */
	private void setupTextUI(TextField t, String ff, double f, double w, Pos p, double x, double y, boolean e){
		t.setFont(Font.font(ff, f));
		t.setMinWidth(w);
		t.setMaxWidth(w);
		t.setAlignment(p);
		t.setLayoutX(x);
		t.setLayoutY(y);		
		t.setEditable(e);
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
	private void setupComboBoxUI(ComboBox <String> c, String ff, double f, double w, double x, double y){
		c.setStyle("-fx-font: " + f + " " + ff + ";");
		c.setMinWidth(w);
		c.setLayoutX(x);
		c.setLayoutY(y);
	}
	
	/**********
	 * Private local method to initialize the standard fields for a ListView
	 * 
	 * @param c		The ComboBox object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The width of the ComboBox
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
	private void setupTableViewUI(TableView <User> t, String ff, double f, double w, double x, double y, double h){
		t.setStyle("-fx-font: " + f + " " + ff + ";");
		t.setMinWidth(w);
		t.setMaxWidth(w);
		t.setMinHeight(h);
		t.setMaxHeight(h);
		t.setLayoutX(x);
		t.setLayoutY(y);
	}
	
	/**********
	 * It seems I will need to manually refresh the user's table here, since the refresh method
	 * doesn't cut it, and I'd have to make everything observeable. That could be a huge can of
	 * worms.
	 * 
	 * @param userName	The username to get data from
	 */
	public static void refreshUsersList()
	{
		// Forcefully clear the user table and refresh it. This may be the only way without restructuring
		// Every property in this app to an observeable. Which unfortunately we do not have the capital
		// to do.
		usersList.getItems().clear();
		usersList.getColumns().clear();
		setupUserListData();
	}
	
	/**********
	 * Generate the fields for the user list. Seems TableView is the best way.
	 * 
	 * @param userName	The username to get data from
	 */
	private static void setupUserListData()
	{
		//Create a list of User objects for the TableView
		List<User> allUsers = new ArrayList<>();
		for(String userName : FXCollections.observableArrayList(theDatabase.getUserList())) {
			User selectUser = theDatabase.getUserAsObject(userName);
			allUsers.add(selectUser);
		}
		
		//Generate the column of real names
		TableColumn<User, String> userNameColumn = new TableColumn<>("Username");
		userNameColumn.setMinWidth(120);
		userNameColumn.setMaxWidth(120);
		userNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty (cellData.getValue().getUserName()));
		
		//Generate the column of real names
		TableColumn<User, String> fullNameColumn = new TableColumn<>("Real Name");
		fullNameColumn.setPrefWidth(180);
		fullNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty (cellData.getValue().getFullName()));
		
		//Generate the column of email
		TableColumn<User, String> emailColumn = new TableColumn<>("Email");
		emailColumn.setPrefWidth(240);
		emailColumn.setCellValueFactory(cellData -> new SimpleStringProperty (cellData.getValue().getEmailAddress()));
		
		//Generate the column of roles
		TableColumn<User, String> roleColumn = new TableColumn<>("Roles");
		roleColumn.setMinWidth(120);
		roleColumn.setMaxWidth(120);
		roleColumn.setCellValueFactory(cellData -> new SimpleStringProperty (cellData.getValue().getRoleString()));

		//Generate the TableView
		usersList.setItems(FXCollections.observableArrayList(allUsers));
		usersList.getColumns().addAll(userNameColumn,fullNameColumn,emailColumn,roleColumn);
		
		usersList.getSelectionModel().selectFirst();
	}
}
