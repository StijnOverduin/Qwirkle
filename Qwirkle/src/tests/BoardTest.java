package tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import game.Board;
import game.tiles.Color;
import game.tiles.Shape;
import game.tiles.Tile;

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
    
    board.setTile(91, 93, new Tile(Color.BLAUW, Shape.KLAVER));
    board.setTile(91, 94, new Tile(Color.BLAUW, Shape.DIAMANT));
    assertTrue(board.isValidMove(91, 95, new Tile(Color.BLAUW, Shape.KRUIS)));
    assertTrue(board.isValidMove(91, 95, new Tile(Color.BLAUW, Shape.VIERKANT)));
    assertFalse(board.isValidMove(91, 95, new Tile(Color.ROOD, Shape.VIERKANT)));
    assertFalse(board.isValidMove(91, 95, new Tile(Color.BLAUW, Shape.CIRKEL)));
    
    board.setTile(91, 95, new Tile(Color.BLAUW, Shape.VIERKANT));
    assertFalse(board.isValidMove(91, 96, new Tile(Color.BLAUW, Shape.STER)));
  }

  @Test
  public void testIsEmpty() {
    assertTrue(board.isEmpty(91, 91));
    assertTrue(board.isEmpty(92, 91));
    assertTrue(board.isEmpty(182, 182));

    board.setTile(91, 91, new Tile(Color.BLAUW, Shape.CIRKEL));
    assertFalse(board.isEmpty(91, 91));
  }
  
  @Test
  public void testMaxx() {
    board.setTile(91, 91, new Tile(Color.ROOD, Shape.CIRKEL));
    board.setTile(91, 92, new Tile(Color.ROOD, Shape.VIERKANT));
    board.setTile(91, 93, new Tile(Color.ROOD, Shape.STER));
    assertEquals(board.getMaxx(), 95);
    board.setTile(91, 94, new Tile(Color.ROOD, Shape.DIAMANT));
    assertEquals(board.getMaxx(), 95);
    board.setTile(91, 95, new Tile(Color.ROOD, Shape.KRUIS));
    assertEquals(board.getMaxx(), 96);
    board.setTile(91, 96, new Tile(Color.ROOD, Shape.KLAVER));
    assertEquals(board.getMaxx(), 97);
    
  }
  
  @Test
  public void testMaxy() {
    board.setTile(91, 91, new Tile(Color.ROOD, Shape.CIRKEL));
    board.setTile(90, 91, new Tile(Color.ROOD, Shape.VIERKANT));
    board.setTile(89, 91, new Tile(Color.ROOD, Shape.STER));
    assertEquals(board.getMaxy(), 87);
    board.setTile(88, 91, new Tile(Color.ROOD, Shape.DIAMANT));
    assertEquals(board.getMaxy(), 87);
    board.setTile(87, 91, new Tile(Color.ROOD, Shape.KRUIS));
    assertEquals(board.getMaxy(), 86);
    board.setTile(86, 91, new Tile(Color.ROOD, Shape.KLAVER));
    assertEquals(board.getMaxy(), 85);
  }
  
  
  @Test
  public void testMiny() {
    board.setTile(91, 91, new Tile(Color.ROOD, Shape.CIRKEL));
    board.setTile(92, 91, new Tile(Color.ROOD, Shape.VIERKANT));
    board.setTile(93, 91, new Tile(Color.ROOD, Shape.STER));
    assertEquals(board.getMiny(), 95);
    board.setTile(94, 91, new Tile(Color.ROOD, Shape.DIAMANT));
    assertEquals(board.getMiny(), 95);
    board.setTile(95, 91, new Tile(Color.ROOD, Shape.KRUIS));
    assertEquals(board.getMiny(), 96);
    board.setTile(96, 91, new Tile(Color.ROOD, Shape.KLAVER));
    assertEquals(board.getMiny(), 97);
  }
  
  @Test
  public void testMinx() {
    board.setTile(91, 91, new Tile(Color.ROOD, Shape.CIRKEL));
    board.setTile(91, 90, new Tile(Color.ROOD, Shape.VIERKANT));
    board.setTile(91, 89, new Tile(Color.ROOD, Shape.STER));
    assertEquals(board.getMinx(), 87);
    board.setTile(91, 88, new Tile(Color.ROOD, Shape.DIAMANT));
    assertEquals(board.getMinx(), 87);
    board.setTile(91, 87, new Tile(Color.ROOD, Shape.KRUIS));
    assertEquals(board.getMinx(), 86);
    board.setTile(91, 86, new Tile(Color.ROOD, Shape.KLAVER));
    assertEquals(board.getMinx(), 85);
  }

}
