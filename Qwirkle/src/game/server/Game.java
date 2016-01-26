package game.server;

import game.Board;
import game.player.HumanPlayer;
import game.player.Player;
import game.player.PlayerWrapper;
import game.tiles.Color;
import game.tiles.Shape;
import game.tiles.Tile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Game {
	//TODO game stoppen als er nog maar 1 speler inzit...

	private List<String> jar;
	private List<ClientHandler> threads;
	private List<PlayerWrapper> players;
	private Color[] allColors = Color.values();
	private Shape[] allShapes = Shape.values();
	private Board board;
	private int moveLength;
	private int turn;
	private int numberOfPlayers;
	private boolean activeGame;
	private boolean horizontalTrue;
	private boolean gotTileNextToIt;
	private boolean nextPlayer;

	/**
	 * Creates a game instantiating the variables: threads, players, board, jar
	 * and activeGame;
	 */
	public Game() {
		threads = new ArrayList<ClientHandler>();
		players = new ArrayList<PlayerWrapper>();
		board = new Board();
		jar = new ArrayList<String>();
		activeGame = false;

	}

	/**
	 * If a client send a wrong message to the server this method ensures that
	 * the client is kicked; every other player will get a message with who was
	 * kicked and the number of tiles that went back into the jar.
	 * 
	 * @param client
	 * @param message
	 */
	// @ requires client != null;
	// @ requires message != null;
	private void kickHandler(ClientHandler client, String message) {
		if (players.get(turn).getPlayer() != null) {
			if (!(players.get(turn).getPlayer().getHand().isEmpty())) {
				if (threads.size() > 1) {
					broadcast("KICK " + players.get(turn).getPlayerNumber() + " "
							+ players.get(turn).getPlayer().numberOfTilesInHand() + " " + message);
					tilesBackToStack(players.get(turn).getPlayer());
					client.shutDown();
					updateTurn();
				} else {
					client.sendMessage("KICK " + message);
					client.shutDown();
					System.out.println("Kicked the last Player");
				}
			} else {
				if (threads.size() > 1) {
					broadcast("KICK " + players.get(turn).getPlayerNumber() + " "
							+ players.get(turn).getPlayer().numberOfTilesInHand() + " " + message);
					client.shutDown();
					updateTurn();
				} else {
					client.sendMessage("KICK " + message);
					client.shutDown();
					System.out.println("Kicked the last Player");
				}
			}
		} else {
			client.shutDown();
		}
	}

	/**
	 * This method updates the turn whenever it is called.
	 */

	public void updateTurn() {
		if (activeGame) {
		numberOfPlayers = players.size();
		turn = nextPlayer ? turn : (turn + 1) % numberOfPlayers;
		while (players.get(turn) == null) {
			turn = (turn + 1) % numberOfPlayers;
		}
		
		if (players.size() > 1 && !(checkForMoves(players.get(turn).getPlayer(), board).equals("No options left"))) {
			nextPlayer = false;
			broadcast("NEXT " + players.get(turn).getPlayerNumber());

		} else {
			if (!checkAllPlayers()) {
				endGame();
			} else {
				updateTurn();
			}
		}
		}

	}

	/**
	 * Checks if one of the players can still make a move. If no one can, it
	 * returns false, otherwise it returns true.
	 * 
	 * @return
	 */
	public boolean checkAllPlayers() {
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i) != null) {
			if (!(checkForMoves(players.get(i).getPlayer(), board).equals("No options left"))) {
				turn = (turn + 1) % numberOfPlayers;
				nextPlayer = true;
				return true;
			}
		}
		}
		return false;
	}
	
	public void setNextPlayerTrue() {
		nextPlayer = true;
	}

	/**
	 * Checks if the string is consists of A-Z and a-z and if the string is
	 * longer than 1 character and shorter than 17 chars.
	 * 
	 * @param name
	 * @return
	 */
	// @ requires name != null;
	/* @ pure */ public boolean checkName(String name) {
		if (!(name.matches(".*[^a-zA-Z].*")) && name.length() > 0 && name.length() <= 16) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * This method reads the input of the client, provided by the clientHandler.
	 * In the case "HELLO" it will return a message to the client stating his
	 * name and player number. It creates an object of player with the specified
	 * name and fills the player wrapper. If 4 players have connected with this
	 * game, the game will start.
	 * 
	 * In the case of "MOVE" it reads the input send by the client, will first
	 * check if the move is valid, if it is it will modify the board and the
	 * hand of the player according to the move, and send the player a message
	 * with his new tiles. After the changes it will broadcast to all players
	 * what the move was and update the turn. If it is not a valid move, it will
	 * kick the client with the kickHandler method.
	 * 
	 * In the case of "SWAP" it will check if the player wants to swap more
	 * tiles than the jar has left. If the player does this, he will be kicked
	 * with the kickHandler method. Otherwise it will modify the hand of the
	 * player according to the move, and send the player a message with the new
	 * tiles. After that it will broadcast the turn to the other players and
	 * update the turn.
	 * 
	 * In the case of default it will instantly kick the client with the
	 * kickHandler method because the input is not something the server can
	 * recognize.
	 * 
	 * @param message
	 * @param client
	 */
	// @ requires message != null;
	// @ requires client != null;
	public synchronized void readInput(String message, ClientHandler client) {
		String input = message;
		String[] splittedInput = message.split(" ");
		if (splittedInput.length > 1) {
			switch (splittedInput[0]) {
				case "HELLO":
					if (!(players.get(client.getClientNumber()).getInGame())) {
						if (checkName(splittedInput[1])) {
							client.sendMessage("WELCOME " + splittedInput[1] + " " + client.getClientNumber());
							Player player = new HumanPlayer(board, splittedInput[1], client.getClientNumber());
							fillWrapper(client, player);
							if (players.size() == 4) {
								startGame();
							}
						} else {
							client.sendMessage("Name doesn't consist of [a-z][A-Z] or is longer than 16 chars");
							return;
						}
						break;
					}
					break;
				case "MOVE":
					if (client.getClientNumber() == turn) {
						moveLength = splittedInput.length;
						int nrMoves = (moveLength / 3);
						if (((moveLength - 1) % 3 != 0)) {
							kickHandler(client, "You were kicked, maybe you forgot a tile or a coord?");
							return;
						} else {
							for (int i = 0; i < nrMoves; i++) {
								if (!(players.get(turn).getPlayer().getHand().contains(splittedInput[(1 + i * 3)]))) {
									kickHandler(client, "Tile " + splittedInput[(1 + i * 3)] + " not in possession");
									return;
								}
							}
							for (int i = 0; i < nrMoves; i++) {
	
								String row = splittedInput[2];
								if (!splittedInput[(2 + i * 3)].equals(row)) {
									for (int a = 0; a < nrMoves; a++) {
										String col = splittedInput[3];
										if (!splittedInput[(3 + a * 3)].equals(col)) {
											kickHandler(client, "You were kicked, tiles are not in a straight line");
											return;
	
										}
	
									}
	
								}
	
							}
							Board deepBoard = board.deepCopy();
	
							String newTiles = "NEW";
							if (nrMoves > 1) {
								this.horizontalTrue = (splittedInput[2] == splittedInput[5]);
								for (int i = 0; i < nrMoves; i++) {
									Color color = Color.getColorFromCharacter(splittedInput[(1 + i * 3)].charAt(0));
									Shape shape = Shape.getShapeFromCharacter(splittedInput[(1 + i * 3)].charAt(1));
									try {
										if (deepBoard.isValidMove(Integer.parseInt(splittedInput[(2 + i * 3)]),
												Integer.parseInt(splittedInput[(3 + i * 3)]), new Tile(color, shape))) {
											deepBoard.setTile(Integer.parseInt(splittedInput[(2 + i * 3)]),
													Integer.parseInt(splittedInput[(3 + i * 3)]), new Tile(color, shape));
											players.get(turn).getPlayer().removeTileFromHand(splittedInput[(1 + i * 3)]);
											String randomTile = giveRandomTile();
											newTiles = newTiles.concat(" " + randomTile);
											players.get(turn).getPlayer().addTileToHand(randomTile);
	
											// TODO catch parseint excep
											players.get(turn).setScore(calcScoreCrossedTiles(splittedInput, i, deepBoard)
													+ players.get(turn).getScore());
	
										} else {
											kickHandler(client, "Not a valid move");
											return;
										}
									} catch (NumberFormatException e) {
										System.out.println("Couldn't parse the integer of the given string");
									}
								}
								players.get(turn).setScore(
										calcScoreAddedTiles(splittedInput, deepBoard) + players.get(turn).getScore());
	
							} else if (nrMoves == 1) {
								Color color = Color.getColorFromCharacter(splittedInput[1].charAt(0));
								Shape shape = Shape.getShapeFromCharacter(splittedInput[1].charAt(1));
								try {
									if (deepBoard.isValidMove(Integer.parseInt(splittedInput[2]),
											Integer.parseInt(splittedInput[3]), new Tile(color, shape))) {
										deepBoard.setTile(Integer.parseInt(splittedInput[2]),
												Integer.parseInt(splittedInput[3]), new Tile(color, shape));
	
										players.get(turn).getPlayer().removeTileFromHand(splittedInput[1]);
										String randomTile = giveRandomTile();
										newTiles = newTiles.concat(" " + randomTile);
										players.get(turn).getPlayer().addTileToHand(randomTile);
										players.get(turn).setScore(calcScoreHorAndVer(splittedInput, deepBoard)
												+ players.get(turn).getScore());
									} else {
										kickHandler(client, "Not a valid move");
										return;
									}
								} catch (NumberFormatException e) {
									System.out.println("Couldn't parse the integer in the given string");
								}
	
							}
	
							board = deepBoard;
							if (newTiles.contains("empty")) {
								client.sendMessage("NEW empty");
							} else {
								client.sendMessage(newTiles);
							}
							broadcast("TURN " + players.get(turn).getPlayerNumber() 
											+ " " + input.substring(5));
							updateTurn();
						}
						break;
					} else {
						broadcast("KICK " + client.getClientNumber() + " "
								+ players.get(client.getClientNumber()).getPlayer().numberOfTilesInHand() + " It was not your turn");
						tilesBackToStack(players.get(client.getClientNumber()).getPlayer());
						client.shutDown();
					}
					break;
				case "SWAP":
					if(!(board.getIsFirstMove())) {
					if (client.getClientNumber() == turn) {
						int tilenr = 1;
						String newTiles = "";
						while (tilenr < splittedInput.length) {
							if (tilesInJar() >= (splittedInput.length - 1)) {
								String tile = splittedInput[tilenr];
								if (players.get(turn).getPlayer().getHand().contains(tile)) {
									players.get(turn).getPlayer().removeTileFromHand(tile);
								} else {
									kickHandler(client, "Tile " + tile + " was not in your hand");
									return;
								}
								String randomTile = giveRandomTile();
								players.get(turn).getPlayer().addTileToHand(randomTile);
								newTiles = newTiles.concat(" " + randomTile);
								tilenr++;
							} else {
								kickHandler(client, "Tried to swap while jar was empty");
								return;
							}
						}
						client.sendMessage("NEW" + newTiles);
						broadcast("TURN empty");
						updateTurn();
					} else {
						broadcast("KICK " + client.getClientNumber() + " "
								+ players.get(client.getClientNumber()).getPlayer().numberOfTilesInHand() + " It was not your turn");
						tilesBackToStack(players.get(client.getClientNumber()).getPlayer());
						client.shutDown();
						break;
					}
					} else {
						kickHandler(client, "First move can't be a SWAP");
					}
					break;
				default:
					kickHandler(client, "That was not a valid command");
					break;
			}
		} else {
			kickHandler(client, "Invalid move format");
		}
	}

	/**
	 * TODO Jotte doe jij de score?
	 * 
	 * @param split
	 * @param index
	 * @param deepBoard
	 * @return
	 */
	// @ requires split != null;
	// @ requires index > 0;
	// @ requires deepBoard != null;
	public int calcScoreCrossedTiles(String[] split, int index, Board deepBoard) {
		int dx = horizontalTrue ? 0 : 1; // als het een horizontale rij is dan
		// gaat hij verticaal checken elke
		// keer
		int dy = dx == 1 ? 0 : 1;
		int row = Integer.parseInt(split[2 + 3 * index]);
		int col = Integer.parseInt(split[3 + 3 * index]);
		this.gotTileNextToIt = false;
		int dupRow = row;
		int dupCol = col;

		int lineLength = 0;
		while (!deepBoard.isEmpty(row + dx, col + dy)) {
			lineLength++;
			row += dx;
			col += dy;
			this.gotTileNextToIt = true;
		}
		row = dupRow;
		col = dupCol;
		while (!deepBoard.isEmpty(row - dx, col - dy)) {
			lineLength++;
			row -= dx;
			col -= dy;
			this.gotTileNextToIt = true;
		}
		int addToScore = 0;
		addToScore = gotTileNextToIt ? lineLength + 1 : 0;
		return addToScore;
	}

	/**
	 * This method does something TODO Jotte doe jij de score?
	 * 
	 * @param split
	 * @param deepBoard
	 * @return
	 */
	// @ requires split != null;
	// @ requires deepBoard != null;
	public int calcScoreAddedTiles(String[] split, Board deepBoard) {
		int dx = horizontalTrue ? 1 : 0; // als het een horizontale rij is dan
		// gaat hij verticaal checken elke
		// keer
		int dy = dx == 1 ? 0 : 1;
		int row = Integer.parseInt(split[2]);
		int col = Integer.parseInt(split[3]);
		int dupRow = row;
		int dupCol = col;

		int lengteLijn = 0;
		while (!deepBoard.isEmpty(row + dx, col + dy)) {
			lengteLijn++;
			row += dx;
			col += dy;
		}
		row = dupRow;
		col = dupCol;
		while (!deepBoard.isEmpty(row - dx, col - dy)) {
			lengteLijn++;
			row -= dx;
			col -= dy;
		}
		int addToScore = lengteLijn == 5 ? lengteLijn + 7 : lengteLijn + 1;
		return addToScore;
	}

	/**
	 * TODO Jotte doe jij de score?
	 * 
	 * @param split
	 * @param deepBoard
	 * @return
	 */
	// @ requires split != null;
	// @ requires deepBoard != null;
	public int calcScoreHorAndVer(String[] split, Board deepBoard) {
		if (board.getIsFirstMove() == true) {
			return 1;
		} else {
			int dx = 1;
			int dy = 0;
			int row = Integer.parseInt(split[2]);
			int col = Integer.parseInt(split[3]);
			int dupRow = row;
			int dupCol = col;
			boolean horRow = false;
			int lineLength = 0;
			while (!deepBoard.isEmpty(row + dx, col + dy)) {
				lineLength++;
				row += dx;
				col += dy;
				horRow = true;
			}
			row = dupRow;
			col = dupCol;
			while (!deepBoard.isEmpty(row - dx, col - dy)) {
				lineLength++;
				row -= dx;
				col -= dy;
				horRow = true;
			}
			lineLength = (lineLength == 5) ? lineLength + 6 : lineLength;
			dx = 0;
			dy = 1;
			row = dupRow;
			col = dupCol;
			int addToScore = lineLength;
			lineLength = 0;
			boolean verRow = false;
			while (!deepBoard.isEmpty(row + dx, col + dy)) {
				lineLength++;
				row += dx;
				col += dy;
				verRow = true;
			}
			row = dupRow;
			col = dupCol;
			while (!deepBoard.isEmpty(row - dx, col - dy)) {
				lineLength++;
				row -= dx;
				col -= dy;
				verRow = true;
			}
			lineLength = (lineLength == 5) ? lineLength + 6 : lineLength + 1;
			addToScore += lineLength;
			addToScore = horRow && verRow ? addToScore + 1 : addToScore;
			return addToScore;
		}

	}

	/**
	 * Sends a message to all clientHandlers in the threads list.
	 * 
	 * @param msg
	 */
	// @ requires msg != null;
	public void broadcast(String msg) {
		if (threads.size() != 0) {
			for (int i = 0; i < threads.size(); i++) {
				threads.get(i).sendMessage(msg);
			}
		} else {
			System.out.println("No more players in this game to broadcast to");
		}
	}

	/**
	 * This method will put all the tiles from the given player back in the jar
	 * and remove it from the players hand.
	 * 
	 * @param player
	 */
	// @ requires player != null;
	public void tilesBackToStack(Player player) {
		for (int q = 0; q < player.getHand().size(); q++) {
			addTileToJar(player.getHand().get(q));
			player.removeTileFromHand(player.getHand().get(q));

		}
	}

	/**
	 * This method removes a player from the players list.
	 * 
	 * @param player
	 */
	// @ requires player != null;
	public void removePlayerWrapperFromList(PlayerWrapper player) {
		if (players.contains(player)) {
			players.remove(player);
		}
	}

	/**
	 * This method fills the object playerWrapper.
	 * 
	 * @param client
	 * @param player
	 */
	// @ requires client != null;
	// @ requires player != null;
	public void fillWrapper(ClientHandler client, Player player) {
		players.get(client.getClientNumber()).setPlayer(player);
		players.get(client.getClientNumber()).setScore(0);
		players.get(client.getClientNumber()).setPlayerNumber(client.getClientNumber());
		players.get(client.getClientNumber()).setInGameTrue();
	}

	/**
	 * Fills the jar with three times every color and shape combination.
	 */
	public void fillJar() {
		Shape shape = null;
		Color color = null;
		for (int i = 0; i < 3; i++) {
			for (int q = 0; q < allColors.length - 1; q++) {
				color = allColors[q];
				for (int w = 0; w < allShapes.length - 1; w++) {
					shape = allShapes[w];
					String tile = "" + color.getChar() + shape.getChar();
					addTileToJar(tile);
				}
			}

		}
	}

	/**
	 * Removes the specified tile from the jar list.
	 * 
	 * @param tile
	 */
	// @ requires tile != null;
	public void removeTileFromJar(String tile) {
		jar.remove(tile);
	}

	/**
	 * Adds the specified tile to the jar list.
	 * 
	 * @param tile
	 */
	// @ requires tile != null;
	public void addTileToJar(String tile) {
		jar.add(tile);
	}

	/**
	 * Returns how many tiles are left in the jar.
	 * 
	 * @return
	 */
	/* @ pure */ public int tilesInJar() {
		return jar.size();
	}

	/**
	 * Returns a random object from the jar list.
	 * 
	 * @return
	 */
	public String giveRandomTile() {
		if (jar.size() != 0) {
			int random = (int) Math.round(Math.random() * (jar.size() - 1));
			String newTile = jar.get(random);
			jar.remove(newTile);
			return newTile;
		} else {
			return "empty";
		}
	}

	/**
	 * Returns the boolean activeGame.
	 * 
	 * @return
	 */
	/* @ pure */ public boolean isActive() {
		return activeGame;
	}

	/**
	 * Sets the activeGame boolean true.
	 */
	public void becomeActive() {
		activeGame = true;
	}

	/**
	 * Switches the game to active by calling the becomeActive method, fills the
	 * jar by calling the fillJar method and gives each player in the players
	 * list 6 random tiles from the jar. After that it will determine who gets
	 * the first move, updates the turn accordingly and broadcasts it to all
	 * players.
	 */
	public void startGame() {
		becomeActive();
		fillJar();
		String names = "";
		for (int r = 0; r < players.size(); r++) {
			if (players.get(r) == null) {

			} else {
				names = names
						.concat(" " + players.get(r).getPlayer().getName() + " " + players.get(r).getPlayerNumber());
			}
		}
		broadcast("NAMES" + names + " " + 100);
		ArrayList<String[]> hands = new ArrayList<String[]>();
		for (int t = 0; t < threads.size(); t++) {
			String tiles = "";
			for (int q = 0; q < 6; q++) {
				String randomTile = giveRandomTile();
				tiles = tiles.concat(" " + randomTile);

			}
			threads.get(t).sendMessage("NEW" + tiles);
			String[] splittedTiles = tiles.split(" ");
			hands.add(splittedTiles);
		}
		int counter = 0;
		for (PlayerWrapper playerWrapper : players) {
			for (int w = 1; w < 7; w++) {
				if (playerWrapper == null) {

				} else {
					playerWrapper.getPlayer().addTileToHand(hands.get(counter)[w]);
				}
			}
			if (playerWrapper != null) {
				counter++;
			}
		}
		int nrMoves = 0;
		for (int w = 0; w < players.size(); w++) {
			if (players.get(w) == null) {

			} else {
				nrMoves = Math.max(nrMoves, getLongestStreak(players.get(w).getPlayer(), board));
			}
		}
		for (int a = 0; a < players.size(); a++) {
			if (players.get(a) == null) {

			} else {
				if (nrMoves == getLongestStreak(players.get(a).getPlayer(), board)) {
					turn = players.get(a).getPlayerNumber();
				}
			}
		}
		broadcast("NEXT " + players.get(turn).getPlayerNumber());
	}

	/**
	 * Adds a clientHandler to the threads list. It also creates a playerWrapper
	 * object and puts this in the players list.
	 * 
	 * @param client
	 */
	// @ requires client != null;
	public void addHandler(ClientHandler client) {
		client.setClientNumber(players.size());
		threads.add(client);
		PlayerWrapper playerWrapper = new PlayerWrapper();
		players.add(playerWrapper);
	}

	/**
	 * Removes the clientHandler from the threads list and broadcasts which
	 * client disconnected.
	 * 
	 * @param client
	 */
	// @ requires client != null;
	public void removeHandler(ClientHandler client) {
		if (players.get(client.getClientNumber()).getPlayer() != null) {
			for (int i = 0; i < threads.size(); i++) {
				if (threads.get(i) == client) {
					threads.remove(i);
					players.set(i, null);
				}
			}
		} else {
			threads.remove(client);
		}
	}

	/**
	 * Determines the longest move the given player can make on the given board,
	 * and returns an integer representing the number of tiles the player can
	 * place. Only works on the first move placed on the board. Used to
	 * determine which player can begin.
	 * 
	 * @param player
	 * @param board
	 * @return
	 */
	// @ requires player != null;
	// @ requires board != null;
	public int getLongestStreak(Player player, Board board) {
		int nrMoves = 0;
		int maxMoves = 0;
		int miny = board.getMiny();
		int maxy = board.getMaxy();
		int minx = board.getMinx();
		int maxx = board.getMaxx();
		Color color = null;
		Shape shape = null;
		boolean gotMove;
		Player deepPlayer = new HumanPlayer(board, player.getName(), player.getPlayerNumber());

		for (int firstMove = 0; firstMove < 6; firstMove++) {
			nrMoves = 0;
			Board deepBoard = board.deepCopy();
			deepPlayer.getHand().addAll(player.getHand());

			color = Color.getColorFromCharacter(deepPlayer.getHand().get(firstMove).charAt(0));
			shape = Shape.getShapeFromCharacter(deepPlayer.getHand().get(firstMove).charAt(1));
			deepBoard.setTile(91, 91, new Tile(color, shape));
			deepPlayer.removeTileFromHand("" + color.getChar() + shape.getChar());
			nrMoves += 1;

			for (int i = 0; i < deepPlayer.getHand().size(); i++) {
				gotMove = true;
				String currentTile = deepPlayer.getHand().get(i);
				for (int row = maxy; row <= miny; row++) {
					if (gotMove == true) {
						for (int col = minx; col <= maxx; col++) {
							color = Color.getColorFromCharacter(currentTile.charAt(0));
							shape = Shape.getShapeFromCharacter(currentTile.charAt(1));
							if (deepBoard.isValidMove(row, col, new Tile(color, shape))) {
								deepPlayer.removeTileFromHand("" + color.getChar() + shape.getChar());
								nrMoves += 1;
								gotMove = false;
								break;
							}
						}
					}
				}
			}
			deepPlayer.getHand().removeAll(deepPlayer.getHand());
			maxMoves = Math.max(nrMoves, maxMoves);
		}
		return maxMoves;
	}

	/**
	 * Checks if the given player can make a move on the given board, if the jar
	 * isn't empty it will suggest to swap a tile, otherwise it will return that
	 * there are no options left.
	 * 
	 * @param player
	 * @param board
	 * @return
	 */
	// @ requires player != null;
	// @ requires board != null;
	public String checkForMoves(Player player, Board board) {
		if (player.getHand().size() > 0) {
		String move = "";
		int miny = board.getMiny();
		int maxy = board.getMaxy();
		int minx = board.getMinx();
		int maxx = board.getMaxx();

		Board deepBoard = board.deepCopy();
		Player deepPlayer = new HumanPlayer(board, player.getName(), player.getPlayerNumber());
		deepPlayer.getHand().addAll(player.getHand());

		Color colorFirstMove = Color.getColorFromCharacter(deepPlayer.getHand().get(0).charAt(0));
		Shape shapeFirstMove = Shape.getShapeFromCharacter(deepPlayer.getHand().get(0).charAt(1));

		if (board.isValidMove(91, 91, new Tile(colorFirstMove, shapeFirstMove))) {
			move = move.concat(colorFirstMove.getChar() + shapeFirstMove.getChar() + " " + 91 + " " + 91
					+ "Just place a tile on 91 91");
			deepPlayer.removeTileFromHand(deepPlayer.getHand().get(0));
			return move;

		} else {
			for (int i = 0; i < deepPlayer.getHand().size(); i++) {
				String currentTile = deepPlayer.getHand().get(i);
				for (int row = maxy; row <= miny; row++) {
					for (int col = minx; col <= maxx; col++) {
						Color color = Color.getColorFromCharacter(currentTile.charAt(0));
						Shape shape = Shape.getShapeFromCharacter(currentTile.charAt(1));
						if (deepBoard.isValidMove(row, col, new Tile(color, shape))) {
							move = move.concat(color.getChar() + shape.getChar() + " " + row + " " + col
									+ " You could place that tile");
							deepPlayer.removeTileFromHand("" + color.getChar() + shape.getChar());
							return move;
						}
					}
				}
			}
			if (jar.size() != 0) {
				move = move.concat("Try swapping a tile");
			} else {
				move = move.concat("No options left");
			}
			return move;
		}
		} else {
			return "No options left";
		}
	}

	public List<PlayerWrapper> getPlayers() {
		return players;
	}

	/**
	 * This method is called when the game has come to an end. It will calculate
	 * which player has the highest score and broadcasts the winner to every
	 * other player.
	 */
	public void endGame() {
		int score = 0;
		for (int w = 0; w < players.size(); w++) {
			if (players.get(w) != null) {
			score = Math.max(score, players.get(w).getScore());
			}
		}
		for (int a = 0; a < players.size(); a++) {
			if (players.get(a) != null) {
			if (score == players.get(a).getScore()) {
				broadcast("WINNER " + players.get(a).getPlayerNumber());
				System.out.println("WINNER Player" + players.get(a).getPlayer().getName() + " " + players.get(a).getPlayerNumber());
			}
		}
		}
	}
}
