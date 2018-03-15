package osdi.locks;

/*
 * Grad students only - re-write this in terms of osdi.locks.SpinLock
 */
/*
 * Grad students only - you may not use anything in java.util.concurrent.* you may only use locks from osdi.locks.*
 */
//create another semaphore class
//keep track
//method is prime added to count 
//how numbers we have to check
//exit once all checked 
public class ThreadSemaphore {
   // private final java.util.concurrent.Semaphore javaSemaphore;
	private final SpinLock spinLock;
	private volatile long value;
	
    public ThreadSemaphore(int initialValue) {
    //	if(initialValue < 0) {
      //      throw new IllegalStateException("initialValue >= 0");
       // }
    	spinLock = new SpinLock();
    	value = initialValue;
        
       // javaSemaphore = new java.util.concurrent.Semaphore(initialValue);

    }

    public void down() {
        try{
        	spinLock.lock();
        	
        	while( value == 0 ){
        		try{
        			spinLock.lock();
        			try {
        				spinLock.unlock();
        				Thread.yield();
        				
        			}finally{
        				spinLock.lock();
        			}
        			--value;
        		}finally{
        			spinLock.unlock();
        		}
        	}
           
        } finally{
        }
    }

    public void up() {
    	try{
    		spinLock.lock();
    		++value;
    	}finally{
    		spinLock.unlock();
    	}
       // javaSemaphore.release(1);
    }
}