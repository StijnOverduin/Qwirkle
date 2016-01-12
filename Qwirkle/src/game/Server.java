package game;

import java.util.ArrayList;

public class Server {
	
	public LocalPlayer lplayer;
	public ArrayList<String> jar;

	public Server() {
		jar = new ArrayList<String>();
		
	}
	
	public void fillJar() {
		
	}
	
	public void removeTileFromJar(Tile tile) {
		jar.remove(tile);
	}
	
	public void addTileToJar(Tile tile) {
		Color color = tile.getColor(tile);
		Shape shape = tile.getShape(tile);
		if (jar.size() < 109) {
			jar.add("" + color + shape);
		}
	}
	
	public int TilesInJar() {
		return jar.size();
	}
	
	public Tile GiveRandomTile() {
		for (int i = 0; i < 6; i++) {
			if (lplayer.NumberOfTilesInHand() < 6) {
				
			}
		}
		return null;
	}
	
}
