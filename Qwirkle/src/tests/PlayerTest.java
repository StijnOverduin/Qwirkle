package tests;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import game.Board;
import game.player.HumanPlayer;
import game.player.Naive;
import game.player.Player;

import static org.junit.Assert.*;

public class PlayerTest {

	private Player humanPlayer;
	private Player naivePlayer;
	private Board board;
	private ArrayList<String> hand;

	@Before
	public void setUp() throws Exception {
		board = new Board();
		humanPlayer = new HumanPlayer(board, "Stijn", 0);
		naivePlayer = new Naive(board, "Computer", 1);
		hand = new ArrayList<String>();
	}

	@Test
	public void testAddTilesToHandHumanPlayer() {
		humanPlayer.addTileToHand("Ro");
		hand.add("Ro");
		assertEquals(humanPlayer.getHand(), hand);
		humanPlayer.addTileToHand("Bo");
		hand.add("Bo");
		assertEquals(humanPlayer.getHand(), hand);
		humanPlayer.addTileToHand("Y*");
		hand.add("Y*");
		humanPlayer.addTileToHand("Rs");
		hand.add("Rs");
		humanPlayer.addTileToHand("P*");
		hand.add("P*");
		assertEquals(humanPlayer.getHand(), hand);
		humanPlayer.addTileToHand("Oo");
		hand.add("Oo");
		humanPlayer.addTileToHand("Bs");
		hand.add("Bs");
		assertEquals(humanPlayer.getHand(), hand);

	}

	@Test
	public void testAddTilesToHandNaivePlayer() {
		naivePlayer.addTileToHand("Go");
		hand.add("Go");
		assertEquals(naivePlayer.getHand(), hand);
		naivePlayer.addTileToHand("B*");
		hand.add("B*");
		assertEquals(naivePlayer.getHand(), hand);
		naivePlayer.addTileToHand("Y*");
		hand.add("Y*");
		naivePlayer.addTileToHand("Go");
		hand.add("Go");
		naivePlayer.addTileToHand("P*");
		hand.add("P*");
		assertEquals(naivePlayer.getHand(), hand);
		naivePlayer.addTileToHand("Oo");
		hand.add("Oo");
		naivePlayer.addTileToHand("Bs");
		hand.add("Bs");
		assertEquals(naivePlayer.getHand(), hand);
	}

	@Test
	public void testNumberOfTilesInHand() {
		assertEquals(humanPlayer.numberOfTilesInHand(), 0);
		humanPlayer.addTileToHand("P*");
		assertEquals(humanPlayer.numberOfTilesInHand(), 1);
		humanPlayer.addTileToHand("Y*");
		humanPlayer.addTileToHand("Rs");
		humanPlayer.addTileToHand("P*");
		assertEquals(humanPlayer.numberOfTilesInHand(), 4);
		humanPlayer.addTileToHand("Oo");
		humanPlayer.addTileToHand("P*");
		assertEquals(humanPlayer.numberOfTilesInHand(), 6);
	}

}
