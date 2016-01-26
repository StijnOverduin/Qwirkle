package game.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class ClientHandler extends Thread {
  private BufferedReader in;
  private BufferedWriter out;
  private Game game;
  private int clientNumber;
  private Socket sockArg;
  private boolean running;

  /**
   * Constructs a ClientHandler object Initializes both Data streams.
   */
  // @ requires serverArg != null && sockArg != null;
  public ClientHandler(Socket sockArg, Game game) throws IOException {
    running = true;
    in = new BufferedReader(new InputStreamReader(sockArg.getInputStream()));
    out = new BufferedWriter(new OutputStreamWriter(sockArg.getOutputStream()));
    this.game = game;
    this.sockArg = sockArg;
    
  }

  public void setClientNumber(int clientnr) {
    clientNumber = clientnr;
  }

  public int getClientNumber() {
    return clientNumber;
  }

  /**
   * This method takes care of sending messages from the Client. If an IOException is
   * thrown while reading the message, the method concludes that the socket
   * connection is broken and shutdown() will be called.
   */
  public void run() {
    try {
      while (running) {
        String input = in.readLine();
        game.readInput(input, this);
      }

    } catch (IOException e) {
      game.broadcast("KICK " + getClientNumber() + " " 
      + game.getPlayers().get(getClientNumber()).getPlayer().getHand().size() 
      + " has lost connection");
      game.tilesBackToStack(game.getPlayers().get(getClientNumber()).getPlayer());
      System.out.println("Client " + getClientNumber() + " disconnected");
      shutDown();
      game.updateTurn();
      
    }
  }

  /**
   * This method can be used to send a message over the socket connection to the Client.
   */
  public void sendMessage(String msg) {
    try {
      out.write(msg);
      out.newLine();
      out.flush();

    } catch (IOException e) {
      System.out.println("Client stream is closed");
    }
  }
  /**
   * Shuts down the ClientHandler if player is kicked.
   */
  public void shutDown() {
    try {
      running = false;
      sockArg.close();
      game.removeHandler(this);
    } catch (IOException e) {
      System.out.println("Can't shutdown this client");
    }
  }

}
