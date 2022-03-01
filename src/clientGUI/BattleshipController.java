package clientGUI;

import java.io.IOException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class BattleshipController {

  /*
   * 
   * STRINGS GLOBALES
   * 
   */

  private final String CASILLA_AGUA = "≈";
  private final String CASILLA_TOCADO = "✹";
  private final String CASILLA_BARCO = "⚙";
  private final String CASILLA_BARCO_MARCADO = "☉";

  /*
   * 
   * CLIENTE
   *
   */
  private final String SERVER = "127.0.0.1";
  private final Integer PUERTO = 9999;
  private Client cliente = null;

  @FXML
  private Button bConectar;

  @FXML
  private void conectar(ActionEvent event) throws UnknownHostException, IOException {
    cliente = new Client(SERVER, PUERTO);
    cliente.conectarAServidor();

    hiloCliente = new HiloCliente();
    hiloCliente.start();

    desactivarConectar();
  }

  /*
   * 
   * COLOCAR BARCOS
   * 
   */
  private boolean direccionHorizontal = false;
  private boolean marcado = false;
  private boolean limpio = true;

  private ArrayList<ModeloBarco> modelosBarco = new ArrayList<ModeloBarco>();
  private ArrayList<Button> ultimoBarco = new ArrayList<Button>();

  private ModeloBarco modeloBarcoActual = null;

  @FXML
  private Button bVertical;

  @FXML
  private Button bHorizontal;

  @FXML
  private Button bColocar;

  @FXML
  private void horizontal(ActionEvent event) {
    direccionHorizontal = false;
  }

  @FXML
  private void vertical(ActionEvent event) {
    direccionHorizontal = true;
  }

  private Button iteratorCasillaBarco = null;
  private Button iteratorCasilla = null;

  @FXML
  private void marcarBarco(ActionEvent event) {
    limpiarUltimoBarco();

    Integer row = 0;
    Integer column = 0;

    row = GridPane.getRowIndex((Node) event.getSource());
    column = GridPane.getColumnIndex((Node) event.getSource());


    limpio = true;
    if (direccionHorizontal) {
      marcarBarcoHorizontal(row, column);
    } else {
      marcarBarcoVertical(row, column);
    }

  }

  private void marcarBarcoVertical(Integer row, Integer column) {
    int i;
    for (i = 0; i < modeloBarcoActual.getNumeroCasillas() && column + i < 11 && limpio; i++) {
      iteratorCasillaBarco = (Button) matrixPropia[row][column + i];
      for (int a = row - 1; a <= row + 1 && a < 11 && limpio; a++) {
        for (int b = column + i - 1; b <= column + i + 1 && b < 11 && limpio; b++) {
          a = rectificar(a);
          b = rectificar(b);
          iteratorCasilla = (Button) matrixPropia[a][b];

          if (CASILLA_BARCO.equals(iteratorCasilla.getText())) {
            limpio = false;
          }
        }
      }
      if (limpio) {
        marcarCasilla(iteratorCasillaBarco);
      }
    }
    marcado = true;
    if (i < modeloBarcoActual.getNumeroCasillas() || !limpio) {
      limpiarUltimoBarco();
    }
  }

  int iteratorNumeroCasillaBarco;
  int iteratorRow = 0;
  int iteratorColumn = 0;

  private void marcarBarcoHorizontal(Integer row, Integer column) {
    for (iteratorNumeroCasillaBarco =
        0; iteratorNumeroCasillaBarco < modeloBarcoActual.getNumeroCasillas()
            && row + iteratorNumeroCasillaBarco < 11 && limpio; iteratorNumeroCasillaBarco++) {
      iteratorCasillaBarco = (Button) matrixPropia[row + iteratorNumeroCasillaBarco][column];
      comprobarBordeLimpioCasilla(row, column);

      if (limpio) {
        marcarCasilla(iteratorCasillaBarco);
      }
    }
    marcado = true;
    if (iteratorNumeroCasillaBarco < modeloBarcoActual.getNumeroCasillas() || !limpio) {
      limpiarUltimoBarco();
    }
  }

  private void comprobarBordeLimpioCasilla(Integer row, Integer column) {
    for (iteratorRow =
        row + iteratorNumeroCasillaBarco - 1; iteratorRow <= row + iteratorNumeroCasillaBarco + 1
            && iteratorRow < 11 && limpio; iteratorRow++) {
      for (iteratorColumn = column - 1; iteratorColumn <= column + 1 && iteratorColumn < 11
          && limpio; iteratorColumn++) {
        iteratorRow = rectificar(iteratorRow);
        iteratorColumn = rectificar(iteratorColumn);
        iteratorCasilla = (Button) matrixPropia[iteratorRow][iteratorColumn];

        if (CASILLA_BARCO.equals(iteratorCasilla.getText())) {
          limpio = false;
        }

      }
    }
  }

  private int rectificar(int a) {
    if (a < 1) {
      a = 1;
    }
    return a;
  }

  private void marcarCasilla(Button n) {
    n.setText(CASILLA_BARCO_MARCADO);
    ultimoBarco.add(n);
  }

  private void limpiarUltimoBarco() {
    while (!ultimoBarco.isEmpty()) {
      ultimoBarco.get(0).setText("");
      ultimoBarco.remove(0);
      marcado = false;
    }
  }

  private boolean cambiarBarco() {
    modelosBarco.remove(0);
    if (!modelosBarco.isEmpty()) {
      modeloBarcoActual = modelosBarco.get(0);
      actualizarBarco();
      return true;
    }
    return false;
  }

  private void colocarBarco() {
    while (!ultimoBarco.isEmpty()) {
      ultimoBarco.get(0).setText(CASILLA_BARCO);

      casillasPropias++;
      progresoPropio = (double) casillasPropias / MAXIMO_CASILLAS;
      ownProgress.setProgress(progresoPropio);

      ultimoBarco.remove(0);
      marcado = false;
    }
  }

  private void actualizarBarco() {
    escribirTexto1("Coloca tu " + modeloBarcoActual.getNombre());
    escribirTexto2("Ocupa " + modeloBarcoActual.getNumeroCasillas() + " casillas");

  }

  @FXML
  private void colocar(ActionEvent event) {
    if (marcado) {
      colocarBarco();
      if (!cambiarBarco()) {
        activarFlota(false);
        escribirTexto1("");
        escribirTexto2("");
        activarConectar();
      }
    }
  }

  /*
   * 
   * CAMBIAR CONTROLES
   * 
   */

  private void activarConectar() {
    bConectar.setDisable(false);

  }

  private void desactivarConectar() {
    bConectar.setDisable(true);
  }

  private void activarAtaque() {
    gridEnemigo.setDisable(false);
  }

  private void desactivarAtaque() {
    gridEnemigo.setDisable(true);
  }

  private void activarFlota(boolean activar) {
    gridPropio.setDisable(!activar);
    bHorizontal.setDisable(!activar);
    bVertical.setDisable(!activar);
    bColocar.setDisable(!activar);
  }

  /*
   * 
   * GENERAL
   * 
   */

  private Integer coordenada1 = null;
  private Integer coordenada2 = null;
  @FXML
  private TextField tField1;

  private void escribirTexto1(String s) {
    tField1.setText(s);
  }

  @FXML
  private TextField tField2;

  private void escribirTexto2(String s) {
    tField2.setText(s);
  }

  @FXML
  private URL location;

  @FXML
  private ResourceBundle resources;

  @FXML
  private GridPane gridEnemigo;

  @FXML
  private GridPane gridPropio;

  private Node[][] matrixPropia = null;
  private Node[][] matrixEnemiga = null;

  private Node[][] gridToNodeMatrix(GridPane gridPane) {

    Integer nodeRow = 0;
    Integer nodeColumn = 0;

    Node[][] nodeMatrix = new Node[11][11];

    for (Node node : gridPane.getChildren()) {

      nodeRow = GridPane.getRowIndex(node);
      nodeColumn = GridPane.getColumnIndex(node);

      nodeRow = correctNullToZero(nodeRow);
      nodeColumn = correctNullToZero(nodeColumn);

      nodeMatrix[nodeRow][nodeColumn] = node;
    }
    return nodeMatrix;
  }

  private Integer correctNullToZero(Integer number) {
    if (number == null) {
      number = 0;
    }
    return number;
  }

  /*
   * 
   * PROGRESO
   * 
   */

  private final int MAXIMO_CASILLAS = 17;

  private int casillasPropias = 0;
  private int casillasEnemigas = 0;

  private double progresoPropio = 0.0;
  private double progresoEnemigo = 0.0;

  @FXML
  private ProgressBar ownProgress;

  @FXML
  private ProgressBar enemyProgress;
  /*
   * 
   * FIN
   * 
   */

  private AvisoFinPartida avisoFinPartida = null;


  @FXML
  private void initialize() throws UnknownHostException, IOException {
    assert tField1 != null : "fx:id=\"tTipoBarco\" was not injected: check your FXML file 'ColocarBarcos.fxml'.";
    assert tField2 != null : "fx:id=\"tNumeroCuadros\" was not injected: check your FXML file 'ColocarBarcos.fxml'.";
    assert bVertical != null : "fx:id=\"bVertical\" was not injected: check your FXML file 'ColocarBarcos.fxml'.";
    assert bHorizontal != null : "fx:id=\"bHorizontal\" was not injected: check your FXML file 'ColocarBarcos.fxml'.";
    assert bColocar != null : "fx:id=\"bColocar\" was not injected: check your FXML file 'ColocarBarcos.fxml'.";
    assert enemyProgress != null : "fx:id=\"enemyProgress\" was not injected: check your FXML file 'ColocarBarcos.fxml'.";
    assert gridPropio != null : "fx:id=\"gridPropio\" was not injected: check your FXML file 'ColocarBarcos.fxml'.";
    assert ownProgress != null : "fx:id=\"ownProgress\" was not injected: check your FXML file 'Battleship.fxml'.";
    assert enemyProgress != null : "fx:id=\"enemyProgress\" was not injected: check your FXML file 'Battleship.fxml'.";

    matrixEnemiga = gridToNodeMatrix(gridEnemigo);
    matrixPropia = gridToNodeMatrix(gridPropio);

    modelosBarco.add(new ModeloBarco("portaaviones", 5));
    modelosBarco.add(new ModeloBarco("buque", 4));
    modelosBarco.add(new ModeloBarco("submarino", 3));
    modelosBarco.add(new ModeloBarco("crucero", 3));
    modelosBarco.add(new ModeloBarco("lancha", 2));

    modeloBarcoActual = modelosBarco.get(0);
    actualizarBarco();

    matrixPropia = gridToNodeMatrix(gridPropio);
  }

  /*
   * 
   * JUEGO
   * 
   */
  @FXML
  private void atacar(ActionEvent event) {
    Integer row = 0;
    Integer column = 0;
    row = GridPane.getRowIndex((Node) event.getSource());
    column = GridPane.getColumnIndex((Node) event.getSource());

    String s = (((Button) matrixEnemiga[row][column]).getText());

    if (!(s.equals(CASILLA_AGUA) || s.equals(CASILLA_TOCADO))) {

      cliente.escribirSalida(row.toString());
      cliente.escribirSalida(column.toString());

      coordenada1 = row;
      coordenada2 = column;

      desactivarAtaque();
    }
  }

  /*
   * 
   * HILO CLIENTE
   * 
   */
  private HiloCliente hiloCliente = null;

  private class HiloCliente extends Thread {

    String accion = null;

    Boolean check1 = null;
    Boolean check2 = null;
    Boolean check3 = null;

    Boolean partida = true;

    public void run() {
      try {
        Platform.runLater(new Runnable() {
          @Override
          public void run() {
            casillasEnemigas = 17;
            progresoEnemigo = 1.0;
            enemyProgress.setProgress(progresoEnemigo);
          }
        });
        while (partida) {
          accion = cliente.esperarYLeerEntrada();
          if ("TURNO".equals(accion)) {
            turnoActivo();
          } else {
            turnoPasivo();
          }
        }
      } catch (IOException e) {
        e.printStackTrace();

      } finally {
        try {
          cliente.desconectarDelServidor();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }


    private void turnoPasivo() throws IOException {
      Platform.runLater(new Runnable() {
        @Override
        public void run() {
          escribirTexto1("TURNO PASIVO");
        }
      });

      accion = cliente.esperarYLeerEntrada();
      coordenada1 = Integer.parseInt(cliente.esperarYLeerEntrada());
      coordenada2 = Integer.parseInt(cliente.esperarYLeerEntrada());

      cliente.escribirSalida("CHECK");

      Boolean acertado = check(coordenada1, coordenada2);
      cliente.escribirSalida(acertado.toString());

      if (acertado) {
        casillasPropias--;
        progresoPropio = (double) casillasPropias / MAXIMO_CASILLAS;
        Platform.runLater(new Runnable() {
          @Override
          public void run() {
            ownProgress.setProgress(progresoPropio);
          }
        });
      }

      cliente.escribirSalida("false");

      Boolean finPartida = casillasPropias == 0;
      cliente.escribirSalida(finPartida.toString());
      if (finPartida) {
        Platform.runLater(new Runnable() {
          @Override
          public void run() {
            try {
              finPartida(false);
            } catch (IOException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
          }
        });
      }
    }

    private Boolean check(Integer coordenada1, Integer coordenada2) {
      Button n = (Button) matrixPropia[coordenada1][coordenada2];
      return (CASILLA_BARCO.equals(n.getText()));
    }


    private void turnoActivo() throws IOException {
      Platform.runLater(new Runnable() {
        @Override
        public void run() {
          escribirTexto1("TURNO ACTIVO");
        }
      });

      cliente.escribirSalida("JUGADA");

      Platform.runLater(new Runnable() {
        @Override
        public void run() {
          activarAtaque();
        }
      });

      accion = cliente.esperarYLeerEntrada();
      check1 = Boolean.parseBoolean(cliente.esperarYLeerEntrada());
      check2 = Boolean.parseBoolean(cliente.esperarYLeerEntrada());
      check3 = Boolean.parseBoolean(cliente.esperarYLeerEntrada());

      Button n = (Button) matrixEnemiga[coordenada1][coordenada2];

      if (check1) {
        // Tocado
        Platform.runLater(new Runnable() {
          @Override
          public void run() {
            n.setText(CASILLA_TOCADO);
          }
        });


        casillasEnemigas--;
        progresoEnemigo = (double) casillasEnemigas / MAXIMO_CASILLAS;
        Platform.runLater(new Runnable() {
          @Override
          public void run() {
            enemyProgress.setProgress(progresoEnemigo);
          }
        });

        if (check2) {
          // hundido
        }
        if (check3) {
          // fin del juego
          Platform.runLater(new Runnable() {
            @Override
            public void run() {
              try {
                finPartida(true);
              } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
              }
            }
          });
        }

      } else {
        // Agua
        Platform.runLater(new Runnable() {
          @Override
          public void run() {
            n.setText(CASILLA_AGUA);
          }
        });
      }
    }
  }

  private void finPartida(boolean victoria) throws IOException {

    desconectarCliente();

    crearAvisoFinPartida(victoria);
    mostrarAvisoFinPartida();

    cerrarPrograma();
  }

  public void desconectarCliente() throws IOException {
    cliente.desconectarDelServidor();
  }

  public void cerrarPrograma() {
    System.exit(0);
  }

  public void mostrarAvisoFinPartida() {
    avisoFinPartida.showAndWait();
  }

  public void crearAvisoFinPartida(boolean victoria) {
    avisoFinPartida = new AvisoFinPartida(victoria);
  }
}
