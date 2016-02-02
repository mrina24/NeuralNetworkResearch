package com.nwgjb.neuralnetwork.image;

import java.awt.Color;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.nwgjb.commons.Tuple;

public class MinstReader {
	
	DataInputStream label;
	DataInputStream img;
	int itemNum;
	int current=0;
	int row;
	int col;
	
	public MinstReader(File im, File lbl) throws IOException{
		label=new DataInputStream(new FileInputStream(lbl));
		img=new DataInputStream(new FileInputStream(im));
		label.readInt();
		img.readInt();
		itemNum=label.readInt();
		if(itemNum!=img.readInt()){
			throw new RuntimeException("B");
		}
		row=img.readInt();
		col=img.readInt();
		
	}
	
	public Tuple._2<Byte, Color[][]> readImage() throws IOException{
		if(current++==itemNum){
			return null;
		}
		Color[][] c=new Color[row][col];
		for(Color[] c1:c){
			for(int i=0;i<col;i++){
				int b=img.readUnsignedByte();
				c1[i]=new Color(b, b, b);
			}
		}
		
		return Tuple.as(label.readByte(), c);
	}
	
	
}

