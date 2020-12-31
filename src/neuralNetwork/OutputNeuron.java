package neuralNetwork;
public class OutputNeuron extends Neuron{

	@Override
	public float activate() {
		input=0;
		//Adds all the outputs from connected neurons in previous layer to the input for this neuron
		for(int i=0; i<connections.size(); i++){
			//System.out.println(input + " weight " + connections.get(i).weight + " output from " + connections.get(i).neuron().output + " total = " + connections.get(i).weight*connections.get(i).neuron().output);
			input+=(connections.get(i).weight*connections.get(i).neuron().output);
			//System.out.println(input);
		}
		//Adds the bias
		input+=bias;
		
		//Output is the combined input put through the sigmoid function
		output=(float) (1/(1+(Math.pow(Math.E, -input))));	//Sigmoid function
		return output;
	}

	@Override
	public void calculateError(float target, float linkWeight, float previousError) {
		//error calculation function for output nodes
		error = output*(1-output)*(target-output);
		//System.out.println(error);
	}
}
