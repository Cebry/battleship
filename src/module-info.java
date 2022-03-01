
/**
 * @author Javier Cebrián Muñoz
 *
 */
module battleship {
  requires transitive javafx.graphics;
  requires javafx.controls;
  requires javafx.fxml;
  requires javafx.web;
  requires javafx.base;
  requires org.junit.jupiter.api;

  opens clientGUI to javafx.fxml;
  opens serverGUI to javafx.fxml;

  exports clientGUI;
  exports serverGUI;
}
