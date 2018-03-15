package osdi.collections;
import osdi.locks.Semaphore;
import osdi.locks.SpinLock;

/*
 * Modify this as you see fit. you may not use anything in java.util.concurrent.* you may only use locks from osdi.locks.*
 */
class BoundBufferImpl<T> implements SimpleQueue<T> {
    private final int bufferSize;
    private final java.util.Queue<T> queue;
    private final SpinLock spinLock;
    private final Semaphore empty, full, mutex;

    
    public BoundBufferImpl(int bufferSize) {
        this.bufferSize = bufferSize;
        queue = new java.util.ArrayDeque<>();
        mutex = new Semaphore(1);
        empty = new Semaphore(bufferSize);
        full = new Semaphore(0);
        spinLock = new SpinLock();
    }

    @Override
    public void enqueue(T item) {
    	empty.down();
    	mutex.down();
    	spinLock.lock();
        queue.offer(item);    // O(1) queue.offer adds to the head
        spinLock.unlock();
        mutex.up();
        full.up(); 
    }

    @Override
    public T dequeue() {
     	T item = null;
    	full.down();
    	mutex.down();
    	spinLock.lock();
     	if( queue.size() > 0  && size() <= bufferSize){
    	  item = queue.poll(); // O(1) removes the head of this queue  
    	}
     	spinLock.unlock();
    	mutex.up();
    	empty.up();
        return item;
    }
    public int size(){
    	spinLock.lock();
    	int size = queue.size();
    	spinLock.unlock();
    	return size;
    }
}
 