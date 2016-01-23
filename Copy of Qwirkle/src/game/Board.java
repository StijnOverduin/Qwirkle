package game;

import game.tiles.Color;
import game.tiles.Shape;
import game.tiles.Tile;

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
  private boolean isFirstMove;
  private int dupRow;
  private int dupCol;
  private int dy;
  private int dx;

  public Board() {
    isFirstMove = true;
    board = new Tile[DIM][DIM];
    for (int row = 0; row < board.length; row++) {
      for (int col = 0; col < board.length; col++) {
        board[row][col] = new Tile(Color.EMPTY, Shape.EMPTY);

      }
    }
    miny = 95; // TODO magic number
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

  public int getMaxx() {
    return maxx;
  }

  public int getMinx() {
    return minx;
  }

  public int getMaxy() {
    return maxy;
  }

  public int getMiny() {
    return miny;
  }
  
  public void setBoundries(Board board) {
    miny = board.getMiny();
    maxy = board.getMaxy();
    minx = board.getMinx();
    maxx = board.getMaxx();
  }

  // Geredeneerd vanuit het middelpunt 91, 91
  // Een colom naar boven betekent dus 91, 92
  // Een colom naar beneden betekent dus 91,90
  // Een rij naar boven betekend 90, 91
  // Een rij naar beneden betekend 92,91
  public void setTile(int row, int col, Tile tile) {
    board[row][col] = tile;
    maxx = Math.max(col + 1, maxx);
    minx = Math.min(col - 1, minx);
    miny = Math.max(row + 1, miny);
    maxy = Math.min(row - 1, maxy);

  }

  public static void main(String[] args) {

    Board board = new Board();
    System.out.println(board);
  }

  public boolean isValidMove(int row, int col, Tile tile) {
    if (row > EINDVELD || col > EINDVELD || row < BEGINVELD || col < BEGINVELD) {
      return false;
    } else if (!isEmpty(row, col)) {
      return false;
    } else if (isFirstMove == true) {
      isFirstMove = false;
      return (row == MIDDENVELD && col == MIDDENVELD);
    } else if (isEmpty(row + 1, col) && isEmpty(row - 1, col) 
        && isEmpty(row, col + 1) && isEmpty(row, col - 1)) {
      return false;
    } else {
      boolean ans = true;
      if (!isEmpty(row + 1, col) || !isEmpty(row - 1, col)) {
        ans = checkRow(tile, row, col, false);
      }
      if (ans && (!isEmpty(row, col + 1) || !isEmpty(row, col - 1))) {
        ans = checkRow(tile, row, col, true);
      }
      return ans;
    }
  }

  private boolean checkRow(Tile tile, int row, int col, boolean horizontalCheck) {
    dx = horizontalCheck ? 0 : 1; // als horizontal check, dan dx 1 dy
    // 0, anders dx 0 dy 1.
    dy = dx == 1 ? 0 : 1;
    dupRow = row;
    dupCol = col;
    
    ArrayList<Tile> set = new ArrayList<Tile>();
    set.add(tile);
    while (!isEmpty(row + dx, col + dy)) {
      set.add(getTile(row + dx, col + dy));
      row = row + dx;
      col = col + dy;
    }
    row = dupRow;
    col = dupCol;
    while (!isEmpty(row - dx, col - dy)) {
      set.add(getTile(row - dx, col - dy));
      row = row - dx;
      col = col - dy;
    }

    if (set.size() > 6) {
      return false;
    }
    boolean ans = true;
    if (set.get(0).getColor() == (set.get(1).getColor())) {
      for (int a = 1; a < set.size(); a++) {
        for (int b = 0; b < set.size() - 1; b++) {
          if (a != b) { 
            if (set.get(b).getColor() != (set.get(a).getColor()) 
                || set.get(b).getShape() == (set.get(a).getShape())) {
              ans = false;
            }
          }
        }
      }
    } else if (set.get(0).getShape() == (set.get(1).getShape())) {
      for (int a = 1; a < set.size(); a++) {
        for (int b = 0; b < set.size() - 1; b++) {
          if (a != b) {
            if (set.get(0).getColor() == (set.get(a).getColor()) 
                || set.get(0).getShape() != (set.get(a).getShape())) {
              ans = false;
            }
          }
        }
      }
    } else {
      ans = false;
    }
    return ans;
  }

  @Override
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

  public void isFirstMoveBecomesFalse() {
    isFirstMove = false;
  }

  public Board deepCopy() {
    Board bb = new Board();
    for (int row = 0; row < board.length; row++) {
      for (int col = 0; col < board.length; col++) {
        bb.setTile(row, col, getTile(row, col));
      }
    }
    bb.setBoundries(this);
    if (!isEmpty(91, 91)) {
      bb.isFirstMoveBecomesFalse();
    }
    return bb;
  }

  public boolean getIsFirstMove() {
    return isFirstMove;
  }
}