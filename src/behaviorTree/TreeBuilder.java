package behaviorTree;


import behaviorTree.TreeNode.Result;
import pacman.game.Game;

//The class used for building the tree in the controller
public class TreeBuilder {
	
	//Needs to have a root node and the game state
	TreeNode rootNode;
	final Game game;
	
	//Constructor, sets the game state, creates the root node and assigns it to the rootNode variable.
	public TreeBuilder(TreeNode root, Game game){
		this.game = game;
		addNode(root);
		rootNode = root;
	}
	
	//adds a node to the tree without specifying a parent. Only used for the root node as all other nodes will have parents.
	private void addNode(TreeNode node){
		node.init(game);
	}
	
	//Adds a node to the tree and specifies a parent for the node.
	public void addNode(TreeNode node, TreeNode childOf){
		node.init(game);
		childOf.addChild(node);
	}

	//The function used in the controller for testing the tree. 
	//Nodes are responsible for the behaviours and returns the result of the run through of the tree in the end.
	public Result testTree(){
		return rootNode.testNode();
	}
}
