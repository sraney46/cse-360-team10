package guiTools;

import javafx.scene.Scene;
import javafx.stage.Stage;
import entityClasses.User;


/*******
 * <p> Title: GUISingleRoleDispatch Class. </p>
 * 
 * <p> Description: The class dispatches the execution to the appropriate role's home
 * page when the user has only one role.  This is not actually a GUI page... it just dispatches
 * to an actual GUI page for the specified role.
 * 
 * WHen a user has more than one role, a different
 * class, GUIMultipleRoleHomePage, asks the user which of their roles do they want to use,
 * and then it dispatches the user to that role's home page.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00		2025-04-20 Initial version
 *  
 */

public class GUISingleRoleDispatch {
	
	/**********************************************************************************************

	Attributes
	
	**********************************************************************************************/
	
	// These are the application values required by the user interface

	
	public Scene theViewStudentHomeScene;


	
	/**********************************************************************************************

	Constructors
	
	**********************************************************************************************/

	
	/**********
	 * <p> Method: GUISingleRoleDispatch() </p>
	 * 
	 * <p> Description: This method initializes all the elements of the graphical user interface. 
	 * This method determines the location, size, font, color, and change and event handlers for 
	 * each GUI object. </p>
	 * 
	 */
	public GUISingleRoleDispatch() {
	}

	
	/**********
	 * <p> Method: doSingleRoleDispatch(Stage ps, Pane theRoot, Database database, User user) </p>
	 * 
	 * <p> Description: This method is called after a GUI page has already been established and
	 * it is being display with potentially new contents for the various GUI elements. </p>
	 * 
	 * @param ps specifies the JavaFX Stage to be used for this GUI and it's methods
	 * 
	 * @param theRoot specifies the JavaFX Pane to be used for this GUI and it's methods
	 * 
	 * @param database specifies the Database to be used by this GUI and it's methods
	 * 
	 * @param user specifies the User for this GUI and it's methods
	 * 
	 */
	public static void doSingleRoleDispatch(Stage ps, User user) {
		System.out.println("************** Just entered single role dispatch page");

		if (user.getAdminRole()) {
			guiAdminHome.ViewAdminHome.displayAdminHome(ps, user);
		} else if (user.getNewRole1()) {
			guiRole1.ViewRole1Home.displayRole1Home(ps, user);
		} else if (user.getNewRole2()) {
			guiRole2.ViewRole2Home.displayRole2Home(ps, user);
		} else {
			// Invalid role
			System.out.println("*** ERROR *** GUISingleRoleDispatch was asked to dispatch to " +
			"a role that is not supported!");
		}
	}
}
