import java.util.Random;

public class Producer implements Runnable {

    private Buffer buf;
    private String producerName;
    private int numConsumers;
    private Random rng;

    private final int TOTAL_ITEMS = 50;

    public Producer(Buffer buf, int prodId, int numConsumers) {
        this.buf = buf;
        this.producerName = "Producer-" + prodId;
        this.numConsumers = numConsumers;
        this.rng = new Random();
    }

    @Override
    public void run() {
        System.out.println(producerName + " başladı.");

        int produced = 0;

        try {
            while (produced < TOTAL_ITEMS) {
                int randomNum = 1 + rng.nextInt(100);

                buf.produce(randomNum, producerName);

                int sleepMs = 10 + rng.nextInt(41);
                Thread.sleep(sleepMs);

                produced++;
            }

            System.out.println(producerName + " üretimi tamamladı. (" + TOTAL_ITEMS + " sayı üretildi)");

            buf.signalEnd();

        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            System.err.println(producerName + " interrupted!");
        }
    }
}

