package game.player;

import game.Board;

public class HumanPlayer extends Player {

	public HumanPlayer(Board board, String name, int playerNumber) {
		super(board, name, playerNumber);
	}

	/**
	 * This method is abstract in the Player class, so it needs to be defined
	 * here. HumanPlayer doesn't use this method so it doesn't do anything.
	 */
	public String determineMove(long thinkinTime) {
		return "";
	}

}
