package game;

import java.util.ArrayList;

public class Game {
	
	private Board board;
	public ArrayList<Player> players;
	public ArrayList<String> jar;
	public Color[] color = Color.values();
	public Shape[] shape = Shape.values();

	public Game(Board board) {
		players = new ArrayList<Player>();
		this.board = board;
		
	}
	
	/*
	 * Dit is een beschrijving voor de pot met tegeltjes.
	 */
	public void fillJar() {
		Shape Fshape = null;
		Color Fcolor = null;
		for (int i = 0; i < 3; i++) {
			for (int q = 0; q < color.length; q++) {
				Fcolor = color[q];
				for (int w = 0; w < shape.length; w++) {
					Fshape = shape[w];
					Tile tile = new Tile(Fcolor, Fshape);
					addTileToJar(tile);
				}
			}

		}
	}

	public void removeTileFromJar(Tile tile) {
		Color color = tile.getColor();
		Shape shape = tile.getShape();
		String removedTile = "" + color + shape;
		jar.remove(removedTile);
	}

	public void addTileToJar(Tile tile) {
		// if (networkPlayer.getHand().contains(tile)) {
		Color color = tile.getColor();
		Shape shape = tile.getShape();
		if (jar.size() < 109) {
			jar.add("" + color + shape);
		}
	}
	// }

	public int tilesInJar() {
		return jar.size();
	}

	public Tile giveRandomTile() {
		if (jar.size() != 0) {
			int random = (int) Math.round(Math.random() * jar.size());
			String newTile = jar.get(random);
			Color color = Color.getColorFromCharacter(newTile.charAt(0));
			Shape shape = Shape.getShapeFromCharacter(newTile.charAt(1));
			Tile tile = new Tile(color, shape);
			return tile;
		} else {
			return null;
		}
	}
	
	

}
