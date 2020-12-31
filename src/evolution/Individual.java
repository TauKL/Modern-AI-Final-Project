package evolution;

import java.io.Serializable;
import java.util.Random;

import controllers.EvolutionPacMan;
import pacman.controllers.Controller;
import pacman.game.Constants.MOVE;

public class Individual implements Serializable{
	public Controller<MOVE> controller;
	Random r;
	
	//0 = eat pills
	//1 = eat power pill
	//2 = kill ghosts
	//3 = flee from ghosts
	//4 = go to junction
	public int[][] functions;
	public int minDistance=100;
	
	public double fitness;
	
	//Functions for each different action pacman can take.
	public Individual(){
		r = new Random();
		functions= new int[5][5];
		for(int i=0; i<functions.length; i++){
			for (int j=0; j<functions[i].length; j++){
				functions[i][j]=r.nextInt(21)-10;
			}
		}
		minDistance=r.nextInt(100);
		
		//Creates a controller with random functions defined above
		this.controller = new EvolutionPacMan(functions, minDistance);
	}
	
	//Mutates the functions with a certain chance and amount of iterations
	public void mutate(float chance, int mutations){
		//Mutates the functions
		for(int i = 0; i< mutations; i++){
			if(r.nextInt(100) >= chance * 100){
				functions[r.nextInt(4)][r.nextInt(4)]=r.nextInt(21)-10;
			}
		}
		//Mutates the minimum distance by +- 10
		if(r.nextInt(100) >= chance*100){
			minDistance=r.nextInt(10)+(minDistance);
		}
		if(minDistance<=0)
			minDistance=0;
		//Creates new controller with mutated functions
		this.controller = new EvolutionPacMan(functions,minDistance);
	}
}
