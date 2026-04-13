package guiTicketForum;

import entityClasses.Ticket;
import entityClasses.Reply;
import entityClasses.User;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.*;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import database.Database;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/*******
 * <p>Title: ViewTicketForum Class.</p>
 *
 * <p>Description: The JavaFX-based discussion forum page. Displays a scrollable
 * list of Tickets on the left (1/3 width) and the selected Ticket's full content
 * and replies on the right (2/3 width). Supports creating, editing, and deleting
 * Tickets as well as adding replies.</p>
 */
public class ViewTicketForum {

    /*-*******************************************************************************************
     Attributes
    */

    private static double width  = applicationMain.FoundationsMain.WINDOW_WIDTH  * 1.75;
    private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT * 1.75;

    // GUI Area 1 — Top bar
    protected static Label label_PageTitle = new Label("Discussion Forum");
    protected static Label label_UserDetails = new Label();
    protected static Button button_NewTicket = new Button("+ New Ticket");
    protected static Button button_NewThread = new Button("+ New Thread");
    protected static Button button_PopulateDatabase = new Button("Populate Database");
    protected static Button button_UpdateThisUser = new Button("Account Update");
    protected static ComboBox<String> combo_Category = new ComboBox<>();
    protected static ComboBox<String> combo_SearchCriteria = new ComboBox<>();
    protected static TextField textField_searchCriteria = new TextField();
    protected static Label label_searchText = new Label();
    protected static ObservableList<String> threadCategories = FXCollections.observableArrayList(
    	    "General", "Homework", "Lectures", "Assignments", "Exams"
    	);
    protected static ComboBox<String> combo_ReadStatus = new ComboBox<>();

    protected static Line line_Separator1 = new Line();

    // GUI Area 2 — Left Ticket list
    protected static ScrollPane scrollPane_TicketList;
    protected static VBox vbox_TicketList;

    // GUI Area 2 — Right Ticket detail panel
    protected static ScrollPane scrollPane_TicketDetail;
    protected static VBox vbox_TicketDetail;

    // GUI Area 3 — Bottom bar
    protected static Button button_Return = new Button("Return");
    protected static Button button_Logout = new Button("Logout");
    protected static Button button_Quit   = new Button("Quit");

    // Internal state
    private static ViewTicketForum theView;
    private static Database theDatabase = applicationMain.FoundationsMain.database;
    private static ModelTicketForum model = new ModelTicketForum();

    protected static Stage theStage;
    protected static Pane theRootPane;
    protected static User theUser;
    protected static Ticket theSelectedTicket = null;
    protected static String returnPage = "";

    // The scene used for the discussion forum. This can get away with being protected...
    protected static Scene theTicketForumScene = null;

    // Alerts
    protected static Alert alertDeleteConfirm = new Alert(Alert.AlertType.CONFIRMATION);
    protected static Alert alertValidation = new Alert(Alert.AlertType.WARNING);
 	protected static Alert alertPopulateDatabase = new Alert(AlertType.CONFIRMATION);


    /*-*******************************************************************************************
     Entry Point
    */

    /**********
     * <p>Method: displayTicketForum(Stage ps, User user)</p>
     *
     * <p>Description: Single entry point to show the Discussion Forum page.
     * Sets shared references, instantiates the singleton if needed, refreshes
     * the Ticket list, and displays the scene.</p>
     *
     * @param ps   the JavaFX Stage to use
     * @param user the currently logged-in User
     * @param returnPageStr the page to return to
     */
    public static void displayTicketForum(Stage ps, User user, String returnPageStr) {
        theStage = ps;
        theUser  = user;
        returnPage = returnPageStr;

        if (theView == null) theView = new ViewTicketForum();
        
        // Only show if user is staff
        if (returnPage.equals("Staff")) {
        	  button_NewThread.setVisible(true);
              button_NewThread.setManaged(true);
        } else {
        	  button_NewThread.setVisible(false);
              button_NewThread.setManaged(false);
        }
        
        combo_ReadStatus.setValue("Read Status");
      

        label_UserDetails.setText("User: " + theUser.getUserName());
        refreshTicketList();

        theStage.setTitle("Discussion Forum");
        theStage.setScene(theTicketForumScene);
        theStage.show();
    }

    /*-*******************************************************************************************
     Constructor — builds all static GUI elements once
    */

    /**********
     * <p>Constructor: ViewTicketForum()</p>
     *
     * <p>Description: Initializes all GUI widgets, sets their layout, fonts, sizes,
     * and event handlers. This is a singleton so it runs only once.</p>
     */
    public ViewTicketForum() {

        theRootPane = new Pane();
        theTicketForumScene = new Scene(theRootPane, width, height);

        theTicketForumScene.getStylesheets().add(
            getClass().getResource("/applicationMain/application.css").toExternalForm()
        );

        double topBarHeight  = 95;
        double botBarY = height - 75;
        double contentY = topBarHeight + 15;
        double contentHeight = botBarY - contentY - 15;
        double leftWidth = (width - 60) / 3.0;
        double rightWidth = (width - 60) - leftWidth - 15;
        double leftX = 20;
        double rightX = leftX + leftWidth + 15;

        // ── GUI Area 1 ──────────────────────────────────────────────

        setupLabelUI(label_PageTitle, "Arial", 28, width, Pos.CENTER, 0, 5);
        label_PageTitle.setText("Discussion Forum");

        setupLabelUI(label_UserDetails, "Arial", 16, 300, Pos.BASELINE_LEFT, 20, 5);

        setupButtonUI(button_NewTicket, "Dialog", 16, 100, Pos.BASELINE_LEFT, width/2 - 475, 55);
        button_NewTicket.setOnAction(_ -> showNewTicketDialog());
        
        setupButtonUI(button_NewThread, "Dialog", 16, 100, Pos.BASELINE_LEFT, width/2 - 475, 20);
        button_NewThread.setOnAction(_ -> showNewThreadDialog());
        

        
        setupButtonUI(button_PopulateDatabase, "Dialog", 16, 100, Pos.BASELINE_LEFT, width/2 - 75, 55);
        button_PopulateDatabase.setOnAction((_) -> { 
        
        	
        	// Show the alert and wait for user response
		    Optional<ButtonType> result = alertPopulateDatabase.showAndWait();
		    if (result.isPresent() && result.get() == ButtonType.OK) {
		        // User pressed OK, proceed
		        theDatabase.populateDatabaseWithTestPosts();
		        refreshTicketList();
		    } else {
		        // User cancelled, do nothing
		        System.out.println("Database population cancelled by user.");
		    }
        });
        
        button_PopulateDatabase.setVisible(false);
        button_PopulateDatabase.setManaged(false);
        
        ObservableList<String> filterCategories = FXCollections.observableArrayList("All Threads");
        filterCategories.addAll(threadCategories);
        combo_Category.setItems(filterCategories);

        combo_Category.setValue("All Threads");
        combo_Category.getStyleClass().add("thread-combo-box");
        combo_Category.setLayoutX((width/2 - 600) - 50);
        combo_Category.setLayoutY(55);
        combo_Category.setPrefWidth(150);
        combo_Category.setPrefHeight(16);
        combo_Category.setOnAction(_ -> {     
            refreshTicketList();
        });
        
        combo_ReadStatus.setItems(FXCollections.observableArrayList(
        	    "Read Status", "All", "Read", "Unread"
        	));
    	combo_ReadStatus.setValue("Read Status");
    	combo_ReadStatus.getStyleClass().add("default-combo-box");
    	combo_ReadStatus.setLayoutX(width/2 - 490 + 150);  // adjust x to sit next to combo_Category
    	combo_ReadStatus.setLayoutY(18);
    	combo_ReadStatus.setPrefWidth(130);
    	combo_ReadStatus.setPrefHeight(16);
    	combo_ReadStatus.setOnAction(_ -> refreshTicketList());
        
        // Andrew C -- Data for the text search bar
        setupLabelUI(label_searchText, "Arial", 18, 64, Pos.BASELINE_LEFT, 355, 60);
        label_searchText.setText("Search For:");
        label_searchText.setStyle(
        		"-fx-font-size: 14px;"
        );
        
        setupTextFieldUI(textField_searchCriteria, "Dialog", 18, 600, Pos.BASELINE_LEFT, 460, 52);
        textField_searchCriteria.textProperty().addListener(_ -> {
        	refreshTicketList();
        });
        
        // Categorical text filter combo
        combo_SearchCriteria.setItems(FXCollections.observableArrayList(
            "Title", "Content", "Author"
        ));
        combo_SearchCriteria.setValue("All");
        combo_SearchCriteria.getStyleClass().add("default-combo-box");
        combo_SearchCriteria.setLayoutX(width/2 + 372);
        combo_SearchCriteria.setLayoutY(55);
        combo_SearchCriteria.setPrefWidth(100);
        combo_SearchCriteria.setPrefHeight(16);
        combo_SearchCriteria.setOnAction(_ -> {     
            refreshTicketList();
        });
        
        // Here's what's left of GUI Area 1
        setupButtonUI(button_UpdateThisUser, "Dialog", 16, 160, Pos.CENTER,
                      width - 180, 45);
        button_UpdateThisUser.setOnAction(_ ->
            guiUserUpdate.ViewUserUpdate.displayUserUpdate(theStage, theUser));

        line_Separator1.setStartX(20);
        line_Separator1.setStartY(topBarHeight);
        line_Separator1.setEndX(width - 20);
        line_Separator1.setEndY(topBarHeight);
        line_Separator1.setStyle("-fx-stroke: #555;");

        // ── GUI Area 2 — Left Ticket List 

        vbox_TicketList = new VBox(8);
        vbox_TicketList.setPadding(new Insets(12));
        vbox_TicketList.setStyle("-fx-background-color: transparent;");
        vbox_TicketList.setFillWidth(true);

        scrollPane_TicketList = new ScrollPane(vbox_TicketList);
        scrollPane_TicketList.setFitToWidth(true);
        scrollPane_TicketList.setFitToHeight(false);
        scrollPane_TicketList.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane_TicketList.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane_TicketList.setStyle(
            "-fx-background-color: #000;" +
            "-fx-background-radius: 15px;" +
            "-fx-border-radius: 15px;"
        );
        scrollPane_TicketList.viewportBoundsProperty().addListener((_) -> {
            scrollPane_TicketList.lookup(".viewport").setStyle(
                "-fx-background-color: transparent;" +
                "-fx-background-radius: 15px;"
            );
        });
        scrollPane_TicketList.setLayoutX(leftX);
        scrollPane_TicketList.setLayoutY(contentY);
        scrollPane_TicketList.setPrefWidth(leftWidth);
        scrollPane_TicketList.setPrefHeight(contentHeight);
        scrollPane_TicketList.getStyleClass().add("custom-scroll");

        // ── GUI Area 2 — Right Ticket Detail 

        vbox_TicketDetail = new VBox(12);
        vbox_TicketDetail.setPadding(new Insets(20));
        vbox_TicketDetail.setStyle("-fx-background-color: transparent;");
        vbox_TicketDetail.setFillWidth(true);

        scrollPane_TicketDetail = new ScrollPane(vbox_TicketDetail);
        scrollPane_TicketDetail.setFitToWidth(true);
        scrollPane_TicketDetail.setFitToHeight(false);
        scrollPane_TicketDetail.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane_TicketDetail.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane_TicketDetail.setStyle(
            "-fx-background-color: #1a1a1e;" +
            "-fx-background-radius: 15px;" +
            "-fx-border-radius: 15px;"
        );
        scrollPane_TicketDetail.viewportBoundsProperty().addListener((_) -> {
            scrollPane_TicketDetail.lookup(".viewport").setStyle(
                "-fx-background-color: transparent;" +
                "-fx-background-radius: 15px;"
            );
        });
        scrollPane_TicketDetail.setLayoutX(rightX);
        scrollPane_TicketDetail.setLayoutY(contentY);
        scrollPane_TicketDetail.setPrefWidth(rightWidth);
        scrollPane_TicketDetail.setPrefHeight(contentHeight);
        scrollPane_TicketDetail.getStyleClass().add("custom-scroll");

        // ── GUI Area 3 — Bottom Bar ───────────────────────────────────

        Line line_Separator4 = new Line(20, botBarY - 10, width - 20, botBarY - 10);
        line_Separator4.setStyle("-fx-stroke: #555;");

        setupButtonUI(button_Return, "Dialog", 18, 210, Pos.CENTER, 20, botBarY);
        button_Return.setOnAction(_ -> ControllerTicketForum.performReturn());

        setupButtonUI(button_Logout, "Dialog", 18, 210, Pos.CENTER,
                      (width / 2) - 105, botBarY);
        button_Logout.setOnAction(_ -> ControllerTicketForum.performLogout());

        setupButtonUI(button_Quit, "Dialog", 18, 210, Pos.CENTER,
                      width - 230, botBarY);
        button_Quit.setOnAction(_ -> ControllerTicketForum.performQuit());

        // Alerts
        alertDeleteConfirm.setTitle("Delete Ticket");
        alertDeleteConfirm.setHeaderText("Are you sure you want to delete this Ticket?");
        alertDeleteConfirm.setContentText("This action cannot be undone.");

        alertValidation.setTitle("Validation Error");
        alertValidation.setHeaderText("Please fix the following:");
        
        alertPopulateDatabase.setTitle("Confirm Database Population");
		alertPopulateDatabase.setHeaderText("This action will reset the database Ticket\nand reply tables with default values");
		alertPopulateDatabase.setContentText("Press OK to continue or Cancel to abort.");

        // ── Add all to pane ───────────────────────────────────────────

        theRootPane.getChildren().addAll(
            label_PageTitle, label_UserDetails,
            button_NewTicket, combo_Category, button_UpdateThisUser,
            button_PopulateDatabase,
            line_Separator1,
            scrollPane_TicketList,
            scrollPane_TicketDetail,
            button_NewThread,
            combo_ReadStatus,
            line_Separator4,
            button_Return, button_Logout, button_Quit,
            textField_searchCriteria, combo_SearchCriteria, label_searchText
        );
    }

    /*-*******************************************************************************************
     Ticket List Population
    */

    /**********
     * <p>Method: refreshTicketList()</p>
     *
     * <p>Description: Fetches Tickets from the database based on the current filter
     * selections and repopulates the left panel Ticket list. Applies category filtering,
     * text search filtering by title, content, or author, and read status filtering
     * client side. Called on page load and after any CRUD operation.</p>
     */
    private static void refreshTicketList() {
        List<String> args = new ArrayList<>();
        String selectedCategory = combo_Category.getValue();
        String searchFilterMode = combo_SearchCriteria.getValue();
        String textFilterContent = textField_searchCriteria.textProperty().getValue();
        String readStatusFilter = combo_ReadStatus.getValue();

        if (selectedCategory != null && combo_Category.getSelectionModel().getSelectedIndex() > 0)
            args.add("category = " + selectedCategory);

        switch (searchFilterMode) {
            default:
            case "Title":
                if (textFilterContent.length() > 0)
                    args.add("UPPER(title) LIKE %" + textFilterContent.toUpperCase() + "%");
                break;
            case "Content":
                if (textFilterContent.length() > 0)
                    args.add("UPPER(content) LIKE %" + textFilterContent.toUpperCase() + "%");
                break;
            case "Author":
                if (textFilterContent.length() > 0)
                    args.add("UPPER(author) = " + textFilterContent.toUpperCase());
                break;
        }

        List<Ticket> allTickets = model.getAllTickets(args);

        // Filter by read status client side
        if (allTickets != null && !readStatusFilter.equals("All") && !readStatusFilter.equals("Read Status")) {
            boolean filterRead = readStatusFilter.equals("Read");
            allTickets = allTickets.stream()
                .filter(p -> model.isTicketRead(theUser.getUserName(), p.getPostID()) == filterRead)
                .collect(java.util.stream.Collectors.toList());
        }

        populateTicketList(allTickets);
    }

    /**********
     * <p>Method: populateTicketList(List Ticket Tickets)</p>
     *
     * <p>Description: Clears the left panel and rebuilds it using the provided
     * list of Tickets. Each Ticket becomes a clickable row. Extracted from refreshTicketList
     * so subset filter results can also use this method. Displays a "No Tickets found"
     * message if the list is null or empty.</p>
     *
     * @param Tickets the list of Ticket objects to display
     */
    private static void populateTicketList(List<Ticket> Tickets) {

        vbox_TicketList.getChildren().clear();

        if (Tickets == null || Tickets.isEmpty()) {
            Label empty = new Label("No Tickets found.");
            empty.setStyle("-fx-text-fill: #888; -fx-font-size: 14px;");
            vbox_TicketList.getChildren().add(empty);
            return;
        }

        for (Ticket Ticket : Tickets) {
            HBox row = createTicketRow(Ticket);
            vbox_TicketList.getChildren().add(row);
        }
    }

    /**********
     * <p>Method: createTicketRow(Ticket Ticket)</p>
     *
     * <p>Description: Builds a single clickable row for the left panel representing
     * one Ticket. Displays the Ticket author, category badge, a read or unread status pill,
     * and a truncated preview of the title and content. Clicking the row marks the Ticket
     * as read in the database, updates the pill instantly, and loads the full Ticket
     * in the right panel.</p>
     *
     * @param Ticket the Ticket object to represent as a row
     * @return the configured HBox row
     */
    private static HBox createTicketRow(Ticket Ticket) {

        HBox row = new HBox(8);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10, 14, 10, 14));
        row.setStyle(
            "-fx-background-color: #000;" +
            "-fx-background-radius: 10px;" +
            "-fx-cursor: hand;"
        );
        row.prefWidthProperty().bind(vbox_TicketList.widthProperty().subtract(24));

        VBox textCol = new VBox(4);
        HBox.setHgrow(textCol, Priority.ALWAYS);

        // Author + category badge + read status pill on one line
        HBox topLine = new HBox(8);
        topLine.setAlignment(Pos.CENTER_LEFT);
        Label authorLabel = new Label(theDatabase.getUserAsObject(Ticket.getAuthor()).getUserName());
        authorLabel.setStyle(
            "-fx-font-family: 'Montserrat SemiBold'; -fx-font-size: 13px; -fx-text-fill: #fff;"
        );
        Label categoryBadge = new Label(Ticket.getCategory());
        categoryBadge.setStyle(
            "-fx-background-color: #5865f2; -fx-text-fill: white;" +
            "-fx-font-size: 10px; -fx-padding: 2 8; -fx-background-radius: 999;"
        );

        // Read status pill — checks database for current user's read status
        boolean isRead = model.isTicketRead(theUser.getUserName(), Ticket.getPostID());
        Label readPill = new Label(isRead ? "Read" : "Unread");
        readPill.setStyle(
            "-fx-background-color: " + (isRead ? "#2ecc71" : "#e74c3c") + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 10px; -fx-padding: 2 8; -fx-background-radius: 999;"
        );

        topLine.getChildren().addAll(authorLabel, categoryBadge, readPill);

        // Title + content preview
        String titlePart = Ticket.getTitle() != null ? Ticket.getTitle() : "";
        String contentPart = Ticket.getContent() != null ? Ticket.getContent() : "";
        String combined = titlePart + " - " + contentPart;
        String preview = combined.length() > 60
            ? combined.substring(0, 60) + "..."
            : combined;
        Label previewLabel = new Label(preview);
        previewLabel.setStyle(
            "-fx-font-family: 'Montserrat SemiBold'; -fx-font-size: 13px; -fx-text-fill: #fff;"
        );
        previewLabel.setWrapText(false);

        textCol.getChildren().addAll(topLine, previewLabel);
        row.getChildren().add(textCol);

        // Highlight on hover
        row.setOnMouseEntered(_ ->
            row.setStyle(
                "-fx-background-color: #1a1a1e;" +
                "-fx-background-radius: 10px; -fx-cursor: hand;"
            )
        );
        row.setOnMouseExited(_ ->
            row.setStyle(
                "-fx-background-color: #000;" +
                "-fx-background-radius: 10px; -fx-cursor: hand;"
            )
        );

        // Click to load full Ticket, mark as read, and update pill instantly
        row.setOnMouseClicked(_ -> {
            model.markTicketAsRead(theUser.getUserName(), Ticket.getPostID());
            readPill.setText("Read");
            readPill.setStyle(
                "-fx-background-color: #2ecc71; -fx-text-fill: white;" +
                "-fx-font-size: 10px; -fx-padding: 2 8; -fx-background-radius: 999;"
            );
            Ticket freshTicket = model.getTicketByID(Ticket.getPostID());
            if (freshTicket == null) {
                theSelectedTicket = null;
                loadTicketDetail(null);
            } else {
                theSelectedTicket = freshTicket;
                loadTicketDetail(freshTicket);
            }
        });

        return row;
    }

    /*-*******************************************************************************************
     Ticket Detail Panel
    */

    /**********
     * <p>Method: loadTicketDetail(Ticket Ticket)</p>
     *
     * <p>Description: Populates the right panel with the full content of the selected
     * Ticket and all of its replies. Renders a Reply button for all users and Edit and
     * Delete buttons only if the logged-in user is the author of the Ticket. If the Ticket
     * is null, a deleted message is shown instead. Includes a role filter combo box
     * to filter replies by author role.</p>
     *
     * @param Ticket the Ticket to display in full detail, or null if the Ticket was deleted
     */
    private static void loadTicketDetail(Ticket Ticket) {
        vbox_TicketDetail.getChildren().clear();
        
        // Handle deleted Ticket
        if (Ticket == null) {
            Label deletedLabel = new Label("This Ticket has been deleted.");
            deletedLabel.setStyle("-fx-text-fill: #888; -fx-font-size: 16px;");
            vbox_TicketDetail.getChildren().add(deletedLabel);
            return;
        }

        // Ticket header
        Label authorLabel = new Label(theDatabase.getUserAsObject(Ticket.getAuthor()).getUserName());
        authorLabel.setStyle(
            "-fx-font-family: 'Montserrat SemiBold'; -fx-font-size: 18px; -fx-text-fill: #fff;"
        );

        Label categoryBadge = new Label(Ticket.getCategory());
        categoryBadge.setStyle(
            "-fx-background-color: #5865f2; -fx-text-fill: white;" +
            "-fx-font-size: 11px; -fx-padding: 3 10; -fx-background-radius: 999;"
        );

        HBox headerLine = new HBox(10, authorLabel, categoryBadge);
        headerLine.setAlignment(Pos.CENTER_LEFT);

        // Full Ticket content
        Label contentLabel = new Label(Ticket.getContent());
        contentLabel.setWrapText(true);
        contentLabel.setStyle(
            "-fx-font-family: 'Montserrat'; -fx-font-size: 15px;" +
            "-fx-text-fill: #ddd; -fx-padding: 10 0 0 0;"
        );

        // Action buttons (Edit/Delete only for the Ticket author)
        HBox actionBar = new HBox(10);
        actionBar.setPadding(new Insets(12, 0, 12, 0));
        actionBar.setAlignment(Pos.CENTER_LEFT);

        Button replyBtn = new Button("Reply");
        replyBtn.setStyle(
            "-fx-background-color: #5865f2; -fx-text-fill: white;" +
            "-fx-font-size: 13px; -fx-background-radius: 5px;"
        );
        replyBtn.setOnAction(_ -> showReplyDialog(Ticket));
        actionBar.getChildren().add(replyBtn);

        if (Ticket.getAuthor() == theUser.getUserId()) {
            Button editBtn = new Button("Edit");
            editBtn.setStyle(
                "-fx-background-color: #2d2d2d; -fx-text-fill: white;" +
                "-fx-font-size: 13px; -fx-background-radius: 5px;"
            );
            editBtn.setOnAction(_ -> showEditTicketDialog(Ticket));

            Button deleteBtn = new Button("Delete");
            deleteBtn.setStyle(
                "-fx-background-color: #dc3545; -fx-text-fill: white;" +
                "-fx-font-size: 13px; -fx-background-radius: 5px;"
            );
            deleteBtn.setOnAction(_ -> handleDeleteTicket(Ticket));

            actionBar.getChildren().addAll(editBtn, deleteBtn);
        }

        // Divider
        Separator divider = new Separator();
        divider.setStyle("-fx-background-color: #333;");

        // Replies section header + role filter combo on same line
        Label repliesHeader = new Label("Replies");
        repliesHeader.setStyle(
            "-fx-font-family: 'Montserrat SemiBold'; -fx-font-size: 15px; -fx-text-fill: #aaa;"
        );

        ComboBox<String> roleFilterCombo = new ComboBox<>();
        roleFilterCombo.setItems(FXCollections.observableArrayList(
            "All", "Admin", "Staff", "Student"
        ));
        roleFilterCombo.setValue("All");
        roleFilterCombo.getStyleClass().add("default-combo-box");
        roleFilterCombo.setPrefWidth(110);

       

        HBox repliesHeaderRow = new HBox(10, repliesHeader, roleFilterCombo);
        repliesHeaderRow.setAlignment(Pos.CENTER_LEFT);

        vbox_TicketDetail.getChildren().addAll(
            headerLine, contentLabel, actionBar, divider, repliesHeaderRow
        );

        // Helper VBox to hold reply cards so we can refresh just replies on filter change
        VBox repliesContainer = new VBox(8);
        vbox_TicketDetail.getChildren().add(repliesContainer);

        // Method reference to load replies into the container based on selected role
        Runnable loadReplies = () -> {
            repliesContainer.getChildren().clear();
            String selectedRole = roleFilterCombo.getValue();

            List<Reply> replies;
            if (selectedRole == null || selectedRole.equals("All")) {
                replies = model.getRepliesByTicket(Ticket.getPostID());
            } else {
                replies = model.getRepliesByTicketAndRole(Ticket.getPostID(), selectedRole);
            }

            if (replies == null || replies.isEmpty()) {
                Label noReplies = new Label(
                    selectedRole.equals("All") ? "No replies yet. Be the first!" :
                    "No replies from " + selectedRole + " users."
                );
                noReplies.setStyle("-fx-text-fill: #666; -fx-font-size: 13px;");
                repliesContainer.getChildren().add(noReplies);
            } else {
                for (Reply reply : replies) {
                    repliesContainer.getChildren().add(createReplyCard(reply));
                }
            }
        };

        // Load all replies initially
        loadReplies.run();

        // Re-run on filter change
        roleFilterCombo.setOnAction(_ -> loadReplies.run());
    }

    /**********
     * <p>Method: createReplyCard(Reply reply)</p>
     *
     * <p>Description: Builds a styled card representing a single reply, including
     * the author name, their role badge colored by role type, and the reply content.</p>
     *
     * @param reply the Reply object to render
     * @return a configured VBox card
     */
    private static VBox createReplyCard(Reply reply) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(12));
        card.setStyle(
            "-fx-background-color: #000;" +
            "-fx-background-radius: 10px;"
        );

        HBox topLine = new HBox(8);
        topLine.setAlignment(Pos.CENTER_LEFT);

        Label authorLabel = new Label(theDatabase.getUserAsObject(reply.getAuthor()).getUserName());
        authorLabel.setStyle(
            "-fx-font-family: 'Montserrat SemiBold'; -fx-font-size: 13px; -fx-text-fill: #fff;"
        );

        Label roleBadge = new Label(theDatabase.getUserAsObject(reply.getAuthor()).getRoleString());
        String badgeColor = switch (theDatabase.getUserAsObject(reply.getAuthor()).getRoleString()) {
            case "Admin" -> "#e74c3c";
            case "Staff" -> "#f39c12";
            default -> "#2ecc71";
        };
        roleBadge.setStyle(
            "-fx-background-color: " + badgeColor + "; -fx-text-fill: white;" +
            "-fx-font-size: 10px; -fx-padding: 2 8; -fx-background-radius: 999;"
        );

        topLine.getChildren().addAll(authorLabel, roleBadge);

        Label contentLabel = new Label(reply.getContent());
        contentLabel.setWrapText(true);
        contentLabel.setStyle(
            "-fx-font-family: 'Montserrat'; -fx-font-size: 13px; -fx-text-fill: #ccc;"
        );

        card.getChildren().addAll(topLine, contentLabel);
        return card;
    }

    /*-*******************************************************************************************
     Dialogs
    */

    /**********
     * <p>Method: showNewTicketDialog()</p>
     *
     * <p>Description: Opens a dialog allowing the user to create a new Ticket.
     * Collects category, title, and content input, validates using Ticket.checkValidation(),
     * and inserts the Ticket into the database via the model if valid.</p>
     */
    private static void showNewTicketDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("New Ticket");
        dialog.setHeaderText("Create a new Ticket");

        ButtonType submitType = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(submitType, ButtonType.CANCEL);
        
        ComboBox<String> categoryCombo = new ComboBox<>();
        
        categoryCombo.setItems(threadCategories);
        categoryCombo.setValue("General");
        
        TextArea titleArea = new TextArea();
        titleArea.setPromptText("Write your title here...");
        titleArea.setWrapText(true);
        titleArea.setPrefHeight(50);

        TextArea contentArea = new TextArea();
        contentArea.setPromptText("Write your Ticket here...");
        contentArea.setWrapText(true);
        contentArea.setPrefHeight(150);

   

        VBox content = new VBox(10,
            new Label("Category:"), categoryCombo,
            new Label("Title:"),    titleArea,
            new Label("Content:"),  contentArea
        );
        content.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(content);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == submitType) {
            Ticket newTicket = new Ticket(
                theUser.getUserId(),
                titleArea.getText(),
                contentArea.getText()
            );
            String error = newTicket.checkValidation();
            if (!error.isEmpty()) {
                alertValidation.setContentText(error);
                alertValidation.showAndWait();
            } else {
            	model.addTicket(newTicket);
                refreshTicketList();
            }
        }
    }

    /**********
     * <p>Method: showEditTicketDialog(Ticket Ticket)</p>
     *
     * <p>Description: Opens a dialog pre-filled with the selected Ticket's current title,
     * content, and category. On submit, validates the updated values using
     * Ticket.checkValidation() and updates the Ticket in the database via the model if valid.
     * Refreshes the Ticket list and reloads the Ticket detail panel on success.</p>
     *
     * @param Ticket the Ticket to be edited
     */
    private static void showEditTicketDialog(Ticket Ticket) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Ticket");
        dialog.setHeaderText("Edit your Ticket");

        ButtonType saveType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveType, ButtonType.CANCEL);
        
        ComboBox<String> categoryCombo = new ComboBox<>();
        categoryCombo.setItems(threadCategories);
        categoryCombo.setValue(Ticket.getCategory());
        
        TextArea titleArea = new TextArea(Ticket.getTitle());
        titleArea.setWrapText(true);
        titleArea.setPrefHeight(50);

        TextArea contentArea = new TextArea(Ticket.getContent());
        contentArea.setWrapText(true);
        contentArea.setPrefHeight(150);


        VBox content = new VBox(10,
            new Label("Category:"), categoryCombo,
            new Label("Title:"),    titleArea,
            new Label("Content:"),  contentArea
        );
        content.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(content);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == saveType) {
        	Ticket.setTitle(titleArea.getText());
            Ticket.setContent(contentArea.getText());
            Ticket.setCategory(categoryCombo.getValue());
            String error = Ticket.checkValidation();
            if (!error.isEmpty()) {
                alertValidation.setContentText(error);
                alertValidation.showAndWait();
            } else {
            	model.updateTicket(Ticket);
                refreshTicketList();
                loadTicketDetail(Ticket);
            }
        }
    }

    /**********
     * <p>Method: showReplyDialog(Ticket Ticket)</p>
     *
     * <p>Description: Opens a dialog for the user to write a reply to the selected Ticket.
     * Collects reply content, validates using Reply.checkValidation(), and inserts the
     * reply via the model if valid. After a successful reply, marks the Ticket as unread
     * for all other users so they are notified of new activity.</p>
     *
     * @param Ticket the Ticket being replied to
     */
    private static void showReplyDialog(Ticket Ticket) {

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Reply");
        dialog.setHeaderText("Reply to " + Ticket.getAuthor() + "'s Ticket");

        ButtonType submitType = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(submitType, ButtonType.CANCEL);

        TextArea replyArea = new TextArea();
        replyArea.setPromptText("Write your reply here...");
        replyArea.setWrapText(true);
        replyArea.setPrefHeight(120);

        VBox content = new VBox(10, new Label("Reply:"), replyArea);
        content.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(content);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == submitType) {
            // Determine the author's role
            Reply newReply = new Reply(
                Ticket.getPostID(),
                theUser.getUserId(),
                replyArea.getText()
            );
            String error = newReply.checkValidation();
            if (!error.isEmpty()) {
                alertValidation.setContentText(error);
                alertValidation.showAndWait();
            } else {
                model.addReply(newReply, Ticket.getPostID());
                model.markTicketAsUnread(Ticket.getPostID(), theUser.getUserName()); // add this
                loadTicketDetail(Ticket);
            }
        }
    }

    /**********
     * <p>Method: handleDeleteTicket(Ticket Ticket)</p>
     *
     * <p>Description: Shows a confirmation dialog before soft deleting the selected Ticket.
     * If confirmed, calls softDeleteTicket() which overwrites the Ticket content with a
     * deleted placeholder instead of removing the row, allowing replies to remain visible.
     * Clears the right panel and refreshes the Ticket list on confirmation.</p>
     *
     * @param Ticket the Ticket to be soft deleted
     */
    private static void handleDeleteTicket(Ticket Ticket) {
        Optional<ButtonType> result = alertDeleteConfirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
        	model.softDeleteTicket(Ticket.getPostID());
            theSelectedTicket = null;
            vbox_TicketDetail.getChildren().clear();
            refreshTicketList();
        }
    }
    
    /**********
     * <p>Method: showNewThreadDialog()</p>
     *
     * <p>Description: Opens a text input dialog allowing a staff user to create a new
     * thread category. On submit, adds the new category to the shared threadCategories
     * list and to the category filter combo box so it is immediately available for
     * filtering and Ticket creation. Only accessible by staff users.</p>
     */
    private static void showNewThreadDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Thread");
        dialog.setHeaderText("Create a new thread category");
        dialog.setContentText("Thread name:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(threadName -> {
            if (threadName.trim().isEmpty()) {
                alertValidation.setContentText("Thread name cannot be empty.");
                alertValidation.showAndWait();
            } else {
                threadCategories.add(threadName.trim());
                combo_Category.getItems().add(threadName.trim());
            }
        });
    }

    /*-*******************************************************************************************
     Helper UI setup methods
    */

    private static void setupLabelUI(Label l, String ff, double f, double w,
                                     Pos p, double x, double y) {
        l.setFont(Font.font(ff, f));
        l.setMinWidth(w);
        l.setAlignment(p);
        l.setLayoutX(x);
        l.setLayoutY(y);
    }

    protected static void setupButtonUI(Button b, String ff, double f, double w,
                                        Pos p, double x, double y) {
        b.setFont(Font.font(ff, f));
        b.setMinWidth(w);
        b.setAlignment(p);
        b.setLayoutX(x);
        b.setLayoutY(y);
    }
    
    /*****
	 * 
	 * @param l		The Textfield object
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The width
	 * @param p		The alignment (e.g. left, centered, or right)
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
	private static void setupTextFieldUI(TextField tf, String ff, double f, double w, Pos p, 
			double x, double y){
		tf.setFont(Font.font(ff, f));
		tf.setMinWidth(w);
		tf.setAlignment(p);
		tf.setLayoutX(x);
		tf.setLayoutY(y);		
	}
}
