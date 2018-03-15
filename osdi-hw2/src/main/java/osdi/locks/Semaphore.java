package osdi.locks;

public class Semaphore {

	private final SpinLock spinLock;
	private volatile long value;
	
    public Semaphore(int initialValue) {
    	spinLock = new SpinLock();
    	value = initialValue;
    }
    
    public void down() {
    	try{
        	spinLock.lock();  	
        	while( value == 0 ){
        		try{
        			spinLock.unlock();
        			Thread.yield();   			
        				
        		}finally{
        			spinLock.lock();
        		}
        	}
        	--value;
        }finally{
        	spinLock.unlock();
        }
    }
    
    public void up() {
    	try{
    		spinLock.lock();
    		++value;
    	}finally{
    		spinLock.unlock();
    	}
    }
  
}