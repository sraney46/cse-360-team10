module FoundationsF25 {
  requires javafx.controls;
  requires java.sql;
  requires javafx.graphics;
  requires javafx.base;

  opens applicationMain to javafx.graphics, javafx.fxml;
}
