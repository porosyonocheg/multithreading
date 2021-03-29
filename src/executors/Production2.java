package executors;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Production2 {
    private int[] array;

    Runnable task = new Runnable() {
        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName() + " is working...");
            long t1 = System.nanoTime();
            new HardWorkModel().sort(array);
            long t2 = System.nanoTime();
            System.out.println(Thread.currentThread().getName() + " ended working in time: " + (t2-t1));
        }
    };

    public Production2() {
        generateArray();
    }
    public static void main(String[] args) {
        LinkedList<Runnable> queue = new LinkedList<>();
        Production2[] p = new Production2[5];
        for (int i = 0; i < 5; i++) {
            p[i] = new Production2();
            queue.offer(p[i].task);
        }
        ExecutorService executor = Executors.newFixedThreadPool(5);
        while (!queue.isEmpty()) {
           executor.submit(queue.poll());
       }
        executor.shutdown();
    }

    private void generateArray() {
        array = new int[9999];
        for (int i = 0; i < array.length; i++) {
            array[i] = (int) (Math.random() * 99999);
        }
    }
}
