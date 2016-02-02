package com.nwgjb.neuralnetwork.image;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Painter extends JComponent implements MouseListener, MouseMotionListener {

	
	static final int PIXEL_SIZE=20;
	static final int PADDING=4;
	
	Color[][] color;
	Color current=Color.WHITE;
	
	boolean dirty=false;
	
	ArrayList<ChangeListener> changeListeners=new ArrayList<>();
	
	public Painter(int w, int h){
		color=new Color[h][w];
		for(Color[] row:color){
			for(int i=0;i<row.length;i++){
				row[i]=Color.BLACK;
			}
		}
		addMouseListener(this);
		addMouseMotionListener(this);
		
	}
	
	public void addChangeListener(ChangeListener l){
		changeListeners.add(l);
	}
	
	public void removeChangeListener(ChangeListener l){
		changeListeners.remove(l);
	}
	
	public Dimension getPreferredSize(){
		return new Dimension(color[0].length*PIXEL_SIZE, color.length*PIXEL_SIZE);
	}
	
	
	public static double[] intToDouble(int value){
		double[] binary = new double[32];  
		int index = 31;  
		do {  
			binary[index--]=(value&1);  
            value >>>= 1;  
		}while(value != 0);
		return binary;
	}
	
	public void paint(Graphics gp){
		Graphics2D g=(Graphics2D)gp;
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());
		for(int y=0;y<color.length;y++){
			Color[] row=color[y];
			for(int x=0;x<row.length;x++){
				g.setColor(row[x]);
				g.fillRect(x*PIXEL_SIZE+PADDING/2, y*PIXEL_SIZE+PADDING/2, PIXEL_SIZE-PADDING, PIXEL_SIZE-PADDING);
			}
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if(current==null)return;
		int x=e.getX()/PIXEL_SIZE;
		int y=e.getY()/PIXEL_SIZE;
		if(x<0||y<0)return;
		if(y>=color.length)return;
		if(x>=color[0].length)return;
		color[y][x]=current;
		if(y!=0)color[y-1][x]=color[y-1][x].brighter();
		if(x!=0)color[y][x-1]=color[y][x-1].brighter();
		dirty=true;
		repaint(x*PIXEL_SIZE, y*PIXEL_SIZE, PIXEL_SIZE, PIXEL_SIZE);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		int x=e.getX()/PIXEL_SIZE;
		int y=e.getY()/PIXEL_SIZE;
		color[y][x]=current;
		repaint(x*PIXEL_SIZE, y*PIXEL_SIZE, PIXEL_SIZE, PIXEL_SIZE);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(dirty){
			fireChange();
		}
	}

	private void fireChange() {
		ChangeEvent e=new ChangeEvent(this);
		for(ChangeListener cl:changeListeners){
			cl.stateChanged(e);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}
	
	
}
