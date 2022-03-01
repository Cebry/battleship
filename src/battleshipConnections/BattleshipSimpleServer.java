package battleshipConnections;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;

public class BattleshipSimpleServer {

  static ServerSocket servidor = null;

  static Socket sc1 = null;
  static BufferedReader in1 = null;
  static PrintWriter out1 = null;

  static Socket sc2 = null;
  static BufferedReader in2 = null;
  static PrintWriter out2 = null;

  static boolean partida = false;

  static String accion = null;
  static String coordenada1 = null;
  static String coordenada2 = null;

  static String check1 = null;
  static String check2 = null;
  static String check3 = null;

  static BufferedWriter log = null;



  static final int PUERTO = 9999;

  public static void main(String args[]) throws IOException {

    try {

      servidor = new ServerSocket(PUERTO);

      log = new BufferedWriter(new FileWriter("log_" + LocalDateTime.now() + ".log"));

      informarEvento("Server started. Waiting for connection from port " + PUERTO + ".");

      while (true) {

        esperarJugadores();

        partida = true;

        while (partida) {

          informarEvento("Comienza el turno del jugador 1");
          turno(in1, out1, in2, out2);

          if (partida) {
            informarEvento("Comienza el turno del jugador 2");
            turno(in2, out2, in1, out1);

          }
        }
      }

    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } finally {
      log.close();
    }
  }

  private static void informarEvento(String s) throws IOException {
    s = LocalDateTime.now() + " - " + s;
    System.out.println(s);
    log.write(s + "\n");
    log.flush();
  }

  private static void esperarJugadores() throws IOException {
    sc1 = servidor.accept();
    in1 = new BufferedReader(new InputStreamReader(sc1.getInputStream()));
    out1 = new PrintWriter(sc1.getOutputStream(), true);

    informarEvento("Jugador 1 aceptado");

    sc2 = servidor.accept();
    in2 = new BufferedReader(new InputStreamReader(sc2.getInputStream()));
    out2 = new PrintWriter(sc2.getOutputStream(), true);

    informarEvento("Jugador 2 aceptado");
  }

  private static void turno(BufferedReader inActivo, PrintWriter outActivo, BufferedReader inPasivo,
      PrintWriter outPasivo) {
    try {

      outActivo.println("TURNO");
      outPasivo.println("NO TURNO");

      recibirJugada(inActivo);
      informarEvento("Coordenadas: " + coordenada1 + " " + coordenada2);

      reenviarJugada(outPasivo);

      recibirCheck(inPasivo);

      informarEvento("check: " + check1 + " " + check2 + " " + check3);

      reenviarCheck(outActivo);

      if (Boolean.parseBoolean(check3)) {
        finJuego();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    if (partida && Boolean.parseBoolean(check1)) {
      turno(inActivo, outActivo, inPasivo, outPasivo);
    }
  }

  private static void finJuego() throws IOException {
    partida = false;

    informarEvento("Fin de la Partida");

    sc1.close();
    informarEvento("Jugador 1 desconectado");

    sc2.close();
    informarEvento("Jugador 2 desconectado");

  }

  private static void reenviarCheck(PrintWriter outJugando) {
    outJugando.println(accion);
    outJugando.println(check1);
    outJugando.println(check2);
    outJugando.println(check3);
  }

  private static void recibirCheck(BufferedReader inEsperando) throws IOException {
    accion = leerCliente(inEsperando);
    check1 = leerCliente(inEsperando);
    check2 = leerCliente(inEsperando);
    check3 = leerCliente(inEsperando);
  }

  private static void reenviarJugada(PrintWriter outEsperando) {
    outEsperando.println(accion);
    outEsperando.println(coordenada1);
    outEsperando.println(coordenada2);
  }

  private static void recibirJugada(BufferedReader inJugando) throws IOException {
    accion = leerCliente(inJugando);
    coordenada1 = leerCliente(inJugando);
    coordenada2 = leerCliente(inJugando);
  }

  private static String leerCliente(BufferedReader in) throws IOException {
    String s = null;
    while (null == (s = in.readLine())) {
    }
    return s;
  }

}
