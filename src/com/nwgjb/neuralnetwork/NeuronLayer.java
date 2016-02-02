package com.nwgjb.neuralnetwork;

public class NeuronLayer {
	Neuron[] neurons;
	boolean inTraining=false;
	double[] cachedOutput;
	double[] cachedRawOutput;
	
	public NeuronLayer(int num, int inputNum){
		neurons=new Neuron[num];
		for(int i=0;i<num;i++){
			neurons[i]=new Neuron(inputNum);
		}
	}
	
	NeuronLayer() {
		
	}

	public double[] getOutput(double[] input){
		double[] retArr=new double[neurons.length];
		for(int i=0;i<neurons.length;i++){
			retArr[i]=neurons[i].getOutput(input);
		}
		return retArr;
	}
	
	public double[] getRawOutput(double[] input){
		double[] retArr=new double[neurons.length];
		for(int i=0;i<neurons.length;i++){
			retArr[i]=neurons[i].getRawOutput(input);
		}
		return retArr;
	}
	
	public static double[] activation(double[] input){
		double[] retArr=new double[input.length];
		for(int i=0;i<input.length;i++){
			retArr[i]=Neuron.activation(input[i]);
		}
		return retArr;
	}
	
	
	
}
