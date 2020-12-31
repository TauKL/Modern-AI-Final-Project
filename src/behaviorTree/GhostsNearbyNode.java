package behaviorTree;

import java.util.ArrayList;

import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;

//The node for checking if a ghost is nearby
public class GhostsNearbyNode extends TreeNode {

	//The mininmum distance acceptable for it to register a nearby ghost.
	private static final int MIN_DISTANCE=20;
	private int fleeing = 0;
	public Result testNode(){

		//Gets ms pacman's current position
		int current=game.getPacmanCurrentNodeIndex();
		//Defaults to a failure (no ghosts nearby)
		result.status=returnStatus.FAILURE;
		
		//Goes through all the ghosts to see if one is nearby
		ArrayList<GHOST> closestGhosts = new ArrayList<GHOST>();
		for(GHOST ghost : GHOST.values())
		{
			//is this ghost dangerous?
			if(game.getGhostEdibleTime(ghost)==0 && game.getGhostLairTime(ghost)==0)
				//get the distance to the ghost, is this distance less than we will accept?
				if(game.getShortestPathDistance(current,game.getGhostCurrentNodeIndex(ghost))<MIN_DISTANCE){
						//adds all the ghosts to an array that are closer than minimum distance
						closestGhosts.add(ghost);
						
						//There is a ghost nearby and we return a success and assign the move to the result.
						//The move is the next spot away from the ghost.
						result.status=returnStatus.SUCCESS;
						//Default move just directly away from the ghost below minimum distance. Not taking other ghosts into account.
						result.move=game.getNextMoveAwayFromTarget(game.getPacmanCurrentNodeIndex(), game.getGhostCurrentNodeIndex(ghost), DM.PATH);
				}
		}
		//If there are more than one ghosts within minimum distance, then they are coming from different sides as ms pacman will run
		//directly away as soon as it is in minimum distance.
		if(closestGhosts.size()>1 && fleeing <= 0){
			//If we are not fleeing, we will run to the nearest junction
			result.move=game.getNextMoveTowardsTarget(current, game.getClosestNodeIndexFromNodeIndex(game.getPacmanCurrentNodeIndex(), game.getJunctionIndices(), DM.PATH), DM.PATH);
			//goes through all junctions in the maze
			for(int i = 0; i<game.getJunctionIndices().length; i++){
				//If ms pacman is currently in a junction, we will overwrite the "go to junction" move command with the following:
				if(game.getPacmanCurrentNodeIndex()==game.getJunctionIndices()[i]){
					//Make a list of all moves that will lead towards a ghost.
					ArrayList<MOVE> illegalMoves = new ArrayList<MOVE>();
					for (GHOST g : closestGhosts){
						illegalMoves.add(game.getNextMoveTowardsTarget(current, game.getGhostCurrentNodeIndex(g), DM.PATH));
					}
					
					//Make a list of all moves possible in the junction ms pacman is currently in.
					ArrayList<MOVE> moves = new ArrayList<MOVE>();
					int[] neighbors = game.getNeighbouringNodes(current);
					for(int j = 0; j<neighbors.length; j++){
						moves.add(game.getNextMoveTowardsTarget(current, neighbors[j], DM.PATH));
					}
					
					//Default chosen move to the direction we're already going
					MOVE chosenMove = game.getPacmanLastMoveMade();
					
					//Loop through all the possible moves
					outerloop:
					for(int j = 0; j < moves.size(); j++){
						boolean noIllegals=true;	//Assume that we can go this way.
						//Loop through all illegal moves (leading to ghosts)
						innerloop:
						for(int k = 0; k<illegalMoves.size(); k++){
							//if the move we assumed is good is actually not
							if(moves.get(j)==illegalMoves.get(k)){
								noIllegals=false; //we say that this move is illegal and break this loop and carry on to the next possible move.
								break innerloop;
							}
						}
						//If we found a legal move
						if(noIllegals){
							chosenMove=moves.get(j);	//Set the chosen move to this legal one
							this.fleeing=100; // Set ms pacman to fleeing state so we don't look for a junction immediately after moving out of it
							break outerloop; //break the loop because we found the direction we want to go.
						}
					}
					//Returns the result
					result.move=chosenMove;
					return result;
				}
			}
		}
		//Counts down the fleeing time, after this, can choose a new junction if it gets cornered.
		if(fleeing>0)
			fleeing-=1;
		
		//Returns the result
		return result;
	}
}
