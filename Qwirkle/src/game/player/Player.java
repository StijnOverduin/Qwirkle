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
	protected static final int EMPTYHAND = 0;

	/**
	 * Creates an instance of the class Player and instantiates the board, name,
	 * playerNumber and hand of the player.
	 * 
	 * @param board
	 * @param name
	 * @param playerNumber
	 */
	public Player(Board board, String name, int playerNumber) {
		this.board = board;
		this.name = name;
		hand = new ArrayList<String>();
		this.playerNumber = playerNumber;
	}

	/**
	 * Places a tile on the board the player has been given.
	 * 
	 * @param row
	 * @param col
	 * @param tile
	 */
	public void makeMove(int row, int col, Tile tile) {
		board.setTile(row, col, tile);
	}

	/**
	 * Returns a string with the move the AI player wants to make.
	 * 
	 * @return String including AI move
	 */
	public abstract String determineMove(long thinkingTime);

	/**
	 * Returns the name of the player.
	 * 
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Adds the specified tile to the hand of the player.
	 * 
	 * @param tile
	 */
	public void addTileToHand(String tile) {
		hand.add(tile);
	}

	/**
	 * Removes the specified tile from the hand of the player.
	 * 
	 * @param tile
	 */
	public void removeTileFromHand(String tile) {
		hand.remove(tile);
	}

	/**
	 * Returns the number of tiles in the hand of the player. If the player
	 * doesn't have any tiles left, it returns the static variable EMPTYHAND,
	 * which is 0.
	 * 
	 * @return hand size
	 */
	public int numberOfTilesInHand() {
		if (hand.size() == EMPTYHAND) {
			return EMPTYHAND;
		} else {
			return hand.size();
		}
	}

	/**
	 * Returns the player number of the player.
	 * 
	 * @return playerNumber
	 */
	public int getPlayerNumber() {
		return playerNumber;
	}

	/**
	 * Returns an arrayList which represents the hand of the player.
	 * 
	 * @return hand
	 */
	public ArrayList<String> getHand() {
		return hand;
	}

}
