import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**One of the classic problems with conditions of limited resource.
 * philosopherCount - is a general number of all the philosophers;
 * forks[] - contains locks for each fork on the table;
 * philosopher[] - contains all the philosophers at the table;
 * semaphore - controls access to the resource (forks[]) to avoid deadlock
 * when all the philosophers pick a left fork at the same moment
 * @author  Sergey Shershavin*/

public class DiningPhilosophers {
    private int meals;
    private static int philosophersCount = 0;
    private final Lock[] forks;
    private final Philosopher[] philosophers;
    private final Semaphore semaphore = new Semaphore(4);

    /**Constructor contains:
     * @param meals number of meals for each philosopher
     * @param numberOfForks number of forks on the table and also number of philosophers at the table*/

    public DiningPhilosophers(int meals, int numberOfForks) {
        this.meals = meals;
        forks = new ReentrantLock[numberOfForks];
        philosophers = new Philosopher[numberOfForks];
        for (int i = 0; i < numberOfForks; i++) {
            forks[i] = new ReentrantLock();
            philosophers[i] = new Philosopher();
        }
    }

    /**To eat each philosopher needs to pick two forks: index of left fork equals an index of philosopher
     * @param philosopher index of current Philosopher*/

    public void wantsToEat(int philosopher,
                           Runnable pickLeftFork,
                           Runnable pickRightFork,
                           Runnable eat,
                           Runnable putLeftFork,
                           Runnable putRightFork) throws InterruptedException {
        int rightFork = (philosopher + 4)%5;
        semaphore.acquire();
        forks[philosopher].lock();
        pickLeftFork.run();
        forks[rightFork].lock();
        pickRightFork.run();
        eat.run();
        putLeftFork.run();
        forks[philosopher].unlock();
        putRightFork.run();
        forks[rightFork].unlock();
        semaphore.release();
    }

    public static void main(String[] args) {
        int numberOfPhilosophers;
        int timesOfEating;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            System.out.println("Input the number of philosophers:");
            numberOfPhilosophers = Integer.parseInt(br.readLine());
            System.out.println("Input the number of meals for each philosopher:");
            timesOfEating = Integer.parseInt(br.readLine());

            DiningPhilosophers diningPhilosophers = new DiningPhilosophers(timesOfEating, numberOfPhilosophers);
            for (Philosopher p : diningPhilosophers.philosophers) {
                p.start();
            }
        }
        catch(IOException ex) {System.out.print("IOException: " + ex.getMessage());}
        catch(NumberFormatException ex) {System.out.print("Your input data.txt is not a number");}
    }
    class Philosopher extends Thread {
        private final int number = philosophersCount;

        public Philosopher() {
            philosophersCount++;
        }

        Runnable pickLeftFork = () -> {
            System.out.println("Philosopher №" + number + " picked a left fork...");
        };
        Runnable pickRightFork = () -> {
            System.out.println("Philosopher №" + number + " picked a right fork...");
        };
        Runnable eat = () -> {
            System.out.println("Philosopher №" + number + " is eating...");
        };
        Runnable putLeftFork = () -> {
            System.out.println("Philosopher №" + number + " put a left fork.");
        };
        Runnable putRightFork = () -> {
            System.out.println("Philosopher №" + number + " put a right fork.");
        };

        @Override
        public void run() {
            while (meals > 0) {
                System.out.println("Philosopher №" + number + " is thinking...");
                try {
                    wantsToEat(number, pickLeftFork, pickRightFork, eat, putLeftFork, putRightFork);
                } catch (InterruptedException ex) {}
                meals--;
            }
        }
    }
}
