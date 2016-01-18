package game;

import java.util.ArrayList;

public class Game {
	
	private Board board;
	private ArrayList<String> jar;
	private Color[] color = Color.values();
	private Shape[] shape = Shape.values();
	
	

	public Game(Board board) {
		this.board = board;
		fillJar();
		
		
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
					String tile = "" + Fcolor + Fshape;
					addTileToJar(tile);
				}
			}

		}
	}

	public void removeTileFromJar(String tile) {
		jar.remove(tile);
	}

	public void addTileToJar(String tile) {
		jar.add(tile);
	}


	public int tilesInJar() {
		return jar.size();
	}

	public String giveRandomTile() {
		if (jar.size() != 0) {
			int random = (int) Math.round(Math.random() * jar.size());
			String newTile = jar.get(random);
			return newTile;
		} else {
			return null;
		}
	}
	
	

}
