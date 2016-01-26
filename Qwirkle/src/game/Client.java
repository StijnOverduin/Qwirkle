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
  
  /**
 * Creates a client with the arguments given to it.
 * If the arguments are incorrect it throws an error and exits the program.
 * Otherwise it will read the command line and gives the input to the sendMessage
 * method. 
 * @param args String[] 
 */
  public static void main(String[] args) {
    if (args.length != 2) {
      System.out.println("Not the right number of arguments");
      System.out.println("Try this: <InetArdress> <port>");
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
      System.out.println("Welcome to the lovely game of Qwirkle");
      System.out.println("Type in: HELLO <name> to queue for a game");
      sendIt = true;

      do {
        String input = readString("");
        client.sendMessage(input);
      } while (sendIt);

    } catch (IOException e) {
      System.out.println("ERROR: couldn't reacht the server!");
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
  private boolean inGame;
  private int moveLength;
  private int virtualJar;
  private long thinkingTime;
  private Tui tui;
  

  /**
   * Constructs a new client object.
   * 
   * @param host InetAddress
   * @param port int
   * @throws IOException exception
   */
  public Client(InetAddress host, int port) throws IOException {
    sock = new Socket(host, port);
    in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
    out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
    virtualJar = 108;
    readIt = true;
    sendIt = true;
    tui = new Tui();
    inGame = false;
  }

  /**
   * Reads the output of the server.
   * In the case "WELCOME" it will create a board and a player on the client side. 
   * If the playerName is "Naive" it will create a Naive AI which will make the moves 
   * instead of the LocalPlayer.
   * In the case "NAMES" it will print out who takes part in the game 
   * and adjusts the jar according to the number of players.
   * In the case "NEXT" it will print out "It's your turn!" 
   * if the player number given equals the your player number.
   * If the player currently playing is an instance of Naive 
   * it will determine his move and send it back to the server.
   * In the case "NEW" it will add the given tiles to your hand and will print:
   * "There are no more tiles in the jar left"
   * If the jar is empty. It will also adjust the virtualJar accordingly.
   * In the case "TURN" it will modify the board according to the move that comes after TURN.
   * It will also print the board and the hand of the player for the UI.
   * In the case "KICK" it will print out the message if the LocalPlayer is the one that got kicked,
   * or adjust the virtualJar
   * if another player was kicked.
   * In the case "WINNER" it will print out the output of the server stating who won the game.
   */
  public void run() {
    while (readIt) {
      String input = "";
      try {
        
        input = in.readLine();
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
              tui.displayInput(input);
              tui.waitingForStart();
              break;
            case "NAMES":
              tui.displayInput(input);
              thinkingTime = Long.parseLong(splittedInput[splittedInput.length - 1]);
              for (int i = 0; i < (splittedInput.length - 2) / 3; i++) {
                virtualJar = virtualJar - 6;
              }
              tui.showBoard(board);
              break;
            case "NEXT":
              String number = splittedInput[1];
              if (Integer.parseInt(number) == player.getPlayerNumber()) {
                tui.yourTurn();
                tui.askingForInput();
                if (player instanceof Naive) {
                  out.write(player.determineMove(thinkingTime));
                  out.newLine();
                  out.flush();
                }
              } else {
                tui.showTurn(splittedInput[1]);
              }
              break;
            case "NEW":
              if (!(splittedInput[1].equals("empty"))) {
                for (int i = 1; i < splittedInput.length; i++) {
                  player.addTileToHand(splittedInput[i]);
                  virtualJar = virtualJar - 1;
                }
                tui.displayInput(input);
                tui.showHand(player.getHand());
              } else {
                tui.jarIsEmpty(input);
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
              tui.showBoard(board);
              tui.showHand(player.getHand());
              board.isFirstMoveBecomesFalse();
    
              break;
            case "KICK":
              try {
                if (Integer.parseInt(splittedInput[1]) == player.getPlayerNumber()) {
                  tui.showKick(Integer.parseInt(splittedInput[1]), input.substring(9));
                  return;
                } else {
                  tui.showKick(Integer.parseInt(splittedInput[1]), input.substring(9));
                  virtualJar = virtualJar + Integer.parseInt(splittedInput[2]);
                  break;
                }
              } catch (NumberFormatException e) {
                tui.parseIntError();
              }
              break;
            case "WINNER":
              tui.showWinner(splittedInput[1]);
              System.exit(0);
              break;     
            default:
              tui.serverMessageError();
          }
        } else {
          tui.disconnected();
          readIt = false;
          sock.close();
        }

      } catch (IOException e) {
        tui.closedServer();
        shutDown();
      } catch (NumberFormatException ex) {
        tui.parseIntError();
      }
    }

  }

  /**
   * Returns how many tiles are left in the virtual jar.
   * @return virtualJar
   */
  public int getVirtualJar() {
    return virtualJar;
  }

  /**
   * Reads the message of the message given to the method.
   * In the case of "MOVE" it will first check if the move is valid, if it is it will modify 
   * the board and the hand of the player according to the move, 
   *       and send the message to the server. 
   * If it is not a valid move, it will not send the move to the server,
   * but instead print out that the move is invalid 
   *       and therefore the player should make a different move.
   * In the case of "SWAP" it checks if the virtualJar is empty,
   * if that is the case it will print out that the jar doesn't have the amount of tiles
   *       the player wants to swap. Otherwise it will send the server the message.
   * In the case of "HELLO" it will send the message to the server.
   * In the case of "JAR" it will print how many tiles are left in the virtualJar.
   * In the case of "HINT" it will give you a valid move based on the players hand and the board, 
   *       and "No options left" if you can't make a move.
   * Default this method will print "Not a valid command" 
   *       because the user didn't enter a valid command in the command line.
   * 
   * @param msg String
   */
  //@ requires msg != null;
  public void sendMessage(String msg) {
    try {
      String input = msg;
      String[] splittedInput = input.split(" ");
      switch (splittedInput[0]) {
        case "MOVE":
          if (inGame) {
            if (splittedInput.length > 1) {
              Player deepPlayer = new HumanPlayer(board, "LocalPlayer", 0);
              deepPlayer.getHand().addAll(player.getHand());
              moveLength = splittedInput.length;
              int nrMoves = (moveLength / 3);
              if ((moveLength - 1) % 3 != 0) {
                tui.showInvalidMove();
                break;
              } else {
                for (int i = 0; i < nrMoves; i++) {
                  if (!(player.getHand().contains(splittedInput[(1 + i * 3)]))) {
                    tui.showInvalidMove();
                    return;
                  }
                }
                for (int i = 0; i < nrMoves; i++) {
      
                  String row = splittedInput[2];
                  if (!splittedInput[(2 + i * 3)].equals(row)) {
                    for (int a = 0; a < nrMoves; a++) {
                      String col = splittedInput[3];
                      if (!splittedInput[(3 + a * 3)].equals(col)) {
                        tui.showInvalidMove();
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
                      tui.showInvalidMove();
                      return;
                    }
                  }
      
                } else if (nrMoves == 1) {
                  Color color = Color.getColorFromCharacter(splittedInput[1].charAt(0));
                  Shape shape = Shape.getShapeFromCharacter(splittedInput[1].charAt(1));
                  if (deepBoard.isValidMove(Integer.parseInt(splittedInput[2]),
                      Integer.parseInt(splittedInput[3]), new Tile(color, shape))) {
                    deepBoard.setTile(Integer.parseInt(splittedInput[2]), 
                        Integer.parseInt(splittedInput[3]), new Tile(color, shape));
                    deepPlayer.removeTileFromHand("" + color.getChar() + shape.getChar());
      
                  } else {
                    tui.showInvalidMove();
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
            } else {
              tui.notValidCommand();
            }
            break;
          } else {
            tui.connectFirst();
            break;
          }
        case "SWAP":
        	if (!(board.getIsFirstMove())) {
          if (inGame) {
            if (splittedInput.length > 1) {
              if (virtualJar >= (splittedInput.length - 1)) {
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
                tui.showInvalidMove();
                break;
              }
            } else {
              tui.notValidCommand();
            }
            break;
          } else {
            tui.connectFirst();
            break;
          }
        	} else {
        		tui.invalidFirstMove();
        		break;
        	}
        case "HELLO":
          if (!inGame) {
            if (splittedInput.length > 1 && checkName(splittedInput[1])) {
              out.write(input);
              out.newLine();
              out.flush();
              inGame = true;
            } else {
              tui.notValidName();
              break;
            }
          } else {
            tui.alreadyInGame();
          }
          break;
        case "JAR":
          if (inGame) {
            tui.tilesLeftInJar(virtualJar);
            break;
          } else {
            tui.connectFirst();
            break;
          }
        case "HINT":
          if (inGame) {
            tui.showHint(checkForMoves(player, board));
            break;
          } else {
            tui.connectFirst();
            break;
          }
        case "AITIME":
          if (inGame) {
            thinkingTime = Integer.parseInt(splittedInput[1]);
            tui.showAiTime(splittedInput[1]);
            break;
          } else {
            tui.connectFirst();
            break;
          }
        case "HELP":
          tui.showHelp();
          break;
        case "BOARD":
          if (inGame) {
            tui.showBoard(board);
            break;
          } else {
            tui.connectFirst();
            break;
          }
        case "HAND":
          if (inGame) {
            tui.showHand(player.getHand());
            break;
          } else {
            tui.connectFirst();
            break;
          }
        default:
          tui.notValidCommand();
          break;
      }
      

    } catch (IOException e) {
      tui.closedServer();
    } catch (NumberFormatException ex) {
      tui.parseIntError();
    }
  }
  
  /**
   * Checks if the string is consists of A-Z and a-z 
   *      and if the string is longer than 1 character and shorter than 17 chars.
   * @param name String
   * @return boolean whether the name complies
   */
  //@ requires name != null;
  /*@ pure*/ public boolean checkName(String name) {
    if ( !(name.matches(".*[^a-zA-Z].*")) && name.length() > 0 && name.length() <= 16) { 
      return true;
    } else {
      return false;
    }
  }

  
  /**
   * Reads what the user enters in the command line.
   * @param tekst String
   * @return text typed in the command line
   */
  //@ requires tekst != null;
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
   * @param player Player
   * @param board Board
   * @return String for a possible move for the player at that time
   */
  //@ requires player != null;
  //@ requires board != null;
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
    try {
      sock.close();
      readIt = false;
      sendIt = false;
      System.exit(0);
    } catch (IOException e) {
      System.out.println("Couldn't close the socket");
    }
  }

}
