import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Main {

    static final int NUM_PRODUCERS = 3;
    static final int NUM_CONSUMERS = 3;

    public static void main(String[] args) {

        printHeader();

        Buffer buffer = new Buffer(NUM_PRODUCERS, NUM_CONSUMERS);

        PrintWriter pw = null;

        try {
            pw = new PrintWriter(new FileWriter("output.txt"));
            pw.println("=== TÜKETIM KAYITLARI ===");
            pw.println("Başlangıç: " + System.currentTimeMillis());
            pw.println("----------------------------------------");

            Thread[] prodThreads = createProducers(buffer);
            Thread[] consThreads = createConsumers(buffer, pw);

            startAll(prodThreads);
            startAll(consThreads);

            joinAll(prodThreads);
            joinAll(consThreads);

            pw.println("----------------------------------------");
            pw.println("Bitiş: " + System.currentTimeMillis());
            pw.println("=== PROGRAM TAMAMLANDI ===");

            buffer.printReport();

            System.out.println("\nTüm veriler 'output.txt' dosyasına yazıldı.");
            System.out.println("Program başarıyla tamamlandı!");

        } catch (IOException ex) {
            System.err.println("Dosya hatası: " + ex.getMessage());
        } catch (InterruptedException ex) {
            System.err.println("Thread hatası: " + ex.getMessage());
        } finally {
            if (pw != null) {
                pw.close();
            }
        }
    }

    private static void printHeader() {
        System.out.println("============================================================");
        System.out.println("     MONITOR PROJESİ - PRODUCER/CONSUMER");
        System.out.println("============================================================");
        System.out.println("HIGH_BUFFER Kapasitesi: 60");
        System.out.println("LOW_BUFFER Kapasitesi : 40");
        System.out.println("Üretici Sayısı        : " + NUM_PRODUCERS);
        System.out.println("Tüketici Sayısı       : " + NUM_CONSUMERS);
        System.out.println("Her üretici üretimi   : 50 sayı");
        System.out.println("Toplam üretilecek     : " + (NUM_PRODUCERS * 50) + " sayı");
        System.out.println("============================================================\n");
    }

    private static Thread[] createProducers(Buffer buf) {
        Thread[] arr = new Thread[NUM_PRODUCERS];
        int i = 0;
        while (i < NUM_PRODUCERS) {
            Producer p = new Producer(buf, i + 1, NUM_CONSUMERS);
            arr[i] = new Thread(p);
            i++;
        }
        return arr;
    }

    private static Thread[] createConsumers(Buffer buf, PrintWriter pw) {
        Thread[] arr = new Thread[NUM_CONSUMERS];
        for (int i = 0; i < NUM_CONSUMERS; i++) {
            Consumer c = new Consumer(buf, i, pw);
            arr[i] = new Thread(c);
        }
        return arr;
    }

    private static void startAll(Thread[] threads) {
        for (Thread t : threads) {
            t.start();
        }
    }

    private static void joinAll(Thread[] threads) throws InterruptedException {
        for (Thread t : threads) {
            t.join();
        }
    }
}
