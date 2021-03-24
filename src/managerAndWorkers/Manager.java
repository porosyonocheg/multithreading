package managerAndWorkers;

public class Manager implements Runnable {
    private final Production production;

    public Manager(Production production) {
        this.production = production;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < production.workTimes; i++) {
                synchronized (production) {
                    while(!production.isManager) production.wait();
                    System.out.println(getClass().getSimpleName() + " checks...");
                    production.isManager = false;
                    production.notifyAll();
                }
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}

