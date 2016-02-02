package com.nwgjb.neuralnetwork;

import java.util.Random;


public class Neuron{
	double[] weighting;
	double bias;
	
	public Neuron(int inputNum){
		weighting=new double[inputNum];
		Random rand=new Random();
		for(int i=0;i<inputNum;i++){
			weighting[i]=rand.nextDouble()*2-1;
		}
		bias=rand.nextDouble()*2-1;
	}
	
	Neuron() {
	
	}

	public double getOutput(double[] input){
		double counter=0;
		for(int i=0;i<input.length;i++){
			counter+=input[i]*weighting[i];
		}
		return activation(counter-bias);
	}
	
	public double getRawOutput(double[] input){
		double counter=0;
		for(int i=0;i<input.length;i++){
			counter+=input[i]*weighting[i];
		}
		return counter-bias;
	}
	
	public static double activation(double input){
		return 1/(1+Math.exp(-input));
	}
	
	public static double derivateByOutput(double output){
		return output*(1-output);
	}
	
	public static double derivateTransfer(double input){
		return activation(input)*(1-activation(input));
	}
	
	public void setWeighting(int i, double w){
		weighting[i]=w;
	}

	public void setBias(double w) {
		bias=w;
	}

	public double getWeighting(int i) {
		return weighting[i];
	}

	public double getBias() {
		return bias;
	}
	
	
}
