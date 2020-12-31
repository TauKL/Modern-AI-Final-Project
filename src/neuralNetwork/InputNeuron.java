package neuralNetwork;

public class InputNeuron extends Neuron{
	//This basically does nothing specific except returns its output, but holds shared variables with other neuron types.
	@Override
	public float activate() {
		return output;
	}

	@Override
	public void calculateError(float target, float linkWeight, float previousError) {	}

}
