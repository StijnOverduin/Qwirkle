package game;

import game.tiles.Color;
import game.tiles.Shape;
import game.tiles.Tile;

import java.util.ArrayList;

public class Board {

  public static final int DIM = 183;
  public static final int BEGINVELD = 0;
  public static final int EINDVELD = DIM - 1;
  public static final int MIDDENVELD = EINDVELD / 2;
  public static final int MINY = 95;
  public static final int MINX = 87;
  public static final int MAXY = 87;
  public static final int MAXX = 95;
  private int miny;
  private int maxy;
  private int minx;
  private int maxx;
  private int dupRow;
  private int dupCol;
  private int dy;
  private int dx;
  private Tile[][] board;
  private boolean isFirstMove;


  /**
   * Fills the double array list "board" with the empty tiles.
   */
  public Board() {
    isFirstMove = true;
    board = new Tile[DIM][DIM];
    for (int row = 0; row < board.length; row++) {
      for (int col = 0; col < board.length; col++) {
        board[row][col] = new Tile(Color.EMPTY, Shape.EMPTY);

      }
    }
    miny = MINY;
    maxy = MAXY;
    minx = MINX;
    maxx = MAXX;
  }

  /**
   * Returns a the tile on the board specified by the row and column.
   * @param int row
   * @param int col
   * @return tile in that location
   */
  //@ requires row >= 0 && row <= 182;
  //@ requires col >= 0 && col <= 182;
  /*@ pure*/ public Tile getTile(int row, int col) {
    return board[row][col];
  }

  /**
   * Checks if the place on the board specified by the row and the column is empty or not,
   * and gives a boolean back.
   * @param int row
   * @param int col
   * @return whether the tile location is empty
   */
  //@ requires row >= 0 && row <= 182;
  //@ requires col >= 0 && col <= 182;
  /*@ pure*/ public boolean isEmpty(int row, int col) {
    return (getTile(row, col)).toString().equals("ee");
  }

  /**
   * Returns the boundary maxx.
   * @return boundary of the maximum column
   */
  /*@ pure*/ public int getMaxx() {
    return maxx;
  }

  /**
   * Returns the boundary minx.
   * @return boundary of the minimal column
   */
  /*@ pure*/ public int getMinx() {
    return minx;
  }

  /**
   * Returns the boundary maxy.
   * @return boundary of the maximum row
   */
  /*@ pure*/ public int getMaxy() {
    return maxy;
  }

  /**
   * Returns the boundary miny.
   * @return boundary of the minimal row
   */
  /*@ pure*/ public int getMiny() {
    return miny;
  }
  
  /**
   * Copies the boundaries from the given board. 
   * This method is mainly used by the deepCopy() method.
   * @param board whose boundaries it inherits
   */
  //@ requires board != null;
  public void setBoundries(Board board) {
    miny = board.getMiny();
    maxy = board.getMaxy();
    minx = board.getMinx();
    maxx = board.getMaxx();
  }

  // Geredeneerd vanuit het middelpunt 91, 91
  // Een colom naar rechts betekent dus 91, 92
  // Een colom naar links betekent dus 91,90
  // Een rij naar boven betekend 90, 91
  // Een rij naar beneden betekend 92,91
  /**
   * Sets a tile on the board. 
   * It also changes the boundaries if the tile is to close to the edge of the board.
   * @param row int
   * @param col int
   * @param tile Tile
   */
  //@ requires row >= 0 && row <= 182;
  //@ requires col >= 0 && col <= 182;
  //@ requires tile != null;
  public void setTile(int row, int col, Tile tile) {
    board[row][col] = tile;
    maxx = Math.max(col + 1, maxx);
    minx = Math.min(col - 1, minx);
    miny = Math.max(row + 1, miny);
    maxy = Math.min(row - 1, maxy);

  }

  /**
   * Checks if the tile can be placed on the board according to the rules of the game.
   * This method returns true if the tile can be placed and false if the tile can't be placed.
   * @param row int
   * @param col int
   * @param tile Tile
   * @return whether the move complies to the game rules
   */
  
  //@ requires row >= 0 && row <= 182;
  //@ requires col >= 0 && col <= 182;
  //@ requires tile != null;
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

  /**
   * Checks if the tile can be placed in the row. 
   * Receives a boolean to check if it has to check horizontally.
   * Returns a boolean whether the tile can be placed or not. 
   * This method is used by isValidMove() to make it less complex.
   * 
   * @param tile Tile
   * @param row int
   * @param col int
   * @param horizontalCheck boolean, whether the row should be checked horizontally or vertically.
   * @return boolean whether it complies to the game rules
   */
  private boolean checkRow(Tile tile, int row, int col, boolean horizontalCheck) {
    dx = horizontalCheck ? 0 : 1; // als horizontal check false, dan dx 1 dy
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

  /**
   * This is a method that returns a String of how the board looks like with the current tiles.
   * When placed in a System.out.println() statement it will print the board to see for the user.
   * It overrides the java.toString() method.
   * @return String toString()
   */
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

  /**
   * The first move of the board has to be on the row 91 and column 91. This method is called 
   * if the first tile has been placed on the board. It then switches the boolean isFirstMove 
   * to false instead of true.
   */
  public void isFirstMoveBecomesFalse() {
    isFirstMove = false;
  }

  /**.
   * This method returns an object of type Board. It copies the board on which this method is 
   * called. This method is used to check whether some moves are valid or not.
   * @return deepcopied Board
   */
  
  public Board deepCopy() {
    Board deepBoard = new Board();
    for (int row = 0; row < board.length; row++) {
      for (int col = 0; col < board.length; col++) {
        deepBoard.setTile(row, col, getTile(row, col));
      }
    }
    deepBoard.setBoundries(this);
    if (!isEmpty(91, 91)) {
      deepBoard.isFirstMoveBecomesFalse();
    }
    return deepBoard;
  }

  /**
   * returns the boolean isFirstMove to check if the first move has been done.
   * @return isFirstMove boolean
   */
  /*@ pure*/ public boolean getIsFirstMove() {
    return isFirstMove;
  }
}