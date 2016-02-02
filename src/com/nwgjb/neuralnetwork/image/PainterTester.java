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
import javax.swing.JProgressBar;
import javax.swing.JToolBar;

import com.nwgjb.commons.Tuple;
import com.nwgjb.neuralnetwork.FileTool;
import com.nwgjb.neuralnetwork.NeuralNetwork;
import com.nwgjb.neuralnetwork.OutputConverter;

public class PainterTester {
	
	
	
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
		final JFrame f=new JFrame("Painter");
		final Painter p=new Painter(28, 28);
		f.add(p);
		
		final NeuralNetwork nn=new NeuralNetwork(1.0, 28*28, 512, 10);
		final OutputConverter<Character> outconv=new OutputConverter<>('0', '1', '2', '3', '4', '5', '6', '7', '8', '9');
		final MinstReader mr=new MinstReader(new File("train_img"), new File("train_label"));
		final MinstReader testReader=new MinstReader(new File("test_img"), new File("test_label"));
		
		JToolBar toolbar=new JToolBar();
		
		JButton but=new JButton("Tell");
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
		
		JButton save=new JButton("Save");
		save.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				FileTool.writeToFile(nn, new File("image-mass.txt"));
			}
		});
		
		toolbar.add(but);
		toolbar.add(clear);
		toolbar.add(save);
		
		/*JButton loadAndTrainOne=new JButton("Train 1");
		loadAndTrainOne.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Tuple._2<Byte, Color[][]> img = mr.readImage();
					p.color=img._2;
					p.repaint();
					double[] exp={0,0,0,0,0,0,0,0,0,0};
					exp[img._1]=1;
					nn.train(colorToDouble(img._2), exp);
					System.out.println("Training "+img._1);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		toolbar.add(loadAndTrainOne);*/
		
		JButton loadAndTest=new JButton("Test 1");
		loadAndTest.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Tuple._2<Byte, Color[][]> img = testReader.readImage();
					p.color=img._2;
					p.repaint();
					double[] output=nn.getOutput(colorToDouble(img._2));
					int max=0;
					for(int j=1;j<output.length;j++){
						if(output[j]>output[max]){
							max=j;
						}
					}
					System.out.println("Correct["+img._1+"], Returned["+max+"], "+(img._1==max?"Success":"Fail"));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		toolbar.add(loadAndTest);
		
		JButton loadAndTrainAll=new JButton("Train All");
		loadAndTrainAll.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				final JProgressBar progress=new JProgressBar();
				f.add(progress, BorderLayout.SOUTH);
				f.pack();
				new Thread(new Runnable(){
					@Override
					public void run() {
						try {
							Tuple._2<Byte, Color[][]> img = null;
							int cases=0;
							while((img=mr.readImage())!=null){
								double[] exp={0,0,0,0,0,0,0,0,0,0};
								exp[img._1]=1;
								nn.train(colorToDouble(img._2), exp);
								//System.out.println("Training "+img._1);
								if(++cases%600==0){
									progress.setValue(cases/600);
								}
							}
							System.out.println("Trained");
							f.remove(progress);
							f.pack();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}).start();
			}
		});
		toolbar.add(loadAndTrainAll);
		
		JButton loadAndTestAll=new JButton("Test All");
		loadAndTestAll.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				final JProgressBar progress=new JProgressBar();
				f.add(progress, BorderLayout.SOUTH);
				f.pack();
				new Thread(new Runnable(){
					@Override
					public void run() {
						try {
							Tuple._2<Byte, Color[][]> img = null;
							int success=0;
							int fail=0;
							int cases=0;
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
								}
								if(++cases%100==0){
									progress.setValue(cases/100);
								}
							}
							System.out.println("Test Finished");
							System.out.println("Correct: "+success+"("+Math.round(success*100./(success+fail))+"%)");
							System.out.println("Failed: "+fail+"("+Math.round(fail*100./(success+fail))+"%)");
							f.remove(progress);
							f.pack();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}).start();
			}
		});
		toolbar.add(loadAndTestAll);
		
		JButton testUtilFail=new JButton("Test Util Fail");
		testUtilFail.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				final JProgressBar progress=new JProgressBar();
				progress.setIndeterminate(true);
				f.add(progress, BorderLayout.SOUTH);
				f.pack();
				new Thread(new Runnable(){
					@Override
					public void run() {
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
									p.color=img._2;
									p.repaint();
									break;
								}
							}
							System.out.println("Test Finished");
							System.out.println("Correct: "+success+"("+Math.round(success*100./(success+fail))+"%)");
							System.out.println("Failed: "+fail+"("+Math.round(fail*100./(success+fail))+"%)");
							f.remove(progress);
							f.pack();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}).start();
			}
		});
		toolbar.add(testUtilFail);
		
		
		
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
