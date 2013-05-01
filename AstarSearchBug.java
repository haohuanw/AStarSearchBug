import java.math.*;

import info.gridworld.actor.Actor;
import info.gridworld.actor.Bug; 
import info.gridworld.actor.Flower; 
import info.gridworld.actor.Rock; 
import info.gridworld.grid.Grid; 
import info.gridworld.grid.Location; 
import java.util.*;
import java.awt.Color; 
/**
 * Write a description of class AstarSearch_bug here.
 * 
 * @Haohuan Wang
 * @1.0
 */
public class AstarSearchBug extends Bug{
    int[][] map;
    AStar astarEngin;
    ArrayList<Node> ansList;
    Location end;
	public AstarSearchBug(Location Destination,int r, int c){
        map = new int[r][c];
		for(int i=0;i<map.length;i++){
    	   for (int j=0;j<map.length;j++){
    		   map[i][j] = 1;
    	   }
       }
       end = Destination;
    }
    
	public ArrayList<Actor> getActors(){
		return getGrid().getNeighbors(getLocation());
	}
    
	public void processActors(ArrayList<Actor> actors){
		for (Actor a : actors){
			if(a instanceof Rock){
				int row = a.getLocation().getRow();
				int col = a.getLocation().getCol();
				map[row][col] = 0;
			}
		}
	}
	
	public int[] determine(){
		int row1 = getLocation().getRow();
		int col1 = getLocation().getCol();
		int row2 = end.getRow();
		int col2 = end.getCol();
		astarEngin = new AStar(map);
		ansList = astarEngin.search(row1, col1, row2, col2);
		int[] a = new int[2];
		a[0] = ansList.get(1).getRow();
		a[1] = ansList.get(1).getCol();
		return a;
	}

    /**
     * the main flow
     */
	public void act(){
		  if (getGrid() == null){
			  return ;
		  }
		  if(!getLocation().equals(end)){
		  ArrayList<Actor> actors = getActors();
		  processActors(actors);
		  int[] ans = new int[2];
		  ans = determine();
		  Location newLoc = new Location(ans[0],ans[1]);
		  moveTo(newLoc);
		  }
    }
}
