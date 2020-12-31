package neuralNetwork;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


public class Network {
	public class Layer{
		ArrayList<Neuron> neurons;
		//Constructor intialises neuron array
		public Layer(){
			neurons=new ArrayList<Neuron>();
		}
		
		//Returns the neuron at the index given. Prints errors if it doesn't work and returns null.
		public Neuron getNeuron(int index){
			try{
				Neuron n=neurons.get(index);
				return n;
			}
			catch(NullPointerException e){System.out.println("no neuron on that index");}
			catch(IndexOutOfBoundsException f){System.out.println("out of bounds");}
			
			return null;
		}
		
		//Returns amount of neurons in this layer
		public int getSize(){
			return neurons.size();
		}
		
		//Puts a neuron in the layer
		public void putNeuron(Neuron n){
			neurons.add(n);
		}
	}

	//The network handles the entire network through this map, it's a map of layers with neurons
	private HashMap<Integer, Layer> neuralNetwork;
	
	private ArrayList<Float> outputs;
	
	//Constructor, define the amount of layers the network will have
	public Network(int layers){
		outputs = new ArrayList<Float>();
		this.neuralNetwork = new HashMap<Integer, Layer>();
		for (int i=0; i<layers; i++){
			neuralNetwork.put(i, new Layer());
		}
	}
	
	//Runs the network with given activations for the input nodes
	public ArrayList<Float> run(float[] activations){
		outputs.clear();
		
		//If the amount of activations don't match the amount of input nodes, returns 0 and prints this
		if(activations.length!=neuralNetwork.get(0).getSize()){
			System.out.println("Activations given: " + activations.length + ". But network has " + neuralNetwork.get(0).getSize() +" input neurons...\nRun was cancelled");
			return null;
		}
		
		//Sets the activation on all the input nodes
		for (int i = 0 ; i<activations.length; i++){
			neuralNetwork.get(0).getNeuron(i).inputActivation(activations[i]);
		}
		
		//Goes through from first hidden layer to output layer and activates the neurons
		for (int i = 1; i<neuralNetwork.size()-1;i++){
			for(int j = 0; j<neuralNetwork.get(i).getSize();j++){
				neuralNetwork.get(i).getNeuron(j).activate();
			}
		}
		for(Neuron n : neuralNetwork.get(neuralNetwork.size()-1).neurons){
			outputs.add(n.activate());
		}
		return outputs;
	}
	public void errorCalculations(float[] target){
		//errors
		//Assigns errors to output layer
		for(int i =0; i<neuralNetwork.get(neuralNetwork.size()-1).neurons.size(); i++){
			neuralNetwork.get(neuralNetwork.size()-1).getNeuron(i).calculateError(target[i], 0, 0);
		}
		
		//Assigns errors to hidden layers
		for(int i=neuralNetwork.size()-2; i>0; i--){
			float error=0;
			//Error is the combined error of all nodes in the layer above
			for(Neuron n : neuralNetwork.get(i+1).neurons){
				error+=n.error;
			}
			
			//Weights are the combined weights of all the nodes in the layer above that are connected to this neuron
			for(int j=0; j<neuralNetwork.get(i).neurons.size(); j++){
				float weights=0;
				for(int k = 0; k<neuralNetwork.get(i+1).neurons.size(); k++){
					for(int l = 0; l<neuralNetwork.get(i+1).getNeuron(k).connections.size(); l++){
						if(neuralNetwork.get(i+1).getNeuron(k).connections.get(l).neuron()==neuralNetwork.get(i).getNeuron(j)){
							weights+=neuralNetwork.get(i+1).getNeuron(k).connections.get(l).weight;
						}
					}
				}
				//Calculates the error on this hidden node
				neuralNetwork.get(i).getNeuron(j).calculateError(0, weights, error);
			}
		}
	}
	public void adjustWeights(float learningRate){
		//Adjusts the weights for the output nodes
				for(Neuron n : neuralNetwork.get(neuralNetwork.size()-1).neurons){
					n.adjustWeights(learningRate);
				}
				//Adjusting weights for the hidden layers
				for(int i=neuralNetwork.size()-2; i>=0; i--){
					for(int j=0; j<neuralNetwork.get(i).neurons.size(); j++){
						neuralNetwork.get(i).getNeuron(j).adjustWeights(learningRate);
					}
				}
	}
	//Backpropagation of the network.
	public void backPropagate(float learningRate, float[] target){
		errorCalculations(target);
		adjustWeights(learningRate);
	}
	//Create an input neuron
	public Neuron createInput(){
		InputNeuron n = new InputNeuron();
		neuralNetwork.get(0).putNeuron(n);
		return n;
	}
	//Create a hidden neuron with specified layer and bias
	public Neuron createHidden(int layer, float bias){
		HiddenNeuron n = new HiddenNeuron();
		//If the layer is not between input and output, returns an error and null
		if(layer<1|| layer>neuralNetwork.size()){
			System.out.println("invalid layer " + layer + " must be between 1 and " + (neuralNetwork.size()-2));
			return null;
		}
		else{
			n.bias=bias;
			neuralNetwork.get(layer).putNeuron(n);
			return n;
		}
			
	}
	//Create an output neuron with specified bias
	public Neuron createOutput(float bias){
		OutputNeuron n = new OutputNeuron();
		n.bias=bias;
		neuralNetwork.get(neuralNetwork.size()-1).putNeuron(n);
		return n;
	}
	
	//Connects all the nodes with random weights.
	public void connectAllNodesRandom(){
		Random r = new Random();
		for(int i = 1; i<neuralNetwork.size(); i++){
			for(int j = 0; j<neuralNetwork.get(i).neurons.size(); j++){
				for (int k = 0; k<neuralNetwork.get(i-1).neurons.size(); k++){
					neuralNetwork.get(i).getNeuron(j).connectNeuron(neuralNetwork.get(i-1).getNeuron(k), r.nextFloat());
				}
			}
		}
	}
}
