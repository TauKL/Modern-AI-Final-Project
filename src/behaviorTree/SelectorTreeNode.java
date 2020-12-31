package behaviorTree;

//A selector node for the behavior tree
public class SelectorTreeNode extends TreeNode {
	
	public Result testNode(){
		
		//defaults to a failure if none of the conditions are tested to be true.
		result.status=returnStatus.FAILURE;
		
		//If it has no children, then all the subconditions are true and it returns success.
		//This should not happen, but is a failsafe if the behavior tree is built wrong.
		if(children.size()<=0){
			System.out.println("Selector Tree has no children");
			result.status=returnStatus.SUCCESS;
		}
		
		//For loop that checks the children of the selector.
		outerloop:
		for(TreeNode n : children){
			//gets the result of the child
			Result test = n.testNode();
			//tests the result of the child, if it succeeds, the entire selector succeeds
			//the for loop is therefore broken and the move and status are passed up through the tree.
			if(test.status == returnStatus.SUCCESS){
				result.status = test.status;
				result.move=test.move;
				break outerloop;
			}
		}
		return result;
	}
}
