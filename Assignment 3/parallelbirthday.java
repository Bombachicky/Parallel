import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

public class parallelbirthday {
    // Thread Count and Guest count listed
    private static final int MAX_THREADS = 4;
    private static final int GUEST_COUNT = 100_000;

    private static final Lock sharedLock = new ReentrantLock();

    // Performs a random task related to party preparation
    public static void executePartyTask(ConcurrentLinkedList guestList, Set<Integer> unassignedPresents, Set<Integer> completedCards) {
        Random taskSelector = new Random();

        List<Runnable> tasks = Arrays.asList(
            () -> addGift(unassignedPresents, guestList),
            () -> writeThankYouCard(guestList, completedCards),
            () -> checkForGift(guestList)
        );

        while (completedCards.size() < GUEST_COUNT) {
            int selectedTask = taskSelector.nextInt(tasks.size());
            tasks.get(selectedTask).run();
        }
    }

    // Adds a gift by to the concurrent linked list in sorted order. Locks the code so no other thread can ruin the data.
    private static void addGift(Set<Integer> presentsPool, ConcurrentLinkedList guestList) {
        sharedLock.lock();
        try {
            if (!presentsPool.isEmpty()) {
                int gift = presentsPool.iterator().next();
                presentsPool.remove(gift);
                guestList.insert(gift);
                System.out.println("Gift added for guest #" + gift);
            }
        } finally {
            sharedLock.unlock();
        }
    }

    // Removes a gift from the linked list for a guest and writes a thank you card for them.
    private static void writeThankYouCard(ConcurrentLinkedList guestList, Set<Integer> cardRegistry) {
        if (!guestList.isEmpty()) {
            int guestId = guestList.removeHead();
            if (guestId != Integer.MIN_VALUE) {
                sharedLock.lock();
                System.out.println("Card written for guest #" + guestId + " (Remaining guests: " + guestList.size() + ")");
                try {
                    cardRegistry.add(guestId);
                } finally {
                    sharedLock.unlock();
                }
            }
        }
    }

    // Checks if a gift is still in the concurrent list.
    private static void checkForGift(ConcurrentLinkedList guestList) {
        int guestToCheck = new Random().nextInt(GUEST_COUNT);
        boolean presentFound = guestList.contains(guestToCheck);
        System.out.println("Checking for guest #" + guestToCheck + "'s gift: " + (presentFound ? "Present" : "Absent"));
    }

    public static void main(String[] args) {
        // Creates a new list for the gifts and those who still need to receive thank you cards.
        ConcurrentLinkedList celebrationList = new ConcurrentLinkedList();
        Set<Integer> giftPool = Collections.synchronizedSet(new HashSet<>(prepareRandomSet(GUEST_COUNT)));
        Set<Integer> cardList = Collections.synchronizedSet(new HashSet<>());

        System.out.println("Initiating party tasks...");
        ExecutorService taskExecutor = Executors.newFixedThreadPool(MAX_THREADS);

        // Execute all tasks.
        for (int i = 0; i < MAX_THREADS; i++) {
            taskExecutor.execute(() -> executePartyTask(celebrationList, giftPool, cardList));
        }

        taskExecutor.shutdown();
        try {
            taskExecutor.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Tasks interrupted.");
        }

        System.out.println("All party tasks completed successfully.");
    }

    // Generates a set with shuffled integers from 0 to size - 1
    private static Set<Integer> prepareRandomSet(int size) {
        List<Integer> numbersList = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            numbersList.add(i);
        }
        Collections.shuffle(numbersList);
        return new HashSet<>(numbersList);
    }
}
