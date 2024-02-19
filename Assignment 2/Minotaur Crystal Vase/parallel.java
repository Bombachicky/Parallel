// Implementation of second option within assignment 2.

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

public class parallel {

    // Global integer to keep track of the number of guests.
    static int numGuests = 10;

    static class minotaurvase implements Runnable {
        // Available is used to keep track of room availability
        // HashSet of guests is used to keep track of who's entered the room so they don't enter twice.
        // Guest ID is used to keep track of what thread is currently attempting to access the run function
        // The lock is to promote mutual exclusion so that multiple guests cannot view the vase at the same time.
        // The Random variable simulates the guest observing the vase for a random amount of time.
        static boolean available = true;
        static HashSet<Integer> guests = new HashSet<>();
        int guestID;
        private static Lock enteredRoom = new ReentrantLock();
        private Random random = new Random();
    
        // Constructor
        minotaurvase(int guestID){
            this.guestID = guestID;
        }
    
        @Override
        public void run() {
            // Thread sleep to promote randomness among guests attempting to observe the vase
            try {
                Thread.sleep(random.nextInt(1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } 
            // We keep running until N guests have viewed the vase
            while (guests.size() < numGuests) {
                // Lock the room once a guest enters.
                enteredRoom.lock();
                System.out.println("Guest " + guestID + " is near the vase room");
                try {
                    // If the guest hasnt viewed the vase before and the room is available, the guests observes the vase for a certain amount of time until they're done and don't reenter the room.
                    // They are added to the hashset so they do not reenter the room again.
                    if (!guests.contains(guestID) && available){
                        available = false;
                        guests.add(guestID);
                        System.out.println("Guest " + guestID + " Saw the Vase");
                        Thread.sleep(random.nextInt(1000));
                        available = true;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finally {
                    // Unlock the room once the guest has left.
                    enteredRoom.unlock();
                }
            }
        }
    }
        
    public static void main(String[] args) throws InterruptedException {
        // Create an executor service and activate all threads
        ExecutorService executor = Executors.newFixedThreadPool(numGuests);
        Random random = new Random();
        for (int i = 0; i < numGuests; i++){
            executor.execute(new minotaurvase(i));
            // Thread sleep to promote randomness among threads running
            Thread.sleep(random.nextInt(1000));
        }

        executor.shutdown();

    }
}