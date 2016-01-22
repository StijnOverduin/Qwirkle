package game.server;

import game.Board;
import game.PlayerWrapper;
import game.player.HumanPlayer;
import game.player.Player;
import game.tiles.Color;
import game.tiles.Shape;
import game.tiles.Tile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



public class Game {

  private ArrayList<String> jar;
  private Color[] color = Color.values();
  private Shape[] shape = Shape.values();
  private List<PlayerWrapper> players;
  private Board board;
  private boolean active = false;
  private int lengteMove;
  private int turn;
  private int numberOfPlayers;
  private List<ClientHandler> threads;
  private boolean horizontalTrue;
  private int addToScore;
  private boolean heeftTilesErnaast;

  public Game() {
    threads = new ArrayList<ClientHandler>();
    players = new ArrayList<PlayerWrapper>();
    board = new Board();
    jar = new ArrayList<String>();

  }

  private void kickHandler(ClientHandler client, String message) {
    if (threads.size() != 0) {
      broadcast("KICK " + players.get(turn).getPlayerNumber() + " "
          + players.get(turn).getPlayer().numberOfTilesInHand() + " " + message);
      tilesBackToStack(players.get(turn).getPlayer());
      players.remove(players.get(turn));
      threads.remove(client);
      updateTurn();
      try {
        client.kick();
      } catch (IOException e) {
        System.out.println("Couldn't close streams from client");
      }
    } else {
      client.sendMessage("KICK " + message);
      tilesBackToStack(players.get(turn).getPlayer());
      players.remove(players.get(turn));
      threads.remove(client);
      try {
        client.kick();
      } catch (IOException e) {
        System.out.println("Last client was removed from threads");
      }
      System.out.println("Kicked the last Player");
    }
  }

  public void updateTurn() {
    if (players.size() != 0) {
      numberOfPlayers = players.size();
      turn = (turn + 1) % numberOfPlayers;
      broadcast("NEXT " + players.get(turn).getPlayerNumber());
    } else {
      System.out.println("No more players in the game");
    }
  }

  public void readInput(String msg, ClientHandler client) {
    String input = msg;
    String[] split = msg.split(" ");
    switch (split[0]) { //TODO default case
      case "HELLO":
        client.sendMessage("WELCOME " + split[1] + " " + client.getClientNumber());
        Player player = new HumanPlayer(board, split[1], client.getClientNumber());
        fillWrapper(client, player);
        if (players.size() == 4) {
          startGame();
        }
        break;

      case "MOVE":
        if (client.getClientNumber() == turn) {
          System.out.println(turn);
          lengteMove = split.length;
          int maalMoves = (lengteMove / 3);
          if ((lengteMove - 1) % 3 != 0) {
            kickHandler(client,
                "KICK " + players.get(turn).getPlayerNumber() + " " 
                + players.get(turn).getPlayer().numberOfTilesInHand()
                  + " You were kicked, maybe you forgot a tile or a coord?");
            break;
          } else {
            System.out.println(turn);
            System.out.println(players.get(turn).getPlayerNumber());
            for (int i = 0; i < maalMoves; i++) {
              if (!(players.get(turn).getPlayer().getHand().contains(split[(1 + i * 3)]))) {
                kickHandler(client, "Tile " + split[(1 + i * 3)] + " not in possession");
                break;
              }
            }
            for (int i = 0; i < maalMoves; i++) {

              String row = split[2];
              if (!split[(2 + i * 3)].equals(row)) {
                for (int a = 0; a < maalMoves; a++) {
                  String col = split[3];
                  if (!split[(3 + a * 3)].equals(col)) {
                    kickHandler(client, "You were kicked, tiles are not in a straight line");
                    break;

                  }

                }

              }

            }
            Board deepboard = board.deepCopy();

            String newTiles = "NEW";
            if (maalMoves > 1) {
              this.horizontalTrue = (split[2] == split[5]);
              this.addToScore = 0;                          
              for (int i = 0; i < maalMoves; i++) {
                Color color = Color.getColorFromCharacter(split[(1 + i * 3)].charAt(0));
                Shape shape = Shape.getShapeFromCharacter(split[(1 + i * 3)].charAt(1));
                if (deepboard.isValidMove(Integer.parseInt(split[(2 + i * 3)]), 
                    Integer.parseInt(split[(3 + i * 3)]), new Tile(color, shape))) {
                  deepboard.setTile(Integer.parseInt(split[(2 + i * 3)]), 
                      Integer.parseInt(split[(3 + i * 3)]), new Tile(color, shape));
                  players.get(turn).getPlayer().removeTileFromHand(split[(1 + i * 3)]);
                  String randomTile = giveRandomTile();
                  newTiles = newTiles.concat(" " + randomTile);
                  players.get(turn).getPlayer().addTilesToHand(randomTile);

                  // TODO catch parseint excep
                  players.get(turn).setScore(calcScoreCrossedTiles(split, i) 
                      + players.get(turn).getScore());
                  System.out.println(players.get(turn).getScore());

                } else {
                  kickHandler(client, "Not a valid move");
                  break;
                }
              }
              players.get(turn).setScore(calcScoreAddedTiles(split) + players.get(turn).getScore());

            } else if (maalMoves == 1) {
              Color color = Color.getColorFromCharacter(split[1].charAt(0));
              Shape shape = Shape.getShapeFromCharacter(split[1].charAt(1));
              if (deepboard.isValidMove(Integer.parseInt(split[2]), 
                  Integer.parseInt(split[3]), new Tile(color, shape))) {
                deepboard.setTile(Integer.parseInt(split[2]), 
                    Integer.parseInt(split[3]), new Tile(color, shape));

                players.get(turn).getPlayer().removeTileFromHand(split[1]);
                String randomTile = giveRandomTile();
                newTiles = newTiles.concat(" " + randomTile);
                players.get(turn).getPlayer().addTilesToHand(randomTile);
                players.get(turn).setScore(calcScoreHorAndVer(split) 
                    + players.get(turn).getScore());
              } else {
                kickHandler(client, "Not a valid move");
                break;
              }

            }

            board = deepboard;
            client.sendMessage(newTiles);
            broadcast("TURN " + players.get(turn).getPlayerNumber() + " " + input.substring(5));
            updateTurn();
          }
          System.out.println(players.get(turn).getScore());
          System.out.println(players.get(turn).getPlayer().getHand());
          break;
        } else {
          kickHandler(client, "It was not your turn!");
          break;
        }
      case "SWAP":
        if (client.getClientNumber() == turn) {
          int swapnr = 1;
          String tiles = "";
          while (swapnr < split.length) {
            if (tilesInJar() != 0) {
              String line1 = split[swapnr];
              if (players.get(turn).getPlayer().getHand().contains(line1)) {
                players.get(turn).getPlayer().removeTileFromHand(line1);
              } else {
                kickHandler(client, "Tile " + line1 + " was not in your hand");
                break;
              }
              String randomTile = giveRandomTile();
              players.get(turn).getPlayer().addTilesToHand(randomTile);
              tiles = tiles.concat(" " + randomTile);
              swapnr++;
            } else {
              kickHandler(client, "Tried to swap while jar was empty");
              break;
            }
          }
          client.sendMessage("NEW" + tiles);
          broadcast("TURN empty");
          updateTurn();
        } else {
          kickHandler(client, "It was not your turn!");
          break;
        }
      default:
        System.out.println("Not valid input");
        break;
    }
  }

  public int calcScoreCrossedTiles(String[] split, int index) {
    int dx = horizontalTrue ? 1 : 0; // als het een horizontale rij is dan
    // gaat hij verticaal checken elke
    // keer
    int dy = dx == 1 ? 0 : 1;
    int row = Integer.parseInt(split[2 + 3 * index]);
    int col = Integer.parseInt(split[3 + 3 * index]);
    this.heeftTilesErnaast = false;
    int dupRow = row;
    int dupCol = col;

    int lengteLijn = 0;
    while (!board.deepCopy().isEmpty(row + dx, col + dy)) {
      lengteLijn++;
      row += dx;
      col += dy;
      this.heeftTilesErnaast = true;
    } 
    row = dupRow;
    col = dupCol;
    while (!board.deepCopy().isEmpty(row - dx, col - dy)) {
      lengteLijn++;
      row -= dx;
      col -= dy;
      this.heeftTilesErnaast = true;
    }
    this.addToScore = heeftTilesErnaast ? addToScore + lengteLijn + 1 : addToScore + lengteLijn;
    return addToScore;
  }

  public int calcScoreAddedTiles(String[] split) {
    addToScore = 0;
    int dx = horizontalTrue ? 0 : 1; // als het een horizontale rij is dan
    // gaat hij verticaal checken elke
    // keer
    int dy = dx == 1 ? 0 : 1;
    int row = Integer.parseInt(split[2]);
    int col = Integer.parseInt(split[3]);
    int dupRow = row;
    int dupCol = col;

    int lengteLijn = 0;
    while (!board.deepCopy().isEmpty(row + dx, col + dy)) {
      lengteLijn++;
      row += dx;
      col += dy;
    }
    row = dupRow;
    col = dupCol;
    while (!board.deepCopy().isEmpty(row - dx, col - dy)) {
      lengteLijn++;
      row -= dx;
      col -= dy;
    }
    
    return (addToScore + lengteLijn + 1);
  }

  public int calcScoreHorAndVer(String[] split) {
    if (board.getIsFirstMove() == true) {
      addToScore = 1;
    } else {
      int dx = 1;
      int dy = 0;
      int row = Integer.parseInt(split[2]);
      int col = Integer.parseInt(split[3]);
      int dupRow = row;
      int dupCol = col;
  
      int lengteLijn = 0;
      while (!board.deepCopy().isEmpty(row + dx, col + dy)) {
        lengteLijn++;
        row += dx;
        col += dy;
      }
      row = dupRow;
      col = dupCol;
      while (!board.deepCopy().isEmpty(row - dx, col - dy)) {
        lengteLijn++;
        row -= dx;
        col -= dy;
      }
      dx = 0;
      dy = 1;
      row = Integer.parseInt(split[2]);
      col = Integer.parseInt(split[3]);
      row = dupRow;
      col = dupCol;
  
      while (!board.deepCopy().isEmpty(row + dx, col + dy)) {
        lengteLijn++;
        row += dx;
        col += dy;
      }
      row = dupRow;
      col = dupCol;
      while (!board.deepCopy().isEmpty(row - dx, col - dy)) {
        lengteLijn++;
        row -= dx;
        col -= dy;
      }
  
      addToScore = lengteLijn;
    }
    return addToScore;
  }

  public void broadcast(String msg) {
    if (threads.size() != 0) {
      for (int i = 0; i < threads.size(); i++) {
        threads.get(i).sendMessage(msg);
      }
    } else {
      System.out.println("No more players in this game to broadcast to");
    }
  }

  private void tilesBackToStack(Player player) {
    for (int q = 0; q < player.getHand().size(); q++) {
      player.removeTileFromHand(player.getHand().get(q));
      addTileToJar(player.getHand().get(q));
    }
  }

  public void removePlayerWrapperFromList(PlayerWrapper player) {
    if (players.contains(player)) {
      players.remove(player);
    }
  }

  public void fillWrapper(ClientHandler client, Player player) {
    players.get(client.getClientNumber()).setPlayer(player);
    players.get(client.getClientNumber()).setScore(0);
    players.get(client.getClientNumber()).setPlayerNumber(client.getClientNumber());
  }

  /*
   * Dit is een beschrijving voor de pot met tegeltjes.
   */
  public void fillJar() {
    Shape ffShape = null;
    Color ffColor = null;
    for (int i = 0; i < 3; i++) {
      for (int q = 0; q < color.length - 1; q++) {
        ffColor = color[q];
        for (int w = 0; w < shape.length - 1; w++) {
          ffShape = shape[w];
          String tile = "" + ffColor.getChar() + ffShape.getChar();
          addTileToJar(tile);
        }
      }

    }
  }

  public void removeTileFromJar(String tile) {
    jar.remove(tile);
  }

  public void addTileToJar(String tile) {
    jar.add(tile);
  }

  public int tilesInJar() {
    return jar.size();
  }

  public String giveRandomTile() {
    if (jar.size() != 0) {
      int random = (int) Math.round(Math.random() * (jar.size() - 1));
      String newTile = jar.get(random);
      jar.remove(newTile);
      return newTile;
    } else {
      return null;
    }
  }

  public boolean isActive() {
    return active;
  }

  public void becomeActive() {
    active = true;
  }

  public void startGame() {
    becomeActive();
    fillJar();
    String names = "";
    for (int r = 0; r < players.size(); r++) {
      names = names.concat(" " + players.get(r).getPlayer().getName() 
          + " " + players.get(r).getPlayerNumber());
    }
    broadcast("NAMES" + names + " " + 100);
    for (int t = 0; t < players.size(); t++) {
      String tiles = "";
      for (int q = 0; q < 6; q++) {
        String randomTile = giveRandomTile();
        tiles = tiles.concat(" " + randomTile);

      }
      threads.get(t).sendMessage("NEW" + tiles);
      String[] split = tiles.split(" ");
      for (int w = 1; w < 7; w++) {
        players.get(t).getPlayer().addTilesToHand(split[w]);
      }
    }
    turn = 0;
    broadcast("NEXT " + players.get(turn).getPlayerNumber());
  }

  public void addHandler(ClientHandler client) {
    client.setClientNumber(threads.size());
    threads.add(client);
    PlayerWrapper playerWrapper = new PlayerWrapper();
    players.add(playerWrapper);
  }

  public void removeHandler(ClientHandler handler) {
    for (int i = 0; i < threads.size(); i++) {
      if (threads.get(i) == handler) {
        threads.remove(i);
      }
    }
  }

}
