module cse360team10 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;
    requires java.sql;
    requires org.junit.jupiter.api;
	requires javafx.swing;
    opens applicationMain to javafx.graphics, javafx.fxml;
}