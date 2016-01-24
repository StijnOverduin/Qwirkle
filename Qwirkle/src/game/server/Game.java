package game.server;

import game.Board;
import game.player.HumanPlayer;
import game.player.Player;
import game.player.PlayerWrapper;
import game.tiles.Color;
import game.tiles.Shape;
import game.tiles.Tile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Game {

  private List<String> jar;
  private List<ClientHandler> threads;
  private List<PlayerWrapper> players;
  private Color[] allColors = Color.values();
  private Shape[] allShapes = Shape.values();
  private Board board;
  private int lengteMove;
  private int turn;
  private int numberOfPlayers;
  private boolean activeGame;
  private boolean horizontalTrue;
  private boolean gotTileNextToIt;
  private boolean nextPlayer;

  public Game() {
    threads = new ArrayList<ClientHandler>();
    players = new ArrayList<PlayerWrapper>();
    board = new Board();
    jar = new ArrayList<String>();
    activeGame = false;

  }

  private void kickHandler(ClientHandler client, String message) {
    if (players.get(turn).getPlayer().getHand().isEmpty()) {
      if (threads.size() > 1) {
        
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
  }

    public void updateTurn() {
      if (players.size() > 0 && !(checkForMoves(players.get(turn).getPlayer(), board).equals("No options left"))) {
      numberOfPlayers = players.size();
      turn = nextPlayer ? turn : (turn + 1) % numberOfPlayers;
      nextPlayer = false;
      broadcast("NEXT " + players.get(turn).getPlayerNumber());
      
        } else {
            if (checkForMoves(players.get(turn).getPlayer(), board).equals("No options left")) {
              broadcast("Player " + players.get(turn).getPlayerNumber() + " couldn't make a move");
              if (!checkAllPlayers()) {
                endGame();
              } else {
                updateTurn();
              }
            } else {
                System.out.println("No more players in the game");
            }
        }
        
      }
  
  public boolean checkAllPlayers() {
    for (int i = 0; i < players.size(); i++) {
      if (!(checkForMoves(players.get(i).getPlayer(), board).equals("No options left"))) {
        turn = (turn + 1) % numberOfPlayers;
        nextPlayer = true;
        return true;
      }
    }
    return false;
  }

  public void readInput(String message, ClientHandler client) {
    String input = message;
    String[] splittedInput = message.split(" ");
    switch (splittedInput[0]) { //TODO default case
      case "HELLO":
        client.sendMessage("WELCOME " + splittedInput[1] + " " + client.getClientNumber());
        Player player = new HumanPlayer(board, splittedInput[1], client.getClientNumber());
        fillWrapper(client, player);
        if (players.size() == 4) {
          startGame();
        }
        
        break;

      case "MOVE":
        if (client.getClientNumber() == turn) {
          lengteMove = splittedInput.length;
          int nrMoves = (lengteMove / 3);
          if ((lengteMove - 1) % 3 != 0) {
            kickHandler(client,
                "KICK " + players.get(turn).getPlayerNumber() + " " 
                + players.get(turn).getPlayer().numberOfTilesInHand()
                  + " You were kicked, maybe you forgot a tile or a coord?");
            return;
          } else {
            for (int i = 0; i < nrMoves; i++) {
              if (!(players.get(turn).getPlayer().getHand().contains(splittedInput[(1 + i * 3)]))) {
                kickHandler(client, "Tile " + splittedInput[(1 + i * 3)] + " not in possession");
                return;
              }
            }
            for (int i = 0; i < nrMoves; i++) {

              String row = splittedInput[2];
              if (!splittedInput[(2 + i * 3)].equals(row)) {
                for (int a = 0; a < nrMoves; a++) {
                  String col = splittedInput[3];
                  if (!splittedInput[(3 + a * 3)].equals(col)) {
                    kickHandler(client, "You were kicked, tiles are not in a straight line");
                    return;

                  }

                }

              }

            }
            Board deepBoard = board.deepCopy();

            String newTiles = "NEW";
            if (nrMoves > 1) {
              this.horizontalTrue = (splittedInput[2] == splittedInput[5]);                        
              for (int i = 0; i < nrMoves; i++) {
                Color color = Color.getColorFromCharacter(splittedInput[(1 + i * 3)].charAt(0));
                Shape shape = Shape.getShapeFromCharacter(splittedInput[(1 + i * 3)].charAt(1));
                if (deepBoard.isValidMove(Integer.parseInt(splittedInput[(2 + i * 3)]), 
                    Integer.parseInt(splittedInput[(3 + i * 3)]), new Tile(color, shape))) {
                  deepBoard.setTile(Integer.parseInt(splittedInput[(2 + i * 3)]), 
                      Integer.parseInt(splittedInput[(3 + i * 3)]), new Tile(color, shape));
                  players.get(turn).getPlayer().removeTileFromHand(splittedInput[(1 + i * 3)]);
                  String randomTile = giveRandomTile();
                  newTiles = newTiles.concat(" " + randomTile);
                  players.get(turn).getPlayer().addTileToHand(randomTile);

                  // TODO catch parseint excep
                  players.get(turn).setScore(calcScoreCrossedTiles(splittedInput, i, deepBoard) 
                      + players.get(turn).getScore());

                } else {
                  kickHandler(client, "Not a valid move");
                  return;
                }
              }
              players.get(turn).setScore(calcScoreAddedTiles(splittedInput, deepBoard) + players.get(turn).getScore());

            } else if (nrMoves == 1) {
              Color color = Color.getColorFromCharacter(splittedInput[1].charAt(0));
              Shape shape = Shape.getShapeFromCharacter(splittedInput[1].charAt(1));
              if (deepBoard.isValidMove(Integer.parseInt(splittedInput[2]), 
                  Integer.parseInt(splittedInput[3]), new Tile(color, shape))) {
                deepBoard.setTile(Integer.parseInt(splittedInput[2]), 
                    Integer.parseInt(splittedInput[3]), new Tile(color, shape));

                players.get(turn).getPlayer().removeTileFromHand(splittedInput[1]);
                String randomTile = giveRandomTile();
                newTiles = newTiles.concat(" " + randomTile);
                players.get(turn).getPlayer().addTileToHand(randomTile);
                players.get(turn).setScore(calcScoreHorAndVer(splittedInput, deepBoard) 
                    + players.get(turn).getScore());
              } else {
                kickHandler(client, "Not a valid move");
                return;
              }

            }

            board = deepBoard;
            if (newTiles.contains("empty")) {
              client.sendMessage("NEW empty");
            } else {
              client.sendMessage(newTiles);
            }
            broadcast("TURN " + players.get(turn).getPlayerNumber() + " " + input.substring(5));
            updateTurn();
          }
          break;
        } else {
          kickHandler(client, "It was not your turn!");
          return;
        }
      case "SWAP":
        if (client.getClientNumber() == turn) {
          int tilenr = 1;
          String newTiles = "";
          while (tilenr < splittedInput.length) {
            if (tilesInJar() >= (splittedInput.length - 1)) {
              String tile = splittedInput[tilenr];
              if (players.get(turn).getPlayer().getHand().contains(tile)) {
                players.get(turn).getPlayer().removeTileFromHand(tile);
              } else {
                kickHandler(client, "Tile " + tile + " was not in your hand");
                return;
              }
              String randomTile = giveRandomTile();
              players.get(turn).getPlayer().addTileToHand(randomTile);
              newTiles = newTiles.concat(" " + randomTile);
              tilenr++;
            } else {
              kickHandler(client, "Tried to swap while jar was empty");
              return;
            }
          }
          client.sendMessage("NEW" + newTiles);
          broadcast("TURN empty");
          updateTurn();
        } else {
          kickHandler(client, "It was not your turn!");
          return;
        }
      default:
        kickHandler(client, "That was not a valid command");
        break;
    }
  }

  public int calcScoreCrossedTiles(String[] split, int index, Board deepBoard) {
    int dx = horizontalTrue ? 0 : 1; // als het een horizontale rij is dan
    // gaat hij verticaal checken elke
    // keer
    int dy = dx == 1 ? 0 : 1;
    int row = Integer.parseInt(split[2 + 3 * index]);
    int col = Integer.parseInt(split[3 + 3 * index]);
    this.gotTileNextToIt = false;
    int dupRow = row;
    int dupCol = col;

    int lineLength = 0;
    while (!deepBoard.isEmpty(row + dx, col + dy)) {
      lineLength++;
      row += dx;
      col += dy;
      this.gotTileNextToIt = true;
    } 
    row = dupRow;
    col = dupCol;
    while (!deepBoard.isEmpty(row - dx, col - dy)) {
      lineLength++;
      row -= dx;
      col -= dy;
      this.gotTileNextToIt = true;
    }
    int addToScore = 0;
    addToScore = gotTileNextToIt ? lineLength + 1 : 0;
    return addToScore;
  }

  public int calcScoreAddedTiles(String[] split, Board deepBoard) {
    int dx = horizontalTrue ? 1 : 0; // als het een horizontale rij is dan
    // gaat hij verticaal checken elke
    // keer
    int dy = dx == 1 ? 0 : 1;
    int row = Integer.parseInt(split[2]);
    int col = Integer.parseInt(split[3]);
    int dupRow = row;
    int dupCol = col;

    int lengteLijn = 0;
    while (!deepBoard.isEmpty(row + dx, col + dy)) {
      lengteLijn++;
      row += dx;
      col += dy;
    }
    row = dupRow;
    col = dupCol;
    while (!deepBoard.isEmpty(row - dx, col - dy)) {
      lengteLijn++;
      row -= dx;
      col -= dy;
    }
    int addToScore = lengteLijn == 5 ? lengteLijn + 7 : lengteLijn + 1;
    return addToScore;
  }

  public int calcScoreHorAndVer(String[] split, Board deepBoard) {
    if (board.getIsFirstMove() == true) {
      return 1;
    } else {
      int dx = 1;
      int dy = 0;
      int row = Integer.parseInt(split[2]);
      int col = Integer.parseInt(split[3]);
      int dupRow = row;
      int dupCol = col;
      boolean horRow = false;
      int lineLength = 0;
      while (!deepBoard.isEmpty(row + dx, col + dy)) {
        lineLength++;
        row += dx;
        col += dy;
        horRow = true;
      }
      row = dupRow;
      col = dupCol;
      while (!deepBoard.isEmpty(row - dx, col - dy)) {
        lineLength++;
        row -= dx;
        col -= dy;
        horRow = true;
      }
      lineLength = (lineLength == 5) ? lineLength + 6 : lineLength;
      dx = 0;
      dy = 1;
      row = dupRow;
      col = dupCol;
      int addToScore = lineLength;
      lineLength = 0;
      boolean verRow = false;
      while (!deepBoard.isEmpty(row + dx, col + dy)) {
        lineLength++;
        row += dx;
        col += dy;
        verRow = true;
      }
      row = dupRow;
      col = dupCol;
      while (!deepBoard.isEmpty(row - dx, col - dy)) {
        lineLength++;
        row -= dx;
        col -= dy;
        verRow = true;
      }
      lineLength = (lineLength == 5) ? lineLength + 6 : lineLength + 1;
      addToScore += lineLength;
      addToScore = horRow && verRow ? addToScore + 1 : addToScore;
      return addToScore;
    }
   
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
      addTileToJar(player.getHand().get(q));
      player.removeTileFromHand(player.getHand().get(q));
      
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
    players.get(client.getClientNumber()).setInGameTrue();
  }

  /*
   * Dit is een beschrijving voor de pot met tegeltjes.
   */
  public void fillJar() {
    Shape shape = null;
    Color color = null;
    for (int i = 0; i < 3; i++) {
      for (int q = 0; q < allColors.length - 1; q++) {
        color = allColors[q];
        for (int w = 0; w < allShapes.length - 1; w++) {
          shape = allShapes[w];
          String tile = "" + color.getChar() + shape.getChar();
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
      return "empty";
    }
  }

  public boolean isActive() {
    return activeGame;
  }

  public void becomeActive() {
    activeGame = true;
  }

  public void startGame() {
    becomeActive();
    fillJar();
    String names = "";
    for (int r = 0; r < players.size(); r++) {
      names = names.concat(" " + players.get(r).getPlayer().getName() 
          + " " + players.get(r).getPlayerNumber());
    }
    broadcast("NAMES" + names + " " + 100); //TODO magic number
    for (int t = 0; t < players.size(); t++) {
      String tiles = "";
      for (int q = 0; q < 6; q++) {
        String randomTile = giveRandomTile();
        tiles = tiles.concat(" " + randomTile);

      }
      threads.get(t).sendMessage("NEW" + tiles);
      String[] splittedTiles = tiles.split(" ");
      for (int w = 1; w < 7; w++) {
        players.get(t).getPlayer().addTileToHand(splittedTiles[w]);
      }
    }
    int nrMoves = 0;
    for (int w = 0; w < players.size(); w++) {
      nrMoves = Math.max(nrMoves, getLongestStreak(players.get(w).getPlayer(), board));
    }
    for (int a = 0; a < players.size(); a++) {
      if (nrMoves == getLongestStreak(players.get(a).getPlayer(), board)) {
        turn = players.get(a).getPlayerNumber();
      }
    }
    broadcast("NEXT " + players.get(turn).getPlayerNumber());
  }

  public void addHandler(ClientHandler client) {
    client.setClientNumber(threads.size());
    threads.add(client);
    PlayerWrapper playerWrapper = new PlayerWrapper();
    players.add(playerWrapper);
  }

  public void removeHandler(ClientHandler client) {
    if (players.get(client.getClientNumber()).getPlayer() != null) {
    for (int i = 0; i < threads.size(); i++) {
      if (threads.get(i) == client) {
        broadcast("Client " + client.getClientNumber() + " " + players.get(client.getClientNumber()).getPlayer().getName() + " has disconnected");
        threads.remove(i);
      }
    }
    } else {
      threads.remove(client);
    }
  }
  
  public int getLongestStreak(Player player, Board board) {
    int nrMoves = 0; 
    int maxMoves = 0;
    int miny = board.getMiny();
    int maxy = board.getMaxy();
    int minx = board.getMinx();
    int maxx = board.getMaxx();
    Color color = null;
    Shape shape = null;
    boolean gotMove;
    Player deepPlayer = new HumanPlayer(board, player.getName(), player.getPlayerNumber());
    
    
    for (int firstMove = 0; firstMove < 6; firstMove++) {
      nrMoves = 0;
      Board deepBoard = board.deepCopy();
      deepPlayer.getHand().addAll(player.getHand());
    
      color = Color.getColorFromCharacter(deepPlayer.getHand().get(firstMove).charAt(0));
      shape = Shape.getShapeFromCharacter(deepPlayer.getHand().get(firstMove).charAt(1));
      deepBoard.setTile(91, 91, new Tile(color, shape));
      deepPlayer.removeTileFromHand("" + color.getChar() + shape.getChar());
      nrMoves += 1;

      for (int i = 0; i < deepPlayer.getHand().size(); i++) {
        gotMove = true;
        String currentTile = deepPlayer.getHand().get(i);
        for (int row = maxy; row <= miny; row++) {
          if (gotMove == true) {
          for (int col = minx; col <= maxx; col++) {
            color = Color.getColorFromCharacter(currentTile.charAt(0));
            shape = Shape.getShapeFromCharacter(currentTile.charAt(1));
            if (deepBoard.isValidMove(row, col, new Tile(color, shape))) {
              deepPlayer.removeTileFromHand("" + color.getChar() + shape.getChar());
              nrMoves += 1;
              gotMove = false;
              break;
            }
            }
          }
        }
      }
      deepPlayer.getHand().removeAll(deepPlayer.getHand());
      maxMoves = Math.max(nrMoves, maxMoves);
    }
    return maxMoves;
  }
  
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
      move = move.concat(colorFirstMove.getChar() + shapeFirstMove.getChar() + " " + 91 + " " + 91 + "Just place a tile on 91 91");
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
              move = move.concat(color.getChar() + shape.getChar() 
              + " " + row + " " + col + " You could place that tile");
              deepPlayer.removeTileFromHand("" + color.getChar() + shape.getChar());
              return move;
            }
          }
        }
      }
      if (jar.size() != 0) {
      move = move.concat("Try swapping a tile"); 
      } else {
        move = move.concat("No options left");
      }
      return move;
    }
  }
  
  public void endGame() {
    int score = 0;
    for (int w = 0; w < players.size(); w++) {
      score = Math.max(score, players.get(w).getScore());
      players.get(w).setInGameFalse();
      players.get(w).getPlayer().getHand().removeAll(players.get(w).getPlayer().getHand());
      players.get(w).setScore(0);
    }
    for (int a = 0; a < players.size(); a++) {
      if (score == players.get(a).getScore()) {
        broadcast("WINNER " + players.get(a).getPlayerNumber());
      }
    }
    jar.removeAll(jar);
    }
  }
