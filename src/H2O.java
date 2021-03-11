import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicInteger;

/** There are two threads: H and O, those are the models of an atom hydrogen and oxygen respectively. Each thread
 * prints its element, the problem is to synchronize this process to get a correct molecule of water:
 * two atoms of hydrogen and then one atom of oxygen. And it needs to continue further in the same order while we get
 * the number of molecules, that entered by the user.
 * @author Sergey Shershavin*/

public class H2O {

    private final AtomicInteger threadNumber = new AtomicInteger(1);

    public H2O() {
    }

    public synchronized void hydrogen(Runnable releaseHydrogen) throws InterruptedException {

        while (this.threadNumber.get()%3 == 0) {
            wait();
        }
        releaseHydrogen.run();
        threadNumber.getAndIncrement();
        notifyAll();
    }

    public synchronized void oxygen(Runnable releaseOxygen) throws InterruptedException {

        while (this.threadNumber.get()%3 != 0) {
            wait();
        }
        releaseOxygen.run();
        threadNumber.getAndIncrement();
        notifyAll();
    }

    public static void main(String[] args) throws IOException {
        H2O h2O = new H2O();
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Input the number of water molecules you need:");
        int molecules = Integer.parseInt(br.readLine());
        for (int i = 0; i < molecules<<1; i++)
        h2O.new H().start();
        for (int i = 0; i < molecules; i++)
        h2O.new O().start();
    }

    class H extends Thread {
        @Override
        public void run() {
            try {
                hydrogen(() -> {
                    System.out.print("H");
                });
            } catch (InterruptedException ex) {
            }
        }
    }

    class O extends Thread {
        @Override
        public void run() {
            try {
                oxygen(() -> {
                    System.out.print("O");
                });
            } catch (InterruptedException ex) {
            }
        }
    }

}


