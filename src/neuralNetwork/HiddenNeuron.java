package neuralNetwork;

public class HiddenNeuron extends Neuron{
	
	@Override
	public float activate() {
		input=0;
		//Adds all the outputs from connected neurons in previous layer to the input for this neuron
		for(int i=0; i<connections.size(); i++){
			input+=(connections.get(i).weight*connections.get(i).neuron().output);
		}
		//Adds the bias
		input+=bias;
		
		//Output is the combined input put through the sigmoid function
		output=(float) (1/(1+(Math.pow(Math.E, -input))));	//Sigmoid function
		return output;
	}

	@Override
	public void calculateError(float target, float linkWeight, float previousError) {
		//error calculation function for hidden nodes
		error = output*(1-output)*(previousError)*(linkWeight);
	}

}
