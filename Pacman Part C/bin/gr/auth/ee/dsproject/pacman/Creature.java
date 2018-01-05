package gr.auth.ee.dsproject.pacman;

import gr.auth.ee.dsproject.node.Node;
import java.util.ArrayList;

/**
 * <p>
 * Title: DataStructures2011
 * </p>
 * 
 * <p>
 * Description: Data Structures project: year 2011-2012
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * 
 * <p>
 * Company: A.U.Th.
 * </p>
 * 
 * @author Michael T. Tsapanos
 * @version 1.0
 */

public class Creature implements gr.auth.ee.dsproject.pacman.AbstractCreature
{

  public String getName ()
  {
    return "Mine";
  }

  private int step = 1;
  private boolean amPrey;

  public Creature (boolean isPrey)
  {
    amPrey = isPrey;

  }
 
  public int calculateNextPacmanPosition (Room[][] Maze, int[] currPosition)
  {
	double max=-1000;
	int maxi=-1;
    Node root=new Node(currPosition[0],currPosition[1],-1,0,Maze);
    root.setParent(null);
    createSubTreePacman(1,root,Maze,currPosition);
    
       	for (int i=0;i<root.getChildren().size();i++) {
       		if (max<root.getChildren().get(i).getMaxOptions()) {
       			max=root.getChildren().get(i).getMaxOptions();
       			maxi=i;
       		}
       	}
       	int direction=root.getChildren().get(maxi).getMove();
       	System.out.println(direction);
       	return direction;
  }

  void createSubTreePacman (int depth, Node parent, Room[][] Maze, int[] currPacmanPosition)
  {
	 
	  int cardinalDirections=4;
	  Room[][] maze;
	  ArrayList<Integer> available=new ArrayList<Integer>();
	  int [] newPacmanPosition = new int[2];
	  for (int i=0;i<cardinalDirections;i++) {
		  if (Maze[currPacmanPosition[0]][currPacmanPosition[1]].walls[i]==1) {
			  available.add(i);
			  
		  }
	  }
	  	    
	  for (int i=0;i<available.size();i++) {
		  maze=PacmanUtilities.copy(Maze);
		  newPacmanPosition=PacmanUtilities.evaluateNextPosition(maze, currPacmanPosition, available.get(i),PacmanUtilities.borders);
		  PacmanUtilities.movePacman(maze,currPacmanPosition,newPacmanPosition);
		  Node child=new Node(newPacmanPosition[0],newPacmanPosition[1],-1,depth,maze);
		  parent.setChildren(child);
		  child.setNodeMove(available.get(i));
		  createSubTreeGhosts(depth+1,child,maze,child.getGhostsPosition());
	  }
	
	  }
    

  

  void createSubTreeGhosts (int depth, Node parent, Room[][] Maze, int[][] currGhostsPosition)
  {
	  Room[][] maze;
	  int cardinalDirections=4;
	  int [][] newGhostsPosition = new int[PacmanUtilities.numberOfGhosts][2];
	  ArrayList<int[][]>  available=PacmanUtilities.allGhostMoves(Maze, currGhostsPosition);
	  double max=-1000;
	  double min=1000;
	  for (int i=0;i<available.size();i++) {
		  maze=PacmanUtilities.copy(Maze);
		  PacmanUtilities.moveGhosts(maze,currGhostsPosition,available.get(i));
		  Node childhood = new Node(parent.getNodeX(),parent.getNodeY(),-1,depth,maze);
		  parent.setChildren(childhood);
		  for (int k=0;k<cardinalDirections;k++) {
			  if (maze[childhood.getNodeX()][childhood.getNodeY()].walls[k]==1) 
				  
				  childhood.setNodeMove(k);
			      if (max<childhood.getEvaluation()) 
				      max=childhood.getEvaluation();
				  
		  }
		  childhood.setMax(max);
		  if (min>childhood.getMinOptions()) {
			  min=childhood.getMinOptions();
		  }
		  
	  }
      parent.setMin(min);
	  
  }

  public int[] getPacPos (Room[][] Maze)
  {
    int[] pacmanPos = new int[2];
    for (int i = 0; i < PacmanUtilities.numberOfRows; i++) {
      for (int j = 0; j < PacmanUtilities.numberOfColumns; j++) {
        if (Maze[i][j].isPacman()) {
          pacmanPos[0] = i;
          pacmanPos[1] = j;
          return pacmanPos;
        }
      }
    }
    return pacmanPos;
  }

  public boolean[] comAvPos (Room[][] Maze, int[][] currentPos, int[] moves, int currentGhost)
  {

    boolean[] availablePositions = { true, true, true, true };

    int[][] newPos = new int[4][2];

    for (int i = 0; i < 4; i++) {

      if (Maze[currentPos[currentGhost][0]][currentPos[currentGhost][1]].walls[i] == 0) {
        availablePositions[i] = false;
        continue;
      }

      if (PacmanUtilities.flagColision(Maze, currentPos[currentGhost], i)) {
        availablePositions[i] = false;
      }

      else if (currentGhost == 0)
        continue;

      else {
        switch (i) {
        case Room.WEST:
          newPos[currentGhost][0] = currentPos[currentGhost][0];
          newPos[currentGhost][1] = currentPos[currentGhost][1] - 1;
          break;
        case Room.SOUTH:
          newPos[currentGhost][0] = currentPos[currentGhost][0] + 1;
          newPos[currentGhost][1] = currentPos[currentGhost][1];
          break;
        case Room.EAST:
          newPos[currentGhost][0] = currentPos[currentGhost][0];
          newPos[currentGhost][1] = currentPos[currentGhost][1] + 1;
          break;
        case Room.NORTH:
          newPos[currentGhost][0] = currentPos[currentGhost][0] - 1;
          newPos[currentGhost][1] = currentPos[currentGhost][1];

        }

        for (int j = (currentGhost - 1); j > -1; j--) {
          switch (moves[j]) {
          case Room.WEST:
            newPos[j][0] = currentPos[j][0];
            newPos[j][1] = currentPos[j][1] - 1;
            break;
          case Room.SOUTH:
            newPos[j][0] = currentPos[j][0] + 1;
            newPos[j][1] = currentPos[j][1];
            break;
          case Room.EAST:
            newPos[j][0] = currentPos[j][0];
            newPos[j][1] = currentPos[j][1] + 1;
            break;
          case Room.NORTH:
            newPos[j][0] = currentPos[j][0] - 1;
            newPos[j][1] = currentPos[j][1];
            // break;
          }

          if ((newPos[currentGhost][0] == newPos[j][0]) && (newPos[currentGhost][1] == newPos[j][1])) {

            availablePositions[i] = false;
            continue;
          }

          if ((newPos[currentGhost][0] == currentPos[j][0]) && (newPos[currentGhost][1] == currentPos[j][1]) && (newPos[j][0] == currentPos[currentGhost][0])
              && (newPos[j][1] == currentPos[currentGhost][1])) {

            availablePositions[i] = false;

          }
        }
      }
    }

    return availablePositions;
  }

  public int comBestPos (boolean[] availablePositions, int[] pacmanPosition, int[] currentPos)
  {

    int[] newVerticalDifference = new int[2];
    for (int i = 0; i < 2; i++)
      newVerticalDifference[i] = currentPos[i] - pacmanPosition[i];

    int[] distanceSquared = new int[4];

    for (int i = 0; i < 4; i++) {
      if (availablePositions[i] == true) {

        switch (i) {
        case Room.WEST:
          newVerticalDifference[1]--;
          break;
        case Room.SOUTH:
          newVerticalDifference[0]++;
          break;
        case Room.EAST:
          newVerticalDifference[1]++;
          break;
        case Room.NORTH:
          newVerticalDifference[0]--;
          break;
        }
        distanceSquared[i] = newVerticalDifference[0] * newVerticalDifference[0] + newVerticalDifference[1] * newVerticalDifference[1];
      } else
        distanceSquared[i] = PacmanUtilities.numberOfRows * PacmanUtilities.numberOfRows + PacmanUtilities.numberOfColumns * PacmanUtilities.numberOfColumns + 1;
    }

    int minDistance = distanceSquared[0];
    int minPosition = 0;

    for (int i = 1; i < 4; i++) {
      if (minDistance > distanceSquared[i]) {
        minDistance = distanceSquared[i];
        minPosition = i;
      }

    }

    return minPosition;
  }

  public int[] calculateNextGhostPosition (Room[][] Maze, int[][] currentPos)
  {

    int[] moves = new int[PacmanUtilities.numberOfGhosts];

    int[] pacmanPosition = new int[2];

    pacmanPosition = getPacPos(Maze);
    for (int i = 0; i < PacmanUtilities.numberOfGhosts; i++) {
      moves[i] = comBestPos(comAvPos(Maze, currentPos, moves, i), pacmanPosition, currentPos[i]);
    }

    return moves;

  }

  public boolean[] checkCollision (int[] moves, int[][] currentPos)
  {
    boolean[] collision = new boolean[PacmanUtilities.numberOfGhosts];

    int[][] newPos = new int[4][2];

    for (int i = 0; i < moves.length; i++) {

      if (moves[i] == 0) {
        if (currentPos[i][1] > 0) {
          newPos[i][0] = currentPos[i][0];
          newPos[i][1] = currentPos[i][1] - 1;
        } else {
          newPos[i][0] = currentPos[i][0];
          newPos[i][1] = PacmanUtilities.numberOfColumns - 1;
        }

      } else if (moves[i] == 1) {
        if (currentPos[i][0] < PacmanUtilities.numberOfRows - 1) {
          newPos[i][0] = currentPos[i][0] + 1;
          newPos[i][1] = currentPos[i][1];
        } else {
          newPos[i][0] = 0;
          newPos[i][1] = currentPos[i][1];
        }
      } else if (moves[i] == 2) {
        if (currentPos[i][1] < PacmanUtilities.numberOfColumns - 1) {
          newPos[i][0] = currentPos[i][0];
          newPos[i][1] = currentPos[i][1] + 1;
        } else {
          newPos[i][0] = currentPos[i][0];
          newPos[i][1] = 0;

        }
      } else {
        if (currentPos[i][0] > 0) {
          newPos[i][0] = currentPos[i][0] - 1;
          newPos[i][1] = currentPos[i][1];
        } else {

          newPos[i][0] = PacmanUtilities.numberOfRows - 1;
          newPos[i][1] = currentPos[i][1];

        }
      }

      collision[i] = false;
    }

    for (int k = 0; k < moves.length; k++) {

    }

    for (int i = 0; i < moves.length; i++) {
      for (int j = i + 1; j < moves.length; j++) {
        if (newPos[i][0] == newPos[j][0] && newPos[i][1] == newPos[j][1]) {

          collision[j] = true;
        }

        if (newPos[i][0] == currentPos[j][0] && newPos[i][1] == currentPos[j][1] && newPos[j][0] == currentPos[i][0] && newPos[j][1] == currentPos[i][1]) {

          collision[j] = true;
        }

      }

    }
    return collision;
  }

}
