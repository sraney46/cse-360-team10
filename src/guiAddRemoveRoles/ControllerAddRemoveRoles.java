package guiAddRemoveRoles;

import database.Database;
import guiUserLogin.ViewUserLogin;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import entityClasses.User;

/*******
 * <p> Title: ControllerAddRemoveRoles Class. </p>
 * 
 * <p> Description: The Java/FX-based Add Remove Roles Page.  This class provides the controller
 * actions basic on the user's use of the JavaFX GUI widgets defined by the View class.
 * 
 * This page has one of the more complex Controller Classes due to the fact that the changing the
 * values of widgets changes the layout of the page.  It is up to the Controller to determine what
 * to do and it involves the proper elements from View Class for this GUI page.
 * 
 * The class has been written assuming that the View or the Model are the only class methods that
 * can invoke these methods.  This is why each has been declared at "protected".  Do not change any
 * of these methods to public.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00		2025-08-17 Initial version
 * @version 1.01		2025-09-16 Update Javadoc documentation *  
 */

public class ControllerAddRemoveRoles {
	
	/*-********************************************************************************************

	User Interface Actions for this page
	
	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and 
	the Model is often just a stub, or will be a singleton instantiated object.
	
	 */

	/**
	 * Default constructor is not used.
	 */
	public ControllerAddRemoveRoles() {
	}
	
	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;		

	
	
	
	/**********
	 * <p> Method: repaintTheWindow() </p>
	 * 
	 * <p> Description: This method determines the current state of the window and then establishes
	 * the appropriate list of widgets in the Pane to show the proper set of current values. </p>
	 * 
	 */
	protected static void repaintTheWindow() {
		// Clear what had been displayed
		ViewAddRemoveRoles.theRootPane.getChildren().clear();
		
		// Determine which of the two views to show to the user
		if (ViewAddRemoveRoles.theSelectedUser.compareTo("<Select a User>") == 0) {
			// Only show the request to select a user to be updated and the ComboBox
			ViewAddRemoveRoles.theRootPane.getChildren().addAll(
					ViewAddRemoveRoles.label_PageTitle, ViewAddRemoveRoles.label_UserDetails, 
					ViewAddRemoveRoles.scrollPane,
					ViewAddRemoveRoles.button_UpdateThisUser, ViewAddRemoveRoles.line_Separator1,
					// ViewAddRemoveRoles.label_SelectUser, ViewAddRemoveRoles.combobox_SelectUser, 
					ViewAddRemoveRoles.line_Separator4, ViewAddRemoveRoles.button_Return,
					ViewAddRemoveRoles.button_Logout, ViewAddRemoveRoles.button_Quit);
		}
		else {
			// Show all the fields as there is a selected user (as opposed to the prompt)
			ViewAddRemoveRoles.theRootPane.getChildren().addAll(
					ViewAddRemoveRoles.label_PageTitle, ViewAddRemoveRoles.label_UserDetails,
					ViewAddRemoveRoles.button_UpdateThisUser, ViewAddRemoveRoles.line_Separator1,
					ViewAddRemoveRoles.scrollPane,
					//ViewAddRemoveRoles.label_SelectUser,
					//ViewAddRemoveRoles.combobox_SelectUser, 
					//ViewAddRemoveRoles.label_CurrentRoles,
					//ViewAddRemoveRoles.label_SelectRoleToBeAdded,
					//ViewAddRemoveRoles.combobox_SelectRoleToAdd,
					//ViewAddRemoveRoles.button_AddRole,
					//ViewAddRemoveRoles.label_SelectRoleToBeRemoved,
					//ViewAddRemoveRoles.combobox_SelectRoleToRemove,
					//ViewAddRemoveRoles.button_RemoveRole,
					ViewAddRemoveRoles.line_Separator4, 
					ViewAddRemoveRoles.button_Return,
					ViewAddRemoveRoles.button_Logout,
					ViewAddRemoveRoles.button_Quit);
		}
		
		// Add the list of widgets to the stage and show it
		
		// Set the title for the window
		ViewAddRemoveRoles.theStage.setTitle("");
		ViewAddRemoveRoles.theStage.setScene(ViewAddRemoveRoles.theAddRemoveRolesScene);
		ViewAddRemoveRoles.theStage.show();
	}
	
	
	
	/**********
	 * <p> Method: performReturn() </p>
	 * 
	 * <p> Description: This method returns the user (who must be an Admin as only admins are the
	 * only users who have access to this page) to the Admin Home page. </p>
	 * 
	 */
	protected static void performReturn() {
		guiAdminHome.ViewAdminHome.displayAdminHome(ViewAddRemoveRoles.theStage,
				ViewAddRemoveRoles.theUser);
	}
	
	
	/**********
	 * <p> Method: performLogout() </p>
	 * 
	 * <p> Description: This method logs out the current user and proceeds to the normal login
	 * page where existing users can log in or potential new users with a invitation code can
	 * start the process of setting up an account. </p>
	 * 
	 */
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewAddRemoveRoles.theStage);
	}
	
	
	/**********
	 * <p> Method: performQuit() </p>
	 * 
	 * <p> Description: This method terminates the execution of the program.  It leaves the
	 * database in a state where the normal login page will be displayed when the application is
	 * restarted.</p>
	 * 
	 */
	protected static void performQuit() {
		System.exit(0);
	}
}