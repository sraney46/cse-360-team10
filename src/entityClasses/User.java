package entityClasses;

import java.util.ArrayList;
import java.util.List;

/*******
 * <p>
 * Title: User Class
 * </p>
 * 
 * <p>
 * Description: This User class represents a user entity in the system. It
 * contains the user's
 * details such as userName, password, and roles being played.
 * </p>
 * 
 * <p>
 * Copyright: Lynn Robert Carter © 2025
 * </p>
 * 
 * @author Lynn Robert Carter
 * 
 * 
 */

public class User {

  /*
   * These are the private attributes for this entity object
   */
  private String userName;
  private String password;
  private String OTP;
  private String firstName;
  private String middleName;
  private String lastName;
  private String preferredFirstName;
  private String emailAddress;
  private boolean adminRole;
  private boolean role1;
  private boolean role2;
  private boolean tempPW;

  /*****
   * <p>
   * Method: User()
   * </p>
   * 
   * <p>
   * Description: This default constructor is not used in this system.
   * </p>
   */
  public User() {

  }

  /*****
   * <p>
   * Method: User(String userName, String password, String OTP, String fn, String
   * mn, String ln, String pfn,
   * String ea, boolean r1, boolean r2, boolean r3, boolean tpw)
   * </p>
   * 
   * <p>
   * Description: This constructor is used to establish user entity objects.
   * </p>
   * 
   * @param userName specifies the account userName for this user
   * 
   * @param password specifies the account password for this user
   * 
   * @param OTP      specifies the one time password for this user
   *
   * @param fn       specifies the first name for this user
   *
   * @param mn       specifies the middle name for this user
   *
   * @param ln       specifies the last name for this user
   *
   * @param pfn      specifies the preferred first name for this user
   *
   * @param ea       specifies the email address for this user
   *
   * @param r1       specifies the the Admin attribute (TRUE or FALSE) for this
   *                 user
   * 
   * @param r2       specifies the the Student attribute (TRUE or FALSE) for this
   *                 user
   * 
   * @param r3       specifies the the Reviewer attribute (TRUE or FALSE) for this
   *                 user
   *
   * @param tpw      specifies the temp password for this user
   * 
   * 
   */
  // Constructor to initialize a new User object with userName, password, and
  // role.
  public User(String userName, String password, String OTP, String fn, String mn, String ln, String pfn,
      String ea, boolean r1, boolean r2, boolean r3, boolean tpw) {
    this.userName = userName;
    this.password = password;
    this.OTP = OTP;
    this.firstName = fn;
    this.middleName = mn;
    this.lastName = ln;
    this.preferredFirstName = pfn;
    this.emailAddress = ea;
    this.adminRole = r1;
    this.role1 = r2;
    this.role2 = r3;
    this.tempPW = tpw;
  }

  /*****
   * <p>
   * Method: void setAdminRole(boolean role)
   * </p>
   * 
   * <p>
   * Description: This setter defines the Admin role attribute.
   * </p>
   * 
   * @param role is a boolean that specifies if this user in playing the Admin
   *             role.
   * 
   */
  // Sets the role of the Admin user.
  public void setAdminRole(boolean role) {
    this.adminRole = role;
  }

  /*****
   * <p>
   * Method: void setRole1User(boolean role)
   * </p>
   * 
   * <p>
   * Description: This setter defines the role1 attribute.
   * </p>
   * 
   * @param role is a boolean that specifies if this user in playing role1.
   * 
   */
  // Sets the role1 user.
  public void setRole1User(boolean role) {
    this.role1 = role;
  }

  /*****
   * <p>
   * Method: void setRole2User(boolean role)
   * </p>
   * 
   * <p>
   * Description: This setter defines the role2 attribute.
   * </p>
   * 
   * @param role is a boolean that specifies if this user in playing role2.
   * 
   */
  // Sets the role2 user.
  public void setRole2User(boolean role) {
    this.role2 = role;
  }

  /*****
   * <p>
   * Method: String getUserName()
   * </p>
   * 
   * <p>
   * Description: This getter returns the UserName.
   * </p>
   * 
   * @return a String of the UserName
   * 
   */
  // Gets the current value of the UserName.
  public String getUserName() {
    return userName;
  }

  /*****
   * <p>
   * Method: String getPassword()
   * </p>
   * 
   * <p>
   * Description: This getter returns the Password.
   * </p>
   * 
   * @return a String of the password
   *
   */
  // Gets the current value of the Password.
  public String getPassword() {
    return password;
  }

  /*****
   * <p>
   * Method: String getOTP()
   * </p>
   * 
   * <p>
   * Description: This getter returns the OTP.
   * </p>
   * 
   * @return a String of the OTP
   *
   */

  // Gets the current OTP.
  public String getOTP() {
    return OTP;
  }

  /*****
   * <p>
   * Method: String getFirstName()
   * </p>
   * 
   * <p>
   * Description: This getter returns the FirstName.
   * </p>
   * 
   * @return a String of the FirstName
   *
   */
  // Gets the current value of the FirstName.
  public String getFirstName() {
    return firstName;
  }

  /*****
   * <p>
   * Method: String getMiddleName()
   * </p>
   * 
   * <p>
   * Description: This getter returns the MiddleName.
   * </p>
   * 
   * @return a String of the MiddleName
   *
   */
  // Gets the current value of the Student role attribute.
  public String getMiddleName() {
    return middleName;
  }

  /*****
   * <p>
   * Method: String getLasteName()
   * </p>
   * 
   * <p>
   * Description: This getter returns the LastName.
   * </p>
   * 
   * @return a String of the LastName
   *
   */
  // Gets the current value of the Student role attribute.
  public String getLastName() {
    return lastName;
  }

  /*****
   * <p>
   * Method: String getPreferredFirstName()
   * </p>
   * 
   * <p>
   * Description: This getter returns the PreferredFirstName.
   * </p>
   * 
   * @return a String of the PreferredFirstName
   *
   */
  // Gets the current value of the Student role attribute.
  public String getPreferredFirstName() {
    return preferredFirstName;
  }

  /*****
   * <p>
   * Method: String getFullame()
   * </p>
   * 
   * <p>
   * Description: This getter returns the FullName.
   * </p>
   * 
   * @return a String of the MiddleName
   *
   */
  // Gets the full name of the user
  public String getFullName() {
    return firstName + " " + (middleName.length() > 0 ? middleName : " ") + " " + lastName;
  }

  /*****
   * <p>
   * Method: String getEmailAddress()
   * </p>
   * 
   * <p>
   * Description: This getter returns the EmailAddress.
   * </p>
   * 
   * @return a String of the EmailAddress
   *
   */
  // Gets the current value of the Student role attribute.
  public String getEmailAddress() {
    return emailAddress;
  }

  /*****
   * <p>
   * Method: void setUserName(String s)
   * *
   * </p>
   *
   * 
   * <p>
   * Description: This setter sets the username.
   * </p>
   *
   * @param s is a vale taken from the input of username text field.
   * 
   *
   */
  // Sets the current value of the Student role attribute.

  public void setUserName(String s) {
    userName = s;
  }

  /*****
   * <p>
   * Method: void setPassword(String s)
   * *
   * </p>
   *
   * 
   * <p>
   * Description: This setter sets the password.
   * </p>
   *
   * @param s is a vale taken from the input of password text field.
   * 
   *
   */
  // Sets the current value of the Student role attribute.
  public void setPassword(String s) {
    password = s;
  }

  /*****
   * <p>
   * Method: void setFirstName(String s)
   * *
   * </p>
   *
   * 
   * <p>
   * Description: This setter sets the users first name.
   * </p>
   *
   * @param s is a vale taken from the input of first name text field.
   * 
   *
   */
  // Sets the current value of the Student role attribute.

  public void setFirstName(String s) {
    firstName = s;
  }

  /*****
   * <p>
   * Method: void setMiddleName(String s)
   * *
   * </p>
   *
   * 
   * <p>
   * Description: This setter sets the users middle name.
   * </p>
   *
   * @param s is a vale taken from the input of middle name text field.
   * 
   *
   */
  // Sets the current value of the Student role attribute.

  public void setMiddleName(String s) {
    middleName = s;
  }

  /*****
   * <p>
   * Method: void setLastName(String s)
   * *
   * </p>
   *
   * 
   * <p>
   * Description: This setter sets the users last name.
   * </p>
   *
   * @param s is a vale taken from the input of last name text field.
   * 
   *
   */
  // Sets the current value of the Student role attribute.

  public void setLastName(String s) {
    lastName = s;
  }

  /*****
   * <p>
   * Method: void setPreferredFirstName(String s)
   * *
   * </p>
   *
   * 
   * <p>
   * Description: This setter sets the users perferred first name.
   * </p>
   *
   * @param s is a vale taken from the input of perferred first name text field.
   * 
   *
   */
  // Sets the current value of the Student role attribute.

  public void setPreferredFirstName(String s) {
    preferredFirstName = s;
  }

  /*****
   * <p>
   * Method: void setEmailAddress(String s)
   * *
   * </p>
   *
   * 
   * <p>
   * Description: This setter sets the users email.
   * </p>
   *
   * @param s is a vale taken from the input of email text field.
   * 
   *
   */
  // Sets the current value of the Student role attribute.

  public void setEmailAddress(String s) {
    emailAddress = s;
  }

  /*****
   * <p>
   * Method: String getAdminRole()
   * </p>
   * 
   * <p>
   * Description: This getter returns the value of the Admin role attribute.
   * </p>
   * 
   * @return a String of "TRUE" or "FALSE" based on state of the attribute
   *
   */
  // Gets the current value of the Admin role attribute.
  public boolean getAdminRole() {
    return adminRole;
  }

  /*****
   * <p>
   * Method: String getRole1()
   * </p>
   * 
   * <p>
   * Description: This getter returns the value of the role1 attribute.
   * </p>
   * 
   * @return a String of "TRUE" or "FALSE" based on state of the attribute
   *
   */
  // Gets the current value of the role1 attribute.
  public boolean getNewRole1() {
    return role1;
  }

  /*****
   * <p>
   * Method: String getRole2()
   * </p>
   * 
   * <p>
   * Description: This getter returns the value of the role2 attribute.
   * </p>
   * 
   * @return a String of "TRUE" or "FALSE" based on state of the attribute
   *
   */
  // Gets the current value of the role2 attribute.
  public boolean getNewRole2() {
    return role2;
  }

  /*****
   * <p>
   * Method: boolean isTemporaryPassword()
   * </p>
   * 
   * <p>
   * Description: This getter returns whether the user has a temporary password
   * </p>
   * 
   * @return a boolean
   *
   */
  // Gets the current value of the role2 attribute.
  public boolean isTemporaryPassword() {
    return tempPW;
  }

  /*****
   * <p>
   * Method: String getRoleString()
   * </p>
   * 
   * <p>
   * Description: This getter returns the string of all the roles.
   * </p>
   * 
   * @return a String of "TRUE" or "FALSE" based on state of the attribute
   *
   */
  // Gets the current value of the role2 attribute.
  public String getRoleString() {
    List<String> roles = new ArrayList<>();
    if (adminRole)
      roles.add("Admin");
    if (role1)
      roles.add("Staff");
    if (role2)
      roles.add("Student");
    return String.join("|", roles);
  }

  /*****
   * <p>
   * Method: String getRoles()
   * </p>
   * 
   * <p>
   * Description: This getter returns the string of all the roles.
   * </p>
   * 
   * @return a String of "TRUE" or "FALSE" based on state of the attribute
   *
   */
  // Gets the current value of the role2 attribute.
  public String getRoles() {
    List<String> roles = new ArrayList<>();

    if (getAdminRole()) {
      roles.add("Admin");
    }
    if (getNewRole1()) {
      roles.add("Staff");
    }
    if (getNewRole2()) {
      roles.add("Student");
    }

    // If no roles assigned
    if (roles.isEmpty()) {
      return "No Roles";
    }

    // Join roles with comma and space
    return String.join(", ", roles);
  }

  /*****
   * <p>
   * Method: int getNumRoles()
   * </p>
   * 
   * <p>
   * Description: This getter returns the number of roles this user plays (0 - 5).
   * </p>
   * 
   * @return a value 0 - 5 of the number of roles this user plays
   *
   */
  // Gets the current value of the Staff role attribute.
  public int getNumRoles() {
    int numRoles = 0;
    if (adminRole)
      numRoles++;
    if (role1)
      numRoles++;
    if (role2)
      numRoles++;
    return numRoles;
  }
}
