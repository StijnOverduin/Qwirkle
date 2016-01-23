package game;

import game.player.HumanPlayer;
import game.player.Naive;
import game.player.Player;
import game.tiles.Color;
import game.tiles.Shape;
import game.tiles.Tile;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;



public class Client extends Thread {

  public static void main(String[] args) {
    if (args.length != 2) {
      System.out.println("Not the right number of arguments");
      System.exit(0);
    }

    InetAddress host = null;
    int port = 0;

    try {
      host = InetAddress.getByName(args[0]);
    } catch (UnknownHostException e) {
      System.out.println("ERROR: no valid hostname!");
      System.exit(0);
    }

    try {
      port = Integer.parseInt(args[1]);
    } catch (NumberFormatException e) {
      System.out.println("ERROR: no valid portnummer!");
      System.exit(0);
    }

    try {
      Client client = new Client(host, port);
      client.start();

      do {
        String input = readString("");
        client.sendMessage(input);
      } while (true);

    } catch (IOException e) {
      e.printStackTrace();
      System.out.println("ERROR: couldn't construct a client object!");
      System.exit(0);
    }

  }

  private Socket sock;
  private BufferedReader in;
  private BufferedWriter out;
  private Player player;
  private Board board;
  private Boolean readIt = true;
  private int lengteMove;
  private int virtualJar;

  public Client(InetAddress host, int port) throws IOException {
    sock = new Socket(host, port);
    in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
    out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
    virtualJar = 108;
  }

  public void run() {
    while (readIt) {
      String line = "";
      try {

        line = in.readLine();
        System.out.println("Server -> Client " + line);
        if (line != null) {
        String[] split = line.split(" ");
        switch (split[0]) {
          case "WELCOME":
            board = new Board();
            String playerName = split[1];
            int playerNumber = Integer.parseInt(split[2]);
            if (playerName.equals("Naive")) {
              player = new Naive(board, playerName, playerNumber);
            } else {
              player = new HumanPlayer(board, playerName, playerNumber);
            }
            System.out.println(line);
            System.out.println(board.toString());
            break;
          case "NAMES":
            System.out.println(line);
            for (int i = 0; i < (split.length - 2) / 3; i++) {
              virtualJar = virtualJar - 6;
            }
            break;
          case "NEXT":
            String number = split[1];
            if (Integer.parseInt(number) == player.getPlayerNumber()) {
              System.out.println("It's your turn!");
              if (player instanceof Naive) {
                out.write(player.determineMove());
                out.newLine();
                out.flush();
              }
            }
            break;
          case "NEW":
            if (!(split[1].equals("empty"))) {
              for (int i = 1; i < split.length; i++) {
                player.addTilesToHand(split[i]);
                virtualJar = virtualJar - 1;
              }
              System.out.println(player.getHand());
            } else {
              System.out.println(line + " No more tiles in the jar");
            }
            break;
          case "TURN":
            int qq = 2;
            int rij = 0;
            int colom = 0;
            Color color;
            Shape shape;
            Tile tile;
            while (qq < split.length - 2) {
  
              String line1 = split[qq];
              color = Color.getColorFromCharacter(line1.charAt(0));
              shape = Shape.getShapeFromCharacter(line1.charAt(1));
              tile = new Tile(color, shape);
              qq++;
  
              rij = Integer.parseInt(split[qq]);
              qq++;
  
              colom = Integer.parseInt(split[qq]);
              qq++;
  
              player.makeMove(rij, colom, tile);
  
            }
            System.out.println(board.toString());
            System.out.println(player.getHand());
  
            break;
          case "KICK":
            if (Integer.parseInt(split[1]) == player.getPlayerNumber()) {
              System.out.println(line);
              return;
            } else {
              System.out.println(line);
              virtualJar = virtualJar + Integer.parseInt(split[2]);
              System.out.println("Tiles in jar left: " + virtualJar);
              break;
            }
          case "WINNER":
            System.out.println(line);
            board = new Board();
            virtualJar = 108;
            break;
          default:
            System.out.println("");
        }
        } else {
          System.out.println("You have been disconnected");
          readIt = false;
        }

      } catch (IOException e) {
        System.out.println("You were kicked son!");
        readIt = false;
      }
    }

  }

  public int getVirtualJar() {
    return virtualJar;
  }

  /** send a message to the ClientHandler. */
  public void sendMessage(String msg) {
    try {
      String message = msg;
      String[] split = message.split(" ");
      switch (split[0]) {
        case "MOVE":
          Player deepplayer = new HumanPlayer(board, "LocalPlayer", 0);
          deepplayer.getHand().addAll(player.getHand());
          lengteMove = split.length;
          int maalMoves = (lengteMove / 3);
          if ((lengteMove - 1) % 3 != 0) {
            System.out.println("Not a valid move, try again 1");
            break;
          } else {
            for (int i = 0; i < maalMoves; i++) {
              if (!(player.getHand().contains(split[(1 + i * 3)]))) {
                System.out.println("You don't have that tile 2");
                return;
              }
            }
            for (int i = 0; i < maalMoves; i++) {
  
              String row = split[2];
              if (!split[(2 + i * 3)].equals(row)) {
                for (int a = 0; a < maalMoves; a++) {
                  String col = split[3];
                  if (!split[(3 + a * 3)].equals(col)) {
                    System.out.println("That was not a valid move, try again 3");
                    return;
  
                  }
  
                }
  
              }
  
            }
            Board deepboard = board.deepCopy();
            
            if (maalMoves > 1) {
              for (int i = 0; i < maalMoves; i++) {
                Color color = Color.getColorFromCharacter(split[(1 + i * 3)].charAt(0));
                Shape shape = Shape.getShapeFromCharacter(split[(1 + i * 3)].charAt(1));
                if (deepboard.isValidMove(Integer.parseInt(split[(2 + i * 3)]), 
                    Integer.parseInt(split[(3 + i * 3)]),
                    new Tile(color, shape))) {
                  deepboard.setTile(Integer.parseInt(split[(2 + i * 3)]), 
                      Integer.parseInt(split[(3 + i * 3)]),
                      new Tile(color, shape));
                  deepplayer.removeTileFromHand(split[(1 + i * 3)]);
                  // TODO catch parseint excep
  
                } else {
                  System.out.println("Not a valid move 4");
                  return;
                }
              }
  
            } else if (maalMoves == 1) {
              Color color = Color.getColorFromCharacter(split[1].charAt(0));
              Shape shape = Shape.getShapeFromCharacter(split[1].charAt(1));
              if (deepboard.isValidMove(Integer.parseInt(split[2]), Integer.parseInt(split[3]), 
                  new Tile(color, shape))) {
                deepboard.setTile(Integer.parseInt(split[2]), 
                    Integer.parseInt(split[3]), new Tile(color, shape));
                deepplayer.removeTileFromHand("" + color.getChar() + shape.getChar());
  
              } else {
                System.out.println("Not a valid Move 5");
                return;
              }
  
            }
          }
          player.getHand().addAll(deepplayer.getHand());
          out.write(message);
          out.newLine();
          out.flush();
          break;
        case "SWAP":
          if (getVirtualJar() >= (split.length - 1)) {
            int swaptile = 1;
            for (int i = 0; i < split.length - 1; i++) {
              if (split[swaptile] != null) {
                String tile = split[swaptile];
                swaptile++;
                player.removeTileFromHand(tile);
  
              }
            }
            out.write(message);
            out.newLine();
            out.flush();
            return;
          } else {

            return;
          }
        case "HELLO":
          if (split[1] != null) {
            out.write(message);
            out.newLine();
            out.flush();
          }
          break;
        case "JAR":
          System.out.println("Jar has " + getVirtualJar() + " tiles left");
          break;
        case "HINT":
          System.out.println(checkForMoves(player, board));
          break;
        default:
          System.out.println("That's not a valid command");
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static String readString(String tekst) {
    System.out.print(tekst);
    String antw = null;
    try {
      BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
      antw = in.readLine();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return (antw == null) ? "" : antw;
  }
  
  public String checkForMoves(Player player, Board board) {
    String move = "";
    int miny = board.getMiny();
    int maxy = board.getMaxy();
    int minx = board.getMinx();
    int maxx = board.getMaxx();
    
    Board b = board.deepCopy();
    Player newPlayer = new HumanPlayer(board, player.getName(), player.getPlayerNumber());
    newPlayer.getHand().addAll(player.getHand());

    Color color1 = Color.getColorFromCharacter(newPlayer.getHand().get(0).charAt(0));
    Shape shape1 = Shape.getShapeFromCharacter(newPlayer.getHand().get(0).charAt(1));

    if (board.isValidMove(91, 91, new Tile(color1, shape1))) {
      move = move.concat("Just place a tile on 91 91");
      newPlayer.removeTileFromHand(newPlayer.getHand().get(0));
      return move;

    } else {
      for (int i = 0; i < newPlayer.getHand().size(); i++) {
        String currentTile = newPlayer.getHand().get(i);
        for (int row = maxy; row <= miny; row++) {
          for (int col = minx; col <= maxx; col++) {
            Color color = Color.getColorFromCharacter(currentTile.charAt(0));
            Shape shape = Shape.getShapeFromCharacter(currentTile.charAt(1));
            if (b.isValidMove(row, col, new Tile(color, shape))) {
              move = move.concat(color.getChar() + shape.getChar() 
              + " " + row + " " + col + " You could place that tile");
              newPlayer.removeTileFromHand("" + color.getChar() + shape.getChar());
              return move;
            }
          }
        }
      }
      if (virtualJar != 0) {
      move = move.concat("Try swapping a tile"); 
      } else {
        move = move.concat("No options left");
      }
      return move;
    }
  }

}
