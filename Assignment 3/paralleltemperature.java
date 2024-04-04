import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.stream.*;

public class paralleltemperature {
    // Variables used to store data and constants based on assignment
    private static final int SENSOR_QUANTITY = 8;
    private static final int TIME_SLICES = 60;
    private static final int DURATION_HOURS = 72;
    private List<Integer> climateData = Collections.synchronizedList(new ArrayList<>(Collections.nCopies(SENSOR_QUANTITY * TIME_SLICES, 0)));
    private AtomicBoolean[] operationalSensors = new AtomicBoolean[SENSOR_QUANTITY];

    // set all sensors operational
    public paralleltemperature() {
        Arrays.setAll(operationalSensors, i -> new AtomicBoolean(true));
    }

    // check if all sensors are active excluding the excludingSensor
    private boolean areSensorsOperational(int excludingSensor) {
        return IntStream.range(0, SENSOR_QUANTITY)
                        .filter(i -> i != excludingSensor)
                        .allMatch(i -> operationalSensors[i].get());
    }

    // Add all temperatures recorded to the list based on current thread running.
    private void recordTemperatureData(int sensorID) {
        IntStream.range(0, DURATION_HOURS).forEach(hour -> {
            IntStream.range(0, TIME_SLICES).forEach(minute -> {
                // sensor is false while adding the data
                operationalSensors[sensorID].set(false);
                int readingIndex = sensorID * TIME_SLICES + minute;
                climateData.set(readingIndex, generateRandomNumber(-100, 70));
                operationalSensors[sensorID].set(true);

                while (!areSensorsOperational(sensorID)) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
            synchronized (this) {
                if (sensorID == 0) {
                    generateHourlyReport(hour);
                }
            }
        });
    }

    private void generateHourlyReport(int hour) {
        System.out.println("[Hour " + (hour + 1) + " Report]");
        printLargestDifference();
        printTemperatureExtremes(true); // For highest temperatures
        printTemperatureExtremes(false); // For lowest temperatures
        System.out.println();
    }

    // find larget difference per hour and print them.
    private void printLargestDifference() {
        int maxDifference = Integer.MIN_VALUE;
        int startMinute = 0;
        int interval = 10;

        for (int i = 0; i <= TIME_SLICES - interval; i++) {
            List<Integer> sublist = new ArrayList<>(climateData.subList(i, i + interval));
            int max = Collections.max(sublist);
            int min = Collections.min(sublist);
            int diff = max - min;

            if (diff > maxDifference) {
                maxDifference = diff;
                startMinute = i;
            }
        }

        System.out.println("Largest temperature difference: " + maxDifference + "F"
                + " starting at minute " + startMinute
                + " and ending at minute " + (startMinute + interval));
    }

    // Print highest and lowest temperatures.
    private void printTemperatureExtremes(boolean highest) {
        List<Integer> sortedTemperatures = new ArrayList<>(climateData);
        Collections.sort(sortedTemperatures);
        if (!highest) {
            Collections.reverse(sortedTemperatures);
        }

        List<Integer> extremes = sortedTemperatures.stream().distinct().limit(5).collect(Collectors.toList());

        System.out.print(highest ? "Highest temperatures: " : "Lowest temperatures: ");
        extremes.forEach(temp -> System.out.print(temp + "F "));
        System.out.println();
    }

    public void start() {
        Thread[] sensorThreads = new Thread[SENSOR_QUANTITY];
        IntStream.range(0, SENSOR_QUANTITY).forEach(i -> {
            sensorThreads[i] = new Thread(() -> recordTemperatureData(i));
            sensorThreads[i].start();
        });

        Arrays.stream(sensorThreads).forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    public static int generateRandomNumber(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    public static void main(String[] args) {
        new paralleltemperature().start();
    }
}

