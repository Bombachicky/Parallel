import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.*;

public class paralleltemperature {
    // Constants based on assignments and concurrent list to represent data.
    private static final int SENSOR_QUANTITY = 8;
    private static final int TIME_SLICES = 60;
    private static final int DURATION_HOURS = 72;
    private ExecutorService climateSensors = Executors.newFixedThreadPool(SENSOR_QUANTITY);
    private List<Integer> climateData = Collections.synchronizedList(new ArrayList<>(Collections.nCopies(SENSOR_QUANTITY * TIME_SLICES, 0)));
    private AtomicBoolean[] operationalSensors = new AtomicBoolean[SENSOR_QUANTITY];

    public paralleltemperature() {
        for (int i = 0; i < SENSOR_QUANTITY; i++) {
            operationalSensors[i] = new AtomicBoolean(true);
        }
    }

    // If a sensor is not operational and is not the snsor we are searching for, we return false
    private boolean checkSensorStatus(int sensorID) {
        for (int i = 0; i < operationalSensors.length; i++) {
            if (!operationalSensors[i].get() && i != sensorID) {
                return false;
            }
        }
        return true;
    }

    // Reads in all the data at a specific time point based on the thread.
    private void captureReadings(int sensorID) {
        for (int hr = 0; hr < DURATION_HOURS; hr++) {
            for (int min = 0; min < TIME_SLICES; min++) {
                operationalSensors[sensorID].set(false);
                climateData.set(min + (sensorID * TIME_SLICES), ThreadLocalRandom.current().nextInt(-100, 71));
                operationalSensors[sensorID].set(true);

                while (!checkSensorStatus(sensorID)) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }

            if (sensorID == 0) {
                synchronized (this) {
                    compileReport(hr);
                }
            }
        }
    }

    // prints out hourly report
    private void compileReport(int hourMark) {
        System.out.println("[Report for Hour " + (hourMark + 1) + "]");
        findTemperatureFluctuations();
        showTemperatureExtremes(true);  // For highest temperatures
        showTemperatureExtremes(false); // For lowest temperatures
    }

    // Finds difference between highest and lowest temperature and times.
    private void findTemperatureFluctuations() {
        int greatestFluctuation = Integer.MIN_VALUE, startMinute = 0;
        int interval = ThreadLocalRandom.current().nextInt(5, 16);

        synchronized (climateData) {
            for (int i = 0; i < climateData.size() - interval; i++) {
                int high = Collections.max(climateData.subList(i, i + interval));
                int low = Collections.min(climateData.subList(i, i + interval));
                int difference = high - low;

                if (difference > greatestFluctuation) {
                    greatestFluctuation = difference;
                    startMinute = i;
                }
            }
        }

        System.out.println("Max Temp Change: " + greatestFluctuation + "F from minute " + startMinute + " to " + (startMinute + interval));
    }

    // Finds highest and lowest temperatures in list
    private void showTemperatureExtremes(boolean isMax) {
        List<Integer> sortedData;
        synchronized (climateData) {
            sortedData = new ArrayList<>(climateData);
        }
        sortedData.sort(null);

        List<Integer> extremes = isMax ? sortedData.subList(sortedData.size() - 5, sortedData.size()) : sortedData.subList(0, 5);

        System.out.print(isMax ? "Peak Temperatures: " : "Minimum Temperatures: ");
        extremes.forEach(temp -> System.out.print(temp + "F "));
        System.out.println();
    }

    // starts process of assignment
    public void initiate() {
        IntStream.range(0, SENSOR_QUANTITY).forEach(i ->
            climateSensors.submit(() -> captureReadings(i))
        );

        climateSensors.shutdown();
        try {
            if (!climateSensors.awaitTermination(DURATION_HOURS, TimeUnit.HOURS)) {
                climateSensors.shutdownNow();
            }
        } catch (InterruptedException e) {
            climateSensors.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) {
        new paralleltemperature().initiate();
    }
}
