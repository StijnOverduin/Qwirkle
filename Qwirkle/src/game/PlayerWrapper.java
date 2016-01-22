package game;

import game.player.Player;

public class PlayerWrapper {

	private Player player;
	private int score;
	private int playerNumber;

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player firstVal) {
		this.player = firstVal;
	}

	public int getScore() {
		return score;
	}

	public int getPlayerNumber() {
		return playerNumber;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public void setPlayerNumber(int playerNumber) {
		this.playerNumber = playerNumber;
	}

	public PlayerWrapper() {
		
	}
}
