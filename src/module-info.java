module FoundationsF25 {
	requires javafx.controls;
	requires java.sql;

	
	opens applicationMain to javafx.graphics, javafx.fxml;
}
