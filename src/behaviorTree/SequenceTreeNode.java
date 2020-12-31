package behaviorTree;


//A sequence node for the behaviour tree
public class SequenceTreeNode extends TreeNode {
	
	public Result testNode(){
		
		//Defaults to a success if none of the tests fail.
		result.status = returnStatus.SUCCESS;
		
		//checks all the children.
		outerloop:
		for(TreeNode n : children){
			//gets the result of the child's test
			Result test = n.testNode();
			//If the child fails, the entire thing fails.
			if(test.status == returnStatus.FAILURE){
				result.status = returnStatus.FAILURE;
				break outerloop;
			}
			//if the child doesn't fail, the sequence will keep running until that happens, but will also assign the test result to the actual result.
			//This is done in case one of the children will give a running/move command through the tree.
			result=test;
		}
		return result;
	}
}
