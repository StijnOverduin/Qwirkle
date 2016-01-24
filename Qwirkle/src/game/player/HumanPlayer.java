package game.player;

import game.Board;
import game.tiles.Tile;

public class HumanPlayer extends Player {

  public HumanPlayer(Board board, String name, int playerNumber) {
    super(board, name, playerNumber);
  }

  public void makeMove(int row, int col, Tile tile) {
    board.setTile(row, col, tile);
  }

  //this method is needed for the AI, HumanPlayer doesn't use this method.
  public String determineMove() {
    return "";
  }

}
