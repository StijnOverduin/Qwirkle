package game;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

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
			e.printStackTrace();
			System.out.println("ERROR: couldn't construct a client object!");
			System.exit(0);
		}

	}

	private Socket sock;
	private BufferedReader in;
	private BufferedWriter out;
	private Player player;
	private Board board;
	private Boolean readIt = true;
	private int lengteMove;
	private boolean horizontalTrue;
	private int addToScore;
	private boolean heeftTilesErnaast;

	public Client(InetAddress host, int port) throws IOException {
		sock = new Socket(host, port);
		in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
	}

	public void run() {
		while (readIt) {
			String line = "";
			try {

				line = in.readLine();
				System.out.println("Server -> Client " + line);
				String[] split = line.split(" ");
				switch (split[0]) {
					case "WELCOME":
						board = new Board();
						String playerName = split[1];
						int playerNumber = Integer.parseInt(split[2]);
						player = new Player(board, playerName, playerNumber);
						System.out.println(line);
						System.out.println(board.toString());
						break;
					case "NAMES":
						System.out.println(line);
						break;
					case "NEXT":
						String number = split[1];
						if (Integer.parseInt(number) == player.getPlayerNumber()) {
							System.out.println("It's your turn!");
						}
						break;
					case "NEW":
						if (!(split[1].equals("empty"))) {
							for (int i = 1; i < split.length; i++) {
								player.addTilesToHand(split[i]);
							}
							System.out.println(player.getHand());
						} else {
							System.out.println(line + "No more tiles in the jar");
						}
						break;
					case "TURN":
						int q = 2;
						int rij = 0;
						int colom = 0;
						Color color;
						Shape shape;
						Tile tile;
						while (q < split.length - 2) {
							if (true) {
								String line1 = split[q];
								color = Color.getColorFromCharacter(line1.charAt(0));
								shape = Shape.getShapeFromCharacter(line1.charAt(1));
								tile = new Tile(color, shape);
								q++;
	
								if (true) {
									rij = Integer.parseInt(split[q]);
									q++;
									if (true) {
										colom = Integer.parseInt(split[q]);
	
										q++;
									}
								}
							}
							player.makeMove(rij, colom, tile);
							player.removeTileFromHand("" + color.getChar() + shape.getChar());
	
						}
						System.out.println(board.toString());
						System.out.println(player.getHand());
	
						break;
					case "KICK":
						System.out.println(line);
						readIt = false;
						break;
					case "WINNER":
						System.out.println(line);
						break;
					default:
						System.out.println("");
				}

			} catch (IOException e) {
				System.out.println("You were kicked son!");
				readIt = false;
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
						lengteMove = split.length;
						int maalMoves = (lengteMove / 3);
						if ((lengteMove - 1) % 3 != 0) {
							System.out.println("Not a valid move, try again");
							break;
						} else {
							for (int i = 0; i < maalMoves; i++) {
								if (!(player.getHand().contains(split[(1 + i * 3)]))) {
									System.out.println("You don't have that tile");
									break;
								}
							}
							for (int i = 0; i < maalMoves; i++) {
						
								String row = split[2];
								if (!split[(2 + i * 3)].equals(row)) {
									for (int a = 0; a < maalMoves; a++) {
										String col = split[3];
										if (!split[(2 + a * 3)].equals(col)) {
											System.out.println("That was not a valid move, try again");
											break;
						
										}
						
									}
						
								}
						
							}
							Board deepboard = board.deepCopy();
						
							String newTiles = "NEW";
							if (maalMoves > 1) {
								this.horizontalTrue = (split[2] == split[5]);
								this.addToScore = 0;
								for (int i = 0; i < maalMoves; i++) {
									Color color = Color.getColorFromCharacter(split[(1 + i * 3)].charAt(0));
									Shape shape = Shape.getShapeFromCharacter(split[(1 + i * 3)].charAt(1));
									if (deepboard.isValidMove(Integer.parseInt(split[(2 + i * 3)]),
											Integer.parseInt(split[(3 + i * 3)]), new Tile(color, shape))) {
										deepboard.setTile(Integer.parseInt(split[(2 + i * 3)]),
												Integer.parseInt(split[(3 + i * 3)]), new Tile(color, shape));
										player.removeTileFromHand(split[(1 + i * 3)]);
										// TODO catch parseint excep
						
									} else {
										System.out.println("Not a valid move");
										break;
									}
								}
						
							} else if (maalMoves == 1) {
								Color color = Color.getColorFromCharacter(split[1].charAt(0));
								Shape shape = Shape.getShapeFromCharacter(split[1].charAt(1));
								if (deepboard.isValidMove(Integer.parseInt(split[2]), Integer.parseInt(split[3]),
										new Tile(color, shape))) {
									deepboard.setTile(Integer.parseInt(split[2]), Integer.parseInt(split[3]),
											new Tile(color, shape));
						
									
								} else {
									System.out.println("Not a valid Move");
									break;
								}
						
							}
						}
						out.write(message);
						out.newLine();
						out.flush();
						break;
					case "SWAP":
						int s = 1;
						for (int i = 0; i < split.length - 1; i++) {
							if (split[s] != null) {
								String tile = split[s];
								s++;
								player.removeTileFromHand(tile);
	
							}
						}
						out.write(message);
						out.newLine();
						out.flush();
						break;
					case "HELLO":
						if (split[1] != null) {
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
