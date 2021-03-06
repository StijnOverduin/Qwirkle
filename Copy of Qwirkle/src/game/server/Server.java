package game.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Server {

  private int port;
  private List<Game> games;

  /** Constructs a new Server object. */
  public Server(int portArg) {
    games = new ArrayList<Game>();
    games.add(new Game());
    port = portArg;
  }

  public void run() {
    try {
      Game game = null;
      ServerSocket serverSock = new ServerSocket(port);
      while (true) {
        Socket sock = serverSock.accept();
        if (games.get(games.size() - 1).isActive()) {
          game = new Game();
          games.add(game);
        } else {
          game = games.get(games.size() - 1);
        }
        // create new thread to handle
        ClientHandler client = new ClientHandler(sock, game);
        game.addHandler(client);
        client.start();

      }

    } catch (IOException e) {
      System.out.println("No more players on the server");

    }

  }

  /** Start een Server-applicatie op. */
  public static void main(String[] args) {
    if (args.length != 1) {
      System.out.println("Not the right number of arguments, try this: <port>");
      System.exit(0);
    }

    Server server = new Server(Integer.parseInt(args[0]));
    new Thread(() -> server.readTerminalInput()).start();
    server.run();

  }

  private void readTerminalInput() {
    while (true) {

      Scanner input = new Scanner(System.in);
      if (input.hasNextLine()) {
        String start = input.next();
        if (start.equals("START")) {
          games.get(games.size() - 1).startGame();
        }
      }
    }
  }
}
