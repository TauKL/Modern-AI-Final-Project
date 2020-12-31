package behaviorTree;

import java.util.ArrayList;

import pacman.game.Constants.DM;

//The node if there are no ghosts nearby
public class CollectPillsNode extends TreeNode {

	public Result testNode(){

		//gets pacmans current position
		int current=game.getPacmanCurrentNodeIndex();
		

		//go after the pills and power pills
		int[] pills=game.getPillIndices();
		int[] powerPills=game.getPowerPillIndices();		
		
		ArrayList<Integer> targets=new ArrayList<Integer>();
		
		for(int i=0;i<pills.length;i++)					//check which pills are available			
			if(game.isPillStillAvailable(i))
				targets.add(pills[i]);
		
		for(int i=0;i<powerPills.length;i++)			//check with power pills are available
			if(game.isPowerPillStillAvailable(i))
				targets.add(powerPills[i]);				
		
		int[] targetsArray=new int[targets.size()];		//convert from ArrayList to array
		
		for(int i=0;i<targetsArray.length;i++)
			targetsArray[i]=targets.get(i);
		
		//return the next direction once the closest target has been identified and returns a success through the tree.
		result.status=returnStatus.SUCCESS;
		result.move = game.getNextMoveTowardsTarget(current,game.getClosestNodeIndexFromNodeIndex(current,targetsArray,DM.PATH),DM.PATH);
		
		//If all tests fail, the move will be neutral.
		return result;
	}
}
