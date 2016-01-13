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
		if (args.length != 3) {
			System.out.println("Not the right number of arguments");
			System.exit(0);
		}

		InetAddress host = null;
		int port = 0;

		try {
			host = InetAddress.getByName(args[1]);
		} catch (UnknownHostException e) {
			System.out.println("ERROR: no valid hostname!");
			System.exit(0);
		}

		try {
			port = Integer.parseInt(args[2]);
		} catch (NumberFormatException e) {
			System.out.println("ERROR: no valid portnummer!");
			System.exit(0);
		}

		try {
			Client client = new Client(args[0], host, port);
			client.sendMessage(args[0]);
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
	
	private String clientName;
	private Socket sock;
	private BufferedReader in;
	private BufferedWriter out;

	public Client(String name, InetAddress host, int port) throws IOException {
		clientName = name;
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
				switch(split[0]) {
				case "WELCOME":
					String playerName = split[1];
					String playerNumber = split[2];
					break;
				case "NAMES":
					System.out.println(line);
					break;
				case "NEXT":
					String number = split[1];
					if(number.equals("localplayernumber")) {
						System.out.println("It's your turn!");
						//start determineMove in TUI
						
					}
					break;
				case "NEW":
					if(split[1] != "EMPTY") {
						for(int i = 1; i < 7; i++) {
						Color color = Color.getColorFromCharacter(split[i].charAt(0));
						Shape shape = Shape.getShapeFromCharacter(split[i].charAt(1));
						Tile tile = new Tile(color, shape);
						//player.addTilesToHand(tile);
						}
					} else {
						System.out.println(line);
					}
					break;
				case "TURN":
					if(split[1] != "EMPTY") {
						for(int i = 1; i < 7; i++) {
							Color color = Color.getColorFromCharacter(split[i].charAt(0));
							Shape shape = Shape.getShapeFromCharacter(split[i].charAt(1));
							Tile tile = new Tile(color, shape);
							//Zet de tegels op het bord
						}
					}
					break;
				case "KICK":
					System.out.println(line);
					//tiles terug naar de jar
					break;
				case "WINNER":
					System.out.println(line);
					//misschien nog wat winner stuf dat nog geimplement moet worden
					break;
				default:
					System.out.println("");
					}

			} catch (IOException e) {

			}
		}
	}

	/** send a message to the Server. */
	public void sendMessage(String msg) {
		try {
			out.write(msg);
			out.newLine();
			out.flush();
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
