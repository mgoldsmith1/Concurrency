
//package osdi.collections;
package osdi.primesolver;
import osdi.locks.Mutex;
import osdi.locks.ThreadSemaphore;

import java.util.ArrayList;
import java.util.Collection;


/*
 * Modify this as you see fit. you may not use anything in java.util.concurrent.* you may only use locks from osdi.locks.*
 */

// Not used 
public class ThreadManager<T> implements ThreadQueue<T> {
    private final int threadCount;
    private final Collection<Thread> _threads;
    private final java.util.Queue<T> queue;
    private final ThreadSemaphore empty, full, mutex;
    private Thread _t;

    public ThreadManager(int threadCount) {
    	this.threadCount = threadCount;
        _threads = new ArrayList<>();
        queue = new java.util.ArrayDeque<>();
        mutex = new ThreadSemaphore(1);
        empty = new ThreadSemaphore(threadCount); // (size())
        full = new ThreadSemaphore(0); 
    }

	@Override
	public void enqueue(T t) {
    	this._t = (Thread) t;
	    //_t.setDaemon(true);
		//_t.start();
		for( int i = 0; i < threadCount; i++){
			queue.add((T) _t);
		}
	}

	@Override
	public T dequeue() {
        T t1 = null;
        t1 = queue.poll();
        Thread t = (Thread) t1;
        t.run();
		return (T) t;
	}
}

