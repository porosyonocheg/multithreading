import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.IntConsumer;

/**Each thread consistently prints its own number. Zero prints "0" in front of every other number, even prints
 * even numbers, odd - odd numbers from 1 to the number entered by the user.
 * @author Sergey Shershavin*/

public class ZeroEvenOdd {

    public static void main(String[] args) throws IOException {
        IntConsumer printNumber = System.out::print;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Input your number:");
        ZeroEvenOdd zeroEvenOdd = new ZeroEvenOdd(Integer.parseInt(br.readLine()));
        Thread zero = new Thread(() -> {
            try {
                zeroEvenOdd.zero(printNumber);
            } catch (InterruptedException ex) {
            }
        });
        Thread even = new Thread(() -> {
            try {
                zeroEvenOdd.even(printNumber);
            } catch (InterruptedException ex) {
            }
        });
        Thread odd = new Thread(() -> {
            try {
                zeroEvenOdd.odd(printNumber);
            } catch (InterruptedException ex) {
            }
        });
        even.start();
        odd.start();
        zero.start();

    }

    /**count - is a current number, that needs to print
     * isNull - shows "false" if a previous number was "0" and "true", if you have to print "0" right now
     * condition - helps to synchronized all threads by logical conditions*/

    private final int n;
    private final AtomicInteger count = new AtomicInteger(1);
    private volatile boolean isNull;
    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();

    /**Constructor contains
    * @param n the number, that the user enters*/

    public ZeroEvenOdd(int n) {
        this.n = n;
        isNull = true;
    }

    public void zero(IntConsumer printNumber) throws InterruptedException {
        lock.lock();
        try {
            while (count.get() <= n) {
                if (!isNull) condition.await();
                else {
                    printNumber.accept(0);
                    isNull = false;
                    condition.signalAll();
                }
            }
        }
        finally{lock.unlock();}
    }

    public void even(IntConsumer printNumber) throws InterruptedException {
        lock.lock();
        try {
            while (count.get() <= n) {
                if (isNull || count.get() % 2 != 0) {
                    condition.await();
                } else {
                    printNumber.accept(count.get());
                    count.getAndIncrement();
                    isNull = true;
                    condition.signalAll();
                }
            }
        }
        finally{lock.unlock();}
    }

    public void odd(IntConsumer printNumber) throws InterruptedException {
        lock.lock();
        try {
            while (count.get() <= n) {
                if (isNull || count.get() % 2 == 0) {
                    condition.await();
                } else {
                    printNumber.accept(count.get());
                    count.getAndIncrement();
                    isNull = true;
                    condition.signalAll();
                }
            }
        }
        finally{lock.unlock();}
    }
}
