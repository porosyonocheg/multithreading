/** A cigarette agent, a paper supplier, a tobacco supplier, and a match supplier are seated at the table.
 * The agent places two random items on the table for making a cigarette, and the one who has the missing component
 * takes them, makes and smokes a cigarette. The process continues for the specified number of rounds.
 * Synchronization is done using the object of CigaretteSmokers.class as a monitor and boolean flags, those show which
 * items are on the table at the moment
 * @author Sergey Shershavin*/

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
        CigaretteSmokers cigaretteSmokers = new CigaretteSmokers(3);
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
        new Thread(new TobaccoDealer(),"Tobacco dealer").start();
        new Thread(new PaperDealer(),"Paper dealer").start();
        new Thread(new MatchDealer(),"Match dealer").start();
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
                try {
                while ((isTobaccoOnTheTable && isPaperOnTheTable) || (isTobaccoOnTheTable && isMatchOnTheTable) || (isMatchOnTheTable && isPaperOnTheTable) && count > 0) {
                    count--;
                    cigaretteSmokers.wait();
                }
                } catch (InterruptedException ignored) {}
            }
        }
    }

    class MatchDealer implements Runnable {
        public void run() {
        while (count > 0) {
            synchronized (cigaretteSmokers) {
                try {
                    while ((!isTobaccoOnTheTable || !isPaperOnTheTable) && count > 0) {
                        cigaretteSmokers.wait();
                    }
                    if (isTobaccoOnTheTable && isPaperOnTheTable) {
                    isTobaccoOnTheTable = false;
                    isPaperOnTheTable = false;
                    cigaretteSmokers.smoke();
                    }
                } catch (InterruptedException ignored) {}
                cigaretteSmokers.notifyAll();
            }
        }
        }
    }

    class TobaccoDealer implements Runnable {
        public void run() {
            while (count > 0) {
                synchronized (cigaretteSmokers) {
                    try {
                        while ((!isMatchOnTheTable || !isPaperOnTheTable) && count > 0) {
                            cigaretteSmokers.wait();
                        }
                        if (isPaperOnTheTable && isMatchOnTheTable) {
                        isMatchOnTheTable = false;
                        isPaperOnTheTable = false;
                        cigaretteSmokers.smoke();
                        }
                    } catch (InterruptedException ignored) {
                    }
                    cigaretteSmokers.notifyAll();
                }
            }
        }
    }

    class PaperDealer implements Runnable {
        public void run() {
            while (count > 0) {
                synchronized (cigaretteSmokers) {
                    try {
                        while ((!isTobaccoOnTheTable || !isMatchOnTheTable) && count > 0) {
                            cigaretteSmokers.wait();
                        }
                        if (isTobaccoOnTheTable && isMatchOnTheTable) {
                        isTobaccoOnTheTable = false;
                        isMatchOnTheTable = false;
                        cigaretteSmokers.smoke();
                        }
                    } catch (InterruptedException ignored) {
                    }
                    cigaretteSmokers.notifyAll();
                }
            }
        }
    }
}
