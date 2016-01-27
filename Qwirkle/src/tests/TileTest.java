package tests;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import game.tiles.Color;
import game.tiles.Shape;
import game.tiles.Tile;

public class TileTest {

	private Tile tile;
	private Tile tile2;

	@Before
	public void setUp() {
		tile = new Tile(Color.ROOD, Shape.CIRKEL);
		tile2 = new Tile(Color.BLAUW, Shape.VIERKANT);
	}

	@Test
	public void testToString() {
		assertTrue(tile.toString().equals("Ro"));
		assertFalse(tile.toString().equals("Bs"));
	}

	@Test
	public void testGetColor() {
		assertTrue(tile.getColor().equals(Color.ROOD));
		assertFalse(tile2.getColor().equals(Color.ROOD));
	}

	@Test
	public void testGetShape() {
		assertTrue(tile.getShape().equals(Shape.CIRKEL));
		assertFalse(tile2.getShape().equals(Shape.CIRKEL));
	}

}
