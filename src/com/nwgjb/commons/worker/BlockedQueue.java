package com.nwgjb.commons.worker;

import java.util.Iterator;
import java.util.LinkedList;

public class BlockedQueue<E>{
	
	LinkedList<E> queue=new LinkedList<>();

	public int size() {
		return queue.size();
	}

	public boolean isEmpty() {
		return queue.isEmpty();
	}

	public boolean contains(Object o) {
		return queue.contains(o);
	}

	public Iterator<E> iterator() {
		return queue.iterator();
	}

	public boolean remove(Object o) {
		return queue.remove(o);
	}

	public void clear() {
		queue.clear();
	}

	public synchronized void add(E e) {
		queue.add(e);
		notify();
	}

	public synchronized E poll() {
		if(isEmpty()){
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return queue.poll();
	}
	
}
