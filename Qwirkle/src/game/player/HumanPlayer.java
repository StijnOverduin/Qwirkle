package game.player;

import game.Board;
import game.tiles.Tile;

public class HumanPlayer extends Player {

	public HumanPlayer(Board board, String name, int playerNumber) {
		super(board, name, playerNumber);
	}
	
	public void makeMove(int i, int j, Tile tile) {
		board.setTile(i, j, tile);
	}
	
	public String determineMove() {
		return "";
	}

}
