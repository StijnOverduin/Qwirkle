package game;

import java.util.ArrayList;

public class Tui {

  public Tui() {
  }
  
  public void connectFirst() {
    System.out.println("You haven't been connected yet");
    System.out.println("Connect by typing 'HELLO <playername>'");
  }
  
  public void invalidFirstMove() {
	  System.out.println("First move can't be a SWAP");
  }
  
  public void showBoard(Board board) {
    System.out.println(board.toString());
  }
  
  public void showHand(ArrayList<String> hand) {
    System.out.println("Tiles in your hand: " + hand);
  }
  
  public void showInvalidMove() {
    System.out.println("That is not a valid move");
  }
  
  public void displayInput(String input) {
    System.out.println(input);
  }
  
  public void waitingForStart() {
    System.out.println("Waiting for game to start...");
  }
  
  public void yourTurn() {
    System.out.println("It's your turn!");
  }
  
  public void showTurn(String input) {
    System.out.println("Player " + input + " is making a move...");
  }
  
  public void askingForInput() {
    System.out.println("Type in your move or type 'HELP' for a list of commands");
  }
  
  public void jarIsEmpty(String input) {
    System.out.println(input + " No more tiles in the jar");
  }
  
  public void tilesLeftInJar(int virtualJar) {
    System.out.println("Tiles in jar left: " + virtualJar);
  }
  
  public void parseIntError() {
    System.out.println("Couldn't parse the integer");
  }
  
  public void serverMessageError() {
    System.out.println("Couldn't understand the server");
  }
  
  public void disconnected() {
    System.out.println("You have been disconnected");
  }
  
  public void closedServer() {
    System.out.println("Server has been closed");
  }
  
  public void showHint(String move) {
    System.out.println(move);
  }
  
  public void showAiTime(String time) {
    System.out.println("The thinking time of the AI is changed to: " + time);
  }
  
  public void notValidCommand() {
    System.out.println("That's not a valid command");
  }
  
  public void notValidName() {
    System.out.println("That's not a valid name");
  }
  
  public void alreadyInGame() {
    System.out.println("You are already in a game");
  }
  
  public void showKick(int playerNumber, String message) {
    System.out.println("Player " + playerNumber + " was kicked from the game.");
    System.out.println("The reason: " + message);
  }
  /**
   * Shows the winner and further instructions.
   * @param input String: the winner of the game
   */
  public void showWinner(String input) {
    System.out.println("And the winner is.......");
    System.out.println("Player " + input);
    System.out.println("Game Over");
    System.out.println("If you want to play another game you can disconnect and connect to the server again");
  }
  /**
   * Shows all the different commands which you can use to play the game.
   */
  public void showHelp() {
    System.out.println("This is a list of commands you can use:");
    System.out.println("1: HELLO <name> --> to queue for a game");
    System.out.println("2: MOVE <tile> <row> <column> --> to place tile(s) on the board");
    System.out.println("3: SWAP <tile> --> to swap tiles");
    System.out.println("4: HINT --> to ask for a hint");
    System.out.println("5: JAR --> to view how many tiles are left in the jar");
    System.out.println("6: AiTime <time> --> to change the thinking time of the computer player");
    System.out.println("7: BOARD --> to show to board");
    System.out.println("8: HAND --> to show your hand");
  }
}
