package game;

public class Tile {
	
	private Color color;
	private Shape shape;

	public Tile(Color color, Shape shape) {
		this.color = color;
		this.shape = shape;
	}
	
	public String toString() {
		return color + " " + shape;
	}
	
	public Color getColor(Tile tile) {
		return tile.color;
	}
	public Shape getShape(Tile tile) {
		return tile.shape;
	}
}
