package com.nwgjb.commons.worker;

public abstract class Task<T> {
	public abstract void work();	
	public abstract T asyncGet();
	public abstract int getProcess();
	
	Worker worker;
	
	public void pause(){
		if(worker!=null)worker.pause=true;
	}
	
	public void resume(){
		if(worker!=null)worker.workdis.resumeTask(this);
	}
	
}
