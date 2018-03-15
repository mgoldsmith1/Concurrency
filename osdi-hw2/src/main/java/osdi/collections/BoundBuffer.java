package osdi.collections;

import osdi.primesolver.ThreadManager;
import osdi.primesolver.ThreadQueue;

/*
 * you may not use anything in java.util.concurrent.* you may only use locks from osdi.locks.*
 */
public class BoundBuffer {
    public static <T> SimpleQueue<T> createBoundBufferWithSemaphores(int bufferSize) {
        return new BoundBufferImpl<>(bufferSize);
    }
    public static <T> ThreadQueue<Thread> createBoundBufferWithThreadSemaphores(int bufferSize) {
        return new ThreadManager<>(bufferSize);
    }
}