package tests;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import org.junit.Assert;

import game.Board;
import game.Color;
import game.Shape;
import game.Tile;

public class BoardTest {
	private Board board;

	@Before
	public void setUp() {
		board = new Board();
	}

	@Test
	public void testGetTile() {
		assertTrue((board.getTile(91, 91).toString()).equals(new Tile(Color.EMPTY, Shape.EMPTY).toString()));
		assertTrue((board.getTile(0, 0).toString()).equals(new Tile(Color.EMPTY, Shape.EMPTY).toString()));
		assertTrue((board.getTile(182, 182).toString()).equals(new Tile(Color.EMPTY, Shape.EMPTY).toString()));
		assertTrue((board.getTile(34, 112).toString()).equals(new Tile(Color.EMPTY, Shape.EMPTY).toString()));

		board.setTile(92, 83, new Tile(Color.ROOD, Shape.DIAMANT));
		assertTrue((board.getTile(92, 83).toString()).equals(new Tile(Color.ROOD, Shape.DIAMANT).toString()));
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

}
