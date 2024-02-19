To compile and run either file:

enter the directory of the file you want to run

run javac parallel.java

run java parallel


This assignment focuses on using mutual exclusion to keep track of and prevent multiple threads from mutating signifcant variables. I keep a track of the number of guests using a constant variable in my implementation. If you want to alter the number of guests, simply change the value of the numGuests variable to increase or decrease the amount of guests for either part.

Part 1:

For the first part, We know that the guests cannot speak with eachother after entering the maze and we need to know whether all guests have entered the maze. The solution for keeping track of this is to allow one guest to be the one to request new cupcakes every time the cupcake platter is found empty. We will also tell all the guests to only eat the cupcake once so that the guest who's keeping track of the cupcakes knows that once he's replaced a total of N cupcakes (N being the number of guests), then all the guests have entered the maze. In my implementation, I simply ask the first guest - guest 0 - to keep track of the number of cupcakes eaten and to be the one to replace a cupcake if its eaten.

Part 2:

For the second part, I chose to use the second option. The first option was the simplest to implement but was very random since multiple guests could be waiting at any point to enter the room. The third option was the most efficient but would be difficult to implement. The second option was a good compromise between efficiency and implementation complexity as it prevents the guests from wasting time while waiting for another guest to finish viewing the vase using the "AVAILABLE" or "BUSY" sign and is much easier to implement than the third option. Since we don't need to communicate a way to prove all guests have viewed the vase, I can just tell guests not to enter the room again once entered. Thus, over time all guests will have the chance to view the vase.