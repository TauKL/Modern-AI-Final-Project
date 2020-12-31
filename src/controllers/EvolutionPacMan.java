package controllers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;

import behaviorTree.TreeNode.returnStatus;
import pacman.controllers.Controller;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Constants;
import pacman.game.Game;


//My implementation of an evolutionary algorithm controller.
//Supporting classes are found in the evolution package
public class EvolutionPacMan extends Controller<MOVE> implements Serializable{
	//Controller has all the same functions as the individual
	public int[][] functions;
	int minDistance=100;
	float fleeing=0;
	
	//Initialize controller with the functions from the individual
	public EvolutionPacMan(int[][] individualFunctions, int minimumDistance){
		functions= new int[5][5];
		for(int i=0; i<functions.length; i++){
			for (int j=0; j<functions[i].length; j++){
				functions[i][j]=individualFunctions[i][j];
			}
		}
		this.minDistance=minimumDistance;
	}
	
	public MOVE getMove(Game game, long timeDue) {
		if(fleeing>0)
			fleeing-=1;
		int pacmanPos=game.getPacmanCurrentNodeIndex();
		GHOST nearestGhost = GHOST.BLINKY;
		TreeMap<Integer, GHOST> ghostDistances = new TreeMap<Integer, GHOST>();
		TreeMap<Integer, GHOST> edibleGhostDistances = new TreeMap<Integer, GHOST>();
		float threat = 0;
		
		//Saves the dangerous and edible ghosts to their respective treemaps
		for(GHOST ghost : GHOST.values()){
			int ghostPos=game.getGhostCurrentNodeIndex(ghost);
			if(game.getShortestPathDistance(pacmanPos, ghostPos)>0 && game.getShortestPathDistance(pacmanPos, ghostPos)<minDistance){
				if(!game.isGhostEdible(ghost))
					ghostDistances.put(game.getShortestPathDistance(pacmanPos, ghostPos), ghost);
				else
					edibleGhostDistances.put(game.getShortestPathDistance(pacmanPos, ghostPos), ghost);
				if(game.getShortestPathDistance(pacmanPos, ghostPos)<game.getShortestPathDistance(pacmanPos, game.getGhostCurrentNodeIndex(nearestGhost)))
					nearestGhost=ghost;
			}
		}
		
		//If the ghosts have gotten out of the cage we adjust the threat.
		//Threat fluctuates between -1 and 2
		//-2 is many edible ghosts really close
		//-1 is edible ghost really close
		//0 is no danger
		//1 is dangerous ghost nearby or many dangerous ghosts approaching
		//2 is many dangerous ghosts very close
		
		//If there are dangerous ghosts too close
		if(ghostDistances.firstEntry()!=null){
			
			//Add threat the closer the nearest dangerous ghost is
			threat+=1-((float)ghostDistances.firstKey()/100);
			
			//add threat for average distance to all ghosts
			float average = 0;
			for(Integer key : ghostDistances.keySet()){
				average+=(float)key;
			}
			average/=ghostDistances.size();
			threat+=1-(average)/100;

		}
		//If there are edible ghosts close by and no dangerous ghosts.
		else{
			if(edibleGhostDistances.firstEntry()!=null){

				//Subtract threat the closer the nearest edible ghost is 
				threat-=1-((float)edibleGhostDistances.firstKey()/100);
				
				//Subtract threat for average distance to all edible ghosts
				float average = 0;
				for(Integer key : edibleGhostDistances.keySet()){
					average+=(float)key;
				}
				average/=edibleGhostDistances.size();
				threat-= 1-(average)/100;
			}
		}

		//converts float to double for integer math
		double x = threat;
		
		//Calculates the utility value for each function and puts them into map
		HashMap<String, Double> utilities = new HashMap<String, Double>();
		utilities.put("eat", Math.pow(functions[0][0]*x, 3.0) + Math.pow(functions[0][1]*x, 2.0) + functions[0][2]*x + functions[0][3]);
		utilities.put("powerup", Math.pow(functions[1][0]*x, 3.0) + Math.pow(functions[1][1]*x, 2.0) + functions[1][2]*x + functions[1][3]);
		utilities.put("kill", Math.pow(functions[2][0]*x, 3.0) + Math.pow(functions[2][1]*x, 2.0) + functions[2][2]*x + functions[2][3]);
		utilities.put("flee", Math.pow(functions[3][0]*x, 3.0) + Math.pow(functions[3][1]*x, 2.0) + functions[3][2]*x + functions[3][3]);
		utilities.put("junction", Math.pow(functions[4][0]*x, 3.0) + Math.pow(functions[4][1]*x, 2.0) + functions[4][2]*x + functions[4][3]);

		//gets the highest entry from the map
		HashMap.Entry<String, Double> bestUtility = null;
		for (HashMap.Entry<String, Double> entry : utilities.entrySet()){
			if (bestUtility == null || entry.getValue().compareTo(bestUtility.getValue()) > 0){
				bestUtility = entry;
			}
		}		
		
		//if going for the power pills is highest utility
		if(bestUtility.getKey()=="powerup"){
			int[] powerPills=game.getPowerPillIndices();
			ArrayList<Integer> targets=new ArrayList<Integer>();
			
			for(int i=0;i<powerPills.length;i++)			//check which power pills are available
				if(game.isPowerPillStillAvailable(i))
					targets.add(powerPills[i]);		
			
			int[] targetsArray=new int[targets.size()];		//convert from ArrayList to array
			for(int i=0;i<targetsArray.length;i++)
				targetsArray[i]=targets.get(i);
			if(targetsArray.length>0)
				return game.getNextMoveTowardsTarget(pacmanPos,game.getClosestNodeIndexFromNodeIndex(pacmanPos,targetsArray,DM.PATH),DM.PATH);
		}
		
		//If kill has highest utility and there is something to kill
		else if(bestUtility.getKey()=="kill" && edibleGhostDistances.size()>0){
			return game.getNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(),game.getGhostCurrentNodeIndex(edibleGhostDistances.firstEntry().getValue()), DM.PATH);
		}
		
		//If we need to eat pills.
		else if(bestUtility.getKey()=="eat"){
			int[] pills=game.getPillIndices();
			int[] powerPills=game.getPowerPillIndices();		
			
			ArrayList<Integer> targets=new ArrayList<Integer>();
			
			for(int i=0;i<pills.length;i++)					//check which pills are available			
				if(game.isPillStillAvailable(i))
					targets.add(pills[i]);
			
			for(int i=0;i<powerPills.length;i++)			//check which power pills are available
				if(game.isPowerPillStillAvailable(i))
					targets.add(powerPills[i]);				
			
			int[] targetsArray=new int[targets.size()];		//convert from ArrayList to array
			
			for(int i=0;i<targetsArray.length;i++)
				targetsArray[i]=targets.get(i);
			
			//return the next direction once the closest target has been identified and returns a success through the tree.
			return game.getNextMoveTowardsTarget(pacmanPos,game.getClosestNodeIndexFromNodeIndex(pacmanPos,targetsArray,DM.PATH),DM.PATH);
		}
		
		//If we need to get to the nearest junction
		else if (bestUtility.getKey()=="junction" && fleeing <=0){
			for(int i = 0; i<game.getJunctionIndices().length; i++){
				//If ms pacman is currently in a junction, she cannot go to a junction and will instead choose default action, which is fleeing
				if(game.getPacmanCurrentNodeIndex()==game.getJunctionIndices()[i]){
					fleeing=200;
				}
			}
			return game.getNextMoveTowardsTarget(pacmanPos, game.getClosestNodeIndexFromNodeIndex(game.getPacmanCurrentNodeIndex(), game.getJunctionIndices(), DM.PATH), DM.PATH);
		}
		//Defaults to fleeing directly away from ghosts
		return game.getNextMoveAwayFromTarget(game.getPacmanCurrentNodeIndex(),game.getGhostCurrentNodeIndex(nearestGhost),DM.PATH);
	}
}