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

  public String determineMove() {
    return "";
  }

}
