package osdi.primesolver;

//Not used 
public interface ThreadQueue<T> {
	//public void addThreads(Thread t);
	//public void controlThreads() throws InterruptedException;
	void enqueue(T t);
    T dequeue();
	
}
