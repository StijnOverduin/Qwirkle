package game;

public class Board {

	public static final int DIM = 183;
	public Tile[][] board;

	public Board() {
		board = new Tile[DIM][DIM];
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board.length; j++) {
				board[i][j] = new Tile(Color.EMPTY, Shape.EMPTY);
			}
		}
	}

	public Tile getTile(int i, int j) {
		return board[i][j];
	}

	public boolean isEmpty(int i, int j) {
		return (board[i][j]).toString().equals("EMPTY EMPTY");
	}

	public void setTile(int i, int j, Tile tile) {
		board[i][j] = tile;
	}

	public static void main(String[] args) {

		Board board = new Board();
		board.setTile(2, 4, new Tile(Color.ROOD, Shape.RUIT));
		System.out.println(board.isEmpty(2, 4));
		System.out.println(board.getTile(2, 4));
	}

	public boolean isValidMove(int i, int j, Tile tile) {
		if (isEmpty(i, j) == false) {
			return false;
		} else if (i == 91 && j == 91) {
			return true;
		}

	public boolean compareTile(Tile tile1, Tile tile2) {
		if (tile1.equals(tile2)) {
			return false;
		} else if (getColor(tile1).equals(getColor(tile2))) {
			return false;
		} else {
			return true;
		}
	}
}
