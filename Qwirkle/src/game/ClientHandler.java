package game;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;


	public class ClientHandler extends Thread {
		private Server server;
		private BufferedReader in;
		private BufferedWriter out;
		private String clientName;

		/**
		 * Constructs a ClientHandler object Initialises both Data streams.
		 */
		// @ requires serverArg != null && sockArg != null;
		public ClientHandler(Server serverArg, Socket sockArg) throws IOException {
			server = serverArg;
			in = new BufferedReader(new InputStreamReader(sockArg.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(sockArg.getOutputStream()));
			
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
	            server.readInput(input);
				}
	                
	        } catch (IOException e) {
	        	e.printStackTrace();
	            shutdown();
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

		/**
		 * This ClientHandler signs off from the Server and subsequently sends a
		 * last broadcast to the Server to inform that the Client is no longer
		 * participating in the chat.
		 */
		private void shutdown() {
			server.removeHandler(this);
			server.broadcast("[" + clientName + " has left]");
		}
	}

