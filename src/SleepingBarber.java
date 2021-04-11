/**@author Sergey Shershavin*/

public class SleepingBarber {
    private int clients;
    private final Client[] clientsQueue;
    private final Barber barber;
    private Client currentClient;

    public SleepingBarber(int sizeOfQueue) {
        clients = 0;
        clientsQueue = new Client[sizeOfQueue];
        barber = new Barber(this);
        currentClient = null;
    }

    public void setClient(Client client) {
        currentClient = client;
    }

    public Client getCurrentClient() {
        return currentClient;
    }

    public static void main(String[] args) throws InterruptedException {
        SleepingBarber sb = new SleepingBarber(3);
        sb.barber.start();
        while (System.currentTimeMillis() % 5 != 0) {
            sb.new Client(sb).start();
            int time = (int)(Math.random()*51);
            Thread.sleep(time);
        }
        sb.barber.isWorkTime = false;
    }
    private class Client implements Runnable {
        private final Thread self;
        private final SleepingBarber sb;
        public Client(SleepingBarber sb) {
            int id = ++clients;
            self = new Thread(this, "Client№" + id);
            this.sb = sb;
        }

        public void start() {
            self.start();
        }

        @Override
        public void run() {
            synchronized(sb) {
                if (sb.barber.isSleeping) {
                    setClient(this);
                    sb.notifyAll();
                    try{sb.wait();}catch(InterruptedException ignored) {}
                }
                else {
                    try {
                        if (!addToTheQueue()) {
                            System.out.println(Thread.currentThread().getName() + " leaves...");
                            return;
                        }
                        else sb.wait();
                    }
                    catch(InterruptedException ignored) {}
                }
            }
            System.out.println(Thread.currentThread().getName() + " leaves...");
        }

        public boolean addToTheQueue() throws InterruptedException {
            int time = (int)(Math.random()*101);
            System.out.println(Thread.currentThread().getName() + " is going to the reception...");
            Thread.sleep(time);
            for (int i = 0; i < clientsQueue.length; i++) {
                if (clientsQueue[i] == null) {
                    clientsQueue[i] = this;
                    return true;
                }
            }
            return false;
        }
    }

    private class Barber implements Runnable {
        private final SleepingBarber sb;
        private final Thread self;
        private boolean isWorkTime, isSleeping;
        private int i = 0;

        public Barber(SleepingBarber sb) {
            self = new Thread(this, this.getClass().getSimpleName());
            isWorkTime = true;
            isSleeping = false;
            this.sb = sb;
        }

        public void start() {
            self.start();
        }

        public void cutHair(Client client) throws InterruptedException {
            int time = (int)(Math.random()*1001);
            System.out.println(Thread.currentThread().getName() + " makes a haircut for " + client.self.getName());
            Thread.sleep(time);
            System.out.println("Haircut completed!");
        }

        private Client getClient() throws InterruptedException {
            int time = (int)(Math.random()*101);
            System.out.println(Thread.currentThread().getName() + "  is going to the reception...");
            Thread.sleep(time);
            for (; i < sb.clientsQueue.length; i++) {
                if (sb.clientsQueue[i%sb.clientsQueue.length] != null) {
                    sb.setClient(sb.clientsQueue[i%sb.clientsQueue.length]);
                    sb.clientsQueue[i%sb.clientsQueue.length] = null;
                    return sb.getCurrentClient();
                }
            }
            return null;
        }

        @Override
        public void run() {
            try {
                synchronized(sb) {
                    while (isWorkTime) {
                        isSleeping = false;
                        Client client = sb.getCurrentClient();
                        if (client == null) client = getClient();
                        if (client == null) {
                            System.out.println("Barber is sleeping...");
                            isSleeping = true;
                            sb.notifyAll();
                            sb.wait();
                        }
                        else {
                            cutHair(client);
                            sb.setClient(null);
                        }
                    }
                }
            }
            catch (InterruptedException ignored) {}
        }

    }

}
