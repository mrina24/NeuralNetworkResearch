package com.nwgjb.neuralnetwork.image;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JToolBar;

import com.nwgjb.commons.Tuple;
import com.nwgjb.neuralnetwork.FileTool;
import com.nwgjb.neuralnetwork.NeuralNetwork;
import com.nwgjb.neuralnetwork.OutputConverter;

public class PainterRev {
	
	static class Tester implements ActionListener{
		
		NeuralNetwork nn;
		Painter p;
		int num;
		
		public Tester(NeuralNetwork n, Painter pr, int nu){
			nn=n;
			p=pr;
			num=nu;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			double[] input={0,0,0,0,0,0,0,0,0,0};
			input[num]=1;
			double[] out=nn.getOutput(input);
			int counter=0;
			for(Color[] c:p.color){
				for(int i=0;i<c.length;i++){
					double color=out[counter++]*256;
					//if(color>128)
					//	color*=2;
					if(color>255)color=255;
					int co=(int)color;
					c[i]=new Color(co, co, co);
				}
			}
			p.repaint();
		}
	}
	
	public static double[] colorToDouble(Color[][] color){
		double[] d=new double[color.length*color[0].length];
		int i=0;
		for(Color[] c:color){
			for(Color c1:c){
				d[i++]=c1.getRed()/255.;
			}
		}
		return d;
	}
	
	public static void main(String[] args) throws IOException {
		JFrame f=new JFrame("Painter");
		final Painter p=new Painter(28, 28);
		f.add(p);
		
		final NeuralNetwork nn=FileTool.readFromFile(new File("handwriting.txt"));//new NeuralNetwork(0.2, 10, 25, 100, 28*28);//
		//final OutputConverter<Character> outconv=new OutputConverter<>('0', '1', '2', '3', '4', '5', '6', '7', '8', '9');
		final MinstReader mr=new MinstReader(new File("train_img"), new File("train_label"));
		final MinstReader testReader=new MinstReader(new File("test_img"), new File("test_label"));
		
		JToolBar toolbar=new JToolBar();
		
		/*JButton but=new JButton("Tell");
		but.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				double[] d=new double[p.color.length*p.color[0].length];
				int i=0;
				for(Color[] c:p.color){
					for(Color c1:c){
						d[i++]=c1.getRed()/255.;
					}
				}
				double[] output=nn.getOutput(d);
				ArrayList<Tuple._2<Character, Double>> outputList=outconv.convert(output);
				for(int j=0;j<5;j++){
					Tuple._2<Character, Double> t=outputList.get(j);
					if(t._2<0.01)return;
					System.out.println(t._1 + " "+(int)(t._2*100)+"%");
				}
				
			}
		});
		toolbar.add(but);
		*/
		/*
		JButton clear=new JButton("Clear");
		clear.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				for(Color[] c:p.color){
					for(int i=0;i<c.length;i++){
						c[i]=Color.BLACK;
					}
				}
				p.repaint();
			}
		});
		toolbar.add(clear);
		*/
		
		JButton save=new JButton("Save");
		save.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				FileTool.writeToFile(nn, new File("handwriting.txt"));
			}
		});
		
		
		
		toolbar.add(save);
		
		JButton loadAndTrainOne=new JButton("Load and train one");
		loadAndTrainOne.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Tuple._2<Byte, Color[][]> img = mr.readImage();
					p.color=img._2;
					p.repaint();
					double[] exp={0,0,0,0,0,0,0,0,0,0};
					exp[img._1]=1;
					nn.train(exp, colorToDouble(img._2));
					System.out.println("Training "+img._1);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		toolbar.add(loadAndTrainOne);
		
		JButton loadAndTest;
		for(int i=0;i<10;i++){
			loadAndTest=new JButton(i+"");
			loadAndTest.addActionListener(new Tester(nn, p, i));
			toolbar.add(loadAndTest);
		}
		
		JButton loadAndTrainAll=new JButton("Load and train all");
		loadAndTrainAll.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Tuple._2<Byte, Color[][]> img = null;
					while((img=mr.readImage())!=null){
						double[] exp={0,0,0,0,0,0,0,0,0,0};
						exp[img._1]=1;
						nn.train(exp, colorToDouble(img._2));
						System.out.println("Training "+img._1);
					}
					System.out.println("Trained");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		toolbar.add(loadAndTrainAll);
		
		JButton loadAndTestAll=new JButton("Load and test all");
		loadAndTestAll.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Tuple._2<Byte, Color[][]> img = null;
					int success=0;
					int fail=0;
					while((img=testReader.readImage())!=null){
						double[] output=nn.getOutput(colorToDouble(img._2));
						int max=0;
						for(int j=1;j<output.length;j++){
							if(output[j]>output[max]){
								max=j;
							}
						}
						if(img._1==max){
							success++;
						}else{
							fail++;
							//System.out.println("Expected["+img._1+"], Reported["+max+"], Fail");
						}
					}
					System.out.println("Test Finished");
					System.out.println("Correct: "+success+"("+(success/(success+fail))+")");
					System.out.println("Failed: "+fail+"("+(success/(success+fail))+")");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		toolbar.add(loadAndTestAll);
		
		
		
		f.getContentPane().add(toolbar, BorderLayout.NORTH);
		
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.pack();
		f.setVisible(true);
		
		
		/*NeuralNetwork nn;
		if(false){
			nn=new NeuralNetwork(32, 40, 2);
			
			Random rand=new Random();
			for(int i=0;i<200000;i++){
				int r=rand.nextInt(Integer.MAX_VALUE)-Integer.MAX_VALUE/2;
				boolean neg=r<0;
				boolean even=r%2==0;
				double[] output={neg?1:0, even?1:0};
				nn.train(intToDouble(r), output);
			}
		}else{
			nn=FileTool.readFromFile(new File("out.txt"));
		}
		
		Scanner s=new Scanner(System.in);
		int in;
		while((in=s.nextInt())!=0){
			double[] d=nn.getOutput(intToDouble(in));
			System.out.println(in+" -> "+(d[0]>0.5?"Negative ":"Positive ")+(d[1]>0.5?"Even":"Odd"));
			System.out.println(Arrays.toString(d));
		}
		s.close();
		
		//FileTool.writeToFile(nn, new File("out.txt"));*/
	}
	
}
