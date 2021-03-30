import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/** Here is the problem of using a common resource. Five threads want to use field sb, four of them try read it,
 * but one thread every even millis write in it currentTimeMillis without first 9 digits. All the threads work
 * until millis are multiples of five. So we get a cycle of random duration. Synchronization is provided by
 * writeLock (it blocks the resource for all other threads) and readLock (it blocks the resource for writing).
 * @author Sergey Shershavin*/

public class ReaderWriter {
    private final StringBuilder sb = new StringBuilder();
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock writeLock = readWriteLock.writeLock();
    private final Lock readLock = readWriteLock.readLock();

    public void write() {
        sb.append(System.currentTimeMillis()%1000000000).append(" written by ").append(Thread.currentThread().getName()).append("\n");
    }

    public void read() {
        System.out.println(Thread.currentThread().getName() + " is reading: " + sb.toString());
    }

    private final Runnable writing = () -> {
        while (System.currentTimeMillis() % 5 != 0) {
            try {

                writeLock.lock();
                if (System.currentTimeMillis() %2 == 0) write();
                Thread.sleep(50); // hey, buddy, not so fast!!!
            }
            catch (InterruptedException ignored){}
            finally{writeLock.unlock();}
        }
    };

    private final Runnable reading = () -> {
        while (System.currentTimeMillis() % 5 != 0) {
            try {

                readLock.lock();
                read();

            }
            finally{readLock.unlock();}
        }
    };

    public static void main(String[] args) {
        ReaderWriter readerWriter = new ReaderWriter();
        new Thread(readerWriter.writing).start();
        new Thread(readerWriter.reading).start();
        new Thread(readerWriter.reading).start();
        new Thread(readerWriter.reading).start();
        new Thread(readerWriter.reading).start();
    }
}
