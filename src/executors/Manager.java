package executors;

import java.util.LinkedList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Manager implements Runnable {
    private int numberOfWorkers;
    private final BlockingQueue<Runnable> queue;
    private boolean finish;

    public Manager(LinkedList<Runnable> queue, int number) {
        int size = queue.size();
        this.queue = new ArrayBlockingQueue<>(size);
        while (size > 0) {
            this.queue.offer(queue.poll());
            size--;
        }
        Worker[] workers = new Worker[number];
        finish = false;
        for (int i = 0; i < workers.length; i++) {
            workers[i] = new Worker();
            workers[i].start();
        }
    }

    public void stop() {
        finish = true;
    }

    @Override
    public void run() {
        synchronized(queue) {
            try {
                while (!finish) queue.wait();
            } catch (InterruptedException ignored) {}
        }
        System.out.println(getClass().getSimpleName() + " checks...");
    }

    private class Worker implements Runnable {
        private final Thread self;

        public Worker() {
            int number = ++numberOfWorkers;
            self = new Thread(this, "Worker№" + number);
        }

        public void start(){
            self.start();
        }

        @Override
        public void run() {
            while (!finish) {
                if (!queue.isEmpty()) queue.poll().run();
            }
            synchronized(queue) {
                queue.notifyAll();
            }
        }
    }
}
