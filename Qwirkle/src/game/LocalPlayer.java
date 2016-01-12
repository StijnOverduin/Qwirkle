package game;

import java.util.Scanner;

public class LocalPlayer implements Player {
	
	private Board board;
	private String name;
	private int score;

	public LocalPlayer(String name, Board board) {
		this.name = name;
		this.board = board;
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

}
