package controllers;

import java.util.ArrayList;
import java.util.Random;

import dataRecording.DataSaverLoader;
import dataRecording.DataTuple;
import neuralNetwork.*;
import pacman.controllers.Controller;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.internal.Maze;

//My implementation of a Neural Network controller.
//Supporting classes are found in the neuralNetwork package
public class NeuralNetworkPacMan extends Controller<MOVE>{
	private MOVE myMove=MOVE.RIGHT;
	private float learningRate=0.07f;
	Network n;
	//Initialises and trains the network
	public NeuralNetworkPacMan(){
		//Save the maze, this was used for manual changes to the neural network but is currently not used.
		//This was supposed to let us manually adjust the errors by increasing them when Pacman did something especially stupid
//		Maze[] maze = new Maze[4];
//		for(int j = 0; j<4; j++){
//			maze[j]=new Maze(j);
//		}
		
		//Network with 3 layers
		n = new Network(3);

		//Creates all the nodes
		Random r = new Random();
		ArrayList<Neuron> inputs = new ArrayList<Neuron>();
		ArrayList<Neuron> hiddens = new ArrayList<Neuron>();
		for(int i = 0; i<9; i++){
			inputs.add(n.createInput());
		}
		for(int i = 0; i<6; i++){
			hiddens.add(n.createHidden(1, r.nextFloat()));
		}
		//Output neuron
		Neuron output = n.createOutput(r.nextFloat());
		
		//Originally we had 4 outputs.
//		Neuron right = n.createOutput(r.nextFloat());
//		Neuron up = n.createOutput(r.nextFloat());
//		Neuron down = n.createOutput(r.nextFloat());
		
		//Connects all the nodes with random weights.
		n.connectAllNodesRandom();
		
		//Gets the saved data from file
		DataTuple[] t = DataSaverLoader.LoadPacManData();
		
		//Runs the network for each tuple in the data
		for(int i=0; i<t.length; i++){
			//Each of the outcommented variables, are inputs we attempted to use as factors for the network
			ArrayList<Float> result=n.run(new float[]{
//				t[i].normalizeLevel(t[i].mazeIndex),
//				t[i].normalizeLevel(t[i].currentLevel),
				t[i].normalizePosition(t[i].pacmanPosition),
//				t[i].pacmanLivesLeft/3,
//				t[i].normalizeCurrentScore(t[i].currentScore),
//				t[i].normalizeTotalGameTime(t[i].totalGameTime),
//				t[i].normalizeCurrentLevelTime(t[i].currentLevelTime),
//				t[i].normalizeNumberOfPills(t[i].numOfPillsLeft),
//				t[i].normalizeNumberOfPowerPills(t[i].numOfPowerPillsLeft),
//				t[i].normalizeBoolean(t[i].isBlinkyEdible),
//				t[i].normalizeBoolean(t[i].isInkyEdible),
//				t[i].normalizeBoolean(t[i].isPinkyEdible),
//				t[i].normalizeBoolean(t[i].isSueEdible),
				t[i].normalizeDistance(t[i].blinkyDist),
				t[i].normalizeDistance(t[i].inkyDist),
				t[i].normalizeDistance(t[i].pinkyDist),
				t[i].normalizeDistance(t[i].sueDist),
				t[i].normalizeDirection(t[i].blinkyDir),
				t[i].normalizeDirection(t[i].inkyDir),
				t[i].normalizeDirection(t[i].pinkyDir),
				t[i].normalizeDirection(t[i].sueDir)
			});

			//Extracts the chosen direction from the result
			float[] targets = new float[]{0};
			switch(t[i].DirectionChosen){
			case UP:
				targets[0]=0;
				break;
			case RIGHT:
				targets[0]=0.25f;
				break;
			case DOWN:
				targets[0]=0.5f;
				break;
			case LEFT:
				targets[0]=0.75f;
				break;
			}
			//
			MOVE move = getMoveFromResult(result.get(0));
			
			//Backprop part 1, calculating errors on each node except inputs.
			n.errorCalculations(targets);
			
			//Attempt to manually adjust weights when pacman runs into walls.
			//This didn't work as intended so it does nothing, but left in to show the attempt we made.
//			Integer neighbor = maze[t[i].mazeIndex].graph[t[i].pacmanPosition].neighbourhood.get(move);
//			if (neighbor==null){
//				output.error*=1;
//			}
			
			//Backprop part 2, adjusting weights.
			n.adjustWeights(learningRate);
		}
		
	}
	public MOVE getMove(Game game, long timeDue){
		//Gets the game state
		DataTuple state = new DataTuple(game, myMove);
		ArrayList<Float> result = new ArrayList<Float>();
		
		//Runs the network with the necessary variables from the game state.
		result = n.run(new float[]{
				state.normalizePosition(state.pacmanPosition),
				state.normalizeDistance(state.blinkyDist),
				state.normalizeDistance(state.inkyDist),
				state.normalizeDistance(state.pinkyDist),
				state.normalizeDistance(state.sueDist),
				state.normalizeDirection(state.blinkyDir),
				state.normalizeDirection(state.inkyDir),
				state.normalizeDirection(state.pinkyDir),
				state.normalizeDirection(state.sueDir)
			});

		//Translates result to a move
		myMove=getMoveFromResult(result.get(0));
			
		return myMove;
		
	}
	
	//Returns a move from a number between 0 and 1 (results of network running)
	//0 = UP
	//0.25 = RIGHT
	//0.5 = DOWN
	//0.75 = LEFT
	// Whichever number is closer is the chosen result.
	private MOVE getMoveFromResult(float result){

		float[] dists = new float[]{0,0,0,0};
		dists[0]=Math.abs(0-result);
		dists[1]=Math.abs(0.25f-result);
		dists[2]=Math.abs(0.5f-result);
		dists[3]=Math.abs(0.75f-result);
		MOVE move = MOVE.NEUTRAL;
		int highest = 0;
		float highestValue = dists[0];
		for(int j = 1; j<dists.length; j++){
			if (dists[j]<highestValue){
				highest=j;
				highestValue=dists[j];
			}
		}
		switch(highest){
		case 0:
			move=MOVE.UP;
			break;
		case 1:
			move=MOVE.RIGHT;
			break;
		case 2: 
			move=MOVE.DOWN;
			break;
		case 3:
			move=MOVE.LEFT;
			break;
		}
		
		return move;
	}
}	
		
























