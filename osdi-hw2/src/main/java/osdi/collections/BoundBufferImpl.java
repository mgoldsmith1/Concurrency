package osdi.collections;

import osdi.locks.Semaphore;
import osdi.locks.SpinLock;

/*
 * Modify this as you see fit. you may not use anything in java.util.concurrent.* you may only use locks from osdi.locks.*
 */
class BoundBufferImpl<T> implements SimpleQueue<T> {
    private final int bufferSize;
    private final java.util.Queue<T> queue;
    private final Semaphore full;
    private final Semaphore empty;
    private final SpinLock lock;

    public BoundBufferImpl(int bufferSize) {
        this.bufferSize = bufferSize;
        queue = new java.util.ArrayDeque<>();
        full = new Semaphore(0);
        empty = new Semaphore(bufferSize);
        lock = new SpinLock();
    }

    @Override
    public void enqueue(T item) {
        empty.down();
        try {
            lock.lock();
            queue.add(item);
        } finally {
            lock.unlock();
        }
        full.up();
    }

    @Override
    public T dequeue() {
        T item;
        full.down();
        try {
            lock.lock();
            item = queue.remove();
        } finally {
            lock.unlock();
        }
        empty.up();
        return item;
    }

    @Override
    public boolean isEmpty() {
        try {
            lock.lock();
            return queue.isEmpty();
        } finally {
            lock.unlock();
        }
    }
}
