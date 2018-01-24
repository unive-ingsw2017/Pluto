package mama.pluto.utils;

import java.util.concurrent.Semaphore;

public class CircularBuffer<T> {

    private final Semaphore empty;
    private final Semaphore full;
    private final T[] buffer;
    private int start = 0, end = 0;

    public CircularBuffer(int maxSize) {
        //noinspection unchecked
        this.buffer = (T[]) new Object[maxSize + 1];
        empty = new Semaphore(maxSize);
        full = new Semaphore(0);
    }

    public int getMaxSize() {
        return buffer.length - 1;
    }

    public int size() {
        if (end >= start) {
            return end - start;
        } else {
            return end + (buffer.length - start);
        }
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public boolean isFull() {
        return size() == buffer.length - 1;
    }

    public T pop() throws InterruptedException {
        full.acquire();
        synchronized (this) {
            T ret = buffer[start];
            buffer[start] = null;
            start = (start + 1) % buffer.length;
            empty.release();
            return ret;
        }
    }

    public void put(T obj) throws InterruptedException {
        empty.acquire();
        synchronized (this) {
            end = (end + 1) % buffer.length;
            buffer[end] = obj;
            full.release();
        }
    }
}
