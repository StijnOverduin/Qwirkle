package game;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
	
	public NetworkPlayer networkPlayer;
	public ArrayList<String> jar;
	public Color[] color = Color.values();
	public Shape[] shape = Shape.values();
	private int port;
	private List<ClientHandler> threads;

	/** Constructs a new Server object. */
	public Server(int portArg) {
		threads = new ArrayList<ClientHandler>();
		port = portArg;
		jar = new ArrayList<String>();
		fillJar();
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
				ClientHandler c = new ClientHandler(this, sock);
				addHandler(c);
				c.start();
			}

		} catch (IOException e) {
			System.out.println(e);

		}

	}
	
	public void readInput(String msg) {
		System.out.println(msg);
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
	
	
	/*
	 * Dit is een beschrijving voor de pot met tegeltjes.
	 */
	public void fillJar() {
		Shape Fshape = null;
		Color Fcolor = null;
		for (int i = 0; i < 3; i++) {
			for (int q = 0; q < color.length; q++) {
				Fcolor = color[q];
				for (int w = 0; w < shape.length; w++){
					Fshape = shape[w];
					Tile tile = new Tile(Fcolor, Fshape);
					addTileToJar(tile);
				}
			}
			
		}
	}
	
	public void removeTileFromJar(Tile tile) {
		Color color = tile.getColor();
		Shape shape = tile.getShape();
		String removedTile = "" + color + shape;
		jar.remove(removedTile);
	}
	
	public void addTileToJar(Tile tile) {
		//if (networkPlayer.getHand().contains(tile)) {
		Color color = tile.getColor();
		Shape shape = tile.getShape();
			if (jar.size() < 109) {
				jar.add("" + color + shape);
			}
		}
	//}
	
	public int TilesInJar() {
		return jar.size();
	}
	
	public void GiveRandomTile() {
		for (int i = 0; i < 6; i++) {
			if (networkPlayer.NumberOfTilesInHand() < 6 && jar.size() != 0) {
				int random = (int) Math.round(Math.random() * jar.size());
				String newTile = jar.get(random);
				Color color = Color.getColorFromCharacter(newTile.charAt(0));
				Shape shape = Shape.getShapeFromCharacter(newTile.charAt(1));
				Tile tile = new Tile(color, shape);
				networkPlayer.addTilesToHand(tile);
				System.out.println("NEW: " + tile);
			} else {
				System.out.println("EMPTY");
			}
		}
	}
	
	
}
