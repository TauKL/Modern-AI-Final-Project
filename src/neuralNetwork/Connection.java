package neuralNetwork;

//The connection neurons have represented here with a to and a weight.
//Connections are only backwards.
public class Connection{
		private Neuron to;
		public float weight;
		public Connection(Neuron connectTo, float weight){
			to=connectTo;
			this.weight=weight;
		}
		public Neuron neuron(){
			return to;
		}
	}