package game;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;


	public class ClientHandler extends Thread {
		private BufferedReader in;
		private BufferedWriter out;
		private Game game;

		/**
		 * Constructs a ClientHandler object Initialises both Data streams.
		 */
		// @ requires serverArg != null && sockArg != null;
		public ClientHandler(Socket sockArg, Game game) throws IOException {
			in = new BufferedReader(new InputStreamReader(sockArg.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(sockArg.getOutputStream()));
			this.game = game;
		}

		/**
		 * This method takes care of sending messages from the Client. Every message
		 * that is received, is preprended with the name of the Client, and the new
		 * message is offered to the Server for broadcasting. If an IOException is
		 * thrown while reading the message, the method concludes that the socket
		 * connection is broken and shutdown() will be called.
		 */
		public void run() {
			try {
				while (true) {
	            String input = in.readLine();
	            game.readInput(input, this);
				}
	                
	        } catch (IOException e) {
	        	System.out.println("Client was kicked");
	        }
		}

		/**
		 * This method can be used to send a message over the socket connection to
		 * the Client. If the writing of a message fails, the method concludes that
		 * the socket connection has been lost and shutdown() is called.
		 */
		public void sendMessage(String msg) {
			try { 
				out.write(msg);
				out.newLine();
				out.flush();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void kick() throws IOException {
			in.close();
			out.close();
		}
		
	}

