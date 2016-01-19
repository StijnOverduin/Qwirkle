package game;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

//TODO playernumber niet index in de lijst
//TODO player scores bijhouden in Game
public class Server {

	public Player player;
	private int port;
	private List<ClientHandler> threads;
	public int playerNumber = -1;
	public int lengteMove;
	private Game game;
	private Board board;
	private Map<Player, Integer> players;
	private ArrayList<String> jar;
	private Color[] color = Color.values();
	private Shape[] shape = Shape.values();

	/** Constructs a new Server object. */
	public Server(int portArg) {
		threads = new ArrayList<ClientHandler>();
		port = portArg;
		Map<Player, Integer> players = new HashMap<Player, Integer>();
	}

	/**
	 * Listens to a port of this Server if there are any Clients that would like
	 * to connect. For every new socket connection a new ClientHandler thread is
	 * started that takes care of the further communication with the Client.
	 */
	public void run() {
		try {
			ServerSocket serverSock = new ServerSocket(port);
			while (true) {
				Socket sock = serverSock.accept();
				// create new thread to handle
				ClientHandler c = new ClientHandler(this, sock, getPlayerNumber());
				addHandler(c);
				c.start();
			}

		} catch (IOException e) {
			System.out.println(e);

		}

	}

	public void readInput(String msg, ClientHandler client) {
		String input = msg;
		String[] split = msg.split(" ");
		switch (split[0]) {
		case "HELLO":
			client.sendMessage("WELCOME " + split[1] + " " + client.getClientNumber());
			player = new Player(board, split[1], client.getClientNumber());
			Scanner ins = new Scanner(System.in);
			startGame();
			break;

		case "MOVE":
			lengteMove = split.length;
			int maalMoves = (lengteMove / 3);
			if ((lengteMove - 1) % 3 != 0) {
				// TODO kick with reason not correct MOVE format
			} else {
				for (int i = 0; i < maalMoves; i++) {
					if (!player.getHand().contains(split[(1 + i * 3)])) {
						// TODO kick with reason tried to lay tile not in
						// possession
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
				board.deepCopy();
				for (int i = 0; i < maalMoves; i++) {
					Color color = Color.getColorFromCharacter(split[(1 + i * 3)].charAt(0));
					Shape shape = Shape.getShapeFromCharacter(split[(1 + i * 3)].charAt(1));
					if (board.isValidMove(Integer.parseInt(split[(2 + i * 3)]), Integer.parseInt(split[(3 + i * 3)]),
							new Tile(color, shape))) {
						board.deepCopy().setTile(Integer.parseInt(split[(2 + i * 3)]),
								Integer.parseInt(split[(3 + i * 3)]), new Tile(color, shape));
					} else {
						// TODO kick with reason not valid move
					}
				}

				broadcast("TURN " + client.getClientNumber() + " " + input.substring(5));
			}
			// TODO catch parseInt
			break;

		case "SWAP":
			int q = 1;
			String tiles = "";
			while (q < split.length - 1) {
				if (true) {
					String line1 = split[q];
					if (player.getHand().contains(line1)) {
						player.removeTileFromHand(line1);
						q++;
					} else {
						// TODO kick with reason: tile not in hand
					}
					if (giveRandomTile() == null) {
						// TODO kick with reason: swap terwijl pot empty
					}
				}
				tiles.concat(" " + giveRandomTile());
			}
			client.sendMessage("NEW" + tiles);
		}
	}

	/**
	 * Sends a message using the collection of connected ClientHandlers to all
	 * connected Clients.
	 * 
	 * @param msg
	 *            message that is send
	 */
	public void broadcast(String msg) {
		for (int i = 0; i < threads.size(); i++) {
			threads.get(i).sendMessage(msg);
		}
	}

	/**
	 * Add a ClientHandler to the collection of ClientHandlers.
	 * 
	 * @param handler
	 *            ClientHandler that will be added
	 */
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

	/** Start een Server-applicatie op. */
	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("Not the right number of arguments, try this: <port>");
			System.exit(0);
		}

		Server server = new Server(Integer.parseInt(args[0]));
		server.run();

	}

	public int getPlayerNumber() {
		if (playerNumber < 4) {
			playerNumber++;
			return playerNumber;
		} else {
			return -1;
		}
	}

	public void startGame() {
		board = new Board();
		jar = new ArrayList<String>();
		fillJar();

		for (int i = 0; i < threads.size(); i++) {
			threads.get(i).sendMessage("NAMES " + "Stijn" + " " + 0);
			threads.get(i).sendMessage("NEW " + giveRandomTile() + " " + giveRandomTile() + " " + giveRandomTile() + " "
					+ giveRandomTile() + " " + giveRandomTile() + " " + giveRandomTile());
		}
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
			int random = (int) Math.round(Math.random() * jar.size());
			String newTile = jar.get(random);
			return newTile;
		} else {
			return null;
		}
	}

}
