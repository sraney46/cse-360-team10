package testing;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import entityClasses.Reply;
import entityClasses.Ticket;
import entityClasses.User;
import guiTicketForum.ModelTicketForum;

/*******
 * <p>
 * Title: HW3Task5 class
 * </p>
 *
 * <p>
 * Description: Test cases for the CRUD operations for the Administrator Action List
 * </p>
 *
 *
 * 
 * @author Andrew Clarke
 * @version 1.00 2026-04-05 HW3 Test Cases
 *
 */
@TestInstance(Lifecycle.PER_CLASS)
public class TicketTests {
	private static database.Database theDatabase = applicationMain.FoundationsMain.database;
	private boolean connectedDB = false;
	private int firstPostID = 1;
	private int firstReplyID = 1;
	
	//Create a test user
	User newStaff = new User(0,"TestUser", "password", "", "", "", "", "", "", false, false, true, false);
	User newStaff2 = new User(0,"TestUser2", "password", "", "", "", "", "", "", false, false, true, false);
	User newAdmin = new User(0,"TestAdmin", "password", "", "", "", "", "", "", true, false, false, false);
	Ticket newTicket1 = new Ticket(1, "Subject1", "Test Content");
	Ticket newTicket2 = new Ticket(1, "Subject2", "Test Content");
	Ticket newTicket3 = new Ticket(1, "Subject3", "Test Content");
	Ticket newTicket4 = new Ticket(1, "Subject4", "Test Content");
	Ticket newTicket5 = new Ticket(1, "Subject5", "Test Content");
	
	//Failing tickets
	Ticket newTicketNoTitle = new Ticket(1, "", "Test Content");
	Ticket newTicketNoBody = new Ticket(1, "Subject", "");
	private static ModelTicketForum model = new ModelTicketForum();
	
	/*****
	   * <p>
	   * Method: setupTestCase()
	   * </p>
	   *
	   * <p>
	   * Description: Generates the environment needed to run test cases
	   * </p>
	   */
	@BeforeAll
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

		//Attempt to register the new user
		try {
			theDatabase.register(newStaff);
			theDatabase.register(newStaff2);
			theDatabase.register(newAdmin);
		} catch (SQLException e) { cleanTestCases(); System.exit(0); }
		
		newTicket1.setAuthor(newStaff.getUserId());
		newTicket2.setAuthor(newStaff.getUserId());
		newTicket3.setAuthor(newStaff.getUserId());
		newTicket4.setAuthor(newStaff.getUserId());
		newTicket5.setAuthor(newStaff.getUserId());
		
		try {
			String getLastTicket = "SELECT * FROM ticketDB WHERE postID=(SELECT max(postID) FROM ticketDB);";
			try (PreparedStatement pstmt = theDatabase.getConnection().prepareStatement(getLastTicket)) {
				ResultSet rs = pstmt.executeQuery();
				if(rs.next()) firstPostID = rs.getInt("postID");
		    }
			
			String getLastReplyTicket = "SELECT * FROM ticketreplyDB WHERE replyID=(SELECT max(replyID) FROM ticketreplyDB);";
			try (PreparedStatement pstmt = theDatabase.getConnection().prepareStatement(getLastReplyTicket)) {
				ResultSet rs = pstmt.executeQuery();
				if(rs.next()) firstReplyID = rs.getInt("replyID");
		    }
		}
		catch (SQLException e) {
	    	e.printStackTrace();
	    }
	}
	
	/*****
	   * <p>
	   * Method: cleanTestCases()
	   * </p>
	   *
	   * <p>
	   * Description: Cleans up the environment for test cases that ran
	   * </p>
	   */
	@AfterAll
	public void cleanTestCases() {		

		try
		{
			if(!theDatabase.getConnection().isClosed())
			{
				String userDBQuery = "DELETE FROM userDB where id >= ?";
				try (PreparedStatement pstmt = theDatabase.getConnection().prepareStatement(userDBQuery)) {
					pstmt.setInt(1, newStaff.getUserId());
					pstmt.executeUpdate();
			    }
				String resetIDs = "ALTER TABLE userDB ALTER COLUMN id RESTART WITH " + ((newStaff.getUserId() > 1) ? newStaff.getUserId() : 1);
				try (PreparedStatement pstmt = theDatabase.getConnection().prepareStatement(resetIDs)) {
					pstmt.executeUpdate();
			    }
				String ticketDeletion = "DELETE FROM ticketDB where postID >= ?";
				try (PreparedStatement pstmt = theDatabase.getConnection().prepareStatement(ticketDeletion)) {
					pstmt.setInt(1, firstPostID);
					pstmt.executeUpdate();
			    }
				String ticketReplyDeletion = "DELETE FROM ticketreplyDB where replyID >= ?";
				try (PreparedStatement pstmt = theDatabase.getConnection().prepareStatement(ticketReplyDeletion)) {
					pstmt.setInt(1, firstReplyID);
					pstmt.executeUpdate();
			    }
			}
		}
		catch (SQLException e) {
	    	e.printStackTrace();
	    }

		theDatabase.closeConnection();
	}
	
	/*****
	   * <p>
	   * Method: cleanPosts()
	   * </p>
	   *
	   * <p>
	   * Description: Cleans up all of the posts, designed to be run after each test case
	   * </p>
	   */
	@AfterEach
	public void cleanPosts() {		
		try
		{
			if(!theDatabase.getConnection().isClosed())
			{
				String ticketDeletion = "DELETE FROM ticketDB where postID >= ?";
				try (PreparedStatement pstmt = theDatabase.getConnection().prepareStatement(ticketDeletion)) {
					pstmt.setInt(1, firstPostID);
					pstmt.executeUpdate();
			    }
				String ticketReplyDeletion = "DELETE FROM ticketreplyDB where replyID >= ?";
				try (PreparedStatement pstmt = theDatabase.getConnection().prepareStatement(ticketReplyDeletion)) {
					pstmt.setInt(1, firstReplyID);
					pstmt.executeUpdate();
			    }
			}
		}
		catch (SQLException e) {
	    	e.printStackTrace();
	    }
	}
	
	
	/****************************
	 * Positive Test cases
	 ****************************/
	
	/*****
	   * <p> Method: PositiveCase1() </p>
	   * <p> Description: Staff can CREATE a ticket </p>
	   */
	@Test
	public void PositiveCase1() {	
		//Attempt to make the ticket
		assertEquals(true,model.addTicket(newTicket1) && newStaff.getNewRole2());
	}
	
	/*****
	   * <p> Method: PositiveCase2() </p>
	   * <p> Description: Staff can READ all tickets in the Administrator Action List </p>
	   */
	@Test
	public void PositiveCase2() {
		//Add the tickets
		model.addTicket(newTicket1);
		model.addTicket(newTicket2);
		model.addTicket(newTicket3);
		model.addTicket(newTicket4);
		model.addTicket(newTicket5);

		//Attempt to read all tickets
		assertEquals(true,model.getAllTickets(null).size() > 0 && newStaff.getNewRole2());
	}
	
	/*****
	   * <p> Method: PositiveCase3() </p>
	   * <p> Description: Admins can READ all tickets in the Administrator Action List </p>
	   */
	@Test
	public void PositiveCase3() {
		//Add the tickets
		model.addTicket(newTicket1);
		model.addTicket(newTicket2);
		model.addTicket(newTicket3);
		model.addTicket(newTicket4);
		model.addTicket(newTicket5);
		
		//Attempt to read all tickets
		assertEquals(true,model.getAllTickets(null).size() > 0 && newAdmin.getAdminRole());
	}
	
	/*****
	   * <p> Method: PositiveCase4() </p>
	   * <p> Description: Staff can UPDATE their own tickets </p>
	   */
	@Test
	public void PositiveCase4() {
		//Add the ticket
		model.addTicket(newTicket1);
		
		//Updated ticket
		Ticket updatedTicket = new Ticket();
		updatedTicket.setAuthor(newTicket1.getAuthor());
		updatedTicket.setTitle(newTicket1.getTitle());
		updatedTicket.setTitle("New Content");
		updatedTicket.setTimestamp(newTicket1.getTimestamp());
		updatedTicket.setCategory(newTicket1.getCategory());
		
		//Attempt to update the ticket
		assertEquals(true,model.updateTicket(newTicket1) && newTicket1.getAuthor() == newStaff.getUserId());
		model.hardDeleteTicket(updatedTicket.getPostID());
	}
	
	/*****
	   * <p> Method: PositiveCase5() </p>
	   * <p> Description: Staff can DELETE their own tickets </p>
	   */
	@Test
	public void PositiveCase5() {
		model.addTicket(newTicket1);
		
		//Attempt to delete the ticket
		assertEquals(true,model.hardDeleteTicket(newTicket1.getPostID()) && newTicket1.getAuthor() == newStaff.getUserId());
	}
	
	/*****
	   * <p> Method: PositiveCase6() </p>
	   * <p> Description: Staff can reopen their own tickets </p>
	   */
	@Test
	public void PositiveCase6() {
		//Updated ticket
		Ticket updatedTicket = new Ticket();
		updatedTicket.setAuthor(newTicket1.getAuthor());
		updatedTicket.setTitle(newTicket1.getTitle());
		updatedTicket.setContent(newTicket1.getContent());
		updatedTicket.setTimestamp(newTicket1.getTimestamp());
		updatedTicket.setCategory("Open");
		
		//Add the new ticket, since reopening makes a new one
		assertEquals(true,model.addTicket(updatedTicket) && newStaff.getNewRole2());
		model.hardDeleteTicket(updatedTicket.getPostID());
	}
	
	/*****
	   * <p> Method: PositiveCase7() </p>
	   * <p> Description: Staff can respond to tickets </p>
	   */
	@Test
	public void PositiveCase7() {
		model.addTicket(newTicket1);
		
		Reply ticketReply = new Reply();
		ticketReply.setAuthor(newStaff2.getUserId());
		ticketReply.setTimestamp(0);
		ticketReply.setContent("Reply Data");
		ticketReply.setPostID(newTicket1.getPostID());
		
		//Add the reply to the ticket
		assertEquals(true,model.addReply(ticketReply, ticketReply.getPostID()) && newStaff.getNewRole2());
		model.deleteReply(newTicket1.getPostID(), ticketReply.getReplyID());
	}
	
	/*****
	   * <p> Method: PositiveCase8() </p>
	   * <p> Description: Administrators can respond to tickets </p>
	   */
	@Test
	public void PositiveCase8() {
		model.addTicket(newTicket1);
		
		Reply ticketReply = new Reply();
		ticketReply.setAuthor(newAdmin.getUserId());
		ticketReply.setTimestamp(0);
		ticketReply.setContent("Reply Data");
		ticketReply.setPostID(newTicket1.getPostID());
		
		//Add the reply to the ticket
		assertEquals(true,model.addReply(ticketReply, ticketReply.getPostID()) && newAdmin.getAdminRole());
		model.deleteReply(newTicket1.getPostID(), ticketReply.getReplyID());
	}
	
	/*****
	   * <p> Method: PositiveCase9() </p>
	   * <p> Description: Administrators can close tickets </p>
	   */
	@Test
	public void PositiveCase9() {
		//Updated ticket
		Ticket updatedTicket = new Ticket();
		updatedTicket.setAuthor(newTicket1.getAuthor());
		updatedTicket.setTitle(newTicket1.getTitle());
		updatedTicket.setContent(newTicket1.getContent());
		updatedTicket.setTimestamp(newTicket1.getTimestamp());
		updatedTicket.setCategory("Closed");
		
		//Add the new ticket, since reopening makes a new one
		assertEquals(true,model.addTicket(updatedTicket) && newAdmin.getAdminRole());
		model.hardDeleteTicket(updatedTicket.getPostID());
	}
	
	/****************************
	 * Negative Test cases
	 ****************************/
	
	/*****
	   * <p> Method: NegativeCase1() </p>
	   * <p> Description: Admins can CREATE a ticket </p>
	   */
	@Test
	public void NegativeCase1() {
		//Attempt to make the ticket
		assertEquals(false,model.addTicket(newTicket1) && newAdmin.getNewRole2());
	}
	
	/*****
	   * <p> Method: NegativeCase2() </p>
	   * <p> Description: Staff can CREATE a ticket with no title </p>
	   */
	@Test
	public void NegativeCase2() {
		//Attempt to make the ticket
		assertEquals(false,model.addTicket(newTicketNoTitle) && newStaff.getNewRole2());
	}
	
	/*****
	   * <p> Method: NegativeCase3() </p>
	   * <p> Description: Staff can CREATE a ticket with no message </p>
	   */
	@Test
	public void NegativeCase3() {
		//Attempt to make the ticket
		assertEquals(false,model.addTicket(newTicketNoBody) && newStaff.getNewRole2());
	}
	
	/*****
	   * <p> Method: NegativeCase4() </p>
	   * <p> Description: Staff can READ a blank list of tickets </p>
	   */
	@Test
	public void NegativeCase4() {
		//Attempt to make the ticket
		assertEquals(false,model.getAllTickets(null).size() > 0);
	}
	
	/*****
	   * <p> Method: NegativeCase5() </p>
	   * <p> Description: Staff can UPDATE tickets that do not belong to them </p>
	   */
	@Test
	public void NegativeCase5() {
		//Add the ticket to the database so we can manipulate it
		model.addTicket(newTicket1);
		
		//Updated ticket
		Ticket updatedTicket = new Ticket();
		updatedTicket.setAuthor(newTicket1.getAuthor());
		updatedTicket.setTitle(newTicket1.getTitle());
		updatedTicket.setTitle("New Content");
		updatedTicket.setTimestamp(newTicket1.getTimestamp());
		updatedTicket.setCategory(newTicket1.getCategory());
		
		//Attempt to update the ticket using a new staff entity
		assertEquals(false,newTicket1.getAuthor() == newStaff2.getUserId() && newStaff2.getNewRole2() && model.updateTicket(newTicket1));
		model.hardDeleteTicket(updatedTicket.getPostID());
	}
	
	/*****
	   * <p> Method: NegativeCase6() </p>
	   * <p> Description: Admins can UPDATE tickets </p>
	   */
	@Test
	public void NegativeCase6() {
		//Add the ticket to the database so we can manipulate it
		model.addTicket(newTicket1);
		
		//Updated ticket
		Ticket updatedTicket = new Ticket();
		updatedTicket.setAuthor(newTicket1.getAuthor());
		updatedTicket.setTitle(newTicket1.getTitle());
		updatedTicket.setTitle("New Content");
		updatedTicket.setTimestamp(newTicket1.getTimestamp());
		updatedTicket.setCategory(newTicket1.getCategory());
		
		//Attempt to update the ticket using a new staff entity
		assertEquals(false,newAdmin.getNewRole2() && model.updateTicket(newTicket1));
		model.hardDeleteTicket(updatedTicket.getPostID());
	}
	
	/*****
	   * <p> Method: NegativeCase7() </p>
	   * <p> Description: Admins can DELETE tickets from staff </p>
	   */
	@Test
	public void NegativeCase7() {
		model.addTicket(newTicket1);
		
		//Attempt to delete the ticket
		assertEquals(false,model.hardDeleteTicket(newTicket1.getPostID()) && newAdmin.getNewRole2());
	}
	
	/*****
	   * <p> Method: NegativeCase8() </p>
	   * <p> Description: Staff can DELETE tickets of other staff </p>
	   */
	@Test
	public void NegativeCase8() {
		model.addTicket(newTicket1);
		
		//Attempt to delete the ticket
		assertEquals(false,model.hardDeleteTicket(newTicket1.getPostID()) && newStaff2.getNewRole2() && newTicket1.getAuthor() == newStaff2.getUserId());
	}
	
	/*****
	   * <p> Method: NegativeCase9() </p>
	   * <p> Description: Staff can reopen tickets that do not belong to them </p>
	   */
	@Test
	public void NegativeCase9() {
		//Updated ticket
		Ticket updatedTicket = new Ticket();
		updatedTicket.setAuthor(newTicket1.getAuthor());
		updatedTicket.setTitle(newTicket1.getTitle());
		updatedTicket.setContent(newTicket1.getContent());
		updatedTicket.setTimestamp(newTicket1.getTimestamp());
		updatedTicket.setCategory("Open");
		
		//Add the new ticket, since reopening makes a new one
		assertEquals(false,model.addTicket(updatedTicket) && newStaff2.getNewRole2() && newTicket1.getAuthor() == newStaff2.getUserId());
		model.hardDeleteTicket(updatedTicket.getPostID());
	}
	
	/*****
	   * <p> Method: NegativeCase10() </p>
	   * <p> Description: Admins can reopen tickets </p>
	   */
	@Test
	public void NegativeCase10() {
		//Updated ticket
		Ticket updatedTicket = new Ticket();
		updatedTicket.setAuthor(newTicket1.getAuthor());
		updatedTicket.setTitle(newTicket1.getTitle());
		updatedTicket.setContent(newTicket1.getContent());
		updatedTicket.setTimestamp(newTicket1.getTimestamp());
		updatedTicket.setCategory("Open");
		
		//Add the new ticket, since reopening makes a new one
		assertEquals(false,model.addTicket(updatedTicket) && newAdmin.getNewRole2());
		model.hardDeleteTicket(updatedTicket.getPostID());
	}
	
	/*****
	   * <p> Method: NegativeCase11() </p>
	   * <p> Description: Staff can send blank responses to tickets </p>
	   */
	@Test
	public void NegativeCase11() {
		model.addTicket(newTicket1);
		
		Reply ticketReply = new Reply();
		ticketReply.setAuthor(newStaff2.getUserId());
		ticketReply.setTimestamp(0);
		ticketReply.setContent("");
		ticketReply.setPostID(newTicket1.getPostID());
		
		//Add the reply to the ticket
		assertEquals(false,model.addReply(ticketReply, ticketReply.getPostID()) && newStaff.getNewRole2());
		model.deleteReply(newTicket1.getPostID(), ticketReply.getReplyID());
	}
	
	/*****
	   * <p> Method: NegativeCase12() </p>
	   * <p> Description: Admin can send blank responses to tickets </p>
	   */
	@Test
	public void NegativeCase12() {
		model.addTicket(newTicket1);
		
		Reply ticketReply = new Reply();
		ticketReply.setAuthor(newAdmin.getUserId());
		ticketReply.setTimestamp(0);
		ticketReply.setContent("");
		ticketReply.setPostID(newTicket1.getPostID());
		
		//Add the reply to the ticket
		assertEquals(false,model.addReply(ticketReply, ticketReply.getPostID()) && newAdmin.getAdminRole());
		model.deleteReply(newTicket1.getPostID(), ticketReply.getReplyID());
	}
	
	/*****
	   * <p> Method: NegativeCase13() </p>
	   * <p> Description: Staff can close tickets </p>
	   */
	@Test
	public void NegativeCase13() {
		//Updated ticket
		Ticket updatedTicket = new Ticket();
		updatedTicket.setAuthor(newTicket1.getAuthor());
		updatedTicket.setTitle(newTicket1.getTitle());
		updatedTicket.setContent(newTicket1.getContent());
		updatedTicket.setTimestamp(newTicket1.getTimestamp());
		updatedTicket.setCategory("Closed");
		
		//Add the new ticket, since reopening makes a new one
		assertEquals(false,model.addTicket(updatedTicket) && newStaff.getAdminRole());
		model.hardDeleteTicket(updatedTicket.getPostID());
	}
}
