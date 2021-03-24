package managerAndWorkers;

/**Some number of workers (each in self-thread) do their own "work" one after the other. Then manager comes and "checks"
 * the work and it repeats several times in cycle.
 * numberOfWorkers is the number of all the workers we created
 * isManager controls if the manager should check the work right now
 * count helps to control current number of the worker
 * The object of class Production is used as a monitor to synchronize all threads
 * @author Sergey Shershavin*/

public class Production {
    static int numberOfWorkers = 0;
    volatile int count;
    int workTimes;
    boolean isManager;

/**Constructor contains:
 * @param workTimes number of repeating work cycles
 * */

    public Production(int workTimes) {
        this.workTimes = workTimes;
        count = 0;
        isManager = false;
    }

    public static void main(String[] args) {
        Production p = new Production(5);
        new Thread(new Manager(p)).start();
        for (int i = 0; i < 10; i++) new Thread(new Worker(p)).start();
    }
}
