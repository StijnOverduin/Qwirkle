package game.tiles;

public enum Color {
  ROOD('R'), ORANJE('O'), BLAUW('B'), GEEL('Y'), GROEN('G'), PAARS('P'), EMPTY('e');

  char id;

  Color(char ch) {
    id = ch;
  }

  public static Color getColorFromCharacter(char charac) {
    switch (charac) {
      case 'R':
        return Color.ROOD;
      case 'O':
        return Color.ORANJE;
      case 'B':
        return Color.BLAUW;
      case 'Y':
        return Color.GEEL;
      case 'G':
        return Color.GROEN;
      case 'P':
        return Color.PAARS;
      default:
        return Color.EMPTY;
    }
  }

  public char getChar() {
    return id;
  }
}
