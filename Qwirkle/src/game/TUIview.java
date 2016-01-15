package game;

import java.util.Scanner;

public class TUIview {

	private Player player;
	private Client client;
	

	public TUIview(Client client, Player player) {
		this.client = client;
		this.player = player;
	}
	
	public void displayBoard(Board board) {
		System.out.print(board.toString());
	}
	
	public void displayHand(Player player) {
		System.out.println(player.getHand());
	}
	
}
