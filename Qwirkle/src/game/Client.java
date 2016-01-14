package game;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client extends Thread {

	public static void main(String[] args) {
		if (args.length != 2) {
			System.out.println("Not the right number of arguments");
			System.exit(0);
		}

		InetAddress host = null;
		int port = 0;

		try {
			host = InetAddress.getByName(args[0]);
		} catch (UnknownHostException e) {
			System.out.println("ERROR: no valid hostname!");
			System.exit(0);
		}

		try {
			port = Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {
			System.out.println("ERROR: no valid portnummer!");
			System.exit(0);
		}

		try {
			Client client = new Client(host, port);
			client.start();

			do {
				String input = readString("");
				client.sendMessage(input);
			} while (true);

		} catch (IOException e) {
			System.out.println("ERROR: couldn't construct a client object!");
			System.exit(0);
		}

	}

	private Socket sock;
	private BufferedReader in;
	private BufferedWriter out;
	private Player player;

	public Client(InetAddress host, int port) throws IOException {
		sock = new Socket(host, port);
		in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
	}

	public void run() {
		while (true) {
			String line = "";
			try {

				line = in.readLine();
				String[] split = line.split(" ");
				switch (split[0]) {
				case "WELCOME":
					String playerName = split[1];
					String playerNumber = split[2];
					// player = new Player( playerName, playerNumber); (mis het
					// board nog, misschien niet hier new player maken?)
					break;
				case "NAMES":
					System.out.println(line);
					break;
				case "NEXT":
					String number = split[1];
					if (number.equals(player.getName())) {
						System.out.println("It's your turn!");
						// start determineMove in TUI

					}
					break;
				case "NEW":
					if (!(split[1].equals("empty"))) {
						for (int i = 1; i < 7; i++) {
							Color color = Color.getColorFromCharacter(split[i].charAt(0));
							Shape shape = Shape.getShapeFromCharacter(split[i].charAt(1));
							Tile tile = new Tile(color, shape);
							player.addTilesToHand(tile);
						}
					} else {
						System.out.println(line + "No more tiles in the jar");
					}
					break;
				case "TURN":
					if (!(split[1].equals("empty"))) {
						for (int i = 1; i < 7; i++) {
							Color color = Color.getColorFromCharacter(split[i].charAt(0));
							Shape shape = Shape.getShapeFromCharacter(split[i].charAt(1));
							Tile tile = new Tile(color, shape);
							// Zet de tegels op het bord
						}
					}
					break;
				case "KICK":
					System.out.println(line);
					// server moet tiles van de speler terug in de pot stoppen.
					break;
				case "WINNER":
					System.out.println(line);
					// misschien nog wat winner stuf dat nog geimplement moet
					// worden
					break;
				default:
					System.out.println("");
				}

			} catch (IOException e) {

			}
		}
	}

	/** send a message to the ClientHandler. */
	public void sendMessage(String msg) {
		try {
		String message = msg;
		String[] split = message.split(" ");
		if (true) {
			switch (split[0]) {
			case "MOVE":
				int q = 1;
				for (int i = 1; i < 7; i++) {
					if (true) {
						String line = split[1];
						Color color = Color.getColorFromCharacter(line.charAt(0));
						Shape shape = Shape.getShapeFromCharacter(line.charAt(1));
						Tile tile = new Tile(color, shape);
						q++;

						if (true) {
							int rij = Integer.parseInt(split[q]);
							q++;
							if (true) {
								int colom = Integer.parseInt(split[q]);
								// player.makeMove(rij, colom, tile);
								q++;
							}
						}
					}
					out.write(message);
					out.newLine();
					out.flush();
				}
				break;
			case "SWAP":
				int s = 1;
				for (int i = 0; i < 6; i++) {
					if (split[s] != null) {
						String tile = split[s];
						Color color = Color.getColorFromCharacter(tile.charAt(0));
						Shape shape = Shape.getShapeFromCharacter(tile.charAt(1));
						Tile tile1 = new Tile(color, shape);
						s++;
						/*
						 * player.removeTileFromHand(tile1); if
						 * (player.NumberOfTilesInHand() < 6) {
						 * 
						 * }
						 */

					}
				}
				out.write(message);
				out.newLine();
				out.flush();
				break;
			case "HELLO":
				if (split[1] != null) {
					String playerName = split[1];
					out.write(message);
					out.newLine();
					out.flush();
				}
				break;
			default:
				System.out.println("That's not a valid command");
			} 

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String readString(String tekst) {
		System.out.print(tekst);
		String antw = null;
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			antw = in.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return (antw == null) ? "" : antw;
	}

}
