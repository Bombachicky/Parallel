import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.IntStream;

public class Parallel {
    public static void main(String[] args) throws Exception {
        // Declaring constant of 100 million
        final int MAX = 100000000;
        // Setting an array to size max and using true or false values to determine if a number's prime or not.
        boolean[] isPrime = new boolean[MAX];
        Arrays.fill(isPrime, true);
        // 0 and 1 aren't prime so we set them as false.
        isPrime[0] = false;
        isPrime[1] = false;
        int sqrtMax = (int) Math.sqrt(MAX);
        long startTime = System.currentTimeMillis();
        ExecutorService executor = Executors.newFixedThreadPool(8);


        // Sieve of Eratosthenes in Parallel
        for (int i = 2; i <= sqrtMax; i++) {
            int finalI = i;
            executor.execute(() -> {
                if (isPrime[finalI]) {
                    for (int j = finalI * finalI; j < MAX; j += finalI) {
                        isPrime[j] = false;
                    }
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.HOURS);

        long endTime = System.currentTimeMillis();

        // Collect primes after sieve and calculate the sum
        List<Integer> primes = new ArrayList<>();
        long sumPrimes = 0;
        for (int i = 2; i < MAX; i++) {
            if (isPrime[i]) {
                primes.add(i);
                sumPrimes += i;
            }
        }

        // Get top ten Primes
        primes.sort(Collections.reverseOrder());
        List<Integer> topTenPrimes = primes.subList(0, Math.min(10, primes.size()));

        // Write information to file
        try (FileWriter writer = new FileWriter("primes.txt")) {
            writer.write("Number of Primes: " + primes.size() + "\n");
            writer.write("Sum of All Prime Numbers: " + sumPrimes + "\n");
            writer.write("Top Ten Max Prime Numbers: " + topTenPrimes + "\n");
            writer.write("Execution Time: " + (endTime - startTime) + " ms\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
