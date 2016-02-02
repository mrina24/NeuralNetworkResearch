package com.nwgjb.neuralnetwork;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.nwgjb.commons.Tuple;

public class OutputConverter<O> {
	O[] output;
	
	private static final Comparator<Tuple._2<?, Double>> comparator=Tuple._2.comp2D();
	
	@SafeVarargs
	public OutputConverter(O... out){
		output=out;
	}
	
	public ArrayList<Tuple._2<O, Double>> convert(double[] result){
		ArrayList<Tuple._2<O, Double>> list=new ArrayList<>();
		for(int i=0;i<output.length;i++){
			list.add(Tuple.as(output[i], result[i]));
		}
		Collections.sort(list, comparator);
		return list;
	}
	
}
