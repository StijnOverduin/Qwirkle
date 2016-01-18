package game;

import java.util.ArrayList;

public class Board {

	private int miny;
	private int maxy;
	private int minx;
	private int maxx;
	public static final int DIM = 183;
	public static final int BEGINVELD = 0;
	public static final int EINDVELD = DIM - 1;
	public static final int MIDDENVELD = EINDVELD / 2;
	private Tile[][] board;

	public Board() {
		board = new Tile[DIM][DIM];
		for (int row = 0; row < board.length; row++) {
			for (int col = 0; col < board.length; col++) {
				board[row][col] = new Tile(Color.EMPTY, Shape.EMPTY);

			}
		}
		miny = 95;
		maxy = 87;
		minx = 87;
		maxx = 95;
	}

	public Tile getTile(int row, int col) {
		return board[row][col];
	}

	public boolean isEmpty(int row, int col) {
		return (getTile(row, col)).toString().equals("ee");
	}

	// Geredeneerd vanuit het middelpunt 91, 91
	// Een colom naar boven betekent dus 91, 92
	// Een colom naar beneden betekent dus 91,90
	// Een rij naar boven betekend 90, 91
	// Een rij naar beneden betekend 92,91
	public void setTile(int row, int col, Tile tile) {
		board[row][col] = tile;
		maxx = Math.max(col, maxx);
		minx = Math.min(col, minx);
		miny = Math.max(row, miny);
		maxy = Math.min(row, maxy);

	}

	public static void main(String[] args) {

		Board board = new Board();
		System.out.println(board);
	}

	public boolean isValidMove(int row, int col, Tile tile) {
		if (row > EINDVELD || col > EINDVELD || row < BEGINVELD || col < BEGINVELD) {
			return false;
		} else if (isEmpty(row, col) == false) {
			return false;
		} else if (row == MIDDENVELD && col == MIDDENVELD) {
			return true;
		} else {
			boolean ans1 = true;
			boolean ans2 = true;
			if (!isEmpty(row + 1, col) || !isEmpty(row - 1, col)) {
				ans1 = checkRow(tile, row, col, false);
			}
			if (!isEmpty(row, col + 1) || !isEmpty(row, col - 1)) {
				ans2 = checkRow(tile, row, col, true);
			}
			return (ans1 && ans2);
		}
	}

	private boolean checkRow(Tile tile, int row, int col, boolean horizontalCheck) {
		int dx = horizontalCheck ? 0 : 1; // als horizontal check, dan dx 1 dy
											// 0, anders dx 0 dy 1.
		int dy = dx == 1 ? 0 : 1;
		int dupy = row;
		int dupx = col;

		ArrayList<Tile> set = new ArrayList<Tile>();
		set.add(tile);
		while (!isEmpty(row + dx, col + dy)) {
			set.add(getTile(row + dx, col + dy));
			row += dx;
			col += dy;
		}
		while (!isEmpty(row + dx, col + dy)) {
			set.add(getTile(row + dx, col + dy));
			row += dx;
			col += dy;
		}
		row = dupx;
		col = dupy;
		while (!isEmpty(row - dx, col - dy)) {
			set.add(getTile(row - dx, col - dy));
			row += dx;
			col += dy;
		}
		while (!isEmpty(row - dx, col - dy)) {
			set.add(getTile(row - dx, col - dy));
			row += dx;
			col += dy;
		}

		if (set.size() > 6)
			return false;
		boolean ans = true;
		if (set.get(0).getColor() == (set.get(1).getColor())) {
			for (int a = 1; a < set.size(); a++) {
				if (set.get(0).getColor() != (set.get(a).getColor())
						|| set.get(0).getShape() == (set.get(a).getShape())) {
					ans = false;
				}
			}
		} else if (set.get(0).getShape() == (set.get(1).getShape())) {
			for (int a = 1; a < set.size(); a++) {
				if (set.get(0).getColor() == (set.get(a).getColor())
						|| set.get(0).getShape() != (set.get(a).getShape())) {
					ans = false;
				}
			}
		} else {
			ans = false;
		}
		return ans;
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		String newLine = System.getProperty("line.separator");

		builder.append(" ");
		builder.append(newLine);
		builder.append("   ");
		for (int i = minx; i <= maxx; i++) {
			builder.append(i + " ");
		}
		for (int row = maxy; row <= miny; row++) {
			builder.append(newLine);
			builder.append(row + " ");
			for (int col = minx; col <= maxx; col++) {
				if (board[row][col].toString().equals("ee")) {
					builder.append("-- ");

				} else {
					builder.append(board[row][col].toString() + " ");
				}
			}

		}

		return builder.toString();
	}

	public Board deepCopy() {
		Board b = new Board();
		for (int row = 0; row < board.length; row++) {
			for (int col = 0; col < board.length; col++) {
				b.setTile(row, col, getTile(row, col));
			}
		}
		return b;
	}

}
