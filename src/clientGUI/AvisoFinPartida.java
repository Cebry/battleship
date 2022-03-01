package clientGUI;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class AvisoFinPartida {


  private final String tituloFin = "Fin del juego";
  private final String cabeceraFinVictoria = "Enhorabuena, ganaste la partida.";
  private final String cabeceraFinDerrota = "Más suerte la próxima vez.";
  private final String contenidoFin = "Gracias por jugar ^^";

  private Alert ventanaFin = new Alert(AlertType.INFORMATION);



  public AvisoFinPartida(boolean victoria) {
    setTitle();
    if (victoria) {
      setHeaderVictoria();
    } else {
      setHeaderDerrota();
    }
    setContent();
  }

  public void setTitle() {
    ventanaFin.setTitle(tituloFin);
  }

  public void showAndWait() {
    ventanaFin.showAndWait();
  }

  public void setContent() {
    ventanaFin.setContentText(contenidoFin);
  }

  public void setHeaderDerrota() {
    ventanaFin.setHeaderText(cabeceraFinDerrota);
  }

  public void setHeaderVictoria() {
    ventanaFin.setHeaderText(cabeceraFinVictoria);
  }


}
