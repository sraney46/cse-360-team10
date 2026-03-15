package guiDiscussionForum;

import entityClasses.Post;
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
import javafx.stage.Stage;
import database.Database;

import java.util.List;
import java.util.Optional;

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
    protected static Button button_PopulateDatabase = new Button("Populate Database");
    protected static Button button_UpdateThisUser = new Button("Account Update");
    protected static ComboBox<String> combo_Category = new ComboBox<>();

    protected static Line line_Separator1 = new Line();

    // GUI Area 2 — Left post list
    protected static ScrollPane scrollPane_PostList;
    protected static VBox vbox_PostList;

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

    public static Scene theDiscussionForumScene = null;

    // Alerts
    protected static Alert alertDeleteConfirm = new Alert(Alert.AlertType.CONFIRMATION);
    protected static Alert alertValidation = new Alert(Alert.AlertType.WARNING);
 	protected static Alert alertPopulateDatabase = new Alert(AlertType.CONFIRMATION);


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
     */
    public static void displayDiscussionForum(Stage ps, User user, String returnPageStr) {
        theStage = ps;
        theUser  = user;
        returnPage = returnPageStr;

        if (theView == null) theView = new ViewDiscussionForum();

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

        // Category filter combo
        combo_Category.setItems(FXCollections.observableArrayList(
            "All", "General", "Homework", "Lectures", "Assignments", "Exams"
        ));
        combo_Category.setValue("All");
        combo_Category.getStyleClass().add("default-combo-box");
        combo_Category.setLayoutX(width/2 - 600);
        combo_Category.setLayoutY(55);
        combo_Category.setPrefWidth(100);
        combo_Category.setPrefHeight(16);
        combo_Category.setOnAction(_ -> {
            String selected = combo_Category.getValue();
            if (selected == null || selected.equals("All")) {
                refreshPostList();
            } else {
                List<Post> subset = model.getPostsByCategory(selected);
                populatePostList(subset);
            }
        });

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
            line_Separator4,
            button_Return, button_Logout, button_Quit
        );
    }

    /*-*******************************************************************************************
     Post List Population
    */

    /**********
     * <p>Method: refreshPostList()</p>
     *
     * <p>Description: Fetches all posts from the database and repopulates
     * the left panel post list. Called on page load and after any CRUD operation.</p>
     */
    private static void refreshPostList() {
        List<Post> posts = model.getAllPosts();
        populatePostList(posts);
    }

    /**********
     * <p>Method: populatePostList(List<Post> posts)</p>
     *
     * <p>Description: Clears the left panel and rebuilds it using the provided
     * list of posts. Each post becomes a clickable row. Extracted from refreshPostList
     * so subset filter results can also use this method.</p>
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
            HBox row = createPostRow(post);
            vbox_PostList.getChildren().add(row);
        }
    }

    /**********
     * <p>Method: createPostRow(Post post)</p>
     *
     * <p>Description: Builds a single clickable row for the left panel representing
     * one post. Displays the post author, a truncated preview of the content, and
     * the category. Clicking the row loads the full post in the right panel.</p>
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

        // Author + category badge on one line
        HBox topLine = new HBox(8);
        topLine.setAlignment(Pos.CENTER_LEFT);
        Label authorLabel = new Label(post.getAuthor());
        authorLabel.setStyle(
            "-fx-font-family: 'Montserrat SemiBold'; -fx-font-size: 13px; -fx-text-fill: #fff;"
        );
        Label categoryBadge = new Label(post.getCategory());
        categoryBadge.setStyle(
            "-fx-background-color: #5865f2; -fx-text-fill: white;" +
            "-fx-font-size: 10px; -fx-padding: 2 8; -fx-background-radius: 999;"
        );
        topLine.getChildren().addAll(authorLabel, categoryBadge);

        // Content preview 
        String preview = post.getContent().length() > 60
            ? post.getContent().substring(0, 60) + "..."
            : post.getContent();
        Label previewLabel = new Label(preview);
        previewLabel.setStyle(
            "-fx-font-family: 'Montserrat'; -fx-font-size: 12px; -fx-text-fill: #aaa;"
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

        // Click to load full post on right
        row.setOnMouseClicked(_ -> {
            theSelectedPost = post;
            loadPostDetail(post);
        });

        return row;
    }

    /*-*******************************************************************************************
     Post Detail Panel
    */

    /**********
     * <p>Method: loadPostDetail(Post post)</p>
     *
     * <p>Description: Populates the right panel with the full content of the selected post
     * and all of its replies. Also renders Edit and Delete buttons if the logged-in user
     * is the author of the post, and a Reply button for all users.</p>
     *
     * @param post the Post to display in full detail
     */
    private static void loadPostDetail(Post post) {
        vbox_PostDetail.getChildren().clear();

        // Post header
        Label authorLabel = new Label(post.getAuthor());
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

        if (post.getAuthor().equals(theUser.getUserName())) {
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
     * the author name, their role badge, and the reply content.</p>
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

        Label authorLabel = new Label(reply.getAuthor());
        authorLabel.setStyle(
            "-fx-font-family: 'Montserrat SemiBold'; -fx-font-size: 13px; -fx-text-fill: #fff;"
        );

        Label roleBadge = new Label(reply.getAuthorRole());
        String badgeColor = switch (reply.getAuthorRole()) {
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
     * <p>Method: showNewPostDialog()</p>
     *
     * <p>Description: Opens a dialog allowing the user to create a new post.
     * Collects content and category input, validates, and calls postList.addPost().</p>
     */
    private static void showNewPostDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("New Post");
        dialog.setHeaderText("Create a new post");

        ButtonType submitType = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(submitType, ButtonType.CANCEL);

        TextArea contentArea = new TextArea();
        contentArea.setPromptText("Write your post here...");
        contentArea.setWrapText(true);
        contentArea.setPrefHeight(150);

        ComboBox<String> categoryCombo = new ComboBox<>();
        categoryCombo.setItems(FXCollections.observableArrayList(
            "General", "Homework", "Lectures", "Assignments", "Exams"
        ));
        categoryCombo.setValue("General");

        VBox content = new VBox(10,
            new Label("Category:"), categoryCombo,
            new Label("Content:"),  contentArea
        );
        content.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(content);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == submitType) {
            Post newPost = new Post(
                theUser.getUserName(),
                contentArea.getText(),
                categoryCombo.getValue()
            );
            String error = newPost.checkValidation();
            if (!error.isEmpty()) {
                alertValidation.setContentText(error);
                alertValidation.showAndWait();
            } else {
            	model.addPost(newPost);
                refreshPostList();
            }
        }
    }

    /**********
     * <p>Method: showEditPostDialog(Post post)</p>
     *
     * <p>Description: Opens a dialog pre-filled with the selected post's current content
     * and category. On submit, updates the post in the database and refreshes the view.</p>
     *
     * @param post the Post to be edited
     */
    private static void showEditPostDialog(Post post) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Post");
        dialog.setHeaderText("Edit your post");

        ButtonType saveType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveType, ButtonType.CANCEL);

        TextArea contentArea = new TextArea(post.getContent());
        contentArea.setWrapText(true);
        contentArea.setPrefHeight(150);

        ComboBox<String> categoryCombo = new ComboBox<>();
        categoryCombo.setItems(FXCollections.observableArrayList(
            "General", "Homework", "Lectures", "Assignments", "Exams"
        ));
        categoryCombo.setValue(post.getCategory());

        VBox content = new VBox(10,
            new Label("Category:"), categoryCombo,
            new Label("Content:"),  contentArea
        );
        content.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(content);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == saveType) {
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
     * Collects reply content, creates a Reply object, and calls replyList.addReply().</p>
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
            String role = returnPage;
           

            Reply newReply = new Reply(
                post.getPostID(),
                theUser.getUserName(),
                role,
                replyArea.getText()
            );
            String error = newReply.checkValidation();
            if (!error.isEmpty()) {
                alertValidation.setContentText(error);
                alertValidation.showAndWait();
            } else {
            	model.addReply(newReply);
                loadPostDetail(post);
            }
        }
    }

    /**********
     * <p>Method: handleDeletePost(Post post)</p>
     *
     * <p>Description: Shows a confirmation dialog before deleting the selected post.
     * If confirmed, calls postList.deletePost() and clears the right panel.</p>
     *
     * @param post the Post to be deleted
     */
    private static void handleDeletePost(Post post) {
        Optional<ButtonType> result = alertDeleteConfirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
        	model.deletePost(post.getPostID());
            theSelectedPost = null;
            vbox_PostDetail.getChildren().clear();
            refreshPostList();
        }
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
    
    
}