package testing;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;

import org.junit.jupiter.api.Test;

import entityClasses.User;

public class HW3Task2 {
	private static database.Database theDatabase = applicationMain.FoundationsMain.database;
	private boolean connectedDB = false;
	
	//Create a test user
	User newUser = new User(0, "TestUser", "password", "", "", "", "", "", "", true, false, false, false);
	
	//Shortcut to setting up a test case
	public void setupTestCase() {
		if(!connectedDB) {
		// Connect to the in-memory database
		try {
			// Connect to the database
			theDatabase.connectToDatabase();
			connectedDB = true;
		} catch (SQLException e) {
			System.exit(0);
		}}
		
		//Ensure the user is deleted before testing
		theDatabase.deleteUser(newUser.getUserId());

		//Attempt to register the new user
		try {
			theDatabase.register(newUser);
		} catch (SQLException e) { System.exit(0); }
	}
	
	//Standard sign in/sign out
	@Test
	public void PositiveCase1() {
		//Init test case data
		setupTestCase();
		
		//Now attempt to get the user back
		User testUser = theDatabase.getUserAsObject(newUser.getUserId());
		String testPassword = "password";
		assertEquals(testPassword,testUser.getPassword());
		theDatabase.deleteUser(newUser.getUserId());
		theDatabase.closeConnection();
	}
	
	//Try without entering a password
	@Test
	public void NegativeCase1() {
		//Init test case data
		setupTestCase();
		
		//Now attempt to get the user back
		User testUser = theDatabase.getUserAsObject(newUser.getUserId());
		String testPassword = "";
		assertNotEquals(testPassword,testUser.getPassword());
		theDatabase.deleteUser(newUser.getUserId());
		theDatabase.closeConnection();
	}

	//Try using the incorrect password
	@Test
	public void NegativeCase2() {
		//Init test case data
		setupTestCase();
		
		//Now attempt to get the user back
		User testUser = theDatabase.getUserAsObject(newUser.getUserId());
		String testPassword = "Password";
		assertNotEquals(testPassword,testUser.getPassword());
		theDatabase.deleteUser(newUser.getUserId());
		theDatabase.closeConnection();
	}
	
	//Try with an incorrect user
	@Test
	public void NegativeCase3() {
		//Init test case data
		setupTestCase();
		
		//Now attempt to get the user back
		User testUser = theDatabase.getUserAsObject(newUser.getUserId());
		String testPassword = "password";
		assertNotEquals(testPassword,testUser.getPassword());
		theDatabase.deleteUser(newUser.getUserId());
		theDatabase.closeConnection();
	}
	
	//Attempt SQL injection
	@Test
	public void NegativeCase4() {
		//Init test case data
		setupTestCase();
		
		//Now attempt to get the user back
		User testUser = theDatabase.getUserAsObject(newUser.getUserId());
		String testPassword = "password";
		assertNotEquals(testPassword,testUser.getPassword());
		theDatabase.deleteUser(newUser.getUserId());
		theDatabase.closeConnection();
	}
	
	//Attempt a substring of the password
	@Test
	public void NegativeCase5() {
		//Init test case data
		setupTestCase();
		
		//Now attempt to get the user back
		User testUser = theDatabase.getUserAsObject(newUser.getUserId());
		String testPassword = "pass";
		assertNotEquals(testPassword,testUser.getPassword());
		theDatabase.deleteUser(newUser.getUserId());
		theDatabase.closeConnection();
	}
	
	//Attempt a substring of the username
	@Test
	public void NegativeCase6() {
		//Init test case data
		setupTestCase();
		
		//Now attempt to get the user back
		User testUser = theDatabase.getUserAsObject(12);
		String testPassword = "password";
		assertNotEquals(testPassword,testUser.getPassword());
		theDatabase.deleteUser(newUser.getUserId());
		theDatabase.closeConnection();
	}
	
	//Let's say the attacker does actually know the password. Let's try some basic SQL injection
	@Test
	public void NegativeCase7() {
		//Init test case data
		setupTestCase();
		
		//Now attempt to get the user back
		User testUser = theDatabase.getUserAsObject(newUser.getUserId());
		String testPassword = "password";
		assertNotEquals(testPassword,testUser.getPassword());
		theDatabase.deleteUser(newUser.getUserId());
		theDatabase.closeConnection();
	}
	
	//Finally, let's make a test case where we assume the attacker knows nothing and wants
	//to fetch the first user
	@Test
	public void NegativeCase8() {
		//Init test case data
		setupTestCase();
		
		//Now attempt to get the user back
		User testUser = theDatabase.getUserAsObject(newUser.getUserId());
		String testPassword = "password";
		assertNotEquals(testPassword,testUser.getPassword());
		theDatabase.deleteUser(newUser.getUserId());
		theDatabase.closeConnection();
	}
}
