package game;

public enum Color {
	R('R'), O('O'), B('B'), Y('Y'), G('G'),P('P'), E('e');

	char id;
	
	Color(char c) {
		id = c;
	}
	
	public static Color getColorFromCharacter(char charac) {
		switch(charac) {
		case 'R':
			return Color.R;
		case 'O':
			return Color.O;
		case'B':
			return Color.B;
		case'Y':
			return Color.Y;
		case'G':
			return Color.G;
		case'P':
			return Color.P;
			default:
				return Color.E;
		}
	}
}
