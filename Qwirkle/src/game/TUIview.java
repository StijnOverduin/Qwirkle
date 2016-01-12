package game;

import java.util.Scanner;

public class TUIview {

	private LocalPlayer player;

	public TUIview() {

	}

	public void deterMineMove() {
		Scanner in = new Scanner(System.in);
		String next = null;
		if (in.hasNextLine()) {
			next = in.nextLine();
			switch (in.next()) {
			case "MOVE":
				for (int i = 0; i < 5; i++) {
					if (in.hasNext()) {
						String line = in.next();
						Color color = Color.getColorFromCharacter(line.charAt(0));
						Shape shape = Shape.getShapeFromCharacter(line.charAt(1));
						Tile tile = new Tile(color, shape);

						if (in.hasNext()) {
							int rij = Integer.parseInt(in.next());
							if (in.hasNext()) {
								int colom = Integer.parseInt(in.next());
								player.makeMove(rij, colom, tile);
							}
						}
					}
				}
			case "SWAP":
				for (int i = 0; i < 6; i++) {
					if (in.hasNext()) {
						String tile = in.next();
						Color color = Color.getColorFromCharacter(tile.charAt(0));
						Shape shape = Shape.getShapeFromCharacter(tile.charAt(1));
						Tile tile1 = new Tile(color, shape);
						player.removeTileFromHand(tile1);
						if (player.NumberOfTilesInHand() < 6) {
							
						}

					}
				}
			default:
				System.out.println("That's not a valid command");

			}
		}
	}
}
