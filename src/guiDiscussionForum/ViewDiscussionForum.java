package guiDiscussionForum;

import entityClasses.Constraint;
import entityClasses.Post;
import entityClasses.Reply;
import entityClasses.User;
import entityClasses.Constraint.ConstraintType;
import entityClasses.EvaluationTool;
import entityClasses.StaffFeedbackValidator;
import entityClasses.Ticket;
import guiTicketForum.ModelTicketForum;
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
import javafx.stage.Stage;
import database.Database;
import javafx.collections.ObservableList;
import dao.AssessmentParameterDAO;
import CRUDAssessment.AssessmentParameter;
import service.AssessmentParameterService;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

/*******
 * <p>Title: ViewDiscussionForum Class.</p>
 *
 * <p>Description: The JavaFX-based discussion forum page. Displays a scrollable
 * list of posts on the left (1/3 width) and the selected post's full content
 * and replies on the right (2/3 width). Supports creating, editing, and deleting
 * posts as well as adding replies.</p>
 */
public class ViewDiscussionForum {

    /*-*******************************************************************************************
     Attributes
    */

    private static double width  = applicationMain.FoundationsMain.WINDOW_WIDTH  * 1.75;
    private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT * 1.75;

    // GUI Area 1 — Top bar
    protected static Label label_PageTitle = new Label("Discussion Forum");
    protected static Label label_UserDetails = new Label();
    protected static Button button_NewPost = new Button("+ New Post");
    protected static Button button_NewThread = new Button("+ New Thread");
    protected static Button button_PopulateDatabase = new Button("Populate Database");
    protected static Button button_UpdateThisUser = new Button("Account Update");
    protected static ComboBox<String> combo_Category = new ComboBox<>();
    protected static ComboBox<String> combo_SearchCriteria = new ComboBox<>();
    protected static ComboBox<String> combo_StudentFilter = new ComboBox<>();
    protected static ComboBox<String> combo_hiddenFilter = new ComboBox<>();
    protected static TextField textField_searchCriteria = new TextField();
    protected static Label label_searchText = new Label();
    protected static ObservableList<String> threadCategories = FXCollections.observableArrayList(
    	    "General", "Homework", "Lectures", "Assignments", "Exams", "Hidden"
    	);
    protected static ComboBox<String> combo_ReadStatus = new ComboBox<>();

    protected static Line line_Separator1 = new Line();

    // GUI Area 2 — Left post list
    protected static ScrollPane scrollPane_PostList;
    protected static VBox vbox_PostList;
    private static final StaffFeedbackValidator feedbackValidator = new StaffFeedbackValidator();
    private static ModelTicketForum ticketModel = new ModelTicketForum();

    // GUI Area 2 — Right post detail panel
    protected static ScrollPane scrollPane_PostDetail;
    protected static VBox vbox_PostDetail;



    // GUI Area 3 — Bottom bar
    protected static Button button_Return = new Button("Return");
    protected static Button button_Logout = new Button("Logout");
    protected static Button button_Quit   = new Button("Quit");

    // Internal state
    private static ViewDiscussionForum theView;
    private static Database theDatabase = applicationMain.FoundationsMain.database;
    private static ModelDiscussionForum model = new ModelDiscussionForum();

    protected static Stage theStage;
    protected static Pane theRootPane;
    protected static User theUser;
    protected static Post theSelectedPost = null;
    protected static String returnPage = "";
    private static final String[] GRADER_PARAMS = {"Accuracy", "Spelling", "Grammar", "Length"};
    private static final Double[] GRADER_WEIGHTS = {40.0, 20.0, 20.0, 20.0};

    // The scene used for the discussion forum. This can get away with being protected...
    protected static Scene theDiscussionForumScene = null;

    // Alerts
    protected static Alert alertDeleteConfirm = new Alert(Alert.AlertType.CONFIRMATION);
    protected static Alert alertValidation = new Alert(Alert.AlertType.WARNING);
 	protected static Alert alertPopulateDatabase = new Alert(AlertType.CONFIRMATION);
    protected static Alert alertHideConfirm = new Alert(Alert.AlertType.CONFIRMATION);


    /*-*******************************************************************************************
     Entry Point
    */

    /**********
     * <p>Method: displayDiscussionForum(Stage ps, User user)</p>
     *
     * <p>Description: Single entry point to show the Discussion Forum page.
     * Sets shared references, instantiates the singleton if needed, refreshes
     * the post list, and displays the scene.</p>
     *
     * @param ps   the JavaFX Stage to use
     * @param user the currently logged-in User
     * @param returnPageStr the page to return to
     */
    public static void displayDiscussionForum(Stage ps, User user, String returnPageStr) {
        theStage = ps;
        theUser  = user;
        returnPage = returnPageStr;

        if (theView == null) theView = new ViewDiscussionForum();
        
        // Only show if user is staff
        if (returnPage.equals("Staff")) {
        	  button_NewThread.setVisible(true);
              button_NewThread.setManaged(true);
        } else {
        	  button_NewThread.setVisible(false);
              button_NewThread.setManaged(false);
        }
        
        // Only show if user is not a student
        if (!returnPage.equals("Staff")) {
        	combo_StudentFilter.setVisible(false);
        	combo_StudentFilter.setManaged(false);
        } else {
        	combo_StudentFilter.setVisible(true);
        	combo_StudentFilter.setManaged(true);
        }
        
        combo_ReadStatus.setValue("Read Status");
        refreshStudentFilter();
        vbox_PostDetail.getChildren().clear(); 
      

        label_UserDetails.setText("User: " + theUser.getUserName());
        refreshPostList();

        theStage.setTitle("Discussion Forum");
        theStage.setScene(theDiscussionForumScene);
        theStage.show();
    }

    /*-*******************************************************************************************
     Constructor — builds all static GUI elements once
    */

    /**********
     * <p>Constructor: ViewDiscussionForum()</p>
     *
     * <p>Description: Initializes all GUI widgets, sets their layout, fonts, sizes,
     * and event handlers. This is a singleton so it runs only once.</p>
     */
    public ViewDiscussionForum() {

        theRootPane = new Pane();
        theDiscussionForumScene = new Scene(theRootPane, width, height);

        theDiscussionForumScene.getStylesheets().add(
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

        setupButtonUI(button_NewPost, "Dialog", 16, 100, Pos.BASELINE_LEFT, width/2 - 475, 55);
        button_NewPost.setOnAction(_ -> showNewPostDialog());
        
        setupButtonUI(button_NewThread, "Dialog", 16, 100, Pos.BASELINE_LEFT, width/2 - 475, 20);
        button_NewThread.setOnAction(_ -> showNewThreadDialog());
        

        
        setupButtonUI(button_PopulateDatabase, "Dialog", 16, 100, Pos.BASELINE_LEFT, width/2 - 75, 55);
        button_PopulateDatabase.setOnAction((_) -> { 
        
        	
        	// Show the alert and wait for user response
		    Optional<ButtonType> result = alertPopulateDatabase.showAndWait();
		    if (result.isPresent() && result.get() == ButtonType.OK) {
		        // User pressed OK, proceed
		        theDatabase.populateDatabaseWithTestPosts();
		        refreshPostList();
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
            refreshPostList();
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
    	combo_ReadStatus.setOnAction(_ -> refreshPostList());
    	
    	refreshStudentFilter();
    	combo_StudentFilter.getStyleClass().add("default-combo-box");
    	combo_StudentFilter.setLayoutX(width/2 + 372);
    	combo_StudentFilter.setLayoutY(18);
    	combo_StudentFilter.setPrefWidth(130);
    	combo_StudentFilter.setPrefHeight(16);

    	combo_StudentFilter.setOnAction(_ -> refreshPostList());
        
        
        // Andrew C -- Data for the text search bar
        setupLabelUI(label_searchText, "Arial", 18, 64, Pos.BASELINE_LEFT, 355, 60);
        label_searchText.setText("Search For:");
        label_searchText.setStyle(
        		"-fx-font-size: 14px;"
        );
        
        setupTextFieldUI(textField_searchCriteria, "Dialog", 18, 600, Pos.BASELINE_LEFT, 460, 52);
        textField_searchCriteria.textProperty().addListener(_ -> {
        	refreshPostList();
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
            refreshPostList();
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

        // ── GUI Area 2 — Left Post List 

        vbox_PostList = new VBox(8);
        vbox_PostList.setPadding(new Insets(12));
        vbox_PostList.setStyle("-fx-background-color: transparent;");
        vbox_PostList.setFillWidth(true);

        scrollPane_PostList = new ScrollPane(vbox_PostList);
        scrollPane_PostList.setFitToWidth(true);
        scrollPane_PostList.setFitToHeight(false);
        scrollPane_PostList.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane_PostList.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane_PostList.setStyle(
            "-fx-background-color: #000;" +
            "-fx-background-radius: 15px;" +
            "-fx-border-radius: 15px;"
        );
        scrollPane_PostList.viewportBoundsProperty().addListener((_) -> {
            scrollPane_PostList.lookup(".viewport").setStyle(
                "-fx-background-color: transparent;" +
                "-fx-background-radius: 15px;"
            );
        });
        scrollPane_PostList.setLayoutX(leftX);
        scrollPane_PostList.setLayoutY(contentY);
        scrollPane_PostList.setPrefWidth(leftWidth);
        scrollPane_PostList.setPrefHeight(contentHeight);
        scrollPane_PostList.getStyleClass().add("custom-scroll");

        // ── GUI Area 2 — Right Post Detail 

        vbox_PostDetail = new VBox(12);
        vbox_PostDetail.setPadding(new Insets(20));
        vbox_PostDetail.setStyle("-fx-background-color: transparent;");
        vbox_PostDetail.setFillWidth(true);

        scrollPane_PostDetail = new ScrollPane(vbox_PostDetail);
        scrollPane_PostDetail.setFitToWidth(true);
        scrollPane_PostDetail.setFitToHeight(false);
        scrollPane_PostDetail.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane_PostDetail.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane_PostDetail.setStyle(
            "-fx-background-color: #1a1a1e;" +
            "-fx-background-radius: 15px;" +
            "-fx-border-radius: 15px;"
        );
        scrollPane_PostDetail.viewportBoundsProperty().addListener((_) -> {
            scrollPane_PostDetail.lookup(".viewport").setStyle(
                "-fx-background-color: transparent;" +
                "-fx-background-radius: 15px;"
            );
        });
        scrollPane_PostDetail.setLayoutX(rightX);
        scrollPane_PostDetail.setLayoutY(contentY);
        scrollPane_PostDetail.setPrefWidth(rightWidth);
        scrollPane_PostDetail.setPrefHeight(contentHeight);
        scrollPane_PostDetail.getStyleClass().add("custom-scroll");

        // ── GUI Area 3 — Bottom Bar ───────────────────────────────────

        Line line_Separator4 = new Line(20, botBarY - 10, width - 20, botBarY - 10);
        line_Separator4.setStyle("-fx-stroke: #555;");

        setupButtonUI(button_Return, "Dialog", 18, 210, Pos.CENTER, 20, botBarY);
        button_Return.setOnAction(_ -> ControllerDiscussionForum.performReturn());

        setupButtonUI(button_Logout, "Dialog", 18, 210, Pos.CENTER,
                      (width / 2) - 105, botBarY);
        button_Logout.setOnAction(_ -> ControllerDiscussionForum.performLogout());

        setupButtonUI(button_Quit, "Dialog", 18, 210, Pos.CENTER,
                      width - 230, botBarY);
        button_Quit.setOnAction(_ -> ControllerDiscussionForum.performQuit());

        // Alerts
        alertDeleteConfirm.setTitle("Delete Post");
        alertDeleteConfirm.setHeaderText("Are you sure you want to delete this post?");
        alertDeleteConfirm.setContentText("This action cannot be undone.");

        alertValidation.setTitle("Validation Error");
        alertValidation.setHeaderText("Please fix the following:");
        
        alertPopulateDatabase.setTitle("Confirm Database Population");
		alertPopulateDatabase.setHeaderText("This action will reset the database post\nand reply tables with default values");
		alertPopulateDatabase.setContentText("Press OK to continue or Cancel to abort.");

        // ── Add all to pane ───────────────────────────────────────────

        theRootPane.getChildren().addAll(
            label_PageTitle, label_UserDetails,
            button_NewPost, combo_Category, button_UpdateThisUser,
            button_PopulateDatabase,
            line_Separator1,
            scrollPane_PostList,
            scrollPane_PostDetail,
            button_NewThread,
            combo_ReadStatus,
            combo_StudentFilter,
            line_Separator4,
            button_Return, button_Logout, button_Quit,
            textField_searchCriteria, combo_SearchCriteria, label_searchText
        );
    }

    /*-*******************************************************************************************
     Post List Population
    */

    /**********
     * <p>Method: refreshPostList()</p>
     *
     * <p>Description: Fetches posts from the database based on the current filter
     * selections and repopulates the left panel post list. Applies category filtering,
     * text search filtering by title, content, or author, and read status filtering
     * client side. Called on page load and after any CRUD operation.</p>
     */
    private static void refreshPostList() {
        List<Constraint> args = new ArrayList<>();
        
        // Student filter overrides all other filters
        String selectedStudent = combo_StudentFilter.getValue();
        if (selectedStudent != null && !selectedStudent.equals("All Students")) {

            if (selectedStudent.equals("Flagged Posts")) {
                List<Post> allPosts = model.getAllPosts(null);
                if (allPosts != null) {
                    allPosts = allPosts.stream()
                        .filter(p -> feedbackValidator.containsInappropriateContent(p.getTitle()) ||
                                     feedbackValidator.containsInappropriateContent(p.getContent()))
                        .collect(java.util.stream.Collectors.toList());
                }
                populatePostList(allPosts);
                return;
            }

            // existing student filter logic
            List<Post> allPosts = model.getAllPosts(null);
            if (allPosts != null) {
                allPosts = allPosts.stream()
                    .filter(p -> {
                        User u = theDatabase.getUserAsObject(p.getAuthor());
                        return u != null && u.getUserName().equals(selectedStudent);
                    })
                    .collect(java.util.stream.Collectors.toList());
            }
            populatePostList(allPosts);
            return;
        }
        
        
        String selectedCategory = combo_Category.getValue();
        String searchFilterMode = combo_SearchCriteria.getValue();
        String textFilterContent = textField_searchCriteria.textProperty().getValue();
        String readStatusFilter = combo_ReadStatus.getValue();

        if (selectedCategory != null && combo_Category.getSelectionModel().getSelectedIndex() > 0) {
        	
        	if (selectedCategory.equals("Hidden")) {
        		args.add(new Constraint("isPostHidden = TRUE", ConstraintType.AND));
        	} else {
        		args.add(new Constraint("category = " + selectedCategory, ConstraintType.AND));
        	}
        }	
                
        switch (searchFilterMode) {
            default:
            case "Title":
                if (textFilterContent.length() > 0)
                    args.add(new Constraint("UPPER(title) LIKE %" + textFilterContent.toUpperCase() + "%", ConstraintType.AND));
                break;
            case "Content":
                if (textFilterContent.length() > 0)
                    args.add(new Constraint("UPPER(content) LIKE %" + textFilterContent.toUpperCase() + "%", ConstraintType.AND));
                break;
            //This is a very special case
            case "Author":
                if (textFilterContent.length() > 0)
                {
                	List<Integer> authorList = model.getAuthorList(textFilterContent.toUpperCase());
                	if(authorList.size() > 0) {
	                	for(int i : authorList) {
	                		args.add(new Constraint("author = " + i, ConstraintType.OR));
	                	}
                	}
                	else {
                		args.add(new Constraint("author = 0", ConstraintType.OR));
                	}	
                }
                break;
        }

        List<Post> allPosts = model.getAllPosts(args);

        // Filter by read status client side
        if (allPosts != null && !readStatusFilter.equals("All") && !readStatusFilter.equals("Read Status")) {
            boolean filterRead = readStatusFilter.equals("Read");
            allPosts = allPosts.stream()
                .filter(p -> model.isPostRead(theUser.getUserName(), p.getPostID()) == filterRead)
                .collect(java.util.stream.Collectors.toList());
        }

        populatePostList(allPosts);
    }

    /**********
     * <p>Method: populatePostList(List Post posts)</p>
     *
     * <p>Description: Clears the left panel and rebuilds it using the provided
     * list of posts. Each post becomes a clickable row. Extracted from refreshPostList
     * so subset filter results can also use this method. Displays a "No posts found"
     * message if the list is null or empty.</p>
     *
     * @param posts the list of Post objects to display
     */
    private static void populatePostList(List<Post> posts) {

        vbox_PostList.getChildren().clear();

        if (posts == null || posts.isEmpty()) {
            Label empty = new Label("No posts found.");
            empty.setStyle("-fx-text-fill: #888; -fx-font-size: 14px;");
            vbox_PostList.getChildren().add(empty);
            return;
        }

        for (Post post : posts) {
        	if (!post.getPostHiddenStatus() && isStudentUser()) {
	            HBox row = createPostRow(post);
	            vbox_PostList.getChildren().add(row);
        	} else if (isStaffUser() || isAdminUser()){
	            HBox row = createPostRow(post);
	            vbox_PostList.getChildren().add(row);
        	}
        }
    }

    /**********
     * <p>Method: createPostRow(Post post)</p>
     *
     * <p>Description: Builds a single clickable row for the left panel representing
     * one post. Displays the post author, category badge, a read or unread status pill,
     * and a truncated preview of the title and content. Clicking the row marks the post
     * as read in the database, updates the pill instantly, and loads the full post
     * in the right panel.</p>
     *
     * @param post the Post object to represent as a row
     * @return the configured HBox row
     */
    private static HBox createPostRow(Post post) {

        HBox row = new HBox(8);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10, 14, 10, 14));
        row.setStyle(
            "-fx-background-color: #000;" +
            "-fx-background-radius: 10px;" +
            "-fx-cursor: hand;"
        );
        row.prefWidthProperty().bind(vbox_PostList.widthProperty().subtract(24));

        VBox textCol = new VBox(4);
        HBox.setHgrow(textCol, Priority.ALWAYS);

        // Author + category badge + read status pill on one line
        HBox topLine = new HBox(8);
        topLine.setAlignment(Pos.CENTER_LEFT);
        Label authorLabel = new Label(post.getAuthor() == -1 ? "[Deleted]" : 
        	theDatabase.getUserAsObject(post.getAuthor()).getUserName());
        authorLabel.setStyle(
            "-fx-font-family: 'Montserrat SemiBold'; -fx-font-size: 13px; -fx-text-fill: #fff;"
        );
        Label categoryBadge = new Label(post.getCategory());
        categoryBadge.setStyle(
            "-fx-background-color: #5865f2; -fx-text-fill: white;" +
            "-fx-font-size: 10px; -fx-padding: 2 8; -fx-background-radius: 999;"
        );

        // Read status pill — checks database for current user's read status
        boolean isRead = model.isPostRead(theUser.getUserName(), post.getPostID());
        Label readPill = new Label(isRead ? "Read" : "Unread");
        readPill.setStyle(
            "-fx-background-color: " + (isRead ? "#2ecc71" : "#e74c3c") + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 10px; -fx-padding: 2 8; -fx-background-radius: 999;"
        );

        topLine.getChildren().addAll(authorLabel, categoryBadge, readPill);

        if (isStaffUser()) {
            if (feedbackValidator.containsInappropriateContent(post.getTitle()) ||
                feedbackValidator.containsInappropriateContent(post.getContent())) {
                Label flagBadge = new Label("FLAG");
                flagBadge.setStyle(
                    "-fx-background-color: #f1c40f; -fx-text-fill: black;" +
                    "-fx-font-size: 10px; -fx-padding: 2 8; -fx-background-radius: 999;"
                );
                topLine.getChildren().add(flagBadge);
            }
        }
        
        
        
        if (isStaffUser() && isStudentPost(post)) {            
        	Label gradePill;

    		 if (post.isGraded()) {
                 String gradeText = post.getLetterGrade() + " (" + post.getNumberGrade() + "%)";
                 gradePill = new Label(gradeText);
                 gradePill.setStyle(
                     "-fx-background-color: #3498db; -fx-text-fill: white;" +
                     "-fx-font-size: 10px; -fx-padding: 2 8; -fx-background-radius: 999;"
                 );
             } else {
                 gradePill = new Label("Not Graded");
                 gradePill.setStyle(
                     "-fx-background-color: #e67e22; -fx-text-fill: white;" +
                     "-fx-font-size: 10px; -fx-padding: 2 8; -fx-background-radius: 999;"
                 );
             }
             topLine.getChildren().add(gradePill);
        	 
            Label hiddenPill;
            if(post.getPostHiddenStatus()) {
            	hiddenPill = new Label("Hidden");
                hiddenPill.setStyle(
                        "-fx-background-color: #007bff; -fx-text-fill: white;" +
                        "-fx-font-size: 10px; -fx-padding: 2 8; -fx-background-radius: 999;"
                    );
	            topLine.getChildren().add(hiddenPill);
            }
        }

        // Title + content preview
        String titlePart = post.getTitle() != null ? post.getTitle() : "";
        String contentPart = post.getContent() != null ? post.getContent() : "";
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

        // Click to load full post, mark as read, and update pill instantly
        row.setOnMouseClicked(_ -> {
            model.markPostAsRead(theUser.getUserName(), post.getPostID());
            readPill.setText("Read");
            readPill.setStyle(
                "-fx-background-color: #2ecc71; -fx-text-fill: white;" +
                "-fx-font-size: 10px; -fx-padding: 2 8; -fx-background-radius: 999;"
            );
            Post freshPost = model.getPostByID(post.getPostID());
            if (freshPost == null) {
                theSelectedPost = null;
                loadPostDetail(null);
            } else {
                theSelectedPost = freshPost;
                loadPostDetail(freshPost);
            }
        });

        return row;
    }

    /*-*******************************************************************************************
     Post Detail Panel
    */

    /**********
     * <p>Method: loadPostDetail(Post post)</p>
     *
     * <p>Description: Populates the right panel with the full content of the selected
     * post and all of its replies. Renders a Reply button for all users and Edit and
     * Delete buttons only if the logged-in user is the author of the post. Also renders a grade tool button and edit grade button
     * that only shows up if the current user is a staff role and only on post made by students.
     * If the post is null, a deleted message is shown instead. Includes a role filter combo box
     * to filter replies by author role.</p>
     *
     * @param post the Post to display in full detail, or null if the post was deleted
     */
    private static void loadPostDetail(Post post) {
        vbox_PostDetail.getChildren().clear();
        
        // Handle deleted post
        if (post == null) {
            Label deletedLabel = new Label("This post has been deleted.");
            deletedLabel.setStyle("-fx-text-fill: #888; -fx-font-size: 16px;");
            vbox_PostDetail.getChildren().add(deletedLabel);
            return;
        }

        // Post header
        Label authorLabel = new Label(post.getAuthor() == -1 ? "[Deleted]" : 
        	theDatabase.getUserAsObject(post.getAuthor()).getUserName());
        authorLabel.setStyle(
            "-fx-font-family: 'Montserrat SemiBold'; -fx-font-size: 18px; -fx-text-fill: #fff;"
        );

        Label categoryBadge = new Label(post.getCategory());
        categoryBadge.setStyle(
            "-fx-background-color: #5865f2; -fx-text-fill: white;" +
            "-fx-font-size: 11px; -fx-padding: 3 10; -fx-background-radius: 999;"
        );

        HBox headerLine = new HBox(10, authorLabel, categoryBadge);
        headerLine.setAlignment(Pos.CENTER_LEFT);

        // Full post content
        Label contentLabel = new Label(post.getContent());
        contentLabel.setWrapText(true);
        contentLabel.setStyle(
            "-fx-font-family: 'Montserrat'; -fx-font-size: 15px;" +
            "-fx-text-fill: #ddd; -fx-padding: 10 0 0 0;"
        );

        // Action buttons (Edit/Delete only for the post author)
        HBox actionBar = new HBox(10);
        actionBar.setPadding(new Insets(12, 0, 12, 0));
        actionBar.setAlignment(Pos.CENTER_LEFT);

        Button replyBtn = new Button("Reply");
        replyBtn.setStyle(
            "-fx-background-color: #5865f2; -fx-text-fill: white;" +
            "-fx-font-size: 13px; -fx-background-radius: 5px;"
        );
        replyBtn.setOnAction(_ -> showReplyDialog(post));
        actionBar.getChildren().add(replyBtn);

        if (post.getAuthor() == theUser.getUserId()        		
        		|| theUser.getRoles().contains("Staff")) {
            Button editBtn = new Button("Edit");
            editBtn.setStyle(
                "-fx-background-color: #2d2d2d; -fx-text-fill: white;" +
                "-fx-font-size: 13px; -fx-background-radius: 5px;"
            );
            editBtn.setOnAction(_ -> showEditPostDialog(post));

            Button deleteBtn = new Button("Delete");
            deleteBtn.setStyle(
                "-fx-background-color: #dc3545; -fx-text-fill: white;" +
                "-fx-font-size: 13px; -fx-background-radius: 5px;"
            );
            deleteBtn.setOnAction(_ -> handleDeletePost(post));
            
            actionBar.getChildren().addAll(editBtn, deleteBtn);
            
        }
        if (isStaffUser() && isStudentPost(post)) {
            Button emailBtn = new Button("Email");
            emailBtn.setStyle(
                "-fx-background-color: #28a745; -fx-text-fill: white;" +
                "-fx-font-size: 13px; -fx-background-radius: 5px;"
            );
            emailBtn.setOnAction(_ -> showEmailDialog(post));
            actionBar.getChildren().add(emailBtn);
         }
        
        if (isStaffUser() && isStudentPost(post)) {
        	
            Label gradeStatusLabel;
            
            if (post.isGraded()) {
                gradeStatusLabel = new Label("Graded: " + post.getLetterGrade() + " (" + post.getNumberGrade() + "%)");
                gradeStatusLabel.setStyle("-fx-text-fill: #9ad0ff; -fx-font-size: 13px;");

                Button editGradeBtn = new Button("Edit Grade");
                editGradeBtn.setStyle(
                    "-fx-background-color: #2980b9; -fx-text-fill: white;" +
                    "-fx-font-size: 13px; -fx-background-radius: 5px;"
                );
                editGradeBtn.setOnAction(_ -> showStaffEditGradeDialog(post));
                actionBar.getChildren().addAll(gradeStatusLabel, editGradeBtn);
            } else {
                gradeStatusLabel = new Label("Not graded yet");
                gradeStatusLabel.setStyle("-fx-text-fill: #ffcf8c; -fx-font-size: 13px;");
                Button launchGraderBtn = new Button("Launch Grader Tool");
                launchGraderBtn.setStyle(
                    "-fx-background-color: #8e44ad; -fx-text-fill: white;" +
                    "-fx-font-size: 13px; -fx-background-radius: 5px;"
                );
                launchGraderBtn.setOnAction(_ -> showStaffGraderDialog(post));
                actionBar.getChildren().addAll(gradeStatusLabel, launchGraderBtn);
            }
            
            Button testAssessmentParamsBtn = new Button("Test Assessment Params");
            testAssessmentParamsBtn.setStyle(
                "-fx-background-color: #16a085; -fx-text-fill: white;"
                + "-fx-font-size: 13px; -fx-background-radius: 5px;"
            );
            testAssessmentParamsBtn.setOnAction(_ -> showAssessmentParameterTestDialog());
            actionBar.getChildren().add(testAssessmentParamsBtn);
                       
            if(!model.isPostHidden(post.getPostID())) {
	            Button hideBtn = new Button("Hide");
	            hideBtn.setStyle(
	                    "-fx-background-color: #007bff; -fx-text-fill: white;" +
	                    "-fx-font-size: 13px; -fx-background-radius: 5px;"
	                );
	            hideBtn.setOnAction(_ -> handleHidePost(post));
	            actionBar.getChildren().add(hideBtn);
            } else {
	            Button unhideBtn = new Button("Unhide");
	            unhideBtn.setStyle(
	                    "-fx-background-color: #007bff; -fx-text-fill: white;" +
	                    "-fx-font-size: 13px; -fx-background-radius: 5px;"
	                );
	            unhideBtn.setOnAction(_ -> handleUnidePost(post));
	            actionBar.getChildren().add(unhideBtn);
            }
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

        vbox_PostDetail.getChildren().addAll(
            headerLine, contentLabel, actionBar, divider, repliesHeaderRow
        );

        // Helper VBox to hold reply cards so we can refresh just replies on filter change
        VBox repliesContainer = new VBox(8);
        vbox_PostDetail.getChildren().add(repliesContainer);

        // Method reference to load replies into the container based on selected role
        Runnable loadReplies = () -> {
            repliesContainer.getChildren().clear();
            String selectedRole = roleFilterCombo.getValue();

            List<Reply> replies;
            if (selectedRole == null || selectedRole.equals("All")) {
                replies = model.getRepliesByPost(post.getPostID());
            } else {
                replies = model.getRepliesByPostAndRole(post.getPostID(), selectedRole);
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
                	if (isStudentUser()) {
                		if(!model.isReplyHidden(reply.getPostID(), reply.getReplyID())) {
                			repliesContainer.getChildren().add(createReplyCard(reply));
                		}
                	} else {
                		repliesContainer.getChildren().add(createReplyCard(reply));
                	}
                }
            }
        };

        // Load all replies initially
        loadReplies.run();

        // Re-run on filter change
        roleFilterCombo.setOnAction(_ -> loadReplies.run());
    }

    private static void showAssessmentParameterTestDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Assessment Parameter Test Tool");
        dialog.setHeaderText("Temporary staff CRUD tool for assessment parameters");

        ButtonType closeType = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(closeType);

        TextField nameField = new TextField();
        nameField.setPromptText("Parameter Name");

        TextField descriptionField = new TextField();
        descriptionField.setPromptText("Description");

        TextField categoryField = new TextField();
        categoryField.setPromptText("Category");

        TextField thresholdField = new TextField();
        thresholdField.setPromptText("Threshold Value");

        TextField pointValueField = new TextField();
        pointValueField.setPromptText("Point Value");

        TextField createdByField = new TextField();
        createdByField.setPromptText("Created By");
        createdByField.setText(theUser.getUserName());

        TextField targetNameField = new TextField();
        targetNameField.setPromptText("Target Parameter Name for Update/Deactivate");

        CheckBox requiredBox = new CheckBox("Required");
        CheckBox activeBox = new CheckBox("Active");
        activeBox.setSelected(true);

        TextArea outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setWrapText(true);
        outputArea.setPrefHeight(220);

        Button createButton = new Button("Create");
        Button viewAllButton = new Button("View All");
        Button updateButton = new Button("Update By Name");
        Button deactivateButton = new Button("Deactivate By Name");

        AssessmentParameterDAO dao = new AssessmentParameterDAO();
        AssessmentParameterService service = new AssessmentParameterService(dao);

        createButton.setOnAction(_ -> {
            try {
                Integer threshold = thresholdField.getText().isBlank()
                        ? null
                        : Integer.parseInt(thresholdField.getText());

                Double pointValue = pointValueField.getText().isBlank()
                        ? null
                        : Double.parseDouble(pointValueField.getText());

                AssessmentParameter parameter = new AssessmentParameter(
                        nameField.getText(),
                        descriptionField.getText(),
                        categoryField.getText(),
                        threshold,
                        pointValue,
                        requiredBox.isSelected(),
                        activeBox.isSelected(),
                        createdByField.getText()
                );

                boolean created = service.addParameter(parameter);
                outputArea.appendText("Create result: " + created + "\n");
            } catch (Exception ex) {
                outputArea.appendText("Create error: " + ex.getMessage() + "\n");
            }
        });

        viewAllButton.setOnAction(_ -> {
            try {
                outputArea.appendText("\n=== VIEW ALL PARAMETERS ===\n");
                List<AssessmentParameter> parameters = service.listParameters();

                if (parameters.isEmpty()) {
                    outputArea.appendText("No parameters found.\n");
                } else {
                    for (AssessmentParameter p : parameters) {
                        outputArea.appendText(
                                p.getParameterId() + " | "
                                + p.getParameterName() + " | "
                                + p.getDescription() + " | "
                                + p.getCategory() + " | "
                                + p.getThresholdValue() + " | "
                                + p.getPointValue() + " | "
                                + p.isRequired() + " | "
                                + p.isActive() + " | "
                                + p.getCreatedBy() + "\n"
                        );
                    }
                }
            } catch (Exception ex) {
                outputArea.appendText("View error: " + ex.getMessage() + "\n");
            }
        });

        updateButton.setOnAction(_ -> {
            try {
                String targetName = targetNameField.getText().trim();

                if (targetName.isEmpty()) {
                    outputArea.appendText("Update error: target parameter name is required.\n");
                    return;
                }

                List<AssessmentParameter> parameters = service.listParameters();
                AssessmentParameter target = null;

                for (AssessmentParameter p : parameters) {
                    if (p.getParameterName().equalsIgnoreCase(targetName)) {
                        target = p;
                        break;
                    }
                }

                if (target == null) {
                    outputArea.appendText("No parameter found with name: " + targetName + "\n");
                    return;
                }

                target.setDescription("Updated from test dialog");
                target.setPointValue(15.0);

                boolean updated = service.editParameter(target);
                outputArea.appendText("Update result for \"" + targetName + "\": " + updated + "\n");
            } catch (Exception ex) {
                outputArea.appendText("Update error: " + ex.getMessage() + "\n");
            }
        });

        deactivateButton.setOnAction(_ -> {
            try {
                String targetName = targetNameField.getText().trim();

                if (targetName.isEmpty()) {
                    outputArea.appendText("Deactivate error: target parameter name is required.\n");
                    return;
                }

                List<AssessmentParameter> parameters = service.listParameters();
                AssessmentParameter target = null;

                for (AssessmentParameter p : parameters) {
                    if (p.getParameterName().equalsIgnoreCase(targetName)) {
                        target = p;
                        break;
                    }
                }

                if (target == null) {
                    outputArea.appendText("No parameter found with name: " + targetName + "\n");
                    return;
                }

                boolean deactivated = service.deactivateParameter(target.getParameterId());
                outputArea.appendText("Deactivate result for \"" + targetName + "\": " + deactivated + "\n");
            } catch (Exception ex) {
                outputArea.appendText("Deactivate error: " + ex.getMessage() + "\n");
            }
        });

        HBox buttonRow = new HBox(10, createButton, viewAllButton, updateButton, deactivateButton);
        buttonRow.setAlignment(Pos.CENTER_LEFT);

        VBox content = new VBox(10,
                new Label("Parameter Name"), nameField,
                new Label("Description"), descriptionField,
                new Label("Category"), categoryField,
                new Label("Threshold Value"), thresholdField,
                new Label("Point Value"), pointValueField,
                new Label("Created By"), createdByField,
                requiredBox,
                activeBox,
                new Label("Target Parameter Name"), targetNameField,
                buttonRow,
                new Label("Output"),
                outputArea
        );

        content.setPadding(new Insets(12));
        dialog.getDialogPane().setContent(content);

        dialog.showAndWait();
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

        Label authorLabel = new Label(reply.getAuthor() == -1 ? "[Deleted]" : 
        	theDatabase.getUserAsObject(reply.getAuthor()).getUserName());
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
        
        HBox actionBar = new HBox(10);
        actionBar.setPadding(new Insets(12, 0, 12, 0));
        actionBar.setAlignment(Pos.CENTER_RIGHT);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer,  Priority.ALWAYS);
        

        Button editReplyBtn = new Button("Edit");
        editReplyBtn.setStyle(
            "-fx-background-color: #2d2d2d; -fx-text-fill: white;" +
            "-fx-font-size: 13px; -fx-background-radius: 5px;"
        );
        editReplyBtn.setOnAction(_ -> showEditReplyDialog(reply));
        
        Button deleteReplyBtn = new Button("Delete");
        deleteReplyBtn.setStyle(
            "-fx-background-color: #dc3545; -fx-text-fill: white;" +
            "-fx-font-size: 13px; -fx-background-radius: 5px;"
        );
        deleteReplyBtn.setOnAction(_ -> handleDeleteReply(reply)); 
        
        
        if(isStaffUser()) {
            if(!model.isReplyHidden(reply.getPostID(), reply.getReplyID())) {
	            Button hideReplyBtn = new Button("Hide");
	            hideReplyBtn.setStyle(
	                    "-fx-background-color: #007bff; -fx-text-fill: white;" +
	                    "-fx-font-size: 13px; -fx-background-radius: 5px;"
	                );
	            hideReplyBtn.setOnAction(_ -> handleHideReply(reply));
	            actionBar.getChildren().add(hideReplyBtn);
            } else {
	            Button unhideReplyBtn = new Button("Unhide");
	            unhideReplyBtn.setStyle(
	                    "-fx-background-color: #007bff; -fx-text-fill: white;" +
	                    "-fx-font-size: 13px; -fx-background-radius: 5px;"
	                );
	            unhideReplyBtn.setOnAction(_ -> handleUnideReply(reply));
	            actionBar.getChildren().add(unhideReplyBtn);
            }                     
        }
        
        Label hiddenBadge = new Label("Hidden");
        
        hiddenBadge.setStyle(
            "-fx-background-color: #007bff; -fx-text-fill: white;" +
            "-fx-font-size: 10px; -fx-padding: 2 8; -fx-background-radius: 999;"
        );
        
        if (isStaffUser() && model.isReplyHidden(reply.getPostID(), reply.getReplyID())) {
            topLine.getChildren().addAll(authorLabel, roleBadge, hiddenBadge, spacer, editReplyBtn, deleteReplyBtn, actionBar);
        } else if (isStaffUser() || reply.getAuthor() == theUser.getUserId()){
            topLine.getChildren().addAll(authorLabel, roleBadge, spacer, editReplyBtn, deleteReplyBtn, actionBar);

        } else {
            topLine.getChildren().addAll(authorLabel, roleBadge, spacer, actionBar);
        }

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
     * <p>Method: showNewPostDialog()</p>
     *
     * <p>Description: Opens a dialog allowing the user to create a new post.
     * Collects category, title, and content input, validates using Post.checkValidation(),
     * and inserts the post into the database via the model if valid.</p>
     */
    private static void showNewPostDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("New Post");
        dialog.setHeaderText("Create a new post");

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
        contentArea.setPromptText("Write your post here...");
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
            Post newPost = new Post(
                theUser.getUserId(),
                titleArea.getText(),
                contentArea.getText(),
                categoryCombo.getValue()
            );
            String error = newPost.checkValidation();
            if (!error.isEmpty()) {
                alertValidation.setContentText(error);
                alertValidation.showAndWait();
            } else {
            	model.addPost(newPost);
            	if (feedbackValidator.containsInappropriateContent(newPost.getTitle()) ||
        	        feedbackValidator.containsInappropriateContent(newPost.getContent())) {
            		createFlaggedContentTicket(newPost);
        	    }
            	refreshStudentFilter();
                refreshPostList();
            }
        }
    }

    /**********
     * <p>Method: showEditPostDialog(Post post)</p>
     *
     * <p>Description: Opens a dialog pre-filled with the selected post's current title,
     * content, and category. On submit, validates the updated values using
     * Post.checkValidation() and updates the post in the database via the model if valid.
     * Refreshes the post list and reloads the post detail panel on success.</p>
     *
     * @param post the Post to be edited
     */
    private static void showEditPostDialog(Post post) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Post");
        dialog.setHeaderText("Edit your post");

        ButtonType saveType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveType, ButtonType.CANCEL);
        
        ComboBox<String> categoryCombo = new ComboBox<>();
        categoryCombo.setItems(threadCategories);
        categoryCombo.setValue(post.getCategory());
        
        TextArea titleArea = new TextArea(post.getTitle());
        titleArea.setWrapText(true);
        titleArea.setPrefHeight(50);

        TextArea contentArea = new TextArea(post.getContent());
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
        	post.setTitle(titleArea.getText());
            post.setContent(contentArea.getText());
            post.setCategory(categoryCombo.getValue());
            String error = post.checkValidation();
            if (!error.isEmpty()) {
                alertValidation.setContentText(error);
                alertValidation.showAndWait();
            } else {
            	model.updatePost(post);
                refreshPostList();
                loadPostDetail(post);
            }
        }
    }

    /**********
     * <p>Method: showReplyDialog(Post post)</p>
     *
     * <p>Description: Opens a dialog for the user to write a reply to the selected post.
     * Collects reply content, validates using Reply.checkValidation(), and inserts the
     * reply via the model if valid. After a successful reply, marks the post as unread
     * for all other users so they are notified of new activity.</p>
     *
     * @param post the Post being replied to
     */
   private static void showReplyDialog(Post post) {

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Reply");
        dialog.setHeaderText("Reply to " + post.getAuthor() + "'s post");

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
                post.getPostID(),
                theUser.getUserId(),
                replyArea.getText()
            );
            String error = newReply.checkValidation();
            if (!error.isEmpty()) {
                alertValidation.setContentText(error);
                alertValidation.showAndWait();
            } else {
                model.addReply(newReply, post.getPostID());
                model.markPostAsUnread(post.getPostID(), theUser.getUserName()); 
                loadPostDetail(post);
            }
        }
    }
   
   /**********
    * <p>Method: showEditReplyDialog(Reply reply)</p>
    *
    * <p>Description: Opens a dialog pre-filled with the selected reply's current
    * content. On submit, validates the updated values using
    * Post.checkValidation() and updates the post in the database via the model if valid.
    * Refreshes the post list and reloads the post detail panel on success.</p>
    *
    * @param post the Post to be edited
    */
   private static void showEditReplyDialog(Reply reply) {
       Dialog<ButtonType> dialog = new Dialog<>();
       dialog.setTitle("Edit Reply");
       dialog.setHeaderText("Edit your reply");

       ButtonType saveType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
       dialog.getDialogPane().getButtonTypes().addAll(saveType, ButtonType.CANCEL);
       
       ComboBox<String> categoryCombo = new ComboBox<>();

       TextArea contentArea = new TextArea(reply.getContent());
       contentArea.setWrapText(true);
       contentArea.setPrefHeight(150);


       VBox content = new VBox(10,
           new Label("Content:"),  contentArea
       );
       content.setPadding(new Insets(10));
       dialog.getDialogPane().setContent(content);

       Optional<ButtonType> result = dialog.showAndWait();
       if (result.isPresent() && result.get() == saveType) {
           reply.setContent(contentArea.getText());
           String error = reply.checkValidation();
           if (!error.isEmpty()) {
               alertValidation.setContentText(error);
               alertValidation.showAndWait();
           } else {
        	   model.updateReply(reply);
               refreshPostList();
               loadPostDetail(model.getPostByID(reply.getPostID()));
           }
       }
   }
    
   /* private static void showStaffGraderDialog(Post post) {
        if (!isStaffUser() || post == null) {
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Grader Tool");
        dialog.setHeaderText("Evaluate post by " + post.getAuthor());

        ButtonType autoGradeType = new ButtonType("Start Auto Grader", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(autoGradeType, ButtonType.CANCEL);

        VBox content = new VBox(10);
        content.setPadding(new Insets(12));

        Label instructions = new Label("Mark each parameter as met, then run auto grader.");
        instructions.setWrapText(true);
        content.getChildren().add(instructions);

        String[] definedParams;
        Double[] paramWeights;
        CheckBox[] checks;

        try {
            AssessmentParameterDAO dao = new AssessmentParameterDAO(theDatabase.getConnection());
            AssessmentParameterService service = new AssessmentParameterService(dao);
            List<AssessmentParameter> activeParams = service.listActiveParameters();

            if (activeParams != null && !activeParams.isEmpty()) {
                definedParams = new String[activeParams.size()];
                paramWeights = new Double[activeParams.size()];
                checks = new CheckBox[activeParams.size()];

                for (int i = 0; i < activeParams.size(); i++) {
                    AssessmentParameter p = activeParams.get(i);
                    definedParams[i] = p.getParameterName();
                    paramWeights[i] = p.getPointValue();
                    checks[i] = new CheckBox(
                            p.getParameterName() + " (" + p.getPointValue().intValue() + "%)"
                    );
                    content.getChildren().add(checks[i]);
                }
            } else {
                definedParams = GRADER_PARAMS;
                paramWeights = GRADER_WEIGHTS;
                checks = new CheckBox[GRADER_PARAMS.length];

                for (int i = 0; i < GRADER_PARAMS.length; i++) {
                    checks[i] = new CheckBox(
                            GRADER_PARAMS[i] + " (" + GRADER_WEIGHTS[i].intValue() + "%)"
                    );
                    content.getChildren().add(checks[i]);
                }
            }
        } catch (Exception ex) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Grader Error");
            errorAlert.setHeaderText("Unable to load assessment parameters");
            errorAlert.setContentText(ex.getMessage());
            errorAlert.showAndWait();
            return;
        }

        dialog.getDialogPane().setContent(content);
        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == autoGradeType) {
            try {
                double[] scores = new double[checks.length];
                for (int i = 0; i < checks.length; i++) {
                    scores[i] = checks[i].isSelected() ? paramWeights[i] : 0.0;
                }

                EvaluationTool tool = new EvaluationTool(theDatabase);
                EvaluationTool.EvaluationRow row = EvaluationTool.compute(
                        post.getAuthor(),
                        definedParams,
                        paramWeights,
                        scores
                );

                model.savePostGrade(post.getPostID(), row);
                tool.save(row);

                Post refreshed = model.getPostByID(post.getPostID());
                theSelectedPost = refreshed;
                refreshPostList();
                loadPostDetail(refreshed);

            } catch (Exception ex) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Grading Error");
                errorAlert.setHeaderText("Could not compute or save grade");
                errorAlert.setContentText(ex.getMessage());
                errorAlert.showAndWait();
            }
        }
    }

    /**********
     * <p>Method: handleDeletePost(Post post)</p>
     *
     * <p>Description: Shows a confirmation dialog before soft deleting the selected post.
     * If confirmed, calls softDeletePost() which overwrites the post content with a
     * deleted placeholder instead of removing the row, allowing replies to remain visible.
     * Clears the right panel and refreshes the post list on confirmation.</p>
     *
     * @param post the Post to be soft deleted
     */
    private static void handleDeletePost(Post post) {
        Optional<ButtonType> result = alertDeleteConfirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
        	model.softDeletePost(post.getPostID());
            theSelectedPost = null;
            vbox_PostDetail.getChildren().clear();
            refreshPostList();
        }
    }
    
    /**********
     * <p>Method: handleHidePost(Post post)</p>
     *
     * <p>Description: Shows a confirmation dialog before hiding the selected post.
     * If confirmed, calls hidePost(), which sets isPostHidden for the post to TRUE.
     * Clears the right panel and refreshes the post list on confirmation.</p>
     *
     * @param post the Post to be hidden
     */ 
    private static void handleHidePost(Post post) {
        Optional<ButtonType> result = alertHideConfirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
        	model.hidePost(post);
            theSelectedPost = null;
            vbox_PostDetail.getChildren().clear();
            refreshPostList();
        }
    }
    
    /**********
     * <p>Method: handleUnidePost(Post post)</p>
     *
     * <p>Description: Shows a confirmation dialog before unhiding the selected post.
     * If confirmed, calls unhidePost(), which sets isPostHidden for the post to FALSE.
     * Clears the right panel and refreshes the post list on confirmation.</p>
     *
     * @param post the Post to be unhidden
     */   
    private static void handleUnidePost(Post post) {
        Optional<ButtonType> result = alertHideConfirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
        	model.unhidePost(post);
            theSelectedPost = null;
            vbox_PostDetail.getChildren().clear();
            refreshPostList();
        }
    }
    
    /**********
     * <p>Method: showNewThreadDialog()</p>
     *
     * <p>Description: Opens a text input dialog allowing a staff user to create a new
     * thread category. On submit, adds the new category to the shared threadCategories
     * list and to the category filter combo box so it is immediately available for
     * filtering and post creation. Only accessible by staff users.</p>
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
    
    /**********
     * <p>Method: handleHideReply(Reply reply)</p>
     *
     * <p>Description: Shows a confirmation dialog before hiding the selected reply.
     * If confirmed, calls hideReply(), which sets isReplyHidden for the post to TRUE.
     * Clears the right panel and refreshes the post list on confirmation.</p>
     *
     * @param reply the reply to be hidden
     */ 
    private static void handleHideReply(Reply reply) {
        Optional<ButtonType> result = alertHideConfirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
        	model.hideReply(reply);
            theSelectedPost = null;
            vbox_PostDetail.getChildren().clear();
            refreshPostList();
            loadPostDetail(model.getPostByID(reply.getPostID()));
        }
    }
    
    /**********
     * <p>Method: handleUnideReply(Reply reply)</p>
     *
     * <p>Description: Shows a confirmation dialog before unhiding the selected reply.
     * If confirmed, calls unhideReply(), which sets isReplyHidden for the post to FALSE.
     * Clears the right panel and refreshes the post list on confirmation.</p>
     *
     * @param post the reply to be unhidden
     */   
    private static void handleUnideReply(Reply reply) {
        Optional<ButtonType> result = alertHideConfirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
        	model.unhideReply(reply);
            theSelectedPost = null;
            vbox_PostDetail.getChildren().clear();
            refreshPostList();
            loadPostDetail(model.getPostByID(reply.getPostID()));
        }
    }
    
    /**********
     * <p>Method: handleDeleteReply(Reply reply)</p>
     *
     * <p>Description: Shows a confirmation dialog before soft deleting the selected reply.
     * If confirmed, calls handleDeleteReply() which overwrites the reply content with a
     * deleted placeholder instead of removing the row, allowing replies to remain visible.
     * Clears the right panel and refreshes the post list on confirmation.</p>
     *
     * @param post the Post to be soft deleted
     */
    private static void handleDeleteReply(Reply reply) {
        Optional<ButtonType> result = alertDeleteConfirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
        	model.hardDeleteReply(reply.getReplyID());
            theSelectedPost = null;
            vbox_PostDetail.getChildren().clear();
            refreshPostList();
            loadPostDetail(model.getPostByID(reply.getPostID()));
        }
    }
    
  /**********
     * <p>Method: showStaffGraderDialog(Post post)</p>
     *
     * <p>Description: Opens a staff-only grader form where each predefined
     * criterion can be marked as met or unmet, then computes and stores
     * a weighted grade for the selected post.</p>
     *
     * @param post the post being graded
     */
    private static void showStaffGraderDialog(Post post) {
        if (!isStaffUser() || post == null) {
            return;
        }

        System.out.println("showStaffGraderDialog() fired for post " + post.getPostID());

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Grader Tool");
        dialog.setHeaderText("Evaluate post by " + post.getAuthor());

        ButtonType autoGradeType = new ButtonType("Start Auto Grader", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(autoGradeType, ButtonType.CANCEL);

        VBox content = new VBox(10);
        content.setPadding(new Insets(12));

        Label instructions = new Label("Mark each parameter as met, then run auto grader.");
        instructions.setWrapText(true);
        content.getChildren().add(instructions);

        AssessmentParameterDAO dao = new AssessmentParameterDAO();
        AssessmentParameterService service = new AssessmentParameterService(dao);

        List<AssessmentParameter> activeParams = null;
        try {
            activeParams = service.listActiveParameters();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String[] definedParams;
        Double[] paramWeights;
        CheckBox[] checks;

        if (activeParams != null && !activeParams.isEmpty()) {
            definedParams = new String[activeParams.size()];
            paramWeights = new Double[activeParams.size()];
            checks = new CheckBox[activeParams.size()];

            for (int i = 0; i < activeParams.size(); i++) {
                AssessmentParameter p = activeParams.get(i);
                definedParams[i] = p.getParameterName();
                paramWeights[i] = p.getPointValue();
                checks[i] = new CheckBox(p.getParameterName() + " (" + p.getPointValue().intValue() + "%)");
                content.getChildren().add(checks[i]);
            }
        } else {
            definedParams = GRADER_PARAMS;
            paramWeights = GRADER_WEIGHTS;
            checks = new CheckBox[GRADER_PARAMS.length];

            for (int i = 0; i < GRADER_PARAMS.length; i++) {
                checks[i] = new CheckBox(GRADER_PARAMS[i] + " (" + GRADER_WEIGHTS[i].intValue() + "%)");
                content.getChildren().add(checks[i]);
            }
        }

        System.out.println("Active parameter count: " + (activeParams == null ? 0 : activeParams.size()));
        if (activeParams != null) {
            for (AssessmentParameter p : activeParams) {
                System.out.println(p.getParameterName() + " | " + p.getPointValue() + " | active=" + p.isActive());
            }
        }

        dialog.getDialogPane().setContent(content);

        Optional<ButtonType> result = dialog.showAndWait();

        if (!(result.isPresent() && result.get() == autoGradeType)) {
            return;
        }

        try {
            double[] scores = new double[checks.length];
            for (int i = 0; i < checks.length; i++) {
                scores[i] = checks[i].isSelected() ? paramWeights[i] : 0.0;
            }

            System.out.println("=== DEBUG GRADER INPUT ===");
            System.out.println("Post ID: " + post.getPostID());
            System.out.println("Post Author ID: " + post.getAuthor());

            for (int i = 0; i < checks.length; i++) {
                System.out.println(
                    "Param: " + definedParams[i]
                    + " | Weight: " + paramWeights[i]
                    + " | Checked: " + checks[i].isSelected()
                    + " | Score used: " + scores[i]
                );
            }

            EvaluationTool tool = new EvaluationTool(theDatabase);
            EvaluationTool.EvaluationRow row = EvaluationTool.compute(
                    post.getAuthor(),
                    definedParams,
                    paramWeights,
                    scores
            );

            System.out.println("=== DEBUG GRADER OUTPUT ===");
            System.out.println("Percentage: " + row.percentage);
            System.out.println("Number Grade: " + row.numberGrade);
            System.out.println("Letter Grade: " + row.letterGrade);

            model.savePostGrade(post.getPostID(), row);
            tool.save(row);

            Post refreshed = model.getPostByID(post.getPostID());
            theSelectedPost = refreshed;
            refreshPostList();
            loadPostDetail(refreshed);

        } catch (Exception ex) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Grading Error");
            errorAlert.setHeaderText("Could not compute or save grade");
            errorAlert.setContentText(ex.getMessage());
            errorAlert.showAndWait();
        }
    }

   /**********
     * <p>Method: showStaffEditGradeDialog(Post post)</p>
     *
     * <p>Description: Lets staff set the numeric score (0–100) on a student-authored
     * post letter grade is derived from the rubric mapping.</p>
     *
     * @param post the post whose grade is edited
     */
    private static void showStaffEditGradeDialog(Post post) {
        if (!isStaffUser() || post == null) {
            return;
        }
        if (!isStudentUser()) {
            alertValidation.setContentText("Only student-authored posts can have grades edited.");
            alertValidation.showAndWait();
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Grade");
        dialog.setHeaderText("Manual grade for " + post.getAuthor());
        ButtonType saveType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(12));
        TextField numField = new TextField(Integer.toString(post.getNumberGrade()));
        grid.add(new Label("Numeric score (0–100):"), 0, 0);
        grid.add(numField, 1, 0);
        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == saveType) {
            try {
                int n = Integer.parseInt(numField.getText().trim());
                if (n < 0 || n > 100) {
                    throw new NumberFormatException();
                }
                if (!model.savePostGradeManual(post.getPostID(), n)) {
                    alertValidation.setContentText("Could not save grade.");
                    alertValidation.showAndWait();
                    return;
                }
                Post refreshed = model.getPostByID(post.getPostID());
                theSelectedPost = refreshed;
                refreshPostList();
                loadPostDetail(refreshed);
            } catch (NumberFormatException ex) {
                alertValidation.setContentText("Enter an integer from 0 to 100.");
                alertValidation.showAndWait();
            }
        }
    }
    
    
    /**********
     * <p>Method: refreshStudentFilter()</p>
     *
     * <p>Description: Populates the student filter combo box with the usernames of all
     * students who have made at least one post. Fetches all posts to determine which
     * user IDs have authored content, then resolves each ID to a username via the
     * database. The default "All Students" option is always present at the top of the
     * list. Should be called on page load and after any new post is added to ensure
     * the combo box stays up to date.</p>
     */
    private static void refreshStudentFilter() {
        List<Post> posts = model.getAllPosts(null);
        List<Integer> allUsersIDs = theDatabase.getUserList();
        Set<Integer> postStudentIDs = new TreeSet<>();
        ObservableList<String> items = FXCollections.observableArrayList("All Students", "Flagged Posts");

        if (posts != null) {
            for (Post p : posts) {
                postStudentIDs.add(p.getAuthor());
            }
        }

        if (allUsersIDs != null) {
            for (Integer userID : allUsersIDs) {
                for (Integer postID : postStudentIDs) {
                    if (userID == postID) {
                        User grabUserName = theDatabase.getUserAsObject(userID);
                        items.add(grabUserName.getUserName());
                    }
                }
            }
        }

        combo_StudentFilter.setItems(items);
        combo_StudentFilter.setValue("All Students");
    }
    
    /**********
     * <p>Method: showEmailDialog(Post post)</p>
     *
     * <p>Description: Opens a dialog allowing the current user to compose and send
     * a simulated email to the author of the given post. Retrieves the author's email
     * address from the database and displays it in a confirmation alert once the user
     * submits their message. No actual email is sent; this is a simulated interaction.</p>
     *
     * @param post the Post whose author will receive the simulated email
     */
    private static void showEmailDialog(Post post) {
        String recipientEmail = theDatabase.getEmailAddress(post.getAuthor());

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Send Email");
        dialog.setHeaderText("Email post author");

        ButtonType submitType = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(submitType, ButtonType.CANCEL);

        TextArea messageArea = new TextArea();
        messageArea.setPromptText("Write your message here...");
        messageArea.setWrapText(true);
        messageArea.setPrefHeight(150);
       

        VBox content = new VBox(10,
            new Label("To: " + (recipientEmail != null ? recipientEmail : "Unknown")),
            new Label("Message:"),
            messageArea
        );
        
        content.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(content);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == submitType) {
            String validationError = feedbackValidator.validateEmailMessage(messageArea.getText());
            if (!validationError.isEmpty()) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Invalid Message");
                errorAlert.setHeaderText("Could not send email");
                errorAlert.setContentText(validationError);
                errorAlert.showAndWait();
                return;
            }

            if (feedbackValidator.containsInappropriateContent(messageArea.getText())) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Invalid Message");
                errorAlert.setHeaderText("Could not send email");
                errorAlert.setContentText("Message contains inappropriate content.");
                errorAlert.showAndWait();
                return;
            }

            Alert confirmation = new Alert(Alert.AlertType.INFORMATION);
            confirmation.setTitle("Email Sent");
            confirmation.setHeaderText("Email Sent!");
            confirmation.setContentText("Your message was sent to: " +
                (recipientEmail != null ? recipientEmail : "Unknown"));
            confirmation.showAndWait();
        }
    }
    
    /**********
     * <p>Method: createFlaggedContentTicket(Post post)</p>
     *
     * <p>Description: Automatically creates a support ticket in the Ticket Forum when a
     * newly submitted post is detected to contain inappropriate content. The ticket is
     * filed under the poster's author ID and categorized as Open so staff can immediately
     * see and action it. Content is kept concise to satisfy the Post body character limit.</p>
     *
     * @param post the Post that was flagged for inappropriate content
     */
    private static void createFlaggedContentTicket(Post post) {
        String posterName = theDatabase.getUserAsObject(post.getAuthor()).getUserName();
        String contentPreview = post.getContent().length() > 30
            ? post.getContent().substring(0, 30) + "..."
            : post.getContent();

        String ticketTitle = "FLAG: " + posterName;
        String ticketContent = "Flagged post by " + posterName + ": " + contentPreview;

        Ticket flagTicket = new Ticket(post.getAuthor(), ticketTitle, ticketContent);
        ticketModel.addTicket(flagTicket);
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
	
	private static boolean isStudentPost(Post post) {
		int id = post.getAuthor();	 
		String roleString = "";
		
		if (theDatabase.getAdminRole(id)) roleString += "Admin ";
		if (theDatabase.getRole1(id)) roleString += "Staff ";
		if (theDatabase.getRole2(id)) roleString += "Student ";
		
		if (roleString.contains("Student")) {
			return true;
		} else {
			return false;
		}
	}
     private static boolean isStaffUser() {
        return "Staff".equals(returnPage);
    }
     
     private static boolean isAdminUser() {
         return "Admin".equals(returnPage);
    }
     
     private static boolean isStudentUser() {
    	 return "Student".equals(returnPage);
     }
 
      
}
