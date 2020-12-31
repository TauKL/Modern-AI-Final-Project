package neuralNetwork;
import java.util.ArrayList;

public abstract class Neuron {
	//The abstract base neuron that all other neurons inherit from.
	//This has all the shared values input, output, bias, error and an abstract activation, error calculation and weight adjustment function
	//The input neuron does not implement these functions, but just inherits the variables and shared functions
	public ArrayList<Connection> connections = new ArrayList<Connection>();
	public float input = 0;
	public float output = 0;
	public float bias = 0;
	public float error = 0;
	//activation function specific to the type of neuron
	public abstract float activate();
	
	//Error calculation specific to the type of neuron. Only output uses target and only hidden uses linkWeight and previousError.
	public abstract void calculateError(float target, float linkWeight, float previousError);
	
	//Weights adjustment function shared by all types of neurons
	public void adjustWeights(float learningRate) {
		bias=bias+(learningRate*error);
		//System.out.println("Bias: " + bias);
		for(int i = 0; i<connections.size(); i++){
			connections.get(i).weight=connections.get(i).weight+(learningRate*(error*connections.get(i).neuron().output));
		}
	}
	//Connects a neuron to the specified neuron with the weight
	public void connectNeuron(Neuron to, float weight){
		connections.add(new Connection(to, weight));
	}
	//Sets the input neurons activation
	public void inputActivation(float activation){
		input=activation;
		output=activation;
	}
}