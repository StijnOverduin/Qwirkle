package game;

import java.util.ArrayList;
import java.util.List;

public class Game {

	private ArrayList<String> jar;
	private Color[] color = Color.values();
	private Shape[] shape = Shape.values();
	private List<PlayerWrapper> players;
	private Board board;
	private boolean active = false;
	private int lengteMove;
	private int turn = 0;
	private int numberOfPlayers;
	private List<ClientHandler> threads;
	private boolean horizontalTrue;
	private int addToScore;
	private boolean heeftTilesErnaast;

	public Game() {
		threads = new ArrayList<ClientHandler>();
		players = new ArrayList<PlayerWrapper>();
		board = new Board();
		jar = new ArrayList<String>();

	}

	private void kickHandler(ClientHandler client, String message) {
		client.sendMessage(message);
		tilesBackToStack(players.get(turn).getPlayer());
		broadcast("KICK " + client.getClientNumber() + " " + players.get(turn).getPlayer().NumberOfTilesInHand() + " "
				+ message);
	}

	public void updateTurn() {
		numberOfPlayers = players.size();
		turn = (turn + 1) % numberOfPlayers;
	}

	public void readInput(String msg, ClientHandler client) {
		String input = msg;
		String[] split = msg.split(" ");
		switch (split[0]) {
		case "HELLO":
			client.sendMessage("WELCOME " + split[1] + " " + client.getClientNumber());
			Player player = new Player(board, split[1], client.getClientNumber());
			PlayerWrapper playerWrapper = new PlayerWrapper(player, 0, client.getClientNumber());
			players.add(playerWrapper);
			if (players.size() == 4) {
				startGame();
			}
			break;

		case "MOVE":
			lengteMove = split.length;
			int maalMoves = (lengteMove / 3);
			if ((lengteMove - 1) % 3 != 0) {
				// TODO kick with reason not correct MOVE format
			} else {
				for (int i = 0; i < maalMoves; i++) {
					if (!(players.get(turn).getPlayer().getHand().contains(split[(1 + i * 3)]))) {
						// TODO kick with reason tried to lay tile not in
						// possession
						client.sendMessage("Tile " + split[(1 + i * 3)] + " not in possession");
						tilesBackToStack(players.get(turn).getPlayer());
					}
				}
				for (int i = 0; i < maalMoves; i++) {

					String row = split[2];
					if (!split[(2 + i * 3)].equals(row)) {
						for (int a = 0; a < maalMoves; a++) {
							String col = split[3];
							if (!split[(2 + a * 3)].equals(col)) {
								// TODO kick with reason tiles not in straight
								// line
							}

						}

					}

				}
				Board deepboard = board.deepCopy();
				
				String newTiles = "NEW";
				if (lengteMove > 1) {
					this.horizontalTrue = (split[2] == split[5]);
					this.addToScore = 0;
					for (int i = 0; i < maalMoves; i++) {
						Color color = Color.getColorFromCharacter(split[(1 + i * 3)].charAt(0));
						Shape shape = Shape.getShapeFromCharacter(split[(1 + i * 3)].charAt(1));
						if (deepboard.isValidMove(Integer.parseInt(split[(2 + i * 3)]),
								Integer.parseInt(split[(3 + i * 3)]), new Tile(color, shape))) {
							deepboard.setTile(Integer.parseInt(split[(2 + i * 3)]),
									Integer.parseInt(split[(3 + i * 3)]), new Tile(color, shape));
							
							newTiles = newTiles.concat(" " + giveRandomTile());
							players.get(turn).getPlayer().removeTileFromHand(split[(1 + i * 3)]);
							//TODO catch parseint excep
							players.get(turn).setScore(calcScoreCrossedTiles(split, i) + players.get(turn).getScore());

						} else {
							// TODO kick with reason not valid move
						}
				} 
					players.get(turn).setScore(calcScoreAddedTiles(split) + players.get(turn).getScore());
					
				 
					
				}	else if (lengteMove == 1) {
					players.get(turn).setScore(calcScoreHorAndVer(split) + players.get(turn).getScore());
					
				}
				
				board = deepboard;
				client.sendMessage(newTiles);
				broadcast("TURN " + client.getClientNumber() + " " + input.substring(5));
			}
			break;

		case "SWAP":
			int q = 1;
			String tiles = "";
			while (q < split.length) {
				if (tilesInJar() != 0) {
					String line1 = split[q];
					if (players.get(turn).getPlayer().getHand().contains(line1)) {
						System.out.println(line1);
						players.get(turn).getPlayer().removeTileFromHand(line1);
					} else {
						kickHandler(client, "Tile " + line1 + " was not in your hand");
						break;
					}
					// TODO kick with reason: swap terwijl pot empty
					tiles = tiles.concat(" " + giveRandomTile());
					q++;
				} else {
					kickHandler(client, "Tried to swap while jar was empty");
					break;
				}
				client.sendMessage("NEW" + tiles);
				updateTurn();
				broadcast("NEXT " + players.get(turn).getPlayerNumber());
			}
		}
	}
		
		public int calcScoreCrossedTiles(String[] split, int i) {
			int dx = horizontalTrue ? 1 : 0; //als het een horizontale rij is dan gaat hij verticaal checken elke keer
			int dy = dx == 1 ? 0 : 1;
			int row = Integer.parseInt(split[2 + 3 * i]);
			int col = Integer.parseInt(split[3 + 3 * i]);
			int dupy = row;
			int dupx = col;
			
			this.heeftTilesErnaast = false;
			int lengteLijn = 0;
			while (!board.deepCopy().isEmpty(row + dx, col + dy)) {
				lengteLijn++;
				row += dx;
				col += dy;
				this.heeftTilesErnaast = true;
			}
			row = dupx;
			col = dupy;
			while (!board.deepCopy().isEmpty(row - dx, col - dy)) {
				lengteLijn++;
				row -= dx;
				col -= dy;
				this.heeftTilesErnaast = true;
			}
			this.addToScore = heeftTilesErnaast ? addToScore + lengteLijn + 1: addToScore + lengteLijn;
			return addToScore;
		}

		public int calcScoreAddedTiles(String[] split) {
			int dx = horizontalTrue ? 0 : 1; //als het een horizontale rij is dan gaat hij verticaal checken elke keer
			int dy = dx == 1 ? 0 : 1;
			int row = Integer.parseInt(split[2]);
			int col = Integer.parseInt(split[3]);
			int dupy = row;
			int dupx = col;
			

			int lengteLijn = 0;
			while (!board.deepCopy().isEmpty(row + dx, col + dy)) {
				lengteLijn++;
				row += dx;
				col += dy;
			}
			row = dupx;
			col = dupy;
			while (!board.deepCopy().isEmpty(row - dx, col - dy)) {
				lengteLijn++;
				row -= dx;
				col -= dy;
			}
			this.addToScore = heeftTilesErnaast ? addToScore + lengteLijn + 1: addToScore + lengteLijn;
			return addToScore;
		}

		public int calcScoreHorAndVer(String[] split) {
			int dx = 1; 
			int dy = 0;
			int row = Integer.parseInt(split[2]);
			int col = Integer.parseInt(split[3]);
			int dupy = row;
			int dupx = col;
			

			int lengteLijn = 0;
			while (!board.deepCopy().isEmpty(row + dx, col + dy)) {
				lengteLijn++;
				row += dx;
				col += dy;
			}
			row = dupx;
			col = dupy;
			while (!board.deepCopy().isEmpty(row - dx, col - dy)) {
				lengteLijn++;
				row -= dx;
				col -= dy;
			}
				dx = 0; 
				dy = 1;
				row = Integer.parseInt(split[2]);
				col = Integer.parseInt(split[3]);
				dupy = row;
				dupx = col;
				
				while (!board.deepCopy().isEmpty(row + dx, col + dy)) {
					lengteLijn++;
					row += dx;
					col += dy;
				}
				row = dupx;
				col = dupy;
				while (!board.deepCopy().isEmpty(row - dx, col - dy)) {
					lengteLijn++;
					row -= dx;
					col -= dy;
				}

			addToScore = lengteLijn;
			return addToScore;
		}

	public void broadcast(String msg) {
		for (int i = 0; i < players.size(); i++) {
			threads.get(i).sendMessage(msg);
		}
	}

	private void tilesBackToStack(Player player) {
		for (int q = 0; q < player.getHand().size(); q++) {
			player.removeTileFromHand(player.getHand().get(q));
			addTileToJar(player.getHand().get(q));
		}
	}

	public void setBoard(Board board) {
		this.board = board;
	}

	public Board getBoard() {
		return board;
	}

	public List<PlayerWrapper> getPlayers() {
		return players;
	}

	public void putPlayerWrapperInList(PlayerWrapper player) {
		players.add(player);
	}

	public void removePlayerWrapperFromList(PlayerWrapper player) {
		if (players.contains(player)) {
			players.remove(player);
		}
	}

	/*
	 * Dit is een beschrijving voor de pot met tegeltjes.
	 */
	public void fillJar() {
		Shape Fshape = null;
		Color Fcolor = null;
		for (int i = 0; i < 3; i++) {
			for (int q = 0; q < color.length - 1; q++) {
				Fcolor = color[q];
				for (int w = 0; w < shape.length - 1; w++) {
					Fshape = shape[w];
					String tile = "" + Fcolor.getChar() + Fshape.getChar();
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
			int random = (int) Math.round(Math.random() * (jar.size() - 1));
			String newTile = jar.get(random);
			jar.remove(newTile);
			return newTile;
		} else {
			return null;
		}
	}

	public boolean isActive() {
		return active;
	}

	public void becomeActive() {
		active = true;
	}

	public void startGame() {
		becomeActive();
		fillJar();
		String names = "";
		for (int r = 0; r < players.size(); r++) {
			names = names.concat(" " + players.get(r).getPlayer().getName() + " " + players.get(r).getPlayerNumber());
		}
		broadcast("NAMES" + names + " " + 100);
		for (int t = 0; t < players.size(); t++) {
			String tiles = "";
			for (int q = 0; q < 6; q++) {
				String randomTile = giveRandomTile();
				tiles = tiles.concat(" " + randomTile);

			}
			threads.get(t).sendMessage("NEW" + tiles);
			String[] split = tiles.split(" ");
			for (int w = 0; w < 6; w++) {
				players.get(t).getPlayer().addTilesToHand(split[w]);
			}
		}
	}

	public void addHandler(ClientHandler handler) {
		threads.add(handler);
	}

	/**
	 * Remove a ClientHandler from the collection of ClientHanlders.
	 * 
	 * @param handler
	 *            ClientHandler that will be removed
	 */
	public void removeHandler(ClientHandler handler) {
		for (int i = 0; i < threads.size(); i++) {
			if (threads.get(i) == handler) {
				threads.remove(i);
			}
		}
	}

}
