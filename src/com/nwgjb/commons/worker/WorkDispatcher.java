package com.nwgjb.commons.worker;

import java.util.LinkedList;

public class WorkDispatcher extends Thread{

	BlockedQueue<Worker> workerPool=new BlockedQueue<>();
	BlockedQueue<Task<?>> tasks=new BlockedQueue<>();
	LinkedList<Task<?>> running=new LinkedList<>();
	LinkedList<Task<?>> paused=new LinkedList<>();
	
	public WorkDispatcher(int num){
		for(int i=0;i<num;i++){
			new Worker(this).start();
		}
		start();
	}
	
	public synchronized void run(){
		while(true){
			Worker w=workerPool.poll();
			Task<?> task=tasks.poll();
			w.running=task;
			running.add(task);
			w.wakeUp();
		}
	}
	
	public void addTask(Task<?> task){
		tasks.add(task);
	}

	void notifyFree(Worker worker) {
		workerPool.add(worker);
	}

	void finish(Task<?> r) {
		running.remove(r);
		if(r.getProcess()==100){
			paused.add(r);
			r.worker=null;
		}
	}
	
	synchronized void resumeTask(Task<?> task){
		paused.remove(task);
		tasks.add(task);
		notify();
	}
	
	public static void main(String[] args){
		WorkDispatcher w=new WorkDispatcher(2);
		Task<Void> taskA=new Task<Void>(){
			int progress=0;
			@Override
			public void work() {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("A");
				progress+=10;
			}
			@Override
			public Void asyncGet() {
				return null;
			}
			@Override
			public int getProcess() {
				return progress;
			}
		};
		
		Task<Void> taskB=new Task<Void>(){
			int progress=0;
			@Override
			public void work() {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("B");
				progress+=10;
			}
			@Override
			public Void asyncGet() {
				return null;
			}
			@Override
			public int getProcess() {
				return progress;
			}
		};
		
		Task<Void> taskC=new Task<Void>(){
			int progress=0;
			@Override
			public void work() {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("C");
				progress+=10;
			}
			@Override
			public Void asyncGet() {
				return null;
			}
			@Override
			public int getProcess() {
				return progress;
			}
		};
		w.addTask(taskA);
		w.addTask(taskB);
		w.addTask(taskC);
		
		taskA.pause();
	}
	

}
