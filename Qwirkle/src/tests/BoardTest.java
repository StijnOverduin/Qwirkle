package tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import game.Board;
import game.tiles.Color;
import game.tiles.Shape;
import game.tiles.Tile;

import org.junit.Before;
import org.junit.Test;

public class BoardTest {
  private Board board;

  @Before
  public void setUp() {
    board = new Board();
  }

  @Test
  public void testGetTile() {
    assertTrue((board.getTile(91, 91).toString())
        .equals(new Tile(Color.EMPTY, Shape.EMPTY).toString()));
    assertTrue((board.getTile(0, 0).toString())
        .equals(new Tile(Color.EMPTY, Shape.EMPTY).toString()));
    assertTrue((board.getTile(182, 182).toString())
        .equals(new Tile(Color.EMPTY, Shape.EMPTY).toString()));
    assertTrue((board.getTile(34, 112).toString())
        .equals(new Tile(Color.EMPTY, Shape.EMPTY).toString()));

    board.setTile(92, 83, new Tile(Color.ROOD, Shape.DIAMANT));
    assertTrue((board.getTile(92, 83).toString())
        .equals(new Tile(Color.ROOD, Shape.DIAMANT).toString()));
  }

  @Test
  public void testIsValidMove() {
    System.out.println(board.toString());
    assertTrue(board.isValidMove(91, 91, new Tile(Color.BLAUW, Shape.STER)));
    assertFalse(board.isValidMove(91, 92, new Tile(Color.BLAUW, Shape.STER)));

    board.setTile(91, 91, new Tile(Color.BLAUW, Shape.STER));
    assertFalse(board.isValidMove(92, 92, new Tile(Color.BLAUW, Shape.CIRKEL)));
    assertTrue(board.isValidMove(91, 92, new Tile(Color.BLAUW, Shape.CIRKEL)));
    assertFalse(board.isValidMove(12, 56, new Tile(Color.GEEL, Shape.KLAVER)));

    board.setTile(91, 92, new Tile(Color.BLAUW, Shape.CIRKEL));
    assertFalse(board.isValidMove(92, 92, new Tile(Color.GEEL, Shape.STER)));
    assertFalse(board.isValidMove(92, 92, new Tile(Color.BLAUW, Shape.CIRKEL)));
    assertTrue(board.isValidMove(92, 92, new Tile(Color.BLAUW, Shape.STER)));
  }

  @Test
  public void testIsEmpty() {
    assertTrue(board.isEmpty(91, 91));
    assertTrue(board.isEmpty(92, 91));
    assertTrue(board.isEmpty(182, 182));

    board.setTile(91, 91, new Tile(Color.BLAUW, Shape.CIRKEL));
    assertFalse(board.isEmpty(91, 91));
  }

}
