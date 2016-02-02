package com.nwgjb.commons.worker;

public class Worker extends Thread{
	
	Task<?> running;
	WorkDispatcher workdis;
	boolean pause;
	
	Worker(WorkDispatcher disp){
		workdis=disp;
	}
	
	public synchronized void wakeUp(){
		notify();
	}
	
	public void run(){
		while(true){
			if(running==null){
				synchronized(this){
					workdis.notifyFree(this);
					try {
						wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			while(!pause&&running.getProcess()!=100){
				running.work();
			}
			workdis.finish(running);
			running=null;
			pause=false;
		}
	}
	
}
