package game.tiles;

public enum Shape {
	CIRKEL('o'), DIAMANT('d'), VIERKANT('s'), KLAVER('c'), KRUIS('x'), STER('*'), EMPTY('e');

	char id;

	Shape(char ch) {
		id = ch;
	}

	/**
	 * Converts a specific shape into a character.
	 * 
	 * @param charac
	 *            char
	 * @return the case that is used in our protocol
	 */
	public static Shape getShapeFromCharacter(char charac) {
		switch (charac) {
			case 'o':
				return Shape.CIRKEL;
			case 'd':
				return Shape.DIAMANT;
			case 's':
				return Shape.VIERKANT;
			case 'c':
				return Shape.KLAVER;
			case 'x':
				return Shape.KRUIS;
			case '*':
				return Shape.STER;
			default:
				return Shape.EMPTY;
		}
	}

	public char getChar() {
		return id;
	}

}
