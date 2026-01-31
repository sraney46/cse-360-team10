package applicationMain;
	
import java.sql.SQLException;
import database.Database;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.text.Font;

/*******
 * <p> Title: FoundationsMain Class </p>
 *
 * <p> Description: This is the Main Class that launches the Foundations demonstration
 * application.  This is a JavaFX application designed to serve as a foundation for the CSE360
 * Individual Homework and Team Project assignments and demonstrate the use of the following:</p>
 *
 * <p>• The Singleton Design Pattern - The GUI uses the MVC Design Pattern, and each of the
 *      		three components is instantiated once.  This requires special coding.  See this
 *      		article for more insights: https://en.wikipedia.org/wiki/Singleton_pattern</p>
 * <p>• JavaDoc documentation</p>
 * <p>• Internal documentation beyond JavaDoc with a focus on "why" (as well as "what" when it
 *             might not be obvious)  The goal of the documentation is to help those who follow
 *             you to benefit from your work without needing to do all the research and the
 *             sometimes frustrating, if not painful, experimentation until you get it working.
 *             This is especially true when the obvious way to do something does not work!</p>
 *
 * <p> On startup, the application tries to connect with the Foundations in-memory database.  If a
 * connection to the database is currently active, an alert is displayed explaining the situation
 * to the users and the application quits when the user acknowledges the alert.</p>
 *
 *  <p>If the connection is successful, a check is made to see if the database is empty. If so, this
 * must be the first execution of the application and the person running the application is assumed
 * to be an administrator.  That user is required to provide an Admin username and password before 
 * anything else can happen.  Doing this eliminates a common weakness of "hard coded credentials".
 * With that done, the admin can provide more details to the system (e.g., name and email address),
 * and then proceed to do other admin activities.</p>
 * 
 *  <p>If the database is not empty, the system brings up the standard login page and requires an 
 * an existing user to log in or a potential user to provide an invitation code to establish a new
 * account.  Once logged or after creating an account, the user can perform what ever role(s) set
 * for that user.  This class's method stops as soon as the Graphical User Interface (GUI) for the
 * one of the two options has been set up.  From that point forward, and actions are performed in
 * reaction to the user engaging with widgets on the currently visible page.</p>
 * 
 *  <p>This application uses singletons and Model View Controller (MVC) View pattern to control the 
 * use of memory by avoiding multiple copies of the same page.</p>
 * 
 *  <p>This application does not use the command line arguments *i.e., "String[] args"), but Java and
 * JavaFX in Eclipse requires they be made available in the application's main method, even if they
 * are not needed.</p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 *  <p>@author Lynn Robert Carter</p>
 *
 * @version 3.00	2025-08-17 Rewrite of this application for the Fall offering of CSE 360 and
 * other ASU courses.
 * @version 3.02	2025-12-17 Enhancements in support of Spring 2026
 */

public class FoundationsMain extends Application {
	
	/*-*******************************************************************************************

	Attributes
	
	**********************************************************************************************/
	
	// These are the application values required by the user interface.  All the other classes
	// access these constants to provide a uniform window size.	
	public final static double WINDOW_WIDTH = 800;
	public final static double WINDOW_HEIGHT = 600;

	// These attributes establish the database and the fixed reference to it for the rest of the
	// application so we do not need to keep passing the reference in parameters to the rest of the
	// system for other methods that need it can access it.
	public static Database database = new Database();
    private Alert databaseInUse = new Alert(AlertType.INFORMATION);

	public static int activeHomePage = 0;		// Which role's home page is currently active?
												// Role 0 is the admin role number
	@Override
	public void start(Stage theStage) {
		
		// Load custom fonts FIRST
		Font.loadFont(
		    getClass().getResource("/fonts/Montserrat-Black.ttf").toExternalForm(),
		    14
		);
		Font.loadFont(
		    getClass().getResource("/fonts/Montserrat-Regular.ttf").toExternalForm(),
		    14
		);
		Font.loadFont(
		    getClass().getResource("/fonts/Montserrat-Medium.ttf").toExternalForm(),
		    14
		);
		Font.loadFont(
		    getClass().getResource("/fonts/Montserrat-SemiBold.ttf").toExternalForm(),
		    14
		);
		Font.loadFont(
		    getClass().getResource("/fonts/Montserrat-Bold.ttf").toExternalForm(),
		    14
		);
		
		System.out.println("test discord webook 2");

		
		
		// Connect to the in-memory database
		try {
			// Connect to the database
			database.connectToDatabase();
		} catch (SQLException e) {
			// If the connection request fails, it usually means some other app is using it
			databaseInUse.setTitle("*** ERROR ***");
			databaseInUse.setHeaderText("Database Is Already Being Used");
			databaseInUse.setContentText("Please stop the other instance and try again!");
			databaseInUse.showAndWait();
			System.exit(0);
		}
		
		// If the database is empty, no users have been established, so this user must be an admin
		// user doing initial system startup activities and we need to set that admin's username
		// and password using a special start you page.
		if (database.isDatabaseEmpty()) {
			// This is a first use, so have the user set up the admin account
			guiFirstAdmin.ViewFirstAdmin.displayFirstAdmin(theStage);	
		}
		else
			// This is not a first use, so set up for the user to log in or create a new account
			guiUserLogin.ViewUserLogin.displayUserLogin(theStage);
		
		// With the JavaFX pages set up, this thread of the execution comes to an end.
	}

	/*******
	 * <p> Title: FoundationsMain main method that starts up JavaFX</p>
	 * 
	 * <p> Description: This main method does not perform any special function for this application
	 * beyond launching JavaFX.  Java and Eclipse require the application to be able to used the
	 * command line parameters, if needed.  This application does not use them.  If they are
	 * provided, the application will ignore them.</p>
	 * 
	 * @param String[] args   The array of command lines parameters.  These are not used.
	 */
	public static void main(String[] args) {
		launch(args);	// The launch method loads JavaFX and invokes its initialization.  When it
						// is done, it calls the start method shown above.
	}
}
