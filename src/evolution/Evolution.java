package evolution;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.Random;


import static pacman.game.Constants.DELAY;
import pacman.controllers.Controller;
import pacman.controllers.examples.AggressiveGhosts;
import pacman.controllers.examples.Legacy;
import pacman.controllers.examples.Legacy2TheReckoning;
import pacman.controllers.examples.RandomGhosts;
import pacman.controllers.examples.StarterGhosts;
import pacman.game.Game;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;

public class Evolution {
	static ArrayList<Individual> population;
	static Random r;
	static Individual bestInd;
	
	//Crossover variables. 
	//If there are any leftover individuals in the population, they will just be randomly selected from the previous population and inserted into the new one
	static int keepIndividuals = 10;
	static int crossoverIndividuals = 60;
	
	public Evolution(){
		bestInd=new Individual();
		bestInd.fitness=0;
	}
	//Runs the evolution the specified amount of times with a stopping condition and population size
	public Individual run(int evolutions, double stopFitness, int populationSize){
		r = new Random();
		
		//Adds an individual for each spot depending on population size.
		//Initial values for the individual are randomised
		population = new ArrayList<Individual>();
		for(int i = 0; i < populationSize; i++){
			population.add(new Individual());
		}
		
		//Iterates through the evolution the specified amount of times or breaks the loop if stopFitness is reached
		int iteration=0;
		while(iteration<evolutions){
			//Sorts the new population by the highest fitness first
			sortPopulation();
			
			//Continues to next iteration if it's the first iteration
			if(iteration==0){
				iteration++;
				continue;
			}			

			//Mutates the individuals ignoring the best 5
			for(int i = 5; i<population.size(); i++){
				//80% chance to mutate and mutates up to 5 different functions
				population.get(i).mutate(0.8f, r.nextInt(5)+1);
			}
			
			//Evaluates the fitness of each individuals controller against the different pre-build ghost controllers 3 times each.
			for(Individual i : population){
				i.fitness = runExperiment(i.controller, new StarterGhosts(), 10);
				i.fitness += runExperiment(i.controller, new AggressiveGhosts(), 10);
//				i.fitness += runExperiment(i.controller, new RandomGhosts(), 3);
//				i.fitness += runExperiment(i.controller, new Legacy(), 3);
//				i.fitness += runExperiment(i.controller, new Legacy2TheReckoning(), 3);
				i.fitness/=2;
			}
			
			//Selection does the crossover on the population and keeps the best individuals depending on the specified variables defined at the top.
			population = selection();	
			
			//Prints out the fitness of the best performing individual from this population
			System.out.println("Best fitness for iteration " + iteration + ": " + population.get(0).fitness);
			
			//Returns the best individual if we reached the accepted fitness
			if(population.get(0).fitness > stopFitness)
				return population.get(0);
			
			//Saves the best individual we ever got through evolution
			if(population.get(0).fitness>=bestInd.fitness){
				//System.out.println(population.get(0).fitness + " is higher than " + bestIndividual.fitness);
				bestInd.fitness=population.get(0).fitness;
				bestInd.functions=population.get(0).functions;
			}
			iteration++;
		}
		
		//Returns the best individual we ever got.
		return bestInd;
	}
	
	//Does selection of the best, crossover of the specified amount and fills in random individuals and creates a new population for the next iteration.
	public static ArrayList<Individual> selection(){
		ArrayList<Individual> newPopulation = new ArrayList<Individual>();

		//Sorts the population by the highest fitness first
		//This is after they have been mutated and re-evalutated.
		sortPopulation();
		
		
		//Keeps the best individuals as is. Amount specified in the variables at the top.
		for(int i = 0; i<keepIndividuals; i++){
			newPopulation.add(population.get(i));
		}
		
		//Does crossover on the next individuals. Amount specified in the variables at the top.
		for(int i = keepIndividuals; i<keepIndividuals+crossoverIndividuals; i++){
			if(i>=population.size()-1)
				break;
			newPopulation.add(crossover(population.get(i), population.get(i+1)));
		}

		//The rest are randomly selected individuals. Can be ones that are already there
		for(int i = keepIndividuals+crossoverIndividuals; i<population.size(); i++){
			int randomIndividual = r.nextInt(population.size());
			newPopulation.add(population.get(randomIndividual));
		}
		
		return newPopulation;
	}
	
	//Crossover that takes one of the functions from the worse individual and inserts into the better individual.
	//The better individual will then keep 3 of its functions and replace 1.
	public static Individual crossover(Individual ind1, Individual ind2){
		int cross = r.nextInt(4);
		for(int i=0; i<4; i++){
			if(i!=cross)
				continue;
			ind1.functions[i]=ind2.functions[i];
			break;
		}
		return ind1;
	}
	
	//runExperiment copied from the executor and modified to be used for evolution.
	//Adds the fitness as well for the score the controller got.
    public static double runExperiment(Controller<MOVE> pacManController,Controller<EnumMap<GHOST,MOVE>> ghostController,int trials){
    	double avgScore=0;
    	Random rnd=new Random(0);
		Game game;
		
		for(int i=0;i<trials;i++){
			game=new Game(rnd.nextLong());
			
			while(!game.gameOver()){
		        game.advanceGame(pacManController.getMove(game.copy(),System.currentTimeMillis()+DELAY),
		        		ghostController.getMove(game.copy(),System.currentTimeMillis()+DELAY));
			}
			avgScore+=game.getScore();
		}
		return avgScore/trials;
    }
    
    //Sorts the population
    private static void sortPopulation(){
		Collections.sort(population, new IndividualCompare());
    }
    
    //Writes the individual down to a file, used to save the individual that was chosen.
	public static void writeIndividual(Individual ind, String path){
		try {
	         FileOutputStream fileOut = new FileOutputStream(path);
	         ObjectOutputStream out = new ObjectOutputStream(fileOut);
	         out.writeObject(ind);
	         out.close();
	         fileOut.close();
	         System.out.printf("Data is saved as " + path);
	    }
		catch(IOException i) {
	         i.printStackTrace();
	    }
	}
	
	//Used to read an individual from file and returns that individual's controller.
	public static Controller readIndividual(String path){
		Individual individual = null;
	      try {
	         FileInputStream fileIn = new FileInputStream(path);
	         ObjectInputStream in = new ObjectInputStream(fileIn);
	         individual = (Individual) in.readObject();
	         in.close();
	         fileIn.close();
	         return individual.controller;
	      }
	      catch(IOException i) {
	         i.printStackTrace();
	         return null;
	      }
	      catch(ClassNotFoundException c) {
	         System.out.println("Individual class not found");
	         c.printStackTrace();
	         return null;
	      }
	}
}

//Comparator for comparing the fitness of individuals in a population
//Used for the collections.sort method.
class IndividualCompare implements Comparator {

    public int compare(Object arg0, Object arg1) {
        Individual ind0 = (Individual) arg0;
        Individual ind1 = (Individual) arg1;
        
        return Double.compare(ind1.fitness, ind0.fitness);
    }

}
