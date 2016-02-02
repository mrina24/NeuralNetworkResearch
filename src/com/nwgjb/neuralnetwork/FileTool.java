package com.nwgjb.neuralnetwork;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileTool {
	public static void writeToFile(NeuralNetwork nn, File file){
		try(
			DataOutputStream w=new DataOutputStream(new FileOutputStream(file));
		){
			w.writeInt(nn.layer.length);
			for(NeuronLayer layer:nn.layer){
				w.writeInt(layer.neurons.length);
				for(Neuron n:layer.neurons){
					w.writeInt(n.weighting.length);
					w.writeDouble(n.bias);
					for(double ww:n.weighting){
						w.writeDouble(ww);
					}
				}
			}
		}catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static NeuralNetwork readFromFile(File file){
		try(
			DataInputStream s=new DataInputStream(new FileInputStream(file));
		){
			NeuralNetwork nn=new NeuralNetwork(1.0);
			nn.layer=new NeuronLayer[s.readInt()];
			for(int i=0;i<nn.layer.length;i++){
				NeuronLayer nl=new NeuronLayer();
				nn.layer[i]=nl;
				nl.neurons=new Neuron[s.readInt()];
				for(int j=0;j<nl.neurons.length;j++){
					Neuron neuron=new Neuron();
					nl.neurons[j]=neuron;
					neuron.weighting=new double[s.readInt()];
					neuron.bias=s.readDouble();
					for(int k=0;k<neuron.weighting.length;k++){
						neuron.weighting[k]=s.readDouble();
					}
				}
			}
			return nn;
		}catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
	}
}
