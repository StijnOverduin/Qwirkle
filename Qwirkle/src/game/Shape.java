package game;

public enum Shape {
	o('o'), d('d'), s('s'),c('c'), x('x'), S('*'), E('e');

	char id;
	
	 Shape(char c) {
		id = c;
	}
	
	public static Shape getShapeFromCharacter(char charac) {
		switch(charac) {
		case 'o':
			return Shape.o;
		case 'd':
			return Shape.d;
		case's':
			return Shape.s;
		case'c':
			return Shape.c;
		case'x':
			return Shape.x;
		case'*':
			return Shape.S;
			default:
				return Shape.E;
		}
	}

	}
