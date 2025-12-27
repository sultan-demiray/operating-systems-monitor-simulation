import java.util.ArrayDeque;
import java.util.Deque;

public class Buffer {

    private final int CAPACITY_HIGH = 60;
    private final int CAPACITY_LOW = 40;

    private Deque<Integer> queueHigh;
    private Deque<Integer> queueLow;

    public static final int POISON_PILL = -1;

    private int producersRunning;
    private int consumersTotal;

    private int countProduced;
    private int countConsumed;
    private int peakHigh;
    private int peakLow;
    private int[] statsConsumer;

    public Buffer(int numProducers, int numConsumers) {
        queueHigh = new ArrayDeque<>();
        queueLow = new ArrayDeque<>();
        producersRunning = numProducers;
        consumersTotal = numConsumers;
        countProduced = 0;
        countConsumed = 0;
        peakHigh = 0;
        peakLow = 0;
        statsConsumer = new int[numConsumers];
    }

    public synchronized void produce(int num, String name) throws InterruptedException {

        boolean isHighPriority = num <= 50;

        if (isHighPriority) {
            while (queueHigh.size() >= CAPACITY_HIGH) {
                System.out.println(name + " bekliyor... HIGH_BUFFER dolu");
                wait();
            }

            queueHigh.addLast(num);
            countProduced++;
            peakHigh = Math.max(peakHigh, queueHigh.size());

            System.out.println(name + " [HIGH_BUFFER] -> " + num + " (Size: " + queueHigh.size() + ")");
        }
        else {
            while (queueLow.size() >= CAPACITY_LOW) {
                System.out.println(name + " bekliyor... LOW_BUFFER dolu");
                wait();
            }

            queueLow.addLast(num);
            countProduced++;
            peakLow = Math.max(peakLow, queueLow.size());

            System.out.println(name + " [LOW_BUFFER] -> " + num + " (Size: " + queueLow.size() + ")");
        }

        notifyAll();
    }

    public synchronized void signalEnd() {
        producersRunning = producersRunning - 1;

        if (producersRunning == 0) {
            int i = 0;
            while (i < consumersTotal) {
                queueHigh.addLast(POISON_PILL);
                i = i + 1;
            }
            System.out.println("=== TÜM ÜRETİCİLER TAMAMLANDI - POISON PILL EKLENDİ ===");
        }

        notifyAll();
    }

    public synchronized int consume(int idx, String name) throws InterruptedException {

        while (queueHigh.isEmpty() && queueLow.isEmpty()) {
            if (producersRunning == 0) {
                return POISON_PILL;
            }
            System.out.println(name + " bekliyor... Buffer'lar boş");
            wait();
        }

        int result;
        String source;
        long time = System.currentTimeMillis();

        if (!queueHigh.isEmpty()) {
            result = queueHigh.removeFirst();
            source = "HIGH";
        }
        else {
            result = queueLow.removeFirst();
            source = "LOW";
        }

        if (result == POISON_PILL) {
            return POISON_PILL;
        }

        countConsumed = countConsumed + 1;
        statsConsumer[idx] = statsConsumer[idx] + 1;

        System.out.println("[" + name + "][" + source + "][" + time + "] -> " + result);

        notifyAll();
        return result;
    }

    public synchronized void printReport() {
        StringBuilder output = new StringBuilder();

        output.append("\n");
        output.append(repeatChar('=', 60));
        output.append("\n");
        output.append("                    PROGRAM RAPORU\n");
        output.append(repeatChar('=', 60));
        output.append("\n");
        output.append("Toplam üretilen sayı      : " + countProduced + "\n");
        output.append("Toplam tüketilen sayı     : " + countConsumed + "\n");
        output.append("HIGH_BUFFER max doluluk   : " + peakHigh + "/" + CAPACITY_HIGH + "\n");
        output.append("LOW_BUFFER max doluluk    : " + peakLow + "/" + CAPACITY_LOW + "\n");
        output.append(repeatChar('-', 60));
        output.append("\n");

        int index = 0;
        while (index < statsConsumer.length) {
            output.append("Consumer-" + (index + 1) + " tükettiği eleman: " + statsConsumer[index] + "\n");
            index++;
        }

        output.append(repeatChar('=', 60));

        System.out.println(output.toString());
    }

    private String repeatChar(char c, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(c);
        }
        return sb.toString();
    }
}
