package game.player;

import game.Board;
import game.tiles.Color;
import game.tiles.Shape;
import game.tiles.Tile;

public class Naive extends Player implements Ai {

  public Naive(Board board, String name, int playerNumber) {
    super(board, name, playerNumber);
  }


  public String determineMove() {
    String move = "";
    int miny = board.getMiny();
    int maxy = board.getMaxy();
    int minx = board.getMinx();
    int maxx = board.getMaxx();
    if (!getHand().isEmpty()) {
      Color colorFirstMove = Color.getColorFromCharacter(getHand().get(0).charAt(0));
      Shape shapeFirstMove = Shape.getShapeFromCharacter(getHand().get(0).charAt(1));
  
      if (board.isValidMove(91, 91, new Tile(colorFirstMove, shapeFirstMove))) {
        move = move.concat("MOVE " + colorFirstMove.getChar() + shapeFirstMove.getChar() + " " + 91 + " " + 91);
        removeTileFromHand(getHand().get(0));
        return move;
  
      } else {
        for (int i = 0; i < getHand().size(); i++) {
          String currentTile = getHand().get(i);
          for (int row = maxy; row <= miny; row++) {
            for (int col = minx; col <= maxx; col++) {
              Color color = Color.getColorFromCharacter(currentTile.charAt(0));
              Shape shape = Shape.getShapeFromCharacter(currentTile.charAt(1));
              if (board.isValidMove(row, col, new Tile(color, shape))) {
                move = move.concat("MOVE " + color.getChar() + shape.getChar() 
                + " " + row + " " + col);
                removeTileFromHand("" + color.getChar() + shape.getChar());
                return move;
              }
            }
          }
        }
        move = move.concat("SWAP " + getHand().get(0));
        removeTileFromHand(getHand().get(0));
        return move;
      }
    } else {
      return "";
    }
  }

}
