package executors;

import java.util.LinkedList;

/** The task is to do some work by any number of workers a given number of times and then the manager checks it.
 * We create a queue of the tasks and send it to the manager.
 * @author Sergey Shershavin*/

public class Production {
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

    public Production() {
        generateArray();
    }

    public static void main(String[] args) throws InterruptedException {
        LinkedList<Runnable> queue = new LinkedList<>();
        Production[] p = new Production[5];
        for (int i = 0; i < 5; i++) {
            p[i] = new Production();
            queue.offer(p[i].task);
        }
        Manager m = new Manager(queue, 5);
        new Thread(m).start();
        Thread.sleep(1000);
        m.stop();
    }

    private void generateArray() {
        array = new int[10000];
        for (int i = 0; i < array.length; i++) {
            array[i] = (int) (Math.random() * 100000);
        }
    }
}
