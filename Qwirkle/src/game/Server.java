package game;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

	public NetworkPlayer networkPlayer;
	private int port;
	private List<ClientHandler> threads;
	public int playerNumber = -1;
	public int lengteMove;
	private Game game;

	/** Constructs a new Server object. */
	public Server(int portArg) {
		threads = new ArrayList<ClientHandler>();
		port = portArg;
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
			break;

		case "MOVE":
			lengteMove = split.length;
			int maalMoves = (lengteMove / 3);
			if ((lengteMove - 1) % 3 != 0) {
				// TODO kick with reason not correct MOVE format
			} else {
				for (int i = 0; i < maalMoves; i++) {
					if (!networkPlayer.getHand().contains(split[(1 + i * 3)])) {
						// TODO kick with reason tried to lay tile not in
						// possession
					}
				}
				for (int i = 0; i < maalMoves; i++) {

					String j = split[2];
					if (!split[(2 + i * 3)].equals(j)) {
						for (int a = 0; a < maalMoves; a++) {
							String k = split[3];
							if (!split[(2 + a * 3)].equals(k)) {
								// TODO kick with reason tiles not in straight
								// line
							}

						}

					}

				}

				// TODO lay tiles

			}

			broadcast("TURN " + client.getClientNumber() + " " + input);
			break;

		case "SWAP":
			int q = 1;
			while (q < split.length - 1) {
				if (true) {
					String line1 = split[q];
					Color color = Color.getColorFromCharacter(line1.charAt(0));
					Shape shape = Shape.getShapeFromCharacter(line1.charAt(1));
					Tile tile = new Tile(color, shape);
					q++;
					// if player has tile in hand
					// remove tile from player hand
					if (game.giveRandomTile() == null) {
						client.shutdown();
					}
					client.sendMessage("NEW " + game.giveRandomTile());
				}
			}
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

}
