package game;

import java.util.ArrayList;

public class Player {

	protected int score;
	protected ArrayList<String> hand;
	protected Board board;
	protected String name;
	protected int playerNumber;

	public Player(Board board, String name, int playerNumber) {
		this.board = board;
		this.name = name;
		hand = new ArrayList<String>();
		this.playerNumber = playerNumber;
	}

	public void makeMove(int i, int j, Tile tile) {
		board.setTile(i, j, tile);
	}

	public String getName() {
		return name;
	}

	public void addTilesToHand(String tile) {
		hand.add(tile);
	}

	public void removeTileFromHand(String tile) {
		hand.remove(tile);
	}

	public int NumberOfTilesInHand() {
		return hand.size();
	}
	
	public int getPlayerNumber() {
		return playerNumber;
	}
	
	public ArrayList<String> getHand() {
		return hand;
	}
	
}
