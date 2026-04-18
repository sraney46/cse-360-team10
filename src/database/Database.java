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
import entityClasses.EvaluationTool;

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
  private int	 currentIdentification;
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
  
  /**********
   * <p>Method: getConnection()</p>
   *
   * <p>Description: Returns the active database connection so other classes
   * can execute queries without establishing a new connection.</p>
   *
   * @return the active H2 database Connection
   */
  public Connection getConnection() {
      return connection;
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
    
    String postTable = "CREATE TABLE IF NOT EXISTS postDB ("
            + "postID INT AUTO_INCREMENT PRIMARY KEY, "
            + "author INT, "
            + "title VARCHAR(255), "
            + "content LONGTEXT, "
            + "category VARCHAR(50), "
            + "timestamp BIGINT, "
            + "graded BOOLEAN, "
            + "percentageGrade DOUBLE, "
            + "numberGrade INT, "
            + "letterGrade VARCHAR(5), "
            + "flagged BOOLEAN DEFAULT FALSE)";
    statement.execute(postTable);

    String replyTable = "CREATE TABLE IF NOT EXISTS replyDB ("
        + "replyID INT AUTO_INCREMENT PRIMARY KEY, "
        + "postID INT, "
        + "author INT, "
        + "content LONGTEXT, "
        + "timestamp BIGINT)";
    statement.execute(replyTable);
    
    String readStatusTable = "CREATE TABLE IF NOT EXISTS postReadStatus ("
	    + "userName VARCHAR(255), "
	    + "postID INT, "
	    + "PRIMARY KEY (userName, postID))";
	statement.execute(readStatusTable);
	
	String ticketTable = "CREATE TABLE IF NOT EXISTS ticketDB ("
        + "postID INT AUTO_INCREMENT PRIMARY KEY, "
        + "author INT, "
        + "title VARCHAR(255), "
        + "content LONGTEXT, "
        + "category VARCHAR(50), "
        + "timestamp BIGINT,"
        + "reopened BOOL DEFAULT FALSE,"
        + "originalID INT)";
    statement.execute(ticketTable);

    String ticketReplyTable = "CREATE TABLE IF NOT EXISTS ticketreplyDB ("
        + "replyID INT AUTO_INCREMENT PRIMARY KEY, "
        + "postID INT, "
        + "author INT, "
        + "content LONGTEXT, "
        + "timestamp BIGINT)";
    statement.execute(ticketReplyTable);
    
    String readStatusTicketTable = "CREATE TABLE IF NOT EXISTS ticketReadStatus ("
		    + "userName VARCHAR(255), "
		    + "postID INT, "
		    + "PRIMARY KEY (userName, postID))";
		statement.execute(readStatusTicketTable);

    String evaluationToolTable = "CREATE TABLE IF NOT EXISTS evaluationTool ("
        + "studentID INT, "
        + "definedParams VARCHAR(255) ARRAY, "
        + "paramWeights DOUBLE PRECISION ARRAY, "
        + "percentage DOUBLE, "
        + "numberGrade INT, "
        + "letterGrade VARCHAR(5))";
    statement.execute(evaluationToolTable);

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
    //AC - let's update the user object to use this id, when registering
    String getIDQuery = "SELECT * FROM userDB WHERE id=(SELECT max(id) FROM userDB);";
    try {
    	ResultSet resultSet = statement.executeQuery(getIDQuery);
        if (resultSet.next()) {
        	user.setId(resultSet.getInt("id"));
        }
      }
    catch(SQLException e) { e.printStackTrace(); }
  }

  /*******
   * <p>
   * Method: populateDatabaseWithTestUsers(User currentUser)
   * </p>
   * 
   * <p>
   * Description: Keeps the current user data but removes all users from database
   * and re-populates with more users for testing.
   * </p>
   * 
   * 
   * @param currentUser specifies a user object to be added to the database.
   * 
   */
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
          String otp; // Add OTP variable
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
            otp = ""; // Set OTP to empty string for all test users
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
          pstmt.setString(3, otp); // Add OTP parameter (empty string)
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
      } catch (SQLException e) {
        e.printStackTrace();
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
  
  /*******
   * <p>
   * Method: populateDatabaseWithTestPosts()
   * </p>
   *
   * <p>
   * Description: Populates the postDB and replyDB tables with sample discussion
   * forum data for testing purposes. Inserts 10 sample posts across various
   * categories, with 5 of those posts having replies from users of different roles.
   * </p>
   *
   */
  public void populateDatabaseWithTestPosts() {
	    try {
	        // Clear existing post and reply data
//	        statement.execute("DELETE FROM replyDB");
//	        statement.execute("DELETE FROM postDB");
//	        System.out.println("Post and reply tables cleared.");

	        // Insert test posts
	        String postSQL = "INSERT INTO postDB (author, title, content, category, timestamp) VALUES (?, ?, ?, ?, ?)";
	        try (PreparedStatement pstmt = connection.prepareStatement(postSQL)) {
	        	
	        	Object[][] posts = {
	        		    {generateRandomUser(), "STACK/QUEUE", "What is the difference between a stack and a queue?", "General", System.currentTimeMillis() - 200000, false, 0.0, 0, "N/A", false},
	        		    {generateRandomUser(), "INTERFACES", "Can someone explain how interfaces work in Java?", "Lectures", System.currentTimeMillis() - 190000, false, 0.0, 0, "N/A", false},
	        		    {generateRandomUser(), "HW2 CLARIFY", "I am confused about HW2 requirements, can someone clarify?", "Homework", System.currentTimeMillis() - 180000, false, 0.0, 0, "N/A", false},
	        		    {generateRandomUser(), "MIDTERM TOPICS", "What topics will be covered on the midterm?", "Exams", System.currentTimeMillis() - 170000, false, 0.0, 0, "N/A", false},
	        		    {generateRandomUser(), "H2 SETUP", "Does anyone know how to set up the H2 database in Eclipse?", "General", System.currentTimeMillis() - 160000, false, 0.0, 0, "N/A", false},
	        		    {generateRandomUser(), "NULL POINTER", "I keep getting a NullPointerException in my PostList class, any ideas?", "Homework", System.currentTimeMillis() - 150000, false, 0.0, 0, "N/A", false},
	        		    {generateRandomUser(), "PROJECT DEADLINE", "What is the deadline for the team project phase 2?", "Assignments", System.currentTimeMillis() - 140000, false, 0.0, 0, "N/A", false},
	        		    {generateRandomUser(), "SCREENCAST TIPS", "Can someone share tips for recording screencasts on Mac?", "General", System.currentTimeMillis() - 130000, false, 0.0, 0, "N/A", false},
	        		    {generateRandomUser(), "INHERITANCE", "Is inheritance always better than composition in OOP?", "Lectures", System.currentTimeMillis() - 120000, false, 0.0, 0, "N/A", false},
	        		    {generateRandomUser(), "HW2 SUBMISSION", "Where do we submit the HW2 zip file, Canvas or email?", "Homework", System.currentTimeMillis() - 110000, false, 0.0, 0, "N/A", false},
	        		    {generateRandomUser(), "ABSTRACT VS INTERFACE", "Can someone explain the difference between abstract classes and interfaces?", "Lectures", System.currentTimeMillis() - 100000, false, 0.0, 0, "N/A", false},
	        		    {generateRandomUser(), "GIT MERGE", "How do we handle merge conflicts in Git when two people edit the same file?", "General", System.currentTimeMillis() - 90000, false, 0.0, 0, "N/A", false},
	        		    {generateRandomUser(), "SEQUENCE DIAGRAM", "Is the sequence diagram required for every use case or just the main one?", "Assignments", System.currentTimeMillis() - 80000, false, 0.0, 0, "N/A", false},
	        		    {generateRandomUser(), "CRUD TEST", "What is the best way to test CRUD operations without a GUI?", "Homework", System.currentTimeMillis() - 70000, false, 0.0, 0, "N/A", false},
	        		    {generateRandomUser(), "PROJECT STYLE", "Does the team project need to follow the FoundationsF25 documentation style?", "Assignments", System.currentTimeMillis() - 60000, false, 0.0, 0, "N/A", false},
	        		    {generateRandomUser(), "H2 DRIVER ERROR", "I am getting a ClassNotFoundException for the H2 driver, how do I fix this?", "General", System.currentTimeMillis() - 50000, false, 0.0, 0, "N/A", false},
	        		    {generateRandomUser(), "EPICS VS STORIES", "What is the difference between epics and user stories?", "Lectures", System.currentTimeMillis() - 40000, false, 0.0, 0, "N/A", false},
	        		    {generateRandomUser(), "ARRAYLIST/COMPOSITION", "Should our PostList class extend ArrayList or use composition instead?", "Homework", System.currentTimeMillis() - 30000, false, 0.0, 0, "N/A", false},
	        		    {generateRandomUser(), "HW2 SCREENCASTS", "How many screencasts do we need to submit for HW2?", "Assignments", System.currentTimeMillis() - 20000, false, 0.0, 0, "N/A", false},
	        		    {generateRandomUser(), "JAVA FX", "Can we use JavaFX TableView instead of a custom VBox list for displaying posts?", "General", System.currentTimeMillis() - 10000, false, 0.0, 0, "N/A", false},
	        		};

	            for (Object[] post : posts) {
	                pstmt.setInt(1, (Integer) post[0]);
	                pstmt.setString(2, (String) post[1]);
	                pstmt.setString(3, (String) post[2]);
	                pstmt.setString(4, (String) post[3]);
	                pstmt.setLong(5,   (Long)   post[4]);
	                pstmt.addBatch();
	            }
	            pstmt.executeBatch();
	            System.out.println("Inserted 20 test posts into postDB successfully.");
	        }

	        // Get the actual postIDs after insertion
	        List<Integer> postIDs = new ArrayList<>();
	        ResultSet rs_init = statement.executeQuery("SELECT postID FROM postDB ORDER BY postID");
	        while (rs_init.next()) {
	            postIDs.add(rs_init.getInt("postID"));
	        }

	        // Insert replies — every post gets at least 2, some get up to 10
	        String replySQL = "INSERT INTO replyDB (postID, author, content, timestamp) VALUES (?, ?, ?, ?)";
	        try (PreparedStatement pstmt = connection.prepareStatement(replySQL)) {

	            Object[][] replies = {
	                // Post 0 — 5 replies
	                {postIDs.get(0), generateRandomReplyUser(1), "A stack is LIFO and a queue is FIFO. Great question!",                              System.currentTimeMillis() - 199000},
	                {postIDs.get(0), generateRandomReplyUser(1),       "I had the same question, this clears it up!",                                       System.currentTimeMillis() - 198000},
	                {postIDs.get(0), generateRandomReplyUser(1),       "Think of a stack like a pile of plates and a queue like a line at a store.",        System.currentTimeMillis() - 197000},
	                {postIDs.get(0), generateRandomReplyUser(1),      "Great analogy user5! Both are covered in the Week 3 lecture slides.",               System.currentTimeMillis() - 196000},
	                {postIDs.get(0), generateRandomReplyUser(1),       "Thanks everyone, this makes a lot more sense now.",                                 System.currentTimeMillis() - 195000},

	                // Post 1 — 4 replies
	                {postIDs.get(1), generateRandomReplyUser(2), "Interfaces define a contract. A class that implements it must provide all methods.", System.currentTimeMillis() - 189000},
	                {postIDs.get(1), generateRandomReplyUser(2),       "So interfaces are like a blueprint but without any implementation?",                System.currentTimeMillis() - 188000},
	                {postIDs.get(1), generateRandomReplyUser(2), "Exactly right user6! Default methods in Java 8+ are the one exception.",            System.currentTimeMillis() - 187000},
	                {postIDs.get(1), generateRandomReplyUser(2),       "That makes sense, thanks for clarifying!",                                          System.currentTimeMillis() - 186000},

	                // Post 2 — 6 replies
	                {postIDs.get(2), generateRandomReplyUser(3),      "Please refer to the Canvas assignment page for the full requirements breakdown.",    System.currentTimeMillis() - 179000},
	                {postIDs.get(2), generateRandomReplyUser(3),       "The PDF on Canvas has a much clearer breakdown than the website.",                  System.currentTimeMillis() - 178000},
	                {postIDs.get(2), generateRandomReplyUser(3),       "I was confused too, focus on the CRUD and input validation sections.",              System.currentTimeMillis() - 177000},
	                {postIDs.get(2), generateRandomReplyUser(3), "Good advice. Remember you only need CRUD and validation for this assignment.",       System.currentTimeMillis() - 176000},
	                {postIDs.get(2), generateRandomReplyUser(3),       "Does the user story document need to be a PDF or can it be a Word doc?",            System.currentTimeMillis() - 175000},
	                {postIDs.get(2), generateRandomReplyUser(3),      "It must be submitted as a PDF as stated in the deliverables section.",              System.currentTimeMillis() - 174000},

	                // Post 3 — 3 replies
	                {postIDs.get(3), generateRandomReplyUser(4), "Midterm covers weeks 1 through 6. Focus on OOP principles and UML diagrams.",       System.currentTimeMillis() - 169000},
	                {postIDs.get(3), generateRandomReplyUser(4),       "Will there be questions on sequence diagrams specifically?",                        System.currentTimeMillis() - 168000},
	                {postIDs.get(3), generateRandomReplyUser(4), "Yes, expect at least one sequence diagram question on the exam.",                   System.currentTimeMillis() - 167000},

	                // Post 4 — 4 replies
	                {postIDs.get(4), generateRandomReplyUser(5),       "Make sure the JDBC driver JAR is added to your build path in Eclipse.",             System.currentTimeMillis() - 159000},
	                {postIDs.get(4), generateRandomReplyUser(5),       "Right-click the project, Build Path, Add External JARs, then select h2.jar.",       System.currentTimeMillis() - 158000},
	                {postIDs.get(4), generateRandomReplyUser(5), "Good answers. Also ensure your DB URL matches the path in FoundationsMain.",        System.currentTimeMillis() - 157000},
	                {postIDs.get(4), generateRandomReplyUser(5),       "Got it working, the build path was the issue. Thanks everyone!",                    System.currentTimeMillis() - 156000},

	                // Post 5 — 2 replies
	                {postIDs.get(5), generateRandomReplyUser(6),       "Check that you are not calling a method on a null object before it is initialized.", System.currentTimeMillis() - 149000},
	                {postIDs.get(5), generateRandomReplyUser(6), "Add a null check before calling theDatabase.getConnection() in your PostList.",     System.currentTimeMillis() - 148000},

	                // Post 6 — 2 replies
	                {postIDs.get(6), generateRandomReplyUser(7),      "Phase 2 is due two weeks from Friday. Check the course schedule on Canvas.",        System.currentTimeMillis() - 139000},
	                {postIDs.get(6), generateRandomReplyUser(7),       "Thanks, I had the wrong date written down!",                                        System.currentTimeMillis() - 138000},

	                // Post 7 — 3 replies
	                {postIDs.get(7), generateRandomReplyUser(8),       "I use Zoom for screencasts, just share your screen and record with audio.",         System.currentTimeMillis() - 129000},
	                {postIDs.get(7), generateRandomReplyUser(8),       "QuickTime Player on Mac also works great and requires no setup.",                   System.currentTimeMillis() - 128000},
	                {postIDs.get(7), generateRandomReplyUser(8), "Either tool is fine. Make sure your audio is clear and text is readable.",          System.currentTimeMillis() - 127000},

	                // Post 8 — 10 replies
	                {postIDs.get(8), generateRandomReplyUser(9), "Neither is always better. Prefer composition for flexibility and loose coupling.",   System.currentTimeMillis() - 119000},
	                {postIDs.get(8), generateRandomReplyUser(9),       "I read that composition is preferred in most modern OOP design patterns.",          System.currentTimeMillis() - 118000},
	                {postIDs.get(8), generateRandomReplyUser(9),       "Inheritance makes sense when there is a true is-a relationship between classes.",   System.currentTimeMillis() - 117000},
	                {postIDs.get(8), generateRandomReplyUser(9),       "Composition gives you more flexibility at runtime which is usually what you want.", System.currentTimeMillis() - 116000},
	                {postIDs.get(8), generateRandomReplyUser(9),       "Does this mean we should avoid inheritance entirely in our team project?",          System.currentTimeMillis() - 115000},
	                {postIDs.get(8), generateRandomReplyUser(9), "Not at all. Use inheritance where there is a natural hierarchy like User roles.",   System.currentTimeMillis() - 114000},
	                {postIDs.get(8), generateRandomReplyUser(9),       "So BankAccount extending Account would be a good use of inheritance?",              System.currentTimeMillis() - 113000},
	                {postIDs.get(8), generateRandomReplyUser(9), "Yes, that is a classic textbook example of appropriate inheritance use.",           System.currentTimeMillis() - 112000},
	                {postIDs.get(8), generateRandomReplyUser(9),       "This thread is really helpful, should be pinned at the top!",                      System.currentTimeMillis() - 111000},
	                {postIDs.get(8), generateRandomReplyUser(9),      "Agreed, great discussion everyone. This is exactly what Ed Discussion is for.",    System.currentTimeMillis() - 110000},

	                // Post 9 — 2 replies
	                {postIDs.get(9), generateRandomReplyUser(20),      "All submissions go through Canvas only. Do not email the instructor or TAs.",       System.currentTimeMillis() - 109000},
	                {postIDs.get(9), generateRandomReplyUser(20),       "Thanks, I was not sure since the ZIP file gets pretty large.",                      System.currentTimeMillis() - 108000},

	                // Post 10 — 3 replies
	                {postIDs.get(10), generateRandomReplyUser(21), "Abstract classes can have implementation while interfaces traditionally cannot.",    System.currentTimeMillis() - 99000},
	                {postIDs.get(10), generateRandomReplyUser(21),  "So use abstract class when sharing code and interface when defining a contract?",   System.currentTimeMillis() - 98000},
	                {postIDs.get(10), generateRandomReplyUser(21), "Exactly right. That is the key distinction to remember for the exam.",              System.currentTimeMillis() - 97000},

	                // Post 11 — 2 replies
	                {postIDs.get(11), generateRandomReplyUser(22),       "Always pull before you start working and communicate with your team on who edits what.", System.currentTimeMillis() - 89000},
	                {postIDs.get(11), generateRandomReplyUser(22), "Feature branches are a good strategy to avoid conflicts on shared files.",          System.currentTimeMillis() - 88000},

	                // Post 12 — 2 replies
	                {postIDs.get(12), generateRandomReplyUser(23),      "The assignment requires at least one sequence diagram covering a main use case.",   System.currentTimeMillis() - 79000},
	                {postIDs.get(12), generateRandomReplyUser(23),       "Good to know, I was planning to do one for the login flow.",                        System.currentTimeMillis() - 78000},

	                // Post 13 — 2 replies
	                {postIDs.get(13), generateRandomReplyUser(24), "You can write a main method with hardcoded test data and print results to console.", System.currentTimeMillis() - 69000},
	                {postIDs.get(13), generateRandomReplyUser(24),       "That is basically what TP1 does for the user tests, good reference point.",         System.currentTimeMillis() - 68000},

	                // Post 14 — 2 replies
	                {postIDs.get(14), generateRandomReplyUser(25), "Yes, all code and documentation must follow the FoundationsF25 style guide.",       System.currentTimeMillis() - 59000},
	                {postIDs.get(14), generateRandomReplyUser(25),       "Does that include the Javadoc comment format above every method?",                  System.currentTimeMillis() - 58000},

	                // Post 15 — 3 replies
	                {postIDs.get(15), generateRandomReplyUser(26),       "Make sure the H2 JAR is on the build path and the driver string is correct.",       System.currentTimeMillis() - 49000},
	                {postIDs.get(15), generateRandomReplyUser(26),       "The driver class name is org.h2.Driver, double check yours matches exactly.",       System.currentTimeMillis() - 48000},
	                {postIDs.get(15), generateRandomReplyUser(26), "Also verify the DB_URL in your Database class matches the one in FoundationsMain.", System.currentTimeMillis() - 47000},

	                // Post 16 — 2 replies
	                {postIDs.get(16), generateRandomReplyUser(27), "Epics are high level goals and user stories are the specific tasks within them.",   System.currentTimeMillis() - 39000},
	                {postIDs.get(16), generateRandomReplyUser(27),       "So an epic might be manage users and a story is add a new user to the system?",    System.currentTimeMillis() - 38000},

	                // Post 17 — 2 replies
	                {postIDs.get(17), generateRandomReplyUser(28), "Composition is preferred here. PostList should contain a list, not extend one.",   System.currentTimeMillis() - 29000},
	                {postIDs.get(17), generateRandomReplyUser(28),       "That makes sense, extending ArrayList would expose too many unrelated methods.",    System.currentTimeMillis() - 28000},

	                // Post 18 — 2 replies
	                {postIDs.get(18), generateRandomReplyUser(29),      "HW2 requires 7 screencasts total. See the deliverables section for details.",      System.currentTimeMillis() - 19000},
	                {postIDs.get(18), generateRandomReplyUser(29),       "Seven is a lot but at least most of them only need to be 2 to 5 minutes long.",    System.currentTimeMillis() - 18000},

	                // Post 19 — 2 replies
	                {postIDs.get(19), generateRandomReplyUser(30), "TableView works but a custom VBox gives you more control over the row styling.",   System.currentTimeMillis() - 9000},
	                {postIDs.get(19), generateRandomReplyUser(30),       "I tried TableView first but switched to VBox after seeing the TP1 examples.",      System.currentTimeMillis() - 8000},
	            };
     
	            for (Object[] reply : replies) {
	                pstmt.setInt(1,    (Integer) reply[0]);
	                pstmt.setInt(2, (Integer)  reply[1]);
	                pstmt.setString(3, (String)  reply[2]);
	                pstmt.setLong(4,   (Long)    reply[3]);
	                pstmt.addBatch();
	            }
	            pstmt.executeBatch();
	            System.out.println("Inserted replies into replyDB successfully.");
	        }
            // Get the actual postIDs after insertion
          List<Integer> replyIDs = new ArrayList<>();
	       ResultSet rsr_init = statement.executeQuery("SELECT replyID FROM replyDB ORDER BY replyID");
        while (rsr_init.next()) {
           replyIDs.add(rsr_init.getInt("replyID"));
      }

	       

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
  
  /*******
   * <p>
   * Method: int generateRandomUser()
   * </p>
   * 
   * <P>
   * Description: Generate a random user to post a discussion message. And hey,
   * let's exclude admins and staff from this!
   * </p>
   * 
   * @return the id of the user that will make this random post
   */
  private int generateRandomUser()
  {
	  int selectedID = (int)(Math.random() * 25) + 1;
	  User user = this.getUserAsObject(selectedID);
	  while(!user.getNewRole1())
	  {
		  selectedID = (int)(Math.random() * 25) + 1;
		  user = this.getUserAsObject(selectedID);
	  }
	  return user.getUserId();
  }
  
  /*******
   * <p>
   * Method: int generateRandomReplyUser()
   * </p>
   * 
   * <P>
   * Description: Generate a reply post from a random user that is not the OP
   * </p>
   * 
   * @return the id of the user that will make this random post
   */
  private int generateRandomReplyUser(int postID)
  {
	  int selectedID = (int)(Math.random() * 25) + 1;
	  User user = this.getUserAsObject(selectedID);
	  while(user.getUserId() == postID)
	  {
		  selectedID = (int)(Math.random() * 25) + 1;
		  user = this.getUserAsObject(selectedID);
	  }
	  return user.getUserId();
  }

  /*******
   * <p>
   * Method: List getUserList()
   * </p>
   * 
   * <P>
   * Description: Generate an List of Strings, one for each user in the database,
   * starting with "&lt;Select User&gt;" at the start of the list.
   * </p>
   * 
   * @return a list of userNames found in the database.
   */
  public List<Integer> getUserList() {
    List<Integer> userList = new ArrayList<Integer>();
    // Commenting out this line, having this will just cause more problems than it
    // solves
    // userList.add("<Select a User>");
    String query = "SELECT id FROM userDB";
    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
      ResultSet rs = pstmt.executeQuery();
      while (rs.next()) {
        userList.add(rs.getInt("id"));
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
   * starting with "&lt;Select User&gt;" at the start of the list.
   * </p>
   * 
   * @return a list of userNames found in the database.
   */
  public List<String> getUserListAdmin() {
    List<String> userList = new ArrayList<String>();
    // Commenting out this line, having this will just cause more problems than it
    // solves
    // userList.add("&lt;Select a User&gt;");
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
   * Method: boolean doesUserExist(int id)
   * </p>
   * 
   * <p>
   * Description: Check to see that a user with the specified username is in the
   * table.
   * </p>
   * 
   * @param id specifies the specific user that we want to determine if it
   *                 is in the table.
   * 
   * @return true if the specified user is in the table else false.
   * 
   */
  // Checks if a user already exists in the database based on their userName.
  public boolean doesUserExist(int id) {
    String query = "SELECT COUNT(*) FROM userDB WHERE id = ?";
    try (PreparedStatement pstmt = connection.prepareStatement(query)) {

      pstmt.setInt(1, id);
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
   * Method: void updateUserName(String oldUsername, String newUsername)
   * </p>
   * 
   * <p>
   * Description: Update the user name of a user given that user's old user name
   * and
   * the new user name.
   * </p>
   * 
   * @param id is the identification of the user
   * 
   * @param newUsername is the new user name for the user
   * 
   */
  // update the user name
  public void updateUserName(int id, String newUsername) {
    String query = "UPDATE userDB SET userName = ? WHERE id = ?";
    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
      pstmt.setString(1, newUsername);
      pstmt.setInt(2, id);
      pstmt.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /*******
   * <p>
   * Method: void updatePassword(int id, String newPassword)
   * </p>
   * 
   * <p>
   * Description: Update the password of a user given that user's username and the
   * new
   * password.
   * </p>
   * 
   * @param id is the identification of the user
   * @param password is the new password of the user
   *
   * 
   */
  // update the password
  public void updatePassword(int id, String password) {
    String query = "UPDATE userDB SET passWord = ? WHERE id = ?";
    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
      pstmt.setString(1, password);
      pstmt.setInt(2, id);
      pstmt.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /*******
   * <p>
   * Method: String generateOneTimePassword(int id)
   * </p>
   * 
   * <p>
   * Description: Update the password of a user with a one-time, temporary PW.
   * </p>
   * 
   * @param id is the identification of the user
   * @return the generated one-time PW
   *
   * 
   */
  // update the password
  public String generateOneTimePassword(int id) {
    String query = "UPDATE userDB SET OTP = ?, isOneTimePW = ? WHERE id = ?";
    // This is where the password is really generated
    String generatedPW = passWordGenerator();
    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
      pstmt.setString(1, generatedPW);
      pstmt.setBoolean(2, true);
      pstmt.setInt(3, id);
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
  public String passWordGenerator() {
    boolean validPW = false;
    String generatedPW = "";
    int passWordLength = 0;
    // Buy yourself a lottery ticket if this hit
    int overloadPreventer = 0;
    // Set of booleans to check requirements
    boolean hasLowerCase = false;
    boolean hasUpperCase = false;
    boolean hasDigit = false;
    boolean hasSpecialCharacter = false;

    // Password length can be from 8 to 16. Adds even more randomization!
    int randomPassWordLength = (int) (Math.random() * 9) + 8;

    // Loop as long as the password is invalid and it's not trying too many times
    while (!validPW && overloadPreventer < 128) {
      int seed = (int) (Math.random() * 7);
      // Shortcut to specifying the existence of special character
      if (seed >= 3)
        hasSpecialCharacter = true;
      switch (seed) {
        // Lower case characters
        case 0:
          generatedPW += (char) ((Math.random() * 26) + 'a');
          hasLowerCase = true;
          break;
        // Upper case characters
        case 1:
          generatedPW += (char) ((Math.random() * 26) + 'A');
          hasUpperCase = true;
          break;
        // Digit
        case 2:
          generatedPW += (char) ((Math.random() * 10) + '0');
          hasDigit = true;
          break;
        // Special characters, ASCII range 33-47
        case 3:
          generatedPW += (char) ((Math.random() * 15) + '!');
          break;
        // Special characters, ASCII range 58-64
        case 4:
          generatedPW += (char) ((Math.random() * 7) + ':');
          break;
        // Special characters, ASCII range 91-96
        case 5:
          generatedPW += (char) ((Math.random() * 6) + '[');
          break;
        // Special characters, ASCII range 123-126
        case 6:
          generatedPW += (char) ((Math.random() * 4) + '{');
          break;
      }
      // The password has increased. Compare it with the new selected random length
      passWordLength++;
      if (passWordLength >= randomPassWordLength) {
        // If the password is valid, you may now break the loop
        if (hasLowerCase && hasUpperCase && hasDigit && hasSpecialCharacter)
          validPW = true;
        // Otherwise, reset the generated password
        else {
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

    // Password has overflowed, return a fixed password... We can tinker with this,
    // but
    // I'd argue this method is already excessive enough
    if (overloadPreventer >= 128)
      return "#1AbqdeYg%";

    return generatedPW;
  }

  /*******
   * <p>
   * Method: void clearOneTimePassword(int id)
   * </p>
   * 
   * <p>
   * Description: Update the password of a user with a one-time, temporary PW.
   * </p>
   * 
   * @param id is the identification of the user
   *
   * 
   */
  // update the password
  public void clearOneTimePassword(int id) {
    String query = "UPDATE userDB SET OTP = ?, isoneTimePW = ? WHERE id = ?";
    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
      pstmt.setString(1, "");
      pstmt.setBoolean(2, false);
      pstmt.setInt(3, id);
      pstmt.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /*******
   * <p>
   * Method: String getFirstName(int id)
   * </p>
   * 
   * <p>
   * Description: Get the first name of a user given that user's id.
   * </p>
   * 
   * @param id is the identification of the user
   * 
   * @return the first name of a user given that user's id
   * 
   */
  // Get the First Name
  public String getFirstName(int id) {
    String query = "SELECT firstName FROM userDB WHERE id = ?";
    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
      pstmt.setInt(1, id);
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
   * Method: void updateFirstName(int id, String firstName)
   * </p>
   * 
   * <p>
   * Description: Update the first name of a user given that user's id and
   * the new first name.
   * </p>
   * 
   * @param id is the identification of the user
   * 
   * @param firstName is the new first name for the user
   * 
   */
  // update the first name
  public void updateFirstName(int id, String firstName) {
    String query = "UPDATE userDB SET firstName = ? WHERE id = ?";
    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
      pstmt.setString(1, firstName);
      pstmt.setInt(2, id);
      pstmt.executeUpdate();
      currentFirstName = firstName;
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /*******
   * <p>
   * Method: String getMiddleName(int id)
   * </p>
   * 
   * <p>
   * Description: Get the middle name of a user given that user's username.
   * </p>
   * 
   * @param id is the identification of the user
   * 
   * @return the middle name of a user given that user's username
   * 
   */
  // get the middle name
  public String getMiddleName(int id) {
    String query = "SELECT MiddleName FROM userDB WHERE id = ?";
    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
      pstmt.setInt(1, id);
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
   * Method: void updateMiddleName(int id, String middleName)
   * </p>
   * 
   * <p>
   * Description: Update the middle name of a user given that user's username and
   * the new
   * middle name.
   * </p>
   * 
   * @param id is the identification of the user
   * 
   * @param middleName is the new middle name for the user
   * 
   */
  // update the middle name
  public void updateMiddleName(int id, String middleName) {
    String query = "UPDATE userDB SET middleName = ? WHERE id = ?";
    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
      pstmt.setString(1, middleName);
      pstmt.setInt(2, id);
      pstmt.executeUpdate();
      currentMiddleName = middleName;
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /*******
   * <p>
   * Method: String getLastName(int id)
   * </p>
   * 
   * <p>
   * Description: Get the last name of a user given that user's username.
   * </p>
   * 
   * @param id is the identification of the user
   * 
   * @return the last name of a user given that user's username
   * 
   */
  // get he last name
  public String getLastName(int id) {
    String query = "SELECT LastName FROM userDB WHERE id = ?";
    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
      pstmt.setInt(1, id);
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
   * Method: void updateLastName(int id, String lastName)
   * </p>
   * 
   * <p>
   * Description: Update the middle name of a user given that user's username and
   * the new
   * middle name.
   * </p>
   * 
   * @param id is the identification of the user
   * 
   * @param lastName is the new last name for the user
   * 
   */
  // update the last name
  public void updateLastName(int id, String lastName) {
    String query = "UPDATE userDB SET lastName = ? WHERE id = ?";
    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
      pstmt.setString(1, lastName);
      pstmt.setInt(2, id);
      pstmt.executeUpdate();
      currentLastName = lastName;
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /*******
   * <p>
   * Method: String getPreferredFirstName(int id)
   * </p>
   * 
   * <p>
   * Description: Get the preferred first name of a user given that user's
   * username.
   * </p>
   * 
   * @param id is the identification of the user
   * 
   * @return the preferred first name of a user given that user's username
   * 
   */
  // get the preferred first name
  public String getPreferredFirstName(int id) {
    String query = "SELECT preferredFirstName FROM userDB WHERE id = ?";
    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
      pstmt.setInt(1, id);
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
   * Method: void updatePreferredFirstName(int id, String
   * preferredFirstName)
   * </p>
   * 
   * <p>
   * Description: Update the preferred first name of a user given that user's
   * username and
   * the new preferred first name.
   * </p>
   * 
   * @param id is the identification of the user
   * 
   * @param preferredFirstName is the new preferred first name for the user
   * 
   */
  // update the preferred first name of the user
  public void updatePreferredFirstName(int id, String preferredFirstName) {
    String query = "UPDATE userDB SET preferredFirstName = ? WHERE id = ?";
    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
      pstmt.setString(1, preferredFirstName);
      pstmt.setInt(2, id);
      pstmt.executeUpdate();
      currentPreferredFirstName = preferredFirstName;
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /*******
   * <p>
   * Method: String getEmailAddress(int id)
   * </p>
   * 
   * <p>
   * Description: Get the email address of a user given that getIntername.
   * </p>
   * 
   * @param id is the identification of the user
   * 
   * @return the email address of a user given that user's username
   * 
   */
  // get the email address
  public String getEmailAddress(int id) {
    String query = "SELECT emailAddress FROM userDB WHERE id = ?";
    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
      pstmt.setInt(1, id);
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
   * Method: insertEvaluation
   * </p>
   *
   * <p>
   * Description: Persists an evaluation after
   * succeeds.
   * </p>
   */
  public void insertEvaluation(int studentID, String[] definedParams, Double[] paramWeights,
      double percentage, int numberGrade, String letterGrade) {
    EvaluationTool.validateEvaluationRubric(definedParams, paramWeights);

    String sql = "INSERT INTO evaluationTool (studentID, definedParams, paramWeights, percentage, numberGrade, letterGrade) VALUES (?, ?, ?, ?, ?, ?)";
    
    try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
        
        Array dbDefinedParams = connection.createArrayOf("VARCHAR", definedParams);
        Array dbParamWeights = connection.createArrayOf("DOUBLE", paramWeights);
        
        preparedStatement.setInt(1, studentID);
        preparedStatement.setArray(2, dbDefinedParams);
        preparedStatement.setArray(3, dbParamWeights);
        preparedStatement.setDouble(4, percentage);
        preparedStatement.setInt(5, numberGrade);
        preparedStatement.setString(6, letterGrade);
        
        preparedStatement.executeUpdate();
        System.out.println("Evaluation inserted successfully for student ID: " + studentID);
        
    } catch (SQLException e) {
        System.out.println("Error inserting evaluation: " + e.getMessage());
    }
}


  /*******
   * <p>
   * Method: void updateEmailAddress(int id, String emailAddress)
   * </p>
   * 
   * <p>
   * Description: Update the email address name of a user given that user's
   * username and
   * the new email address.
   * </p>
   * 
   * @param id is the identification of the user
   * 
   * @param emailAddress is the new preferred first name for the user
   * 
   */
  // update the email address
  public void updateEmailAddress(int id, String emailAddress) {
    String query = "UPDATE userDB SET emailAddress = ? WHERE id = ?";
    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
      pstmt.setString(1, emailAddress);
      pstmt.setInt(2, id);
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
   * @param id is the identification of the user
   * 
   * @return true of the get is successful, else false
   * 
   */
  // get the attributes for a specified user
  public boolean getUserAccountDetails(String username, String password) {
    String query = "SELECT * FROM userDB WHERE username = ? AND password = ?";
    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
      pstmt.setString(1, username);
      pstmt.setString(2, password);
      ResultSet rs = pstmt.executeQuery();
      if (rs.next()) {
    	currentIdentification = rs.getInt("id");
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
   * Method: User getUserAsObject(int id)
   * </p>
   * 
   * <p>
   * Description: Return an instance of a user, particularly one in the list and
   * therefore not the current user
   * </p>
   * 
   * @param id is the identification of the user
   * 
   * @return a user object
   * 
   */
  // get the attributes for a specified user
  public User getUserAsObject(int id) {
    User user = new User();
    String query = "SELECT * FROM userDB WHERE id = ?";
    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
      pstmt.setInt(1, id);
      ResultSet rs = pstmt.executeQuery();
      if (rs.next()) {
    	user.setId(rs.getInt("id"));
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
   * Method: void deleteUser(int id)
   * </p>
   * 
   * <p>
   * Description: Delete the specified user.
   * </p>
   * 
   * @param id is the identification of the user
   * 
   * 
   */
  // update the last name
  public void deleteUser(int id) {
    String query = "DELETE FROM userDB WHERE id = ?";
    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
      pstmt.setInt(1, id);
      pstmt.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /*******
   * <p>
   * Method: boolean updateUserRole(int id, String role, String value)
   * </p>
   * 
   * <p>
   * Description: Update a specified role for a specified user's and set and
   * update all the
   * current user attributes.
   * </p>
   * 
   * @param id 		 is the identification of the user
   * 
   * @param role     is string that specifies the role to update
   * 
   * @param value    is the string that specified TRUE or FALSE for the role
   * 
   * @return true if the update was successful, else false
   * 
   */
  // Update a users role
  public boolean updateUserRole(int id, String role, String value) {
    if (role.compareTo("Admin") == 0) {
      String query = "UPDATE userDB SET adminRole = ? WHERE id = ?";
      try (PreparedStatement pstmt = connection.prepareStatement(query)) {
        pstmt.setString(1, value);
        pstmt.setInt(2, id);
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
      String query = "UPDATE userDB SET newRole1 = ? WHERE id = ?";
      try (PreparedStatement pstmt = connection.prepareStatement(query)) {
        pstmt.setString(1, value);
        pstmt.setInt(2, id);
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
      String query = "UPDATE userDB SET newRole2 = ? WHERE id = ?";
      try (PreparedStatement pstmt = connection.prepareStatement(query)) {
        pstmt.setString(1, value);
        pstmt.setInt(2, id);
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
   * Method: String getAdminRole(int id)
   * </p>
   * 
   * <p>
   * Description: Get whether the user is an admin.
   * </p>
   * 
   * @param id is the identification of the user
   * 
   * @return boolean of admin role
   * 
   */
  // get the email address
  public boolean getAdminRole(int id) {
    String query = "SELECT emailAddress FROM userDB WHERE id = ? AND adminRole = TRUE";
    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
      pstmt.setInt(1, id);
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
   * Method: String getRole1(int id)
   * </p>
   * 
   * <p>
   * Description: Get whether the user has Role1.
   * </p>
   * 
   * @param id is the identification of the user
   * 
   * @return boolean of Role1
   * 
   */
  // get the email address
  public boolean getRole1(int id) {
    String query = "SELECT emailAddress FROM userDB WHERE id = ? AND newRole1 = TRUE";
    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
      pstmt.setInt(1, id);
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
   * Method: String getRole2(int id)
   * </p>
   * 
   * <p>
   * Description: Get whether the user has Role1.
   * </p>
   * 
   * @param id is the identification of the user
   * 
   * @return boolean of Role2
   * 
   */
  // get the email address
  public boolean getRole2(int id) {
    String query = "SELECT emailAddress FROM userDB WHERE id = ? AND newRole2 = TRUE";
    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
      pstmt.setInt(1, id);
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

  /*******
   * <p>
   * Method: boolean getCurrentOTP()
   * </p>
   * 
   * <p>
   * Description: Get the current user's generated one time password.
   * </p>
   * 
   * @return the one time password value is returned
   * 
   */
  public String getCurrentOTP() {
    return currentOTP;
  }
  
  /*******
   * <p>
   * Method: int getCurrentId()
   * </p>
   * 
   * <p>
   * Description: Get the current user's identification
   * </p>
   * 
   * @return the identification of the user
   * 
   */
  public int getCurrentId() {
    return currentIdentification;
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
