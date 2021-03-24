package managerAndWorkers;

public class Worker implements Runnable {
    private final int number;
    private final Production production;

    public Worker(Production production) {
        number = ++Production.numberOfWorkers;
        this.production = production;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < production.workTimes; i++) {
                synchronized (production) {
                    while (production.count % Production.numberOfWorkers + 1 != number || production.isManager)
                        production.wait();
                    System.out.println("Worker№" + number + " is working from " + Thread.currentThread().getName());
                    production.count++;
                    if (production.count % Production.numberOfWorkers == 0) production.isManager = true;
                    production.notifyAll();
                }
            }
        }
        catch (InterruptedException ex) { ex.printStackTrace(); }
    }
}
