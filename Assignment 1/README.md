
To compile and run:

run javac Parallel.java

run java Parallel

Summary:

This approach used parallelizes the Sieve of Erastothenes algorithm which is known as the most efficient algorithm to
find all the primes of a number. The correctness and efficiency of this approach comes from using an algorithm proven
to both work and be the most efficient at finding all the primes of a number. This algorithm correctly uses multithreading
to check if a number is prime or not using 8 threads, each tackling a number until it reaches 100 million and then seperately
tackling the next number. Primes are carefully added to the list using a synchronized list to ensure data is thread-safe. 
We initialize our piime array to all true as well so if two threads are on the same point in the array both will 
turn the index to false.
