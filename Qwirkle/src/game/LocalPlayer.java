package game;

import java.util.ArrayList;

public class LocalPlayer implements Player {
	
	private Board board;
	private String name;
	private int score;
	public ArrayList<Tile> hand;

	public LocalPlayer(String name, Board board) {
		this.name = name;
		this.board = board;
		hand = new ArrayList<Tile>();
	}
	
	public Tile deterMineMove() {
		return null;
	}
	
	public void makeMove(int i, int j, Tile tile) {
		if (board.isValidMove(i, j , tile)) {
		board.setTile(i, j, tile);
		}
	}
	
	public String getName() {
		return name;
	}
	
	public int getScore() {
		return score;
		
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
	

}
