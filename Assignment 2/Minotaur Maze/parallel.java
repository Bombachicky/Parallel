import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;


public class parallel {

    // Global integer to keep track of the number of guests.
    static int numGuests = 10;

    static class minotaurmaze implements Runnable{
        // The cupcakeFound variable is used to keep track of when a cupcake is eaten
        // The cupcakesEaten variable is used to keep track of how many cupcakes have been eaten so far.
        // Guest ID is used to keep track of what thread is currently attempting to access the run function
        // The lock is to promote mutual exclusion so that multiple guests cannot view the vase at the same time.
        // The Random variable simulates the guest observing the vase for a random amount of time.
        static boolean cupcakeFound = true;
        static int cupcakesEaten = 0;
        int guestID;
        static boolean[] eatenCupcakes = new boolean[numGuests];
        private static Lock enteredMaze = new ReentrantLock();
        private Random random = new Random();
    
        // Constructor
        minotaurmaze(int guestID){
            this.guestID = guestID;
        }
    
        @Override
        public void run() {
            // We keep running until N guests have eaten the cupcakes
            while (cupcakesEaten < numGuests) {
                // Lock the maze once the guest has entered.
                enteredMaze.lock();
                System.out.println("Guest " + guestID + " has entered the maze");
                try {
                    // If the current guest hasn't eaten a cupcake and a cupcake is there, then the guest eats the cupcake
                    if (!eatenCupcakes[guestID] && cupcakeFound){
                        cupcakeFound = false;
                        eatenCupcakes[guestID] = true;
                        System.out.println("Guest " + guestID + " Found the Cupcake: " + cupcakesEaten);
                    }
                    // If the guest has the ID of zero and there's no cupcake, then guest 0 adds 1 to the variable keeping track of the number of
                    // cupcakes eaten and requests a cupcake to be placed (but does not eat it himself).
                    if (guestID == 0 && !cupcakeFound){
                        cupcakesEaten += 1;
                        cupcakeFound = true;
                        System.out.println("Guest 0 Found the Cupcake: " + cupcakesEaten);
                    }
                } finally {
                    // Unlock the maze once a guest has left.
                    enteredMaze.unlock();
                }
                
                // Sleep the thread to promote randomness among which guests enter the maze.
                try {
                    Thread.sleep(random.nextInt(1000));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
        
    public static void main(String[] args) {
        // Create an executor service and activate all threads
        ExecutorService executor = Executors.newFixedThreadPool(numGuests);

        for (int i = 0; i < numGuests; i++){
            executor.execute(new minotaurmaze(i));
        }

        executor.shutdown();

    }
}