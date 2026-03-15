package guiDiscussionForum;

import database.Database;

/*******
 * <p> Title: ControllerDiscussionForum Class. </p>
 * 
 * <p> Description: This class provides the controller
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

public class ControllerDiscussionForum {
	
	private static Database theDatabase = applicationMain.FoundationsMain.database;
	
	/*-********************************************************************************************

	User Interface Actions for this page
	
	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and 
	the Model is often just a stub, or will be a singleton instantiated object.
	
	 */

	/**
	 * Default constructor is not used.
	 */
	public ControllerDiscussionForum() {
	}
	
	
	
	/**********
	 * <p> Method: performReturn() </p>
	 * 
	 * <p> Description: This method returns the user back to the appropriate role page. </p>
	 * 
	 */
	protected static void performReturn() {
		
		System.out.println("return page is " + ViewDiscussionForum.returnPage);
		
		// Admin
		if (ViewDiscussionForum.returnPage.compareTo("Admin") == 0) {
			guiAdminHome.ViewAdminHome.displayAdminHome(ViewDiscussionForum.theStage,
					ViewDiscussionForum.theUser);
		}
		
		// Staff
		else if (ViewDiscussionForum.returnPage.compareTo("Staff") == 0) {
			guiRole1.ViewRole1Home.displayRole1Home(ViewDiscussionForum.theStage,
					ViewDiscussionForum.theUser);
		}
		
		// Student
		else if (ViewDiscussionForum.returnPage.compareTo("Student") == 0) {
			guiRole2.ViewRole2Home.displayRole2Home(ViewDiscussionForum.theStage,
					ViewDiscussionForum.theUser);
		}
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
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewDiscussionForum.theStage);
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