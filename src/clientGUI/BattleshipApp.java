package clientGUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class BattleshipApp extends Application {

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage arg0) throws Exception {
    FXMLLoader loader = new FXMLLoader();
    loader.setLocation(getClass().getResource("Battleship.fxml"));

    Scene scene = new Scene(loader.load());
    Stage primaryStage = new Stage();
    primaryStage.setTitle("BattleShip");

    primaryStage.setResizable(false);

    primaryStage.setScene(scene);
    primaryStage.showAndWait();

  }

}
