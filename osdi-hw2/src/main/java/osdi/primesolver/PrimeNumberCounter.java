package osdi.primesolver;

import osdi.collections.BoundBuffer;
import osdi.collections.SimpleQueue;
import osdi.locks.SpinLock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/*
 * you may not use anything in java.util.concurrent.* you may only use locks from osdi.locks.*
 */
public class PrimeNumberCounter {

    private long currentCount = 0L;
    private volatile long finishedTasks = 0;
    private final SpinLock finishedLock = new SpinLock();

    /*
     * you may not modify this method
     */
    private static int getThreadCount() {
        return Runtime.getRuntime().availableProcessors() * 4;
    }

    /*
     * you may not modify this method
     */
    private void startThreads(SimpleQueue<Long[]> valuesToCheck, SimpleQueue<Long[]> valuesThatArePrime) {
        Collection<Thread> threads = new ArrayList<>();
        int threadCount = getThreadCount();
        for(int i = 0; i < threadCount; i++) {
            Thread t = new Thread(()->findPrimeValues(valuesToCheck, valuesThatArePrime));
            t.setDaemon(true);
            threads.add(t);
        }
        Thread counter = new Thread(()->countPrimeValues(valuesThatArePrime));
        threads.add(counter);

        for(Thread t : threads) {
            t.setDaemon(true);
            t.start();
        }
    }

    /*
     * you may modify this method
     */
    public long countPrimeNumbers(NumberRange range) {
        SimpleQueue<Long[]> valuesToCheck = BoundBuffer.createBoundBufferWithSemaphores(100);
        SimpleQueue<Long[]> valuesThatArePrime = BoundBuffer.createBoundBufferWithSemaphores(50);

        startThreads(valuesToCheck, valuesThatArePrime);

        final int workSize = 2000;
        int index = -1;
        Long[] array = new Long[workSize];
        long taskCount = 0;

        for(Long value : range) {
            ++index;
            if(index == workSize) {
                valuesToCheck.enqueue(array);
                index = 0;
                array = new Long[workSize];
                ++taskCount;
            }
            array[index] = value;
        }
        if(index >= 0) {
            Long[] smaller = new Long[index+1];
            System.arraycopy(array, 0, smaller, 0, index+1);
            valuesToCheck.enqueue(smaller);
            ++taskCount;
        }

        while(true) {
            finishedLock.lock();
            if(finishedTasks == taskCount && valuesThatArePrime.isEmpty()) {
                try {
                    Thread.sleep(20);
                } catch (InterruptedException ignored) {
                }
                break;
            }
            finishedLock.unlock();
            Thread.yield();
        }

        return currentCount;
    }

    /*
     * you may modify this method
     */
    private void findPrimeValues(SimpleQueue<Long[]> valuesToCheck, SimpleQueue<Long[]> valuesThatArePrime) {
        while(true) {
            Long[] toCheck = valuesToCheck.dequeue();
            Collection<Long> primeValues = new ArrayList<>();
            for(Long value : toCheck) {
                if(Number.IsPrime(value)) {
                    primeValues.add(value);
                }
            }
            valuesThatArePrime.enqueue(primeValues.toArray(new Long[0]));
            finishedLock.lock();
            ++finishedTasks;
            finishedLock.unlock();
        }
    }

    /*
     * you may modify this method
     */
    private void countPrimeValues(SimpleQueue<Long[]> valuesThatArePrime) {
        while(true) {
            Long[] primeValues = valuesThatArePrime.dequeue();
            currentCount += primeValues.length;
            if(currentCount % 10000 == 0) {
                System.out.println("have " + currentCount + " prime values");
                System.out.flush();
            }
        }
    }
}
