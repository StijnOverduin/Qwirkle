package game.player;

import game.Board;
import game.tiles.Tile;

import java.util.ArrayList;



public abstract class Player {

  protected int score;
  protected ArrayList<String> hand;
  protected Board board;
  protected String name;
  protected int playerNumber;
  protected final static int EMPTYHAND = 0;

  public Player(Board board, String name, int playerNumber) {
    this.board = board;
    this.name = name;
    hand = new ArrayList<String>();
    this.playerNumber = playerNumber;
  }

  public abstract void makeMove(int row, int col, Tile tile);

  public abstract String determineMove();

  public String getName() {
    return name;
  }

  public void addTileToHand(String tile) {
    hand.add(tile);
  }

  public void removeTileFromHand(String tile) {
    hand.remove(tile);
  }

  public int numberOfTilesInHand() {
    if (hand.size() == EMPTYHAND) {
      return EMPTYHAND;
    } else {
      return hand.size();
    }
  }

  public int getPlayerNumber() {
    return playerNumber;
  }

  public ArrayList<String> getHand() {
    return hand;
  }
  
  

}
