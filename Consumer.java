import java.io.PrintWriter;

public class Consumer implements Runnable {

    private Buffer buf;
    private int consumerIndex;
    private String consumerName;
    private PrintWriter writer;

    public Consumer(Buffer buf, int idx, PrintWriter writer) {
        this.buf = buf;
        this.consumerIndex = idx;
        this.consumerName = "Consumer-" + (idx + 1);
        this.writer = writer;
    }

    @Override
    public void run() {
        System.out.println(consumerName + " başladı.");

        boolean active = true;

        while (active) {
            try {
                int item = buf.consume(consumerIndex, consumerName);

                if (item == Buffer.POISON_PILL) {
                    onShutdown();
                    active = false;
                }
                else {
                    logToFile(item);
                    Thread.sleep(10 + (int)(Math.random() * 30));
                }

            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                System.err.println(consumerName + " interrupted!");
                active = false;
            }
        }

        System.out.println("*** " + consumerName + " düzgün şekilde kapandı. ***");
    }

    private void onShutdown() {
        String msg = consumerName + " POISON_PILL aldı. Kapanıyor...";
        System.out.println(msg);

        synchronized (writer) {
            writer.println(msg);
            writer.println(consumerName + " düzgün şekilde kapandı.");
        }
    }

    private void logToFile(int item) {
        synchronized (writer) {
            long ts = System.currentTimeMillis();
            writer.println("[" + consumerName + "][" + ts + "] -> " + item);
        }
    }
}
