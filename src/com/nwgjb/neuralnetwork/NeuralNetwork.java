package com.nwgjb.neuralnetwork;

import java.util.HashMap;

public class NeuralNetwork {
	
	NeuronLayer[] layer;
	
	double rate;
	
	public NeuralNetwork(double rate, int... num){
		//this.rate=rate;
		layer=new NeuronLayer[num.length-1];
		for(int i=0;i<num.length-1;i++){
			layer[i]=new NeuronLayer(num[i+1], num[i]);
		}
	}
	
	NeuralNetwork(double rate){
		
	}
	
	public double[] getOutput(double... input){
		for(NeuronLayer l:layer){
			input=l.getOutput(input);
		}
		return input;
	}
	
	public void trainOld(double[] input, double[] expected){
		double[][] rawOut=new double[layer.length][];
		double[][] out=new double[layer.length][];
		rawOut[0]=layer[0].getRawOutput(input);
		out[0]=NeuronLayer.activation(rawOut[0]);
		for(int i=1;i<layer.length;i++){
			rawOut[i]=layer[i].getRawOutput(out[i-1]);
			out[i]=NeuronLayer.activation(rawOut[i]);
		}
		double[] rawOuter=rawOut[rawOut.length-1];
		double[] outer=out[out.length-1];
		
		double[] outerDelta=new double[outer.length];
		double[][] delta=new double[layer.length][];
		double[] biasDelta=new double[layer.length];
				
		for(int j=0;j<outer.length;j++){
			outerDelta[j]=Neuron.derivateTransfer(rawOuter[j])*(expected[j]-outer[j]);
		}
		
		delta[delta.length-1]=outerDelta;
		
		for(int l=layer.length-2; l>=0; l--){
			delta[l]=new double[rawOut[l].length];
			for(int i=0;i<rawOut[l].length;i++){
				double sum=0;
				for(int j=0;j<rawOut[l+1].length;j++){
					sum+=layer[l+1].neurons[j].weighting[i]*delta[l+1][j];
				}
				delta[l][i]=Neuron.derivateTransfer(rawOut[l][i])*sum;
				for(int j=0;j<rawOut[l+1].length;j++){
					Neuron n=layer[l+1].neurons[j];
					n.weighting[i]+=rate*out[l][i]*delta[l+1][j];
				}
			}
			double sum=0;
			for(int j=0;j<rawOut[l+1].length;j++){
				sum+=layer[l+1].neurons[j].bias*delta[l+1][j];
			}
			biasDelta[l]=Neuron.derivateTransfer(-1)*sum;
			for(int j=0;j<rawOut[l+1].length;j++){
				Neuron n=layer[l+1].neurons[j];
				n.bias+=rate*-1*delta[l+1][j];
			}
		}
		
		for(int i=0;i<input.length;i++){
			for(int j=0;j<rawOut[0].length;j++){
				Neuron n=layer[0].neurons[j];
				n.weighting[i]+=rate*input[i]*delta[0][j];
			}
		}
		for(int j=0;j<rawOut[0].length;j++){
			Neuron n=layer[0].neurons[j];
			n.bias+=rate*-1*delta[0][j];
		}
	}
	
	public void train(double[] input, double[] expected){
		double[][] out=new double[layer.length][];
		out[0]=layer[0].getOutput(input);
		for(int i=1;i<layer.length;i++){
			out[i]=layer[i].getOutput(out[i-1]);
		}
		double[] outer=out[out.length-1];
		
		double[] outerDelta=new double[outer.length];
		double[][] delta=new double[layer.length][];
		double[] biasDelta=new double[layer.length];
				
		for(int j=0;j<outer.length;j++){
			outerDelta[j]=Neuron.derivateByOutput(outer[j])*(expected[j]-outer[j]);
		}
		
		delta[delta.length-1]=outerDelta;
		
		for(int l=layer.length-2; l>=0; l--){
			delta[l]=new double[out[l].length];
			for(int i=0;i<out[l].length;i++){
				double sum=0;
				for(int j=0;j<out[l+1].length;j++){
					sum+=layer[l+1].neurons[j].weighting[i]*delta[l+1][j];
				}
				delta[l][i]=Neuron.derivateByOutput(out[l][i])*sum;
				for(int j=0;j<out[l+1].length;j++){
					Neuron n=layer[l+1].neurons[j];
					n.weighting[i]+=rate*out[l][i]*delta[l+1][j];
				}
			}
			double sum=0;
			for(int j=0;j<out[l+1].length;j++){
				sum+=layer[l+1].neurons[j].bias*delta[l+1][j];
			}
			biasDelta[l]=Neuron.derivateTransfer(-1)*sum;
			for(int j=0;j<out[l+1].length;j++){
				Neuron n=layer[l+1].neurons[j];
				n.bias+=rate*-1*delta[l+1][j];
			}
		}
		
		for(int i=0;i<input.length;i++){
			for(int j=0;j<out[0].length;j++){
				Neuron n=layer[0].neurons[j];
				n.weighting[i]+=rate*input[i]*delta[0][j];
			}
		}
		for(int j=0;j<out[0].length;j++){
			Neuron n=layer[0].neurons[j];
			n.bias+=rate*-1*delta[0][j];
		}
	}
	
	/*
	private void outputErr(double[] prediction, double[] expected){
		double err=0;
		double[] outputDelta=new double[prediction.length];
		for(int i=0;i<outputDelta.length;i++){
			double out=prediction[i];
			outputDelta[i]=out*(1.-out)*(expected[i]-out);
			err+=Math.abs(outputDelta[i]);
		}
		
		
        hidErrSum = errSum;  
	}*/
}
