package database;

import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import entityClasses.User;

/*******
 * <p>
 * Title: Database Class.
 * </p>
 * 
 * <p>
 * Description: This is an in-memory database built on H2. Detailed
 * documentation of H2 can
 * be found at https://www.h2database.com/html/main.html (Click on "PDF (2MP)
 * for a PDF of 438 pages
 * on the H2 main page.) This class leverages H2 and provides numerous special
 * supporting methods.
 * </p>
 * 
 * <p>
 * Copyright: Lynn Robert Carter © 2025
 * </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 2.00 2025-04-29 Updated and expanded from the version produce by on
 *          a previous
 *          version by Pravalika Mukkiri and Ishwarya Hidkimath Basavaraj
 * @version 2.01 2025-12-17 Minor updates for Spring 2026
 */

/*
 * The Database class is responsible for establishing and managing the
 * connection to the database,
 * and performing operations such as user registration, login validation,
 * handling invitation
 * codes, and numerous other database related functions.
 */
public class Database {

  // JDBC driver name and database URL
  static final String JDBC_DRIVER = "org.h2.Driver";
  static final String DB_URL = "jdbc:h2:~/FoundationDatabase";

  // Database credentials
  static final String USER = "sa";
  static final String PASS = "";

  // Shared variables used within this class
  private Connection connection = null; // Singleton to access the database
  private Statement statement = null; // The H2 Statement is used to construct queries

  // These are the easily accessible attributes of the currently logged-in user
  // This is only useful for single user applications
  private String currentUsername;
  private String currentPassword;
  private String currentOTP;
  private String currentFirstName;
  private String currentMiddleName;
  private String currentLastName;
  private String currentPreferredFirstName;
  private String currentEmailAddress;
  private boolean currentAdminRole;
  private boolean currentNewRole1;
  private boolean currentNewRole2;
  private boolean isTemporaryPassword;

  /*******
   * <p>
   * Method: Database
   * </p>
   * 
   * <p>
   * Description: The default constructor used to establish this singleton object.
   * </p>
   * 
   */

  public Database() {

  }

  /*******
   * <p>
   * Method: connectToDatabase
   * </p>
   * 
   * <p>
   * Description: Used to establish the in-memory instance of the H2 database from
   * secondary
   * storage.
   * </p>
   *
   * @throws SQLException when the DriverManager is unable to establish a
   *                      connection
   * 
   */
  public void connectToDatabase() throws SQLException {
    try {
      Class.forName(JDBC_DRIVER); // Load the JDBC driver
      connection = DriverManager.getConnection(DB_URL, USER, PASS);
      statement = connection.createStatement();
      // You can use this command to clear the database and restart from fresh.
//       statement.execute("DROP ALL OBJECTS");

      createTables(); // Create the necessary tables if they don't exist
    } catch (ClassNotFoundException e) {
      System.err.println("JDBC Driver not found: " + e.getMessage());
    }
  }

  /*******
   * <p>
   * Method: createTables
   * </p>
   * 
   * <p>
   * Description: Used to create new instances of the two database tables used by
   * this class.
   * </p>
   * 
   */
  private void createTables() throws SQLException {
    String userTable = "CREATE TABLE IF NOT EXISTS userDB ("
        + "id INT AUTO_INCREMENT PRIMARY KEY, "
        + "userName VARCHAR(255) UNIQUE, "
        + "password VARCHAR(255), "
        + "OTP VARCHAR(255), "
        + "firstName VARCHAR(255), "
        + "middleName VARCHAR(255), "
        + "lastName VARCHAR (255), "
        + "preferredFirstName VARCHAR(255), "
        + "emailAddress VARCHAR(255), "
        + "adminRole BOOL DEFAULT FALSE, "
        + "newRole1 BOOL DEFAULT FALSE, "
        + "newRole2 BOOL DEFAULT FALSE, "
        + "isOneTimePW BOOL DEFAULT FALSE)";
    statement.execute(userTable);

    String invitationCodesTable = "CREATE TABLE IF NOT EXISTS InvitationCodes ("
        + "code VARCHAR(10) PRIMARY KEY, "
        + "emailAddress VARCHAR(255), "
        + "role VARCHAR(10), "
        + "expirationTime BIGINT)";
    statement.execute(invitationCodesTable);
  }

  /*******
   * <p>
   * Method: isDatabaseEmpty
   * </p>
   * 
   * <p>
   * Description: If the user database has no rows, true is returned, else false.
   * </p>
   * 
   * @return true if the database is empty, else it returns false
   * 
   */
  public boolean isDatabaseEmpty() {
    String query = "SELECT COUNT(*) AS count FROM userDB";
    try {
      ResultSet resultSet = statement.executeQuery(query);
      if (resultSet.next()) {
        return resultSet.getInt("count") == 0;
      }
    } catch (SQLException e) {
      return false;
    }
    return true;
  }

  /*******
   * <p>
   * Method: getNumberOfUsers
   * </p>
   * 
   * <p>
   * Description: Returns an integer .of the number of users currently in the user
   * database.
   * </p>
   * 
   * @return the number of user records in the database.
   * 
   */
  public int getNumberOfUsers() {
    String query = "SELECT COUNT(*) AS count FROM userDB";
    try {
      ResultSet resultSet = statement.executeQuery(query);
      if (resultSet.next()) {
        return resultSet.getInt("count");
      }
    } catch (SQLException e) {
      return 0;
    }
    return 0;
  }

  /*******
   * <p>
   * Method: register(User user)
   * </p>
   * 
   * <p>
   * Description: Creates a new row in the database using the user parameter.
   * </p>
   * 
   * @throws SQLException when there is an issue creating the SQL command or
   *                      executing it.
   * 
   * @param user specifies a user object to be added to the database.
   * 
   */
  public void register(User user) throws SQLException {
    String insertUser = "INSERT INTO userDB (userName, password, OTP, firstName, middleName, "
        + "lastName, preferredFirstName, emailAddress, adminRole, newRole1, newRole2, isOneTimePW ) "
        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
      pstmt.setString(1, user.getUserName());
      pstmt.setString(2, user.getPassword());
      pstmt.setString(3, user.getOTP());
      pstmt.setString(4, user.getFirstName());
      pstmt.setString(5, user.getMiddleName());
      pstmt.setString(6, user.getLastName());
      pstmt.setString(7, user.getPreferredFirstName());
      pstmt.setString(8, user.getEmailAddress());
      pstmt.setBoolean(9, user.getAdminRole());
      pstmt.setBoolean(10, user.getNewRole1());
      pstmt.setBoolean(11, user.getNewRole2());
      pstmt.setBoolean(12, user.isTemporaryPassword());
      pstmt.executeUpdate();
    }
  }

  public void populateDatabaseWithTestUsers(User currentUser) {
	    try {
	      // Drop everything to start fresh
	      statement.execute("DROP ALL OBJECTS");
	      System.out.println("All objects dropped.");

	      // Recreate tables
	      createTables();
	      System.out.println("Tables recreated.");

	      // Insert test users - NOW INCLUDING OTP COLUMN
	      String sql = "INSERT INTO userDB "
	          + "(userName, password, OTP, firstName, middleName, lastName, preferredFirstName, emailAddress, "
	          + "adminRole, newRole1, newRole2, isOneTimePW) "
	          + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	      try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	        for (int i = 1; i <= 25; i++) {
	          String username;
	          String password;
	          String otp;  // Add OTP variable
	          String firstName;
	          String middleName;
	          String lastName;
	          String preferredFirstName;
	          String email;
	          boolean isAdmin;
	          boolean role1;
	          boolean role2;
	          boolean oneTimePw;

	          if (i == 1) {
	            // Use the current user's data for the first entry
	            username = currentUser.getUserName();
	            password = currentUser.getPassword();
	            otp = currentUser.getOTP();  
	            firstName = currentUser.getFirstName() != null ? currentUser.getFirstName() : "";
	            middleName = currentUser.getMiddleName() != null ? currentUser.getMiddleName() : "";
	            lastName = currentUser.getLastName() != null ? currentUser.getLastName() : "";
	            preferredFirstName = currentUser.getPreferredFirstName() != null ? currentUser.getPreferredFirstName() : "";
	            email = currentUser.getEmailAddress() != null ? currentUser.getEmailAddress() : "";
	            isAdmin = currentUser.getAdminRole();
	            role1 = currentUser.getNewRole1();
	            role2 = currentUser.getNewRole2();
	            oneTimePw = false;
	          } else {
	            // Generate test users starting from user2
	            username = "user" + i;
	            password = "Password123!";
	            otp = "";  // Set OTP to empty string for all test users
	            firstName = "TestFirst" + i;
	            middleName = "";
	            lastName = "";
	            preferredFirstName = "";
	            email = "user" + i + "@example.com";

	            isAdmin = (i % 5 == 0); // Every 5th user is admin
	            role1 = !isAdmin && (i % 2 == 0);
	            role2 = !isAdmin && !role1;
	            oneTimePw = false;
	          }

	          pstmt.setString(1, username);
	          pstmt.setString(2, password);
	          pstmt.setString(3, otp);  // Add OTP parameter (empty string)
	          pstmt.setString(4, firstName);
	          pstmt.setString(5, middleName);
	          pstmt.setString(6, lastName);
	          pstmt.setString(7, preferredFirstName);
	          pstmt.setString(8, email);
	          pstmt.setBoolean(9, isAdmin);
	          pstmt.setBoolean(10, role1);
	          pstmt.setBoolean(11, role2);
	          pstmt.setBoolean(12, oneTimePw);

	          pstmt.addBatch();
	        }

	        pstmt.executeBatch();
	        System.out.println("Inserted 25 test users into userDB successfully.");
	        System.out.println("First user preserved: " + currentUser.getUserName());
	      }

	    } catch (SQLException e) {
	      e.printStackTrace();
	    }
	  }

  /*******
   * <p>
   * Method: List getUserList()
   * </p>
   * 
   * <P>
   * Description: Generate an List of Strings, one for each user in the database,
   * starting with "<Select User>" at the start of the list.
   * </p>
   * 
   * @return a list of userNames found in the database.
   */
  public List<String> getUserList() {
    List<String> userList = new ArrayList<String>();
    // Commenting out this line, having this will just cause more problems than it
    // solves
    // userList.add("<Select a User>");
    String query = "SELECT userName FROM userDB";
    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
      ResultSet rs = pstmt.executeQuery();
      while (rs.next()) {
        userList.add(rs.getString("userName"));
      }
    } catch (SQLException e) {
      return null;
    }
    // System.out.println(userList);
    return userList;
  }

  /*******
   * <p>
   * Method: List getUserListAdmin()
   * </p>
   * 
   * <P>
   * Description: Generate an List of Strings, one for each admin user in the
   * database,
   * starting with "<Select User>" at the start of the list.
   * </p>
   * 
   * @return a list of userNames found in the database.
   */
  public List<String> getUserListAdmin() {
    List<String> userList = new ArrayList<String>();
    // Commenting out this line, having this will just cause more problems than it
    // solves
    // userList.add("<Select a User>");
    String query = "SELECT userName FROM userDB WHERE adminRole = TRUE";
    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
      ResultSet rs = pstmt.executeQuery();
      while (rs.next()) {
        userList.add(rs.getString("userName"));
      }
    } catch (SQLException e) {
      return null;
    }
    // System.out.println(userList);
    return userList;
  }

  /*******
   * <p>
   * Method: boolean loginAdmin(User user)
   * </p>
   * 
   * <p>
   * Description: Check to see that a user with the specified username, password,
   * and role
   * is the same as a row in the table for the username, password, and role.
   * </p>
   * 
   * @param user specifies the specific user that should be logged in playing the
   *             Admin role.
   * 
   * @return true if the specified user has been logged in as an Admin else false.
   * 
   */
  public boolean loginAdmin(User user) {
    // Validates an admin user's login credentials so the user can login in as an
    // Admin.
    String query = "SELECT * FROM userDB WHERE userName = ? AND password = ? AND "
        + "adminRole = TRUE";
    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
      pstmt.setString(1, user.getUserName());
      pstmt.setString(2, user.getPassword());
      ResultSet rs = pstmt.executeQuery();
      return rs.next(); // If a row is returned, rs.next() will return true
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  /*******
   * <p>
   * Method: boolean loginRole1(User user)
   * </p>
   * 
   * <p>
   * Description: Check to see that a user with the specified username, password,
   * and role
   * is the same as a row in the table for the username, password, and role.
   * </p>
   * 
   * @param user specifies the specific user that should be logged in playing the
   *             Student role.
   * 
   * @return true if the specified user has been logged in as an Student else
   *         false.
   * 
   */
  public boolean loginRole1(User user) {
    // Validates a student user's login credentials.
    String query = "SELECT * FROM userDB WHERE userName = ? AND password = ? AND "
        + "newRole1 = TRUE";
    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
      pstmt.setString(1, user.getUserName());
      pstmt.setString(2, user.getPassword());
      ResultSet rs = pstmt.executeQuery();
      return rs.next();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  /*******
   * <p>
   * Method: boolean loginRole2(User user)
   * </p>
   * 
   * <p>
   * Description: Check to see that a user with the specified username, password,
   * and role
   * is the same as a row in the table for the username, password, and role.
   * </p>
   * 
   * @param user specifies the specific user that should be logged in playing the
   *             Reviewer role.
   * 
   * @return true if the specified user has been logged in as an Student else
   *         false.
   * 
   */
  // Validates a reviewer user's login credentials.
  public boolean loginRole2(User user) {
    String query = "SELECT * FROM userDB WHERE userName = ? AND password = ? AND "
        + "newRole2 = TRUE";
    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
      pstmt.setString(1, user.getUserName());
      pstmt.setString(2, user.getPassword());
      ResultSet rs = pstmt.executeQuery();
      return rs.next();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  /*******
   * <p>
   * Method: boolean doesUserExist(User user)
   * </p>
   * 
   * <p>
   * Description: Check to see that a user with the specified username is in the
   * table.
   * </p>
   * 
   * @param userName specifies the specific user that we want to determine if it
   *                 is in the table.
   * 
   * @return true if the specified user is in the table else false.
   * 
   */
  // Checks if a user already exists in the database based on their userName.
  public boolean doesUserExist(String userName) {
    String query = "SELECT COUNT(*) FROM userDB WHERE userName = ?";
    try (PreparedStatement pstmt = connection.prepareStatement(query)) {

      pstmt.setString(1, userName);
      ResultSet rs = pstmt.executeQuery();

      if (rs.next()) {
        // If the count is greater than 0, the user exists
        return rs.getInt(1) > 0;
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false; // If an error occurs, assume user doesn't exist
  }

  /*******
   * <p>
   * Method: int getNumberOfRoles(User user)
   * </p>
   * 
   * <p>
   * Description: Determine the number of roles a specified user plays.
   * </p>
   * 
   * @param user specifies the specific user that we want to determine if it is in
   *             the table.
   * 
   * @return the number of roles this user plays (0 - 5).
   * 
   */
  // Get the number of roles that this user plays
  public int getNumberOfRoles(User user) {
    int numberOfRoles = 0;
    if (user.getAdminRole())
      numberOfRoles++;
    if (user.getNewRole1())
      numberOfRoles++;
    if (user.getNewRole2())
      numberOfRoles++;
    return numberOfRoles;
  }

  /*******
   * <p>
   * Method: String generateInvitationCode(String emailAddress, String role)
   * </p>
   * 
   * <p>
   * Description: Given an email address and a roles, this method establishes and
   * invitation
   * code and adds a record to the InvitationCodes table. When the invitation code
   * is used, the
   * stored email address is used to establish the new user and the record is
   * removed from the
   * table.
   * </p>
   * 
   * @param emailAddress specifies the email address for this new user.
   * 
   * @param role         specified the role that this new user will play.
   * 
   * @return the code of six characters so the new user can use it to securely
   *         setup an account.
   * 
   */
  // Generates a new invitation code and inserts it into the database.
  public String generateInvitationCode(String emailAddress, String role) {
    String code = UUID.randomUUID().toString().substring(0, 6); // Generate a random 6-character code
    String query = "INSERT INTO InvitationCodes (code, emailaddress, role, expirationTime) VALUES (?, ?, ?, ?)";
    int secondsDelay = 60 * 60 * 12; // Amount to delay timer in seconds
    long timeStamp = System.currentTimeMillis() + (secondsDelay * 1000); // Get the current time, and add amount of
                                                                         // seconds

    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
      pstmt.setString(1, code);
      pstmt.setString(2, emailAddress);
      pstmt.setString(3, role);
      pstmt.setLong(4, timeStamp);
      pstmt.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return code;
  }

  /*******
   * <p>
   * Method: int getNumberOfInvitations()
   * </p>
   * 
   * <p>
   * Description: Determine the number of outstanding invitations in the table.
   * </p>
   * 
   * @return the number of invitations in the table.
   * 
   */
  // Number of invitations in the database
  public int getNumberOfInvitations() {
    String query = "SELECT COUNT(*) AS count FROM InvitationCodes";
    try {
      ResultSet resultSet = statement.executeQuery(query);
      if (resultSet.next()) {
        return resultSet.getInt("count");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return 0;
  }

  /*******
   * <p>
   * Method: boolean emailaddressHasBeenUsed(String emailAddress)
   * </p>
   * 
   * <p>
   * Description: Determine if an email address has been user to establish a user.
   * </p>
   * 
   * @param emailAddress is a string that identifies a user in the table
   * 
   * @return true if the email address is in the table, else return false.
   * 
   */
  // Check to see if an email address is already in the database
  public boolean emailaddressHasBeenUsed(String emailAddress) {
    String query = "SELECT COUNT(*) AS count FROM InvitationCodes WHERE emailAddress = ?";
    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
      pstmt.setString(1, emailAddress);
      ResultSet rs = pstmt.executeQuery();
      // System.out.println(rs);
      if (rs.next()) {
        // Mark the code as used
        return rs.getInt("count") > 0;
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  /*******
   * <p>
   * Method: String getRoleGivenAnInvitationCode(String code)
   * </p>
   * 
   * <p>
   * Description: Get the role associated with an invitation code.
   * </p>
   * 
   * @param code is the 6 character String invitation code
   * 
   * @return the role for the code or an empty string.
   * 
   */
  // Obtain the roles associated with an invitation code.
  public String getRoleGivenAnInvitationCode(String code) {
    String query = "SELECT * FROM InvitationCodes WHERE code = ?";
    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
      pstmt.setString(1, code);
      ResultSet rs = pstmt.executeQuery();
      if (rs.next()) {
        return rs.getString("role");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return "";
  }

  /*******
   * <p>
   * Method: bool validateUserCodeTime(String code)
   * </p>
   * 
   * <p>
   * Description: Validate the user code's time stamp to check for expiration.
   * </p>
   * 
   * @param code is the 6 character String invitation code
   * 
   * @return whether the timestamp for the expiration has past. Expired codes will
   *         return false.
   * 
   */
  // Obtain the roles associated with an invitation code.
  public boolean validateUserCodeTime(String code) {
    String query = "SELECT * FROM InvitationCodes WHERE code = ?";
    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
      pstmt.setString(1, code);
      ResultSet rs = pstmt.executeQuery();
      if (rs.next()) {
        return rs.getLong("expirationTime") >= System.currentTimeMillis();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  /*******
   * <p>
   * Method: String getEmailAddressUsingCode (String code )
   * </p>
   * 
   * <p>
   * Description: Get the email addressed associated with an invitation code.
   * </p>
   * 
   * @param code is the 6 character String invitation code
   * 
   * @return the email address for the code or an empty string.
   * 
   */
  // For a given invitation code, return the associated email address of an empty
  // string
  public String getEmailAddressUsingCode(String code) {
    String query = "SELECT emailAddress FROM InvitationCodes WHERE code = ?";
    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
      pstmt.setString(1, code);
      ResultSet rs = pstmt.executeQuery();
      if (rs.next()) {
        return rs.getString("emailAddress");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return "";
  }

  /*******
   * <p>
   * Method: void removeInvitationAfterUse(String code)
   * </p>
   * 
   * <p>
   * Description: Remove an invitation record once it is used.
   * </p>
   * 
   * @param code is the 6 character String invitation code
   * 
   */
  // Remove an invitation using an email address once the user account has been
  // setup
  public void removeInvitationAfterUse(String code) {
    String query = "SELECT COUNT(*) AS count FROM InvitationCodes WHERE code = ?";
    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
      pstmt.setString(1, code);
      ResultSet rs = pstmt.executeQuery();
      if (rs.next()) {
        int counter = rs.getInt(1);
        // Only do the remove if the code is still in the invitation table
        if (counter > 0) {
          query = "DELETE FROM InvitationCodes WHERE code = ?";
          try (PreparedStatement pstmt2 = connection.prepareStatement(query)) {
            pstmt2.setString(1, code);
            pstmt2.executeUpdate();
          } catch (SQLException e) {
            e.printStackTrace();
          }
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return;
  }

  /*******
   * <p>
   * Method: void updateFirstName(String username, String firstName)
   * </p>
   * 
   * <p>
   * Description: Update the first name of a user given that user's username and
   * the new
   * first name.
   * </p>
   * 
   * @param username  is the username of the user
   * 
   * @param firstName is the new first name for the user
   * 
   */
  // update the first name
  public void updateUserName(String oldUsername, String newUsername) {
    String query = "UPDATE userDB SET userName = ? WHERE userName = ?";
    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
      pstmt.setString(1, newUsername);
      pstmt.setString(2, oldUsername);
      pstmt.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /*******
   * <p>
   * Method: void updatePassword(String username, String newPassword)
   * </p>
   * 
   * <p>
   * Description: Update the password of a user given that user's username and the
   * new
   * password.
   * </p>
   * 
   * @param username is the username of the user
   * @param password is the new password of the user
   *
   * 
   */
  // update the password
  public void updatePassword(String username, String password) {
    String query = "UPDATE userDB SET passWord = ? WHERE userName = ?";
    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
      pstmt.setString(1, password);
      pstmt.setString(2, username);
      pstmt.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /*******
   * <p>
   * Method: String generateOneTimePassword(String username)
   * </p>
   * 
   * <p>
   * Description: Update the password of a user with a one-time, temporary PW.
   * </p>
   * 
   * @param username is the username of the user
   * @param password is the new password of the user
   * @return the generated one-time PW
   *
   * 
   */
  // update the password
  public String generateOneTimePassword(String username) {
    String query = "UPDATE userDB SET OTP = ?, isOneTimePW = ? WHERE userName = ?";
    //This is where the password is really generated
    String generatedPW = passWordGenerator();
    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
      pstmt.setString(1, generatedPW);
      pstmt.setBoolean(2, true);
      pstmt.setString(3, username);
      pstmt.executeUpdate();
      return generatedPW;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return "";
  }
  
  
  /*******
   * <p>
   * Method: String passWordGenerator()
   * </p>
   * 
   * <p>
   * Description: Function that generates the random password
   * </p>
   * 
   * @return the generated one-time PW
   *
   * 
   */
  // update the password
  public String passWordGenerator()
  {
	    boolean validPW = false;
	    String generatedPW = "";
	    int passWordLength = 0;
	    //Buy yourself a lottery ticket if this hit
	    int overloadPreventer = 0;
	    //Set of booleans to check requirements
	    boolean hasLowerCase = false;
		boolean hasUpperCase = false;
		boolean hasDigit = false;
		boolean hasSpecialCharacter = false;
		
		//Password length can be from 8 to 16. Adds even more randomization!
    	int randomPassWordLength = (int)(Math.random() * 9) + 8;
		
		//Loop as long as the password is invalid and it's not trying too many times
	    while(!validPW && overloadPreventer < 128)
	    {
	    	int seed = (int)(Math.random() * 7);
	    	//Shortcut to specifying the existence of special character
	    	if(seed >= 3) hasSpecialCharacter = true;
	    	switch (seed)
	    	{
	    		//Lower case characters
	    		case 0:
	    			generatedPW += (char)((Math.random() * 26) + 'a');
	    			hasLowerCase = true;
	    			break;
	    		//Upper case characters
	    		case 1:
	    			generatedPW += (char)((Math.random() * 26) + 'A');
	    			hasUpperCase = true;
	    			break;
	    		//Digit
	    		case 2:
	    			generatedPW += (char)((Math.random() * 10) + '0');
	    			hasDigit = true;
	    			break;
	    		//Special characters, ASCII range 33-47
	    		case 3:
	    			generatedPW += (char)((Math.random() * 15) + '!');
	    			break;
	    		//Special characters, ASCII range 58-64
	    		case 4:
	    			generatedPW += (char)((Math.random() * 7) + ':');
	    			break;
	    		//Special characters, ASCII range 91-96
	    		case 5:
	    			generatedPW += (char)((Math.random() * 6) + '[');
	    			break;
	    			//Special characters, ASCII range 123-126
	    		case 6:
	    			generatedPW += (char)((Math.random() * 4) + '{');
	    			break;
	    	}
	    	//The password has increased. Compare it with the new selected random length
	    	passWordLength++;
	    	if(passWordLength >= randomPassWordLength)
	    	{
	    		//If the password is valid, you may now break the loop
	    		if(hasLowerCase && hasUpperCase && hasDigit && hasSpecialCharacter)
	    			validPW = true;
	    		//Otherwise, reset the generated password
	    		else
	    		{
	    			generatedPW = "";
	    			passWordLength = 0;
	    			hasLowerCase = false;
	    			hasUpperCase = false;
	    			hasDigit = false;
	    			hasSpecialCharacter = false;
	    			overloadPreventer++;
	    		}
	    	}
	    }
	    
	    //Password has overflowed, return a fixed password... We can tinker with this, but
	    //I'd argue this method is already excessive enough
	    if(overloadPreventer >= 128)
	    	return "#1AbqdeYg%";
	    
	    return generatedPW;
  }

  /*******
   * <p>
   * Method: void clearOneTimePassword(String username)
   * </p>
   * 
   * <p>
   * Description: Update the password of a user with a one-time, temporary PW.
   * </p>
   * 
   * @param username is the username of the user
   * @param password is the new password of the user
   * @return the generated one-time PW
   *
   * 
   */
  // update the password
  public void clearOneTimePassword(String username) {
    String query = "UPDATE userDB SET OTP = ?, isoneTimePW = ? WHERE userName = ?";
    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
      pstmt.setString(1, "");
      pstmt.setBoolean(2, false);
      pstmt.setString(3, username);
      pstmt.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /*******
   * <p>
   * Method: String getFirstName(String username)
   * </p>
   * 
   * <p>
   * Description: Get the first name of a user given that user's username.
   * </p>
   * 
   * @param username is the username of the user
   * 
   * @return the first name of a user given that user's username
   * 
   */
  // Get the First Name
  public String getFirstName(String username) {
    String query = "SELECT firstName FROM userDB WHERE userName = ?";
    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
      pstmt.setString(1, username);
      ResultSet rs = pstmt.executeQuery();

      if (rs.next()) {
        return rs.getString("firstName"); // Return the first name if user exists
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  /*******
   * <p>
   * Method: void updateFirstName(String username, String firstName)
   * </p>
   * 
   * <p>
   * Description: Update the first name of a user given that user's username and
   * the new
   * first name.
   * </p>
   * 
   * @param username  is the username of the user
   * 
   * @param firstName is the new first name for the user
   * 
   */
  // update the first name
  public void updateFirstName(String username, String firstName) {
    String query = "UPDATE userDB SET firstName = ? WHERE username = ?";
    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
      pstmt.setString(1, firstName);
      pstmt.setString(2, username);
      pstmt.executeUpdate();
      currentFirstName = firstName;
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /*******
   * <p>
   * Method: String getMiddleName(String username)
   * </p>
   * 
   * <p>
   * Description: Get the middle name of a user given that user's username.
   * </p>
   * 
   * @param username is the username of the user
   * 
   * @return the middle name of a user given that user's username
   * 
   */
  // get the middle name
  public String getMiddleName(String username) {
    String query = "SELECT MiddleName FROM userDB WHERE userName = ?";
    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
      pstmt.setString(1, username);
      ResultSet rs = pstmt.executeQuery();

      if (rs.next()) {
        return rs.getString("middleName"); // Return the middle name if user exists
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  /*******
   * <p>
   * Method: void updateMiddleName(String username, String middleName)
   * </p>
   * 
   * <p>
   * Description: Update the middle name of a user given that user's username and
   * the new
   * middle name.
   * </p>
   * 
   * @param username   is the username of the user
   * 
   * @param middleName is the new middle name for the user
   * 
   */
  // update the middle name
  public void updateMiddleName(String username, String middleName) {
    String query = "UPDATE userDB SET middleName = ? WHERE username = ?";
    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
      pstmt.setString(1, middleName);
      pstmt.setString(2, username);
      pstmt.executeUpdate();
      currentMiddleName = middleName;
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /*******
   * <p>
   * Method: String getLastName(String username)
   * </p>
   * 
   * <p>
   * Description: Get the last name of a user given that user's username.
   * </p>
   * 
   * @param username is the username of the user
   * 
   * @return the last name of a user given that user's username
   * 
   */
  // get he last name
  public String getLastName(String username) {
    String query = "SELECT LastName FROM userDB WHERE userName = ?";
    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
      pstmt.setString(1, username);
      ResultSet rs = pstmt.executeQuery();

      if (rs.next()) {
        return rs.getString("lastName"); // Return last name role if user exists
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  /*******
   * <p>
   * Method: void updateLastName(String username, String lastName)
   * </p>
   * 
   * <p>
   * Description: Update the middle name of a user given that user's username and
   * the new
   * middle name.
   * </p>
   * 
   * @param username is the username of the user
   * 
   * @param lastName is the new last name for the user
   * 
   */
  // update the last name
  public void updateLastName(String username, String lastName) {
    String query = "UPDATE userDB SET lastName = ? WHERE username = ?";
    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
      pstmt.setString(1, lastName);
      pstmt.setString(2, username);
      pstmt.executeUpdate();
      currentLastName = lastName;
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /*******
   * <p>
   * Method: String getPreferredFirstName(String username)
   * </p>
   * 
   * <p>
   * Description: Get the preferred first name of a user given that user's
   * username.
   * </p>
   * 
   * @param username is the username of the user
   * 
   * @return the preferred first name of a user given that user's username
   * 
   */
  // get the preferred first name
  public String getPreferredFirstName(String username) {
    String query = "SELECT preferredFirstName FROM userDB WHERE userName = ?";
    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
      pstmt.setString(1, username);
      ResultSet rs = pstmt.executeQuery();

      if (rs.next()) {
        return rs.getString("firstName"); // Return the preferred first name if user exists
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  /*******
   * <p>
   * Method: void updatePreferredFirstName(String username, String
   * preferredFirstName)
   * </p>
   * 
   * <p>
   * Description: Update the preferred first name of a user given that user's
   * username and
   * the new preferred first name.
   * </p>
   * 
   * @param username           is the username of the user
   * 
   * @param preferredFirstName is the new preferred first name for the user
   * 
   */
  // update the preferred first name of the user
  public void updatePreferredFirstName(String username, String preferredFirstName) {
    String query = "UPDATE userDB SET preferredFirstName = ? WHERE username = ?";
    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
      pstmt.setString(1, preferredFirstName);
      pstmt.setString(2, username);
      pstmt.executeUpdate();
      currentPreferredFirstName = preferredFirstName;
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /*******
   * <p>
   * Method: String getEmailAddress(String username)
   * </p>
   * 
   * <p>
   * Description: Get the email address of a user given that user's username.
   * </p>
   * 
   * @param username is the username of the user
   * 
   * @return the email address of a user given that user's username
   * 
   */
  // get the email address
  public String getEmailAddress(String username) {
    String query = "SELECT emailAddress FROM userDB WHERE userName = ?";
    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
      pstmt.setString(1, username);
      ResultSet rs = pstmt.executeQuery();

      if (rs.next()) {
        return rs.getString("emailAddress"); // Return the email address if user exists
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  /*******
   * <p>
   * Method: void updateEmailAddress(String username, String emailAddress)
   * </p>
   * 
   * <p>
   * Description: Update the email address name of a user given that user's
   * username and
   * the new email address.
   * </p>
   * 
   * @param username     is the username of the user
   * 
   * @param emailAddress is the new preferred first name for the user
   * 
   */
  // update the email address
  public void updateEmailAddress(String username, String emailAddress) {
    String query = "UPDATE userDB SET emailAddress = ? WHERE username = ?";
    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
      pstmt.setString(1, emailAddress);
      pstmt.setString(2, username);
      pstmt.executeUpdate();
      currentEmailAddress = emailAddress;
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /*******
   * <p>
   * Method: boolean getUserAccountDetails(String username)
   * </p>
   * 
   * <p>
   * Description: Get all the attributes of a user given that user's username.
   * </p>
   * 
   * @param username is the username of the user
   * 
   * @return true of the get is successful, else false
   * 
   */
  // get the attributes for a specified user
  public boolean getUserAccountDetails(String username) {
    String query = "SELECT * FROM userDB WHERE username = ?";
    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
      pstmt.setString(1, username);
      ResultSet rs = pstmt.executeQuery();
      if (rs.next()) {
        currentUsername = rs.getString("userName");
        currentPassword = rs.getString("password");
        currentOTP = rs.getString("OTP");
        currentFirstName = rs.getString("firstName");
        currentMiddleName = rs.getString("middleName");
        currentLastName = rs.getString("lastName");
        currentPreferredFirstName = rs.getString("preferredFirstName");
        currentEmailAddress = rs.getString("emailAddress");
        currentAdminRole = rs.getBoolean("adminRole");
        currentNewRole1 = rs.getBoolean("newRole1");
        currentNewRole2 = rs.getBoolean("newRole2");
        return true;
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  /*******
   * <p>
   * Method: User getUserAsObject(String username)
   * </p>
   * 
   * <p>
   * Description: Return an instance of a user, particularly one in the list and
   * therefore not the current user
   * </p>
   * 
   * @param username is the username of the user
   * 
   * @return a user object
   * 
   */
  // get the attributes for a specified user
  public User getUserAsObject(String username) {
    User user = new User();
    String query = "SELECT * FROM userDB WHERE username = ?";
    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
      pstmt.setString(1, username);
      ResultSet rs = pstmt.executeQuery();
      if (rs.next()) {
        user.setUserName(rs.getString("userName"));
        user.setPassword(rs.getString("password"));
        user.setFirstName(rs.getString("firstName"));
        user.setMiddleName(rs.getString("middleName"));
        user.setLastName(rs.getString("lastName"));
        user.setPreferredFirstName(rs.getString("preferredFirstName"));
        user.setEmailAddress(rs.getString("emailAddress"));
        user.setAdminRole(rs.getBoolean("adminRole"));
        user.setRole1User(rs.getBoolean("newRole1"));
        user.setRole2User(rs.getBoolean("newRole2"));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return user;
  }

  /*******
   * <p>
   * Method: void deleteUser(String username)
   * </p>
   * 
   * <p>
   * Description: Delete the specified user.
   * </p>
   * 
   * @param username is the username of the user
   * 
   * @param lastName is the new last name for the user
   * 
   */
  // update the last name
  public void deleteUser(String username) {
    String query = "DELETE FROM userDB WHERE username = ?";
    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
      pstmt.setString(1, username);
      pstmt.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /*******
   * <p>
   * Method: boolean updateUserRole(String username, String role, String value)
   * </p>
   * 
   * <p>
   * Description: Update a specified role for a specified user's and set and
   * update all the
   * current user attributes.
   * </p>
   * 
   * @param username is the username of the user
   * 
   * @param role     is string that specifies the role to update
   * 
   * @param value    is the string that specified TRUE or FALSE for the role
   * 
   * @return true if the update was successful, else false
   * 
   */
  // Update a users role
  public boolean updateUserRole(String username, String role, String value) {
    if (role.compareTo("Admin") == 0) {
      String query = "UPDATE userDB SET adminRole = ? WHERE username = ?";
      try (PreparedStatement pstmt = connection.prepareStatement(query)) {
        pstmt.setString(1, value);
        pstmt.setString(2, username);
        pstmt.executeUpdate();
        if (value.compareTo("true") == 0)
          currentAdminRole = true;
        else
          currentAdminRole = false;
        return true;
      } catch (SQLException e) {
        return false;
      }
    }
    if (role.compareTo("Staff") == 0) {
      String query = "UPDATE userDB SET newRole1 = ? WHERE username = ?";
      try (PreparedStatement pstmt = connection.prepareStatement(query)) {
        pstmt.setString(1, value);
        pstmt.setString(2, username);
        pstmt.executeUpdate();
        if (value.compareTo("true") == 0)
          currentNewRole1 = true;
        else
          currentNewRole1 = false;
        return true;
      } catch (SQLException e) {
        return false;
      }
    }
    if (role.compareTo("Student") == 0) {
      String query = "UPDATE userDB SET newRole2 = ? WHERE username = ?";
      try (PreparedStatement pstmt = connection.prepareStatement(query)) {
        pstmt.setString(1, value);
        pstmt.setString(2, username);
        pstmt.executeUpdate();
        if (value.compareTo("true") == 0)
          currentNewRole2 = true;
        else
          currentNewRole2 = false;
        return true;
      } catch (SQLException e) {
        return false;
      }
    }
    return false;
  }

  /*******
   * <p>
   * Method: String getAdminRole(String username)
   * </p>
   * 
   * <p>
   * Description: Get whether the user is an admin.
   * </p>
   * 
   * @param username is the username of the user
   * 
   * @return boolean of admin role
   * 
   */
  // get the email address
  public boolean getAdminRole(String username) {
    String query = "SELECT emailAddress FROM userDB WHERE userName = ? AND adminRole = TRUE";
    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
      pstmt.setString(1, username);
      ResultSet rs = pstmt.executeQuery();

      if (rs.next()) {
        return true; // Return the email address if user exists
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  /*******
   * <p>
   * Method: String getRole1(String username)
   * </p>
   * 
   * <p>
   * Description: Get whether the user has Role1.
   * </p>
   * 
   * @param username is the username of the user
   * 
   * @return boolean of Role1
   * 
   */
  // get the email address
  public boolean getRole1(String username) {
    String query = "SELECT emailAddress FROM userDB WHERE userName = ? AND newRole1 = TRUE";
    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
      pstmt.setString(1, username);
      ResultSet rs = pstmt.executeQuery();

      if (rs.next()) {
        return true; // Return the email address if user exists
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  /*******
   * <p>
   * Method: String getRole2(String username)
   * </p>
   * 
   * <p>
   * Description: Get whether the user has Role1.
   * </p>
   * 
   * @param username is the username of the user
   * 
   * @return boolean of Role2
   * 
   */
  // get the email address
  public boolean getRole2(String username) {
    String query = "SELECT emailAddress FROM userDB WHERE userName = ? AND newRole2 = TRUE";
    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
      pstmt.setString(1, username);
      ResultSet rs = pstmt.executeQuery();

      if (rs.next()) {
        return true; // Return the email address if user exists
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  // Attribute getters for the current user
  /*******
   * <p>
   * Method: String getCurrentUsername()
   * </p>
   * 
   * <p>
   * Description: Get the current user's username.
   * </p>
   * 
   * @return the username value is returned
   * 
   */
  public String getCurrentUsername() {
    return currentUsername;
  };

  /*******
   * <p>
   * Method: String getCurrentPassword()
   * </p>
   * 
   * <p>
   * Description: Get the current user's password.
   * </p>
   * 
   * @return the password value is returned
   * 
   */
  public String getCurrentPassword() {
    return currentPassword;
  };

  /*******
   * <p>
   * Method: String getCurrentFirstName()
   * </p>
   * 
   * <p>
   * Description: Get the current user's first name.
   * </p>
   * 
   * @return the first name value is returned
   * 
   */
  public String getCurrentFirstName() {
    return currentFirstName;
  };

  /*******
   * <p>
   * Method: String getCurrentMiddleName()
   * </p>
   * 
   * <p>
   * Description: Get the current user's middle name.
   * </p>
   * 
   * @return the middle name value is returned
   * 
   */
  public String getCurrentMiddleName() {
    return currentMiddleName;
  };

  /*******
   * <p>
   * Method: String getCurrentLastName()
   * </p>
   * 
   * <p>
   * Description: Get the current user's last name.
   * </p>
   * 
   * @return the last name value is returned
   * 
   */
  public String getCurrentLastName() {
    return currentLastName;
  };

  /*******
   * <p>
   * Method: String getCurrentPreferredFirstName(
   * </p>
   * 
   * <p>
   * Description: Get the current user's preferred first name.
   * </p>
   * 
   * @return the preferred first name value is returned
   * 
   */
  public String getCurrentPreferredFirstName() {
    return currentPreferredFirstName;
  };

  /*******
   * <p>
   * Method: String getCurrentEmailAddress()
   * </p>
   * 
   * <p>
   * Description: Get the current user's email address name.
   * </p>
   * 
   * @return the email address value is returned
   * 
   */
  public String getCurrentEmailAddress() {
    return currentEmailAddress;
  };

  /*******
   * <p>
   * Method: boolean getCurrentAdminRole()
   * </p>
   * 
   * <p>
   * Description: Get the current user's Admin role attribute.
   * </p>
   * 
   * @return true if this user plays an Admin role, else false
   * 
   */
  public boolean getCurrentAdminRole() {
    return currentAdminRole;
  };

  /*******
   * <p>
   * Method: boolean getCurrentNewRole1()
   * </p>
   * 
   * <p>
   * Description: Get the current user's Student role attribute.
   * </p>
   * 
   * @return true if this user plays a Student role, else false
   * 
   */
  public boolean getCurrentNewRole1() {
    return currentNewRole1;
  };

  /*******
   * <p>
   * Method: boolean getCurrentNewRole2()
   * </p>
   * 
   * <p>
   * Description: Get the current user's Reviewer role attribute.
   * </p>
   * 
   * @return true if this user plays a Reviewer role, else false
   * 
   */
  public boolean getCurrentNewRole2() {
    return currentNewRole2;
  };

  /*******
   * <p>
   * Method: boolean getCurrentNewRole2()
   * </p>
   * 
   * <p>
   * Description: Get the current user's Reviewer role attribute.
   * </p>
   * 
   * @return true if this user plays a Reviewer role, else false
   * 
   */
  public boolean isTemporaryPassword() {
    return isTemporaryPassword;
  };

  public String getCurrentOTP() {
    return currentOTP;
  }

  /*******
   * <p>
   * Debugging method
   * </p>
   * 
   * <p>
   * Description: Debugging method that dumps the database of the console.
   * </p>
   * 
   * @throws SQLException if there is an issues accessing the database.
   * 
   */
  // Dumps the database.
  public void dump() throws SQLException {
    String query = "SELECT * FROM userDB";
    ResultSet resultSet = statement.executeQuery(query);
    ResultSetMetaData meta = resultSet.getMetaData();
    while (resultSet.next()) {
      for (int i = 0; i < meta.getColumnCount(); i++) {
        System.out.println(
            meta.getColumnLabel(i + 1) + ": " +
                resultSet.getString(i + 1));
      }
      System.out.println();
    }
    resultSet.close();
  }

  /*******
   * <p>
   * Method: void closeConnection()
   * </p>
   * 
   * <p>
   * Description: Closes the database statement and connection.
   * </p>
   * 
   */
  // Closes the database statement and connection.
  public void closeConnection() {
    try {
      if (statement != null)
        statement.close();
    } catch (SQLException se2) {
      se2.printStackTrace();
    }
    try {
      if (connection != null)
        connection.close();
    } catch (SQLException se) {
      se.printStackTrace();
    }
  }
}
