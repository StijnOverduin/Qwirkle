package game;

import java.util.ArrayList;

public abstract class Player {

	protected int score;
	protected ArrayList<Tile> hand;
	protected Board board;
	protected String name;
	protected int playerNumber;

	public Player(Board board, String name, int playerNumber) {
		this.board = board;
		this.name = name;
		hand = new ArrayList<Tile>();
		this.score = 0;
		this.playerNumber = playerNumber;
	}

	public void makeMove(int i, int j, Tile tile) {
		if (board.isValidMove(i, j, tile)) {
			board.setTile(i, j, tile);
		}
	}

	public String getName() {
		return name;
	}

	public void addTilesToHand(Tile tile) {
		hand.add(tile);
	}

	public void removeTileFromHand(Tile tile) {
		hand.remove(tile);
	}

	public int NumberOfTilesInHand() {
		return hand.size();
	}

	public int getScore() {
		return score;
	}
	
	public int getPlayerNumber() {
		return playerNumber;
	}
	
	public ArrayList<Tile> getHand() {
		return hand;
	}
	
}
