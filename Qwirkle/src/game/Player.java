package game;

public interface Player {
	
	public Tile deterMineMove();
	public void makeMove(int i, int j, Tile tile);
	public String getName();
	public int getScore();
	
}
