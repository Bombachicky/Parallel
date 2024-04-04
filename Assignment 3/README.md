To run the first half of the assignment:

run javac ConcurrentLinkedList.java

run javac parallelbirthday.java

run java parallelbirthday

To run the second half of the assignment:

run javac paralleltemperature.java

run paralleltemperature

For the first half of the assignment, I created a concurrent linked list which essentially works similarly a regular linked list but sections that directly manipulate the list are locked so multiple threads cannot manipulate the data at the same time. 
The actual birthday file is simple enough. Such as in the assignment, a random number is picked to determine what tasks run when. After some time, eventually all the guests from the gift pool have thank you cards written to them and the assignment is complete.|

For the second half of the assignment, I simply use a synchronized list and atomic array to keep track of which sensor is working and how they are storing data to the list without conflicting the same values. Then I find the smallest temperatures, largest temperatures, and the greatest difference over the course of the assignment.
