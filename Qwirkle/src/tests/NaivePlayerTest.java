package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import game.Board;
import game.player.Naive;
import game.player.Player;
import game.tiles.Color;
import game.tiles.Shape;
import game.tiles.Tile;

public class NaivePlayerTest {
  
  private Player naivePlayer;
  private Board board;

  @Before
  public void setUp() {
    board = new Board();
    naivePlayer = new Naive(board, "NaiveComputer", 0);
  }

  @Test
  public void testDeterMineMove() {
    naivePlayer.addTileToHand("Ro");
    assertTrue(naivePlayer.determineMove(100).equals("MOVE Ro 91 91"));
    board.setTile(91, 91, new Tile(Color.ROOD, Shape.CIRKEL));
    assertTrue(naivePlayer.getHand().isEmpty());
    naivePlayer.addTileToHand("G*");
    assertEquals(naivePlayer.determineMove(100), "SWAP G*");
    assertTrue(naivePlayer.getHand().isEmpty());
    naivePlayer.addTileToHand("Y*");
    naivePlayer.addTileToHand("Bs");
    naivePlayer.addTileToHand("Bo");
    assertTrue(naivePlayer.determineMove(100).equals("MOVE Bo 90 91"));
    assertEquals(naivePlayer.numberOfTilesInHand(), 2);
    
  }

}
