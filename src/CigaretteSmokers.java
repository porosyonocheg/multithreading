public class CigaretteSmokers {
    protected final int rounds;

    public CigaretteSmokers(int rounds) {
        this.rounds = rounds;
    }

    public void smoke() throws InterruptedException {
        System.out.println(Thread.currentThread().getName() + " is smoking a cigarette...");
        Thread.sleep(500);
    }

    public static void main(String[] args) {
        CigaretteSmokers cigaretteSmokers = new CigaretteSmokers(10);
        new Agent(cigaretteSmokers).start();
    }
}

class Agent extends Thread {
    private final CigaretteSmokers cigaretteSmokers;
    private boolean isTobaccoOnTheTable;
    private boolean isPaperOnTheTable;
    private boolean isMatchOnTheTable;
    private int count;

    public Agent(CigaretteSmokers cigaretteSmokers) {
        this.cigaretteSmokers = cigaretteSmokers;
        count = cigaretteSmokers.rounds;
        isTobaccoOnTheTable = false;
        isPaperOnTheTable = false;
        isMatchOnTheTable = false;
        new TobaccoDealer().start();
        new PaperDealer().start();
        new MatchDealer().start();
    }


    @Override
    public void run() {
        while (count > 0) {
            int generateRandomNumber = (int)(Math.random() * 3);
            switch(generateRandomNumber) {
                case 0: isTobaccoOnTheTable = true; isPaperOnTheTable = true; System.out.println("The agent puts paper and tobacco on the table..."); break;
                case 1: isMatchOnTheTable = true; isPaperOnTheTable = true; System.out.println("The agent puts match and paper on the table..."); break;
                default: isMatchOnTheTable = true; isTobaccoOnTheTable = true; System.out.println("The agent puts match and tobacco on the table...");
            }
            synchronized(cigaretteSmokers) {
                cigaretteSmokers.notifyAll();
                while ((isTobaccoOnTheTable && isPaperOnTheTable) || (isTobaccoOnTheTable && isMatchOnTheTable) || (isMatchOnTheTable && isPaperOnTheTable) && count > 0) try {
                    count--;
                    cigaretteSmokers.wait();
                } catch (InterruptedException ignored) {}
            }
        }
    }

    class MatchDealer extends Thread {
        public void run() {
        while (count > 0) {
            synchronized (cigaretteSmokers) {
                try {
                    while (!isTobaccoOnTheTable || !isPaperOnTheTable && count > 0) {
                        cigaretteSmokers.wait();
                    }
                    isTobaccoOnTheTable = false;
                    isPaperOnTheTable = false;
                    cigaretteSmokers.smoke();
                } catch (InterruptedException ignored) {}
                cigaretteSmokers.notifyAll();
            }
        }
        }
    }

    class TobaccoDealer extends Thread {
        public void run() {
            while (count > 0) {
                synchronized (cigaretteSmokers) {
                    try {
                        while (!isMatchOnTheTable || !isPaperOnTheTable && count > 0) {
                            cigaretteSmokers.wait();
                        }
                        isMatchOnTheTable = false;
                        isPaperOnTheTable = false;
                        cigaretteSmokers.smoke();
                    } catch (InterruptedException ignored) {
                    }
                    cigaretteSmokers.notifyAll();
                }
            }
        }
    }

    class PaperDealer extends Thread {
        public void run() {
            while (count > 0) {
                synchronized (cigaretteSmokers) {
                    try {
                        while (!isTobaccoOnTheTable || !isMatchOnTheTable && count > 0) {
                            cigaretteSmokers.wait();
                        }
                        isTobaccoOnTheTable = false;
                        isMatchOnTheTable = false;
                        cigaretteSmokers.smoke();
                    } catch (InterruptedException ignored) {
                    }
                    cigaretteSmokers.notifyAll();
                }
            }
        }
    }
}
