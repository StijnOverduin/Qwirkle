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
      sendIt = true;

      do {
        String input = readString("");
        client.sendMessage(input);
      } while (sendIt);

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
  private boolean readIt;
  private static boolean sendIt;
  private int moveLength;
  private int virtualJar;
  

  /**
   * Constructs a new client object.
   * 
   * @param host
   * @param port
   * @throws IOException
   */
  public Client(InetAddress host, int port) throws IOException {
    sock = new Socket(host, port);
    in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
    out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
    virtualJar = 108;
    readIt = true;
  }

  /**
   * Reads the output of the server.
   * 
   * In the case "WELCOME" it will create a board and a player on the client side. If the playerName is "Naive"
   * it will create a Naive AI which will make the moves instead of the localplayer.
   * 
   * In the case "NAMES" it will print out who takes part in the game and adjusts the jar according to the number of players.
   * 
   * In the case "NEXT" it will print out "It's your turn!" if the player number given equals the your player number. If the
   * player currently playing is an instance of Naive it will determine his move and send it back to the server.
   * 
   * In the case "NEW" it will add the given tiles to your hand and will print "There are no more tiles in the jar left" if 
   * the jar is empty. It will also adjust the virtualJar accordingly.
   * 
   * In the case "TURN" it will modify the board according to the move that comes after TURN. It will also print the board 
   * and the hand of the player for the UI.
   * 
   * In the case "KICK" it will print out the message if the localplayer is the one that got kicked, or adjust the virtualJar
   * if another player was kicked.
   * 
   * In the case "WINNER" it will simply print out the output of the server stating who won the game.
   */
  public void run() {
    while (readIt) {
      String input = "";
      try {

        input = in.readLine();
        System.out.println("Server -> Client " + input);
        if (input != null) {
        String[] splittedInput = input.split(" ");
        switch (splittedInput[0]) {
          case "WELCOME":
            board = new Board();
            String playerName = splittedInput[1];
            int playerNumber = Integer.parseInt(splittedInput[2]);
            if (playerName.equals("Naive")) {
              player = new Naive(board, playerName, playerNumber);
            } else {
              player = new HumanPlayer(board, playerName, playerNumber);
            }
            System.out.println(input);
            System.out.println(board.toString());
            break;
          case "NAMES":
            System.out.println(input);
            for (int i = 0; i < (splittedInput.length - 2) / 3; i++) {
              virtualJar = virtualJar - 6;
            }
            break;
          case "NEXT":
            String number = splittedInput[1];
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
            if (!(splittedInput[1].equals("empty"))) {
              for (int i = 1; i < splittedInput.length; i++) {
                player.addTileToHand(splittedInput[i]);
                virtualJar = virtualJar - 1;
              }
              System.out.println(player.getHand());
            } else {
              System.out.println(input + " No more tiles in the jar");
            }
            break;
          case "TURN":
            int splitNr = 2;
            int row = 0;
            int col = 0;
            Color color;
            Shape shape;
            Tile tile;
            while (splitNr < splittedInput.length - 2) {
  
              String tiles = splittedInput[splitNr];
              color = Color.getColorFromCharacter(tiles.charAt(0));
              shape = Shape.getShapeFromCharacter(tiles.charAt(1));
              tile = new Tile(color, shape);
              splitNr++;
  
              row = Integer.parseInt(splittedInput[splitNr]);
              splitNr++;
  
              col = Integer.parseInt(splittedInput[splitNr]);
              splitNr++;
  
              player.makeMove(row, col, tile);
  
            }
            System.out.println(board.toString());
            System.out.println(player.getHand());
  
            break;
          case "KICK":
            if (Integer.parseInt(splittedInput[1]) == player.getPlayerNumber()) {
              System.out.println(input);
              return;
            } else {
              System.out.println(input);
              virtualJar = virtualJar + Integer.parseInt(splittedInput[2]);
              System.out.println("Tiles in jar left: " + virtualJar);
              break;
            }
          case "WINNER":
            System.out.println(input);
            break;
          default:
            System.out.println("");
        }
        } else {
          System.out.println("You have been disconnected");
          readIt = false;
        }

      } catch (IOException e) {
        System.out.println("Server has been closed");
        readIt = false;
        try {
        in.close();
        out.close();
        sock.close();
        sendIt = false;
        } catch (IOException ex) {
          System.out.println("Stream couldn't be closed");
        }
      }
    }

  }

  /**
   * Returns how many tiles are left in the virtual jar.
   * @return
   */
  public int getVirtualJar() {
    return virtualJar;
  }

  /**
   * Reads the message of the message given to the method.
   * 
   * In the case of "MOVE" it will first check if the move is valid, if it is it will modify 
   * the board and the hand of the player according to the move, and send the message to the server. If it is not a valid move, it will not send
   * the move to the server, but instead print out that the move is invalid and therefore the player should make a different move.
   * 
   * In the case of "SWAP" it checks if the virtualJar is empty, if that is the case it will print out that the jar doesn't have the amount of tiles
   * the player wants to swap. Otherwise it will send the server the message.
   * 
   * In the case of "HELLO" it will send the message to the server.
   * 
   * In the case of "JAR" it will print how many tiles are left in the virtualJar.
   * 
   * In the case of "HINT" it will give you a valid move based on the players hand and the board, and "No options left" if you can't make a move.
   * 
   * Default this method will print "Not a valid command" because the user didn't enter a valid command in the command line.
   * 
   * @param msg
   */
  public void sendMessage(String msg) {
    try {
      String input = msg;
      String[] splittedInput = input.split(" ");
      switch (splittedInput[0]) {
        case "MOVE":
          Player deepPlayer = new HumanPlayer(board, "LocalPlayer", 0);
          deepPlayer.getHand().addAll(player.getHand());
          moveLength = splittedInput.length;
          int nrMoves = (moveLength / 3);
          if ((moveLength - 1) % 3 != 0) {
            System.out.println("Not a valid move, try again 1");
            break;
          } else {
            for (int i = 0; i < nrMoves; i++) {
              if (!(player.getHand().contains(splittedInput[(1 + i * 3)]))) {
                System.out.println("You don't have that tile 2");
                return;
              }
            }
            for (int i = 0; i < nrMoves; i++) {
  
              String row = splittedInput[2];
              if (!splittedInput[(2 + i * 3)].equals(row)) {
                for (int a = 0; a < nrMoves; a++) {
                  String col = splittedInput[3];
                  if (!splittedInput[(3 + a * 3)].equals(col)) {
                    System.out.println("That was not a valid move, try again 3");
                    return;
  
                  }
  
                }
  
              }
  
            }
            Board deepBoard = board.deepCopy();
            
            if (nrMoves > 1) {
              for (int i = 0; i < nrMoves; i++) {
                Color color = Color.getColorFromCharacter(splittedInput[(1 + i * 3)].charAt(0));
                Shape shape = Shape.getShapeFromCharacter(splittedInput[(1 + i * 3)].charAt(1));
                if (deepBoard.isValidMove(Integer.parseInt(splittedInput[(2 + i * 3)]), 
                    Integer.parseInt(splittedInput[(3 + i * 3)]),
                    new Tile(color, shape))) {
                  deepBoard.setTile(Integer.parseInt(splittedInput[(2 + i * 3)]), 
                      Integer.parseInt(splittedInput[(3 + i * 3)]),
                      new Tile(color, shape));
                  deepPlayer.removeTileFromHand(splittedInput[(1 + i * 3)]);
                  // TODO catch parseint excep
  
                } else {
                  System.out.println("Not a valid move 4");
                  return;
                }
              }
  
            } else if (nrMoves == 1) {
              Color color = Color.getColorFromCharacter(splittedInput[1].charAt(0));
              Shape shape = Shape.getShapeFromCharacter(splittedInput[1].charAt(1));
              if (deepBoard.isValidMove(Integer.parseInt(splittedInput[2]), Integer.parseInt(splittedInput[3]), 
                  new Tile(color, shape))) {
                deepBoard.setTile(Integer.parseInt(splittedInput[2]), 
                    Integer.parseInt(splittedInput[3]), new Tile(color, shape));
                deepPlayer.removeTileFromHand("" + color.getChar() + shape.getChar());
  
              } else {
                System.out.println("Not a valid Move 5");
                return;
              }
  
            }
          }
          player.getHand().removeAll(player.getHand());
          player.getHand().addAll(deepPlayer.getHand());
          out.write(input);
          out.newLine();
          out.flush();
          break;
        case "SWAP":
          if (getVirtualJar() >= (splittedInput.length - 1)) {
            int swapTile = 1;
            for (int i = 0; i < splittedInput.length - 1; i++) {
              if (splittedInput[swapTile] != null) {
                String tile = splittedInput[swapTile];
                swapTile++;
                player.removeTileFromHand(tile);
  
              }
            }
            out.write(input);
            out.newLine();
            out.flush();
            return;
          } else {

            return;
          }
        case "HELLO":
          if (splittedInput[1] != null) {
            out.write(input);
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

  
  /**
   * Reads what the user enters in the command line.
   * @param tekst
   * @return
   */
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
  
  /**
   * This method gives a valid move or no options left if the player can't make any more moves.
   * @param player
   * @param board
   * @return
   */
  public String checkForMoves(Player player, Board board) {
    String move = "";
    int miny = board.getMiny();
    int maxy = board.getMaxy();
    int minx = board.getMinx();
    int maxx = board.getMaxx();
    
    Board deepBoard = board.deepCopy();
    Player deepPlayer = new HumanPlayer(board, player.getName(), player.getPlayerNumber());
    deepPlayer.getHand().addAll(player.getHand());

    Color colorFirstMove = Color.getColorFromCharacter(deepPlayer.getHand().get(0).charAt(0));
    Shape shapeFirstMove = Shape.getShapeFromCharacter(deepPlayer.getHand().get(0).charAt(1));

    if (board.isValidMove(91, 91, new Tile(colorFirstMove, shapeFirstMove))) {
      move = move.concat("Just place a tile on 91 91");
      deepPlayer.removeTileFromHand(deepPlayer.getHand().get(0));
      return move;

    } else {
      for (int i = 0; i < deepPlayer.getHand().size(); i++) {
        String currentTile = deepPlayer.getHand().get(i);
        for (int row = maxy; row <= miny; row++) {
          for (int col = minx; col <= maxx; col++) {
            Color color = Color.getColorFromCharacter(currentTile.charAt(0));
            Shape shape = Shape.getShapeFromCharacter(currentTile.charAt(1));
            if (deepBoard.isValidMove(row, col, new Tile(color, shape))) {
              move = move.concat("" + color.getChar() + shape.getChar() 
              + " " + row + " " + col + " You could place that tile");
              deepPlayer.removeTileFromHand("" + color.getChar() + shape.getChar());
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
  
  /**
   * Shuts down the client.
   */
  public void shutDown() {
    readIt = false;
    System.exit(0);
  }

}
