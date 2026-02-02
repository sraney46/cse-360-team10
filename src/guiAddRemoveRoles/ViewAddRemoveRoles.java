package guiAddRemoveRoles;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;
import database.Database;
import entityClasses.User;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.geometry.Insets;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import javafx.scene.control.ListCell; 
import java.util.Optional;
import javafx.scene.control.ButtonType;



/*******
 * <p> Title: GUIAddRemoveRolesPage Class. </p>
 * 
 * <p> Description: The Java/FX-based page for changing the assigned roles to users.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00		2025-08-20 Initial version
 *  
 */

public class ViewAddRemoveRoles {
	
	/*-*******************************************************************************************

	Attributes
	
	*/
	
	// These are the application values required by the user interface
	
	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

	
	// These are the widget attributes for the GUI. There are 3 areas for this GUI.
	
	// GUI Area 1: It informs the user about the purpose of this page, whose account is being used,
	// and a button to allow this user to update the account settings.
	protected static Label label_PageTitle = new Label();
	protected static Label label_UserDetails = new Label();
	protected static Button button_PopulateDatebase = new Button("Populate Database");
	protected static Button button_UpdateThisUser = new Button("Account Update");
	
	// This is a separator and it is used to partition the GUI for various tasks
	protected static Line line_Separator1 = new Line(20, 95, width-20, 95);
	
	
	protected static ScrollPane scrollPane;
	protected static VBox userListBox;
	
	// This is a separator and it is used to partition the GUI for various tasks
	protected static Line line_Separator4 = new Line(20, 525, width-20,525);
	
	// GUI Area 3: This is last of the GUI areas.  It is used for quitting the application, logging
	// out, and on other pages a return is provided so the user can return to a previous page when
	// the actions on that page are complete.  Be advised that in most cases in this code, the 
	// return is to a fixed page as opposed to the actual page that invoked the pages.
	protected static Button button_Return = new Button("Return");
	protected static Button button_Logout = new Button("Logout");
	protected static Button button_Quit = new Button("Quit");

	// This is the end of the GUI objects for the page.
	
	// These attributes are used to configure the page and populate it with this user's information
	private static ViewAddRemoveRoles theView;	// Used to determine if instantiation of the class
												// is needed
	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;		

	protected static Stage theStage;			// The Stage that JavaFX has established for us
	protected static Pane theRootPane;			// The Pane that holds all the GUI widgets 
	protected static User theUser;				// The current user of the application
	
	public static Scene theAddRemoveRolesScene = null;	// The Scene each invocation populates
	protected static String theSelectedUser = "";	// The user whose roles are being updated
	protected static String theAddRole = "";		// The role being added
	protected static String theRemoveRole = "";		// The roles being removed
	
	//Errors and other information related to admin protection
	protected static Alert alertCannotModifyAdmin = new Alert(AlertType.INFORMATION);
	// Warning for populate database button 
	protected static Alert alertPopulateDatabase = new Alert(AlertType.CONFIRMATION);



	/*-*******************************************************************************************

	Constructors
	
	*/

	/**********
	 * <p> Method: displayAddRemoveRoles(Stage ps, User user) </p>
	 * 
	 * <p> Description: This method is the single entry point from outside this package to cause
	 * the AddRevove page to be displayed.
	 * 
	 * It first sets up very shared attributes so we don't have to pass parameters.
	 * 
	 * It then checks to see if the page has been setup.  If not, it instantiates the class, 
	 * initializes all the static aspects of the GUI widgets (e.g., location on the page, font,
	 * size, and any methods to be performed).
	 * 
	 * After the instantiation, the code then populates the elements that change based on the user
	 * and the system's current state.  It then sets the Scene onto the stage, and makes it visible
	 * to the user.
	 * 
	 * @param ps specifies the JavaFX Stage to be used for this GUI and it's methods
	 * 
	 * @param user specifies the User whose roles will be updated
	 *
	 */
	public static void displayAddRemoveRoles(Stage ps, User user) {
		
		// Establish the references to the GUI and the current user
		theStage = ps;
		theUser = user;
		
		// If not yet established, populate the static aspects of the GUI by creating the 
		// singleton instance of this class
		if (theView == null) theView = new ViewAddRemoveRoles();		
		
		// Refresh the user list each time the page is shown
	    refreshUserList();
		
		
		ViewAddRemoveRoles.theStage.setTitle("");
		ViewAddRemoveRoles.theStage.setScene(theAddRemoveRolesScene);
		ViewAddRemoveRoles.theStage.show();
	}

	
	/**********
	 * <p> Method: GUIAddRemoveRolesPage() </p>
	 * 
	 * <p> Description: This method initializes all the elements of the graphical user interface.
	 * This method determines the location, size, font, color, and change and event handlers for
	 * each GUI object. </p>
	 * 
	 * This is a singleton, so this is performed just one.  Subsequent uses fill in the changeable
	 * fields using the displayAddRempoveRoles method.</p>
	 * 
	 */
	public ViewAddRemoveRoles() {
		
		// This page is used by all roles, so we do not specify the role being used		
			
		// Create the Pane for the list of widgets and the Scene for the window
		theRootPane = new Pane();
		theAddRemoveRolesScene = new Scene(theRootPane, width, height);
		
		theAddRemoveRolesScene.getStylesheets().add(
		        getClass().getResource("/applicationMain/application.css").toExternalForm()
	    );
		
		// Populate the window with the title and other common widgets and set their static state
		
		// GUI Area 1
		label_PageTitle.setText("Add/Removed Roles Page");
		setupLabelUI(label_PageTitle, "Arial", 28, width, Pos.CENTER, 0, 5);

		label_UserDetails.setText("User: " + theUser.getUserName());
		setupLabelUI(label_UserDetails, "Arial", 20, width, Pos.BASELINE_LEFT, 20, 55);
		
		setupButtonUI(button_UpdateThisUser, "Dialog", 18, 170, Pos.CENTER, 610, 45);
		button_UpdateThisUser.setOnAction((_) -> 
			{guiUserUpdate.ViewUserUpdate.displayUserUpdate(theStage, theUser); });
		
		setupButtonUI(button_PopulateDatebase, "Dialog", 18, 170, Pos.CENTER, 310, 45);
		button_PopulateDatebase.setOnAction((_) -> 
			{	
				// Show the alert and wait for user response
			    Optional<ButtonType> result = alertPopulateDatabase.showAndWait();
			    if (result.isPresent() && result.get() == ButtonType.OK) {
			        // User pressed OK, proceed
			        theDatabase.populateDatabaseWithTestUsers(theUser);
			        refreshUserList();
			    } else {
			        // User cancelled, do nothing
			        System.out.println("Database population cancelled by user.");
			    }
			});
	
		
		// GUI Area 2
		userListBox = new VBox(8);                          
		userListBox.setPadding(new Insets(20));
		userListBox.setStyle("-fx-background-color: transparent;");  
		userListBox.setFillWidth(true);

		scrollPane = new ScrollPane(userListBox);
		scrollPane.setFitToWidth(true);                     
		scrollPane.setFitToHeight(false);                  
		scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
		scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

		scrollPane.setStyle(
		    "-fx-background-color: #000000;" +             
		    "-fx-background-radius: 15px;" +              
		    "-fx-border-radius: 15px;" 
		);

		// Make the viewport transparent so corners show properly
		scrollPane.viewportBoundsProperty().addListener((_) -> {
		    scrollPane.lookup(".viewport").setStyle(
		        "-fx-background-color: transparent;" +
		        "-fx-background-radius: 15px;"
		    );
		});
		
	
		scrollPane.setLayoutX(50);
		scrollPane.setLayoutY(125); 

		scrollPane.setPrefWidth(700);   
		scrollPane.setPrefHeight(375);  

		scrollPane.getStyleClass().add("custom-scroll");
		
		List<String> allUsernames = theDatabase.getUserList();
		
		
		for (String username : allUsernames) {
			
			theDatabase.getUserAccountDetails(username);
			    
		    User user = new User();
		    user.setUserName(username);
		    user.setAdminRole(theDatabase.getCurrentAdminRole());
		    user.setRole1User(theDatabase.getCurrentNewRole1());
		    user.setRole2User(theDatabase.getCurrentNewRole2());
		   
		    
		    HBox userRow = createUserRow(user);
		    
		    userListBox.getChildren().add(userRow);
	
		   
		}
		
		
		// GUI Area 3		
		setupButtonUI(button_Return, "Dialog", 18, 210, Pos.CENTER, 20, 540);
		button_Return.setOnAction((_) -> {ControllerAddRemoveRoles.performReturn(); });

		setupButtonUI(button_Logout, "Dialog", 18, 210, Pos.CENTER, 300, 540);
		button_Logout.setOnAction((_) -> {ControllerAddRemoveRoles.performLogout(); });
    
		setupButtonUI(button_Quit, "Dialog", 18, 210, Pos.CENTER, 570, 540);
		button_Quit.setOnAction((_) -> {ControllerAddRemoveRoles.performQuit(); });
		
		// error messages if an admin user tries to modify another admin
		alertCannotModifyAdmin.setTitle("This user's roles cannot be modified!");
		alertCannotModifyAdmin.setHeaderText(null);
		
		alertPopulateDatabase.setTitle("Confirm Database Population");
		alertPopulateDatabase.setHeaderText("This action will keep the current user data\nbut delete all other users in the database.");
		alertPopulateDatabase.setContentText("Press OK to continue or Cancel to abort.");
		
	
		theRootPane.getChildren().addAll(
				label_PageTitle, label_UserDetails,
				button_UpdateThisUser, button_PopulateDatebase, 
				line_Separator1,
				scrollPane,
				line_Separator4, 
				button_Return,
				button_Logout,
				button_Quit);
	}	

	/*-*******************************************************************************************

	Helper methods used to minimizes the number of lines of code needed above
	
		/**********
	 * <p> Method: createUserRow(User user) </p>
	 * 
	 * <p> Description: Creates and configures a single user row as an HBox for display in the user management list. 
	 * This method assembles all visual components for one user entry, including:
	 * username label, roles label, optional action feedback badge, spacer, action area (with role add/remove ComboBoxes,
	 * confirm and cancel buttons), and a menu button for role management actions.
	 * </p>
	 * 
	 * <p>The row is styled with a dark background, rounded corners, and responsive width binding to the parent list container.
	 * ComboBoxes are initially hidden and only shown when the user selects "Add Role..." or "Remove Role..." from the menu.
	 * A custom ListCell is applied to ensure the prompt text ("Select") reliably reappears after selections and resets.
	 * </p>
	 * 
	 * <p>Event handlers are attached to handle role addition/removal, database updates, UI refresh, and visual feedback
	 * via a persistent colored badge (green for add, red for remove).</p>
	 * 
	 * @param user the User object whose username, roles, and role flags (adminRole, newRole1, newRole2) 
	 *             are used to populate and update this row
	 * @return the fully configured HBox representing one user row, ready to be added to a list or container
	 */

	protected static HBox createUserRow(User user) {
	    HBox userRow = new HBox(4);
	    userRow.setAlignment(Pos.CENTER_LEFT);
	    userRow.setMinHeight(50);
	    userRow.setStyle("-fx-padding: 12 16; -fx-background-color: #000; -fx-background-radius: 15px;");
	    userRow.prefWidthProperty().bind(userListBox.widthProperty().subtract(24));
	    
	    // Username
	    Label usernameLabel = new Label(user.getUserName());
	    usernameLabel.setStyle("-fx-font-family: Montserrat SemiBold; -fx-font-size: 14px; -fx-text-fill: #fff;");
	    usernameLabel.setMinWidth(150);
	    usernameLabel.setMaxWidth(150);
	    
	    // Roles
	    Label rolesLabel = new Label(user.getRoles());
	    rolesLabel.setStyle("-fx-font-family: Montserrat SemiBold; -fx-font-size: 14px; -fx-text-fill: #fff;");
	    rolesLabel.setMinWidth(150);
	    rolesLabel.setMaxWidth(200);
	    rolesLabel.setTranslateX(-30);       
	    
	    // Spacer to push menu button to the right
	    Region spacer = new Region();
	    HBox.setHgrow(spacer, Priority.SOMETIMES);
	    
	    // Action area (initially empty and invisible)
	    HBox actionArea = new HBox(8);
	    actionArea.setAlignment(Pos.CENTER_RIGHT);
	    actionArea.setPrefWidth(0);  // Start with 0 width
	    
	    // Add role combo (initially hidden)
	    ComboBox<String> addCombo = new ComboBox<>();
	    addCombo.setVisible(false);
	    addCombo.setManaged(false);
	    addCombo.setPrefWidth(75);
	    addCombo.setMinWidth(75);   
	    addCombo.getStyleClass().add("role-combo-box");

	    // Remove role combo (initially hidden)
	    ComboBox<String> removeCombo = new ComboBox<>();
	    removeCombo.setVisible(false);
	    removeCombo.setManaged(false);
	    removeCombo.setPrefWidth(75);
	    removeCombo.setMinWidth(75);  
	    removeCombo.getStyleClass().add("role-combo-box");
	   
	   
	    addCombo.setTranslateX(-10);     
	    removeCombo.setTranslateX(-10);  
	    
	    addCombo.setPromptText("Select");
	    removeCombo.setPromptText("Select");
	    
	    
	    // These overides ensure "Select" is added as the 
	    // default value for the combos one repeated uses. 
	    addCombo.setButtonCell(new ListCell<String>() {
	        @Override
	        protected void updateItem(String item, boolean empty) {
	            super.updateItem(item, empty);
	            if (empty || item == null) {
	                setText(addCombo.getPromptText());
	            } else {
	                setText(item);
	            }
	        }
	    });

	    // Same for removeCombo
	    removeCombo.setButtonCell(new ListCell<String>() {
	        @Override
	        protected void updateItem(String item, boolean empty) {
	            super.updateItem(item, empty);
	            if (empty || item == null) {
	                setText(removeCombo.getPromptText());
	            } else {
	                setText(item);
	            }
	        }
	    });

	    
	    HBox.setHgrow(addCombo,    Priority.SOMETIMES);   
	    HBox.setHgrow(removeCombo, Priority.SOMETIMES);
	    
	    // Confirm button (initially hidden)
	    Button confirmBtn = new Button("✓");
	    confirmBtn.setVisible(false);
	    confirmBtn.setManaged(false);
	    confirmBtn.setPrefWidth(35);
	    confirmBtn.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; " +
	                        "-fx-font-size: 16px; -fx-font-family: 'Arial Unicode MS', 'Segoe UI Symbol';");
	    
	    // Cancel button (initially hidden)
	    Button cancelBtn = new Button("✕");
	    cancelBtn.setVisible(false);
	    cancelBtn.setManaged(false);
	    cancelBtn.setPrefWidth(35);
	    cancelBtn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-size: 16px;");
	    
	    actionArea.getChildren().addAll(addCombo, removeCombo, confirmBtn, cancelBtn);
	    
	    // Menu button
	    MenuButton menuButton = new MenuButton("\u22EE"); // vertical ellipsis
	    menuButton.setFont(Font.font("Segoe UI Symbol", 30));
	    menuButton.setStyle(
	        "-fx-background-color: #000;" +
	        "-fx-text-fill: #fff;" +
	        "-fx-cursor: hand;" +
	        "-fx-padding: 0 8;"
	    );
	    menuButton.setMinWidth(40);
	    menuButton.getStyleClass().add("custom-menu-button");
	    menuButton.setFocusTraversable(false); 
	    
	    MenuItem addRoleItem = new MenuItem("Add Role...");
	    MenuItem removeRoleItem = new MenuItem("Remove Role...");
	    
	    // Dynamically show/hide menu items before menu is shown
	    menuButton.setOnShowing((_) -> {
	        // Clear previous menu items
	        menuButton.getItems().clear();

	        // Check which roles the user currently has
	        boolean hasAdmin = user.getAdminRole();
	        boolean hasRole1 = user.getNewRole1();
	        boolean hasRole2 = user.getNewRole2();

	        // Count how many roles the user has
	        int roleCount = 0;
	        if (hasAdmin) roleCount++;
	        if (hasRole1) roleCount++;
	        if (hasRole2) roleCount++;

	        // Only show "Add Role..." if user does not have all roles
	        if (roleCount < 3) {
	            menuButton.getItems().add(addRoleItem);
	        }

	        // Only show "Remove Role..." if user has more than one role
	        if (roleCount > 1) {
	            menuButton.getItems().add(removeRoleItem);
	        }
	    });
	    
	    Label roleActionLabel = new Label();
	    roleActionLabel.setVisible(false);
	    roleActionLabel.setManaged(false);
	    roleActionLabel.setStyle(
	        "-fx-background-color: #2d2d2d;" +
	        "-fx-text-fill: #fff;" +
	        "-fx-padding: 0 10;" +
	        "-fx-background-radius: 999;" +
	        "-fx-font-size: 12px;"
	    );
	    
	    // When "Add Role" clicked
	    addRoleItem.setOnAction(_ -> {
	        // Populate add combo with available roles
	        addCombo.getItems().clear();
	        if (!user.getAdminRole()) addCombo.getItems().add("Admin");
	        if (!user.getNewRole1()) addCombo.getItems().add("Role1");
	        if (!user.getNewRole2()) addCombo.getItems().add("Role2");
	        
	 
	        // Show add combo and buttons
	        addCombo.setVisible(true);
	        addCombo.setManaged(true);
	        removeCombo.setVisible(false);
	        removeCombo.setManaged(false);
	        confirmBtn.setVisible(true);
	        confirmBtn.setManaged(true);
	        cancelBtn.setVisible(true);
	        cancelBtn.setManaged(true);
	        menuButton.setVisible(false);
	        menuButton.setManaged(false);
	    });
	    
	    // When "Remove Role" clicked
	    removeRoleItem.setOnAction(_ -> {
	        // Populate remove combo with current roles
	        removeCombo.getItems().clear();
	        if (user.getAdminRole()) removeCombo.getItems().add("Admin");
	        if (user.getNewRole1()) removeCombo.getItems().add("Role1");
	        if (user.getNewRole2()) removeCombo.getItems().add("Role2");
	        
	     
	        // Show remove combo and buttons
	        removeCombo.setVisible(true);
	        removeCombo.setManaged(true);
	        addCombo.setVisible(false);
	        addCombo.setManaged(false);
	        confirmBtn.setVisible(true);
	        confirmBtn.setManaged(true);
	        cancelBtn.setVisible(true);
	        cancelBtn.setManaged(true);
	        menuButton.setVisible(false);
	        menuButton.setManaged(false);
	    });
	    
	    confirmBtn.setOnAction(_ -> {
	    	
	    	// admin cannot modify other admin account roles
	    	if (user.getAdminRole() && !user.getUserName().equals(theUser.getUserName())) {
	            alertCannotModifyAdmin.setContentText("You cannot modify the roles of another admin!");
	            alertCannotModifyAdmin.showAndWait();
	            return; 
	        }
	    	
	    	
	        if (addCombo.isVisible() && addCombo.getValue() != null) {

	            String roleToAdd = addCombo.getValue();
	            theDatabase.updateUserRole(user.getUserName(), roleToAdd, "true");
	            

	            if (roleToAdd.equals("Admin")) user.setAdminRole(true);
	            else if (roleToAdd.equals("Role1")) user.setRole1User(true);
	            else if (roleToAdd.equals("Role2")) user.setRole2User(true);

	            rolesLabel.setText(user.getRoles());

	            
	            roleActionLabel.setText("Added: " + roleToAdd);
	            roleActionLabel.setStyle(
	                "-fx-background-color: #28a745;" +   // green for added
	                "-fx-text-fill: white;" +
	                "-fx-padding: 4 10;" +
	                "-fx-background-radius: 999;" +
	                "-fx-font-size: 10px;"
	            );
	            roleActionLabel.setVisible(true);
	            roleActionLabel.setManaged(true);

	            // Reset combo...
	            addCombo.setValue(null);
	            addCombo.getSelectionModel().clearSelection();
	            addCombo.setPromptText("Select");
	            addCombo.setStyle(addCombo.getStyle());
	            addCombo.requestLayout();

	        } else if (removeCombo.isVisible() && removeCombo.getValue() != null) {

	            String roleToRemove = removeCombo.getValue();
	            theDatabase.updateUserRole(user.getUserName(), roleToRemove, "false");

	            if (roleToRemove.equals("Admin")) user.setAdminRole(false);
	            else if (roleToRemove.equals("Role1")) user.setRole1User(false);
	            else if (roleToRemove.equals("Role2")) user.setRole2User(false);

	            rolesLabel.setText(user.getRoles());

	            
	            roleActionLabel.setText("Removed: " + roleToRemove);
	            roleActionLabel.setStyle(
	                "-fx-background-color: #dc3545;" +   // red for removed
	                "-fx-text-fill: white;" +
	                "-fx-padding: 4 10;" +
	                "-fx-background-radius: 999;" +
	                "-fx-font-size: 10px;"
	            );
	            roleActionLabel.setVisible(true);
	            roleActionLabel.setManaged(true);

	            // Reset...
	            removeCombo.setValue(null);
	            removeCombo.getSelectionModel().clearSelection();
	            removeCombo.setPromptText("Select");
	            removeCombo.setStyle(removeCombo.getStyle());
	            removeCombo.requestLayout();
	        }

	        hideActionControls(addCombo, removeCombo, confirmBtn, cancelBtn, menuButton);
	    });
	    
	    // Cancel button action
	    cancelBtn.setOnAction(_ -> {
	    	addCombo.setValue(null);
	        addCombo.getSelectionModel().clearSelection();
	        addCombo.setPromptText("Select");
	        addCombo.requestLayout();

	        removeCombo.setValue(null);
	        removeCombo.getSelectionModel().clearSelection();
	        removeCombo.setPromptText("Select");
	        removeCombo.requestLayout();
	    	
	        hideActionControls(addCombo, removeCombo, confirmBtn, cancelBtn, menuButton);
	    });
	    
	    menuButton.getItems().addAll(addRoleItem, removeRoleItem);
	    
	  
	    userRow.getChildren().addAll(usernameLabel, rolesLabel, roleActionLabel, spacer, actionArea, menuButton);
	    
	    return userRow;
	}
	
	/**********
	 * <p> Method: hideActionControls(ComboBox<String> addCombo, ComboBox<String> removeCombo, 
	 * Button confirmBtn, Button cancelBtn, MenuButton menuButton) </p>
	 * 
	 * <p> Description: Hides the action-related controls in a user row and restores the visibility 
	 * of the menu button. This method is called after confirming or canceling a role add/remove operation 
	 * (or on cancel) to clean up the UI by removing the ComboBoxes, confirm button, and cancel button 
	 * from view, while making the ellipsis menu button visible again for future actions. 
	 * </p>
	 * 
	 * <p>It also resets both ComboBoxes by clearing their selected value (setValue(null)) so that 
	 * the prompt text ("Select") can reappear correctly the next time they are shown. 
	 * This helps prevent the known JavaFX behavior where the prompt text fails to display after 
	 * previous selections.</p>
	 * 
	 * @param addCombo    the ComboBox used for adding a new role (initially hidden)
	 * @param removeCombo the ComboBox used for removing an existing role (initially hidden)
	 * @param confirmBtn  the confirmation button (✓) that applies the role change
	 * @param cancelBtn   the cancel button (✕) that discards the operation
	 * @param menuButton  the vertical ellipsis MenuButton that opens the add/remove options
	 */

	private static void hideActionControls(ComboBox<String> addCombo, ComboBox<String> removeCombo, 
	                                       Button confirmBtn, Button cancelBtn, MenuButton menuButton) {
	    addCombo.setVisible(false);
	    addCombo.setManaged(false);
	    removeCombo.setVisible(false);
	    removeCombo.setManaged(false);
	    confirmBtn.setVisible(false);
	    confirmBtn.setManaged(false);
	    cancelBtn.setVisible(false);
	    cancelBtn.setManaged(false);
	    menuButton.setVisible(true);
	    menuButton.setManaged(true);
	    
	    addCombo.setValue(null);
	    removeCombo.setValue(null);
	}
	
	/**********
	 * <p> Method: refreshUserList() </p>
	 * 
	 * <p> Description: Clears and repopulates the user list displayed in the Add/Remove Roles page.
	 * This method fetches the latest list of usernames from the database, retrieves each user's 
	 * account details, constructs a new HBox row for each user using `createUserRow(User user)`, 
	 * and adds it to the `userListBox` VBox. It ensures that any newly added users are immediately 
	 * visible in the GUI without requiring the page to be reloaded.</p>
	 * 
	 * <p>Typically called after a user has been added to the database or when roles are updated, 
	 * so that the displayed list accurately reflects the current state of the system.</p>
	 * 
	 * <p>This method operates on static fields of the class, including `userListBox` and 
	 * `theDatabase`, and is intended for internal use only.</p>
	 */
	private static void refreshUserList() {
	    // Clear current list
	    userListBox.getChildren().clear();

	    // Get updated usernames from the database
	    List<String> allUsernames = theDatabase.getUserList();

	    for (String username : allUsernames) {
	        theDatabase.getUserAccountDetails(username);

	        User user = new User();
	        user.setUserName(username);
	        user.setAdminRole(theDatabase.getCurrentAdminRole());
	        user.setRole1User(theDatabase.getCurrentNewRole1());
	        user.setRole2User(theDatabase.getCurrentNewRole2());

	        HBox userRow = createUserRow(user);
	        userListBox.getChildren().add(userRow);
	    }
	}

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
	
	private static void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x,
			double y){
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
	protected static void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x,
			double y){
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
	protected static void setupComboBoxUI(ComboBox <String> c, String ff, double f, double w,
			double x, double y){
		c.setStyle("-fx-font: " + f + " " + ff + ";");
		c.setMinWidth(w);
		c.setLayoutX(x);
		c.setLayoutY(y);
	}
}
