package game;

import java.awt.List;
import java.util.ArrayList;

public class Board {

	public static final int DIM = 183;
	public static final int BEGINVELD = 0;
	public static final int EINDVELD = DIM - 1;
	public static final int MIDDENVELD = EINDVELD / 2;
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
		return (getTile(i, j)).toString().equals("EMPTY EMPTY");
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
		if (i > EINDVELD || j > EINDVELD || i < BEGINVELD || j < BEGINVELD) {
			return false;
		} else if (isEmpty(i, j) == false) {
			return false;
		} else if (i == MIDDENVELD && j == MIDDENVELD) {
			return true;
		} else {
		boolean ans = false;
			if (!isEmpty(i + 1, j) || !isEmpty(i - 1, j)) {
			ans = checkRow(tile, i, j, false);
			}
			if (!isEmpty(i, j + 1) || !isEmpty(i, j - 1)) {
			ans = checkRow(tile, i, j, true);
			}
			return ans;
		}
	}

	private boolean checkRow(Tile tile, int i, int j, boolean horizontalCheck) {
		int dx = horizontalCheck ? 1 : 0; // als horizontal check, dan dx 1 dy
											// 0, anders dx 0 dy 1.
		int dy = dx == 1 ? 0 : 1;
		int dupy = i;
		int dupx = j;

		ArrayList<Tile> set = new ArrayList<Tile>();
		set.add(tile);
		while (!isEmpty(i + dx, j + dy)) {
			set.add(getTile(i + dx, j + dy));
			i += dx;
			j += dy;
		}
		while (!isEmpty(i + dx, j + dy)) {
			set.add(getTile(i + dx, j + dy));
			i += dx;
			j += dy;
		}
		i = dupx;
		j = dupy;
		while (!isEmpty(i - dx, j - dy)) {
			set.add(getTile(i - dx, j - dy));
			i += dx;
			j += dy;
		}
		while (!isEmpty(i - dx, j - dy)) {
			set.add(getTile(i - dx, j - dy));
			i += dx;
			j += dy;
		}

		if (set.size() > 6)
			return false;
		boolean ans = true;
		if (set.get(0).getColor().equals(set.get(1).getColor())) {
			for (int a = 1; a < set.size(); a++) {
				if (!set.get(0).getColor().equals(set.get(a).getColor())
						|| set.get(0).getShape().equals(set.get(a).getShape())) {
					ans = false;
				}
			}
		} else if (set.get(0).getShape().equals(set.get(1).getShape())) {
			for (int a = 0; a < set.size() - 1; a++) {
				if (!set.get(0).getColor().equals(set.get(a).getColor())
						|| set.get(0).getShape().equals(set.get(a).getShape())) {
					ans = false;
				}
			}
		} else {
			ans = false;
		}
		return ans;
	}

}
