import java.io.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/** ReaderFromFile.class reading data from text file is a producer. DataConverter.class modifies this data
 * by alternating uppercase and lowercase letters and print the updated data to the console.
 * @author Sergey Shershavin*/

public class ProducerConsumer {
    public static void main(String[] args) throws FileNotFoundException {
        QueueOfStrings qos = new QueueOfStrings(5);
        new Thread(new ReaderFromFile(qos, new File("./src/data.txt"))).start();
        new Thread(new DataConverter(qos, 24)).start();
    }
}

class QueueOfStrings {
    public final BlockingQueue<String> queue;

    public QueueOfStrings(int n) {
        queue = new ArrayBlockingQueue<>(n, true);
    }
}

class DataConverter implements Runnable {
    private final QueueOfStrings qos;
    private int count;

    /**Constructor contains:
     * @param qos read data store
     * @param count number of strings we have to change and print*/
    public DataConverter(QueueOfStrings qos, int count) {
        this.qos = qos;
        this.count = count;
    }


    @Override
    public void run() {
        try {
            while (count > 0) {
                String current = qos.queue.take();
                char[] chars = current.toCharArray();
                for (int i = 0; i < chars.length; i+=2) {
                    chars[i] = Character.toUpperCase(chars[i]);
                }
                Thread.sleep(500); // as some hard math work
                for (int i = 1; i < chars.length; i+=2) {
                    chars[i] = Character.toLowerCase(chars[i]);
                }
                System.out.println(String.valueOf(chars));
                count--;
            }
        } catch (InterruptedException ex) {System.out.println("InterruptedException in " + Thread.currentThread().getName());}
    }

}

class ReaderFromFile implements Runnable {
    private final QueueOfStrings qos;
    private final File file;

    /**Constructor contains:
     * @param qos read data store
     * @param file target file*/
    public ReaderFromFile(QueueOfStrings qos, File file) throws FileNotFoundException {
        this.qos = qos;
        this.file = file;
    }

    @Override
    public void run() {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String current = br.readLine();
            while (current != null) {
                Thread.sleep(1000); // as some hard math work
                qos.queue.offer(current);
                current = br.readLine();
            }
        } catch (IOException ex) {
            System.out.println("IOException in " + Thread.currentThread().getName());
        } catch (InterruptedException ex) {
            System.out.println("InterruptedException in " + Thread.currentThread().getName());
        }
    }
}