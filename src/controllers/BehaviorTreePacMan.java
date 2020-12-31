package controllers;

import java.util.ArrayList;

import behaviorTree.CollectPillsNode;
import behaviorTree.GhostsNearbyNode;
import behaviorTree.SelectorTreeNode;
import behaviorTree.SequenceTreeNode;
import behaviorTree.TreeBuilder;
import behaviorTree.TreeNode.Result;
import behaviorTree.TreeNode.returnStatus;
import pacman.controllers.Controller;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

import static pacman.game.Constants.*;

//My implementation of a behavior tree controller.
//Supporting classes are found in the behaviorTree package
public class BehaviorTreePacMan extends Controller<MOVE>{
	private MOVE myMove=MOVE.RIGHT;
	private static final int MIN_DISTANCE=20;	//if a ghost is this close, run away
	private TreeBuilder t;
	private GhostsNearbyNode ghostsNode;
	public BehaviorTreePacMan(){
		ghostsNode=new GhostsNearbyNode();
	}
	
	public MOVE getMove(Game game, long timeDue) {
		
		//Creates the nodes that have children for use later when applying children to other nodes
		SelectorTreeNode root = new SelectorTreeNode();
		SequenceTreeNode left = new SequenceTreeNode();
		
		//Creates the behavior tree with the root node and passes the game state
		t = new TreeBuilder(root, game);
		
		//Creates the branches and leaves and specifies which nodes they are children of
		t.addNode(left, root);
		t.addNode(ghostsNode, left);
		t.addNode(new CollectPillsNode(), root);
		
		//Tests the tree from the root node down.
		Result r = t.testTree();
		return r.move;
	}
}	
		
























