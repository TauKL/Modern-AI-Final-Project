package behaviorTree;

import java.util.ArrayList;

import pacman.game.Constants.MOVE;
import pacman.game.Game;

public abstract class TreeNode {
	//The return statuses possible for use for the result that is returned. Running is used as default and not checked.
	public enum returnStatus{
		SUCCESS, FAILURE, RUNNING
	}
	
	//Result class return by every test done, the returnStatus and move is passed through the tree when appropriate, usually on successes.
	public class Result{
		public returnStatus status;
		public MOVE move;
		
		//Needs a status and a move to return to the controller
		public Result (returnStatus status, MOVE move){
			this.status=status;
			this.move=move;
		}
	}
	
	//Defines variables that all nodes must have.
	protected Game game;
	protected ArrayList<TreeNode> children;
	protected Result result;
	
	//Constructor, sets defaults.
	public TreeNode()
	{
		this.children=new ArrayList<TreeNode>();
		this.result=new Result(returnStatus.RUNNING, MOVE.NEUTRAL);
	}
	
	//Inits the node with the game. This is only run by the TreeBuilder and should not manually be called ever.
	public final void init(Game game){
		this.game=game;
	}
	
	//Adds a child to the node. This is also only run the TreeBuilder and should not manually be called ever.
	public void addChild(TreeNode node){
		children.add(node);
	}
	
	//The abstract class for testing the nodes conditions. This is what the classes will use for their specific conditions (fx selector, sequence, custom)
	public abstract Result testNode();
}
