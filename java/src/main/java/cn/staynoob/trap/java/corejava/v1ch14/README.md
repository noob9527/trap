# 14 Concurrency

## 14.1 What are Threads?
Skipped

## 14.2 Interrupting Threads
A thread terminates when its `run` method returns--by executing a `return` statement, after executing the last statement in the method body, of if an exception occurs that is not caught in the method. In the initial release of Java, there also was a `stop` method that another thread could call to terminate a thread. However, that method is now deprecated. We discuss the reason in Section 14.5.15.\
Other than with the deprecated `stop` method, there is no way to `force` a thread to terminate. However, the `interrupt` method can be used to `request` termination of a thread.\
When the `interrupt` method is called on a thread, the interrupted status of the thread is set. This is a boolean flag that is present in every thread. Each thread should occasionally check whether it has been interrupted.\
To find out whether the interrupted status was set, first call the static `Thread.currentThread` method to get the current thread, and then call the `isInterrupted` method:
```java
while (!Thread.currentThread().isInterrupted() && more work to do) {
    // do more work
}
```
However, if a thread is blocked, it cannot check the interrupted status. This is where the `InterruptedException` comes in. When the `interrupt` method is called on a thread that blocks on a call such as sleep or wait, the blocking call is terminated by the `InterruptedException`.(There are blocking I/O calls that cannot be interrupted; you should consider interruptible alternatives. See Chapters 1 and 3 of Volume 2 for details.)\
There is no language requirement that a thread which is interrupted should terminate. Interrupting a thread simply grabs its attention. The interrupted thread can decide how to react to the interruption. Some threads are so important that they should handle the exception and continue. But quite commonly, a thread will simply want to interpret an interruption as a request for termination. The `run` method of such a thread has the following form:
```java
Runnable r = () -> {
    try {
        // ...
        while (!Thread.currentThread().isInterrupted() && more work to do) {
            // do more work
        }
    } catch(InterruptedException e) {
        // thread was interrupted during sleep or wait
    }
    finally {
        // cleanup, if required
    }
    // exiting the run method terminates the thread
};
```
The `isInterrupted` check is neither necessary nor useful if you call the `sleep` method (or another interruptible method) after every work iteration. If you call the `sleep` method when the interrupted status is set, it doesn't sleep. Instead, it clears the status and throws an `InterruptedException`. Therefore, if your loop calls sleep, don't check the interrupted status. Instead, catch the `InterruptedException`, like this
```java
Runnable r = () -> {
    try {
        // ...
        while (more work to do) {
            // do more work
            Thread.sleep(delay);
        }
    } catch(InterruptedException e) {
        // thread was interrupted during sleep
    } finally {
        // cleanup, if required
    }
    // exiting the run method terminates the thread
};
```
> Note: There are two very similar methods, `interrupted` and `isInterrupted`. The `interrupted` method is a static method that checks whether the current thread has been interrupted. Furthermore, calling the `interrupted` method clears the interrupted status of the thread. On the other hand, the `isInterrupted` method is an instance method that you can use to check whether any thread has been interrupted. Calling it does not change the interrupted status.

You'll find lots of published code in which the `InterruptedException` is squelched at a low level, like this:
```java
void mySubTask() {
    // ...
    try { sleep(delay); }
    catch (InterruptedException e) {} // Don't ignore!
    // ...
}
```
Don't do that! If you can't think of anything good to do in the `catch` clause, you still have two reasonable choices:
- In the `catch` clause, call `Thread.currentTread().interrupt()` to set the interrupted status. Then the caller can test it.
    ```java
    void mySubTask() {
        // . . .
        try { sleep(delay); }
        catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        // . . .
    }
    ```
- Or, even better, tag your method with `throws InterruptedException` and drop the `try` block. Then the caller (or, ultimately, the `run` method) can catch it
    ```java
    void mySubTask() throws InterruptedException {
        // . . .
        sleep(delay);
        // . . .
    }
    ```

## 14.3 Thread States
Threads can be in one of six states:
- New
- Runnable
- Blocked
- Waiting
- Timed waiting
- Terminated

Each of these states is explained in the sections that follow.\
To determine the current state of a thread, simply call the `getState` method.

### 14.3.1 New Threads
When you create a thread with the new operator, the thread is not yet running. This means that it is in the new state. When a thread is in the new state, the program has not started executing code inside of it. A certain amount of bookkeeping needs to be done before a thread can run.

### 14.3.2 Runnable Threads
Once you invoke the start method, the thread is in the runnable state. A runnable thread may or may not actually be running. It is up to the operating system to give the thread time to run. (The Java specification does not call this a separate state, though, A running thread is still in the runnable state).\
Once a thread is running, it doesn't necessarily keep running. In fact, it is desirable that running threads occasionally pause so that other threads have a chance to run. The details of thread scheduling depend on the services that the operating system provides. Preemptive scheduling systems give each runnable thread a slice of time to perform its task. When that slice of time is exhausted, the operating system preempts the thread and gives another thread an opportunity to work. When selecting the next thread, the operating system takes into account the thread priorities.\
All modern desktop and server operating systems use preemptive scheduling. However, small devices such as cell phones may use cooperative scheduling. In such a device, a thread loses control only when it calls the `yield` method, or when it is blocked or waiting.\
On a machine with multiple processors, each processor can run a thread, and you can have multiple threads run in parallel. Of course, if there are more threads than processors, the scheduler still has to do time slicing.\
Always keep in mind that a runnable thread may or may not be running at any given time.(This is why the state is called "runnable" and not "running")

### 14.3.3 Blocked and Waiting Threads
When a thread is blocked or waiting, it is temporarily inactive. It doesn't execute any code and consumes minimal resources. It is up to the thread scheduler to reactivate it. The details depend on how the inactive state was reached.
- When the thread tries to acquire an intrinsic object lock (but not a `Lock` in the `java.util.concurrent` library) that is currently held by another thread, it becomes blocked. The thread becomes unblocked when all other threads have relinquished the lock and the thread scheduler has allowed this thread to hold it.
- When the thread waits for another thread to notify the scheduler of a condition, it enters the waiting state. This happens by calling the `Object.wait` or `Thread.join` method, or by waiting for a Lock or Condition in the `java.util.concurrent` library. In practice, the difference between the blocked and waiting state is not significant.
- Several methods have a timeout parameter. Calling them causes the thread to enter the timed waiting state. This state persists either until the timeout expires or the appropriate notification has been received. Methods with timeout include `Thread.sleep` and the timed versions of `Object.wait`, `Thread.join`, `Lock.tryLock`, and `Condition.await`.

### 14.3.4 Terminated Threads
A thread is terminated for one of two reasons:
- It dies a natural death because the run method exits normally.
- It dies abruptly because an uncaught exception terminates the run method.

In particular, you can kill a thread by invoking its stop method. That method throws a `ThreadDeath` error object that kills the thread. However, the `stop` method is deprecated, and you should never call it in your own code.

## 14.4 Thread Properties
### 14.4.1 Thread Priorities
In the Java programming language, every thread has a priority. By default, a thread inherits the priority of the thread that constructed it. You can increase or decrease the priority of any thread with the `setPriority` method. You can set the priority to any value between `MIN_PRIORITY` and `MAX_PRIORITY`. `NORM_PRIORITY` is defined as 5.\
Whenever the thread scheduler has a chance to pick a new thread, it prefers threads with higher priority. However, thread priorities are highly system dependent. When the virtual machine relies on the thread implementation of the host platform, the Java thread priorities are mapped to the priority levels of the host platform, which may have more or fewer thread priority levels.\
For example, Windows has seven priority levels. Some of the Java priorities will map to the same operating system level. In the Oracle JVM for Linux, thread priorities are ignored altogether--all threads have the same priority.\
Beginning programmers sometimes overuse thread priorities. There are few reasons ever to tweak priorities. You should certainly never structure your programs so that their correct functioning depends on priority levels.

### 14.4.2 Daemon Treads
You can turn a thread into a daemon thread by calling
```java
t.setDaemon(true);
```
There is nothing demonic about such a thread. A daemon is simply a thread that has no other role in life than to server others. Examples are timer threads that send regular "timer ticks" to other threads or threads that clean up stale cache entries.\
When only daemon threads remain, the virtual machine exits. There is no point in keeping the program running if all remaining threads are daemons.\
Daemon threads are sometimes mistakenly used by beginners who don't want to think about shutdown actions. However, this can be dangerous. A daemon thread should never access a persistent resource such as a file or database since it can terminate at any time, even in the middle of an operation.

### 14.4.3 Handlers for Uncaught Exceptions
The run method of a thread cannot throw any checked exceptions, but it can be terminated by an unchecked exception.\
However, there is no catch clause to which the exception can be propagated. Instead, just before the thread dies, the exception is passed to a handler for uncaught exceptions.\
The handler must belong to a class that implements the `Thread.UncaughtExceptionHandler` interface. That interface has a single method.
```java
void uncaughtException(Thread t, Throwable e)
```
You can install a handler into any thread with the `setUncaughtExceptionHandler` method. You can also install a default handler for all threads with the static method `setDefaultUncaughtExcpetionHandler` of the Thread class. A replacement handler might use the logging API to send reports of uncaught exceptions into a log file.\
If you don't install a default handler, the default handler is null, However, if you don't install a hander for an individual thread, the handler is the thread's `ThreadGroup` object.
> Note: A thread group is a collection of threads that can be managed together. By default, all threads that you create belong to the same thread group, but it is possible to establish other groupings. Since there are now better features for operating on collections of threads, we recommend that you do not use thread groups in your programs.

The `ThreadGroup` class implements the `Thread.UncaughtExceptionHandler` interface. Its `uncaughtException` method takes the following action:
1. If the thread group has a parent, then the `uncaughtException` method of the parent group is called.
2. Otherwise, if the `Thread.getDefaultUncaughtExceptionHandler` method returns a non-null handler, it is called.
3. Otherwise, if the Throwable is an instance of `ThreadDeath`(by calling `Thread.stop` method), nothing happens.
4. Otherwise, the name of the thread and the stack trace of the Throwable are printed on System.err.

That is the stack trace that you have undoubtedly seen many times in your programs.

## 14.5 Synchronization
In most practical multithreaded applications, two or more threads need to share access to the same data. What happens if two threads have access to the same object and each calls a method that modifies the state of the object? As you might imagine, the threads can step on each other's toes. Depending on the order in which the data were accessed, corrupted objects can result. Such a situation is often called a race condition.

### 14.5.1 An Example of a Race Condition
To avoid corruption of shared data by multiple threads, you must learn how to synchronize the access. In the next test program, we simulate a bank with a number of accounts. We randomly generate transactions that move money between these accounts. Each account has one thread. Each transaction moves a random amount of money from the account serviced by the thread to another random account.\
The simulation code is straightforward. We have the class Bank with the method `transfer`. This method transfers some amount of money from one account to another. (We don't yet worry about negative account balances.) Here is the code for transfer method of the Bank class.
```java
// CAUTION: unsafe when called from multiple threads
public void transfer(int from, int to, double amount) {
    System.out.print(Thread.currentThread());
    accounts[from] -= amount;
    System.out.printf(" %10.2f from %d to %d", amount, from, to);
    accounts[to] += amount;
    System.out.printf(" Total Balance: %10.2f%n", getTotalBalance());
}
```
Here is the code for the Runnable instances. The run method keeps moving money out of a given bank account. In each iteration, the run method picks a random target account and a random amount, calls transfer on the bank object, and then sleeps.
```java
Runnable r = () -> {
    try {
        while (true) {
            int toAccount = (int) (bank.size() * Math.random());
            double amount = MAX_AMOUNT * Math.random();
            bank.transfer(fromAccount, toAccount, amount);
            Thread.sleep((int) (DELAY * Math.random()));
        }
    }
    catch (InterruptedException e) { }
};
```
When this simulation runs, we do not know how much money is in any one bank account at any time. But we do know that the total amount of money in all the accounts should remain unchanged because all we do is move money from one account to another.\
At the end of each transaction, the transfer method recomputes the total and prints it. Here is a typical printout:
```
. . .
521.51 from 14 to 22 Total Balance: 100000.00
Thread[Thread-13,5,main]
359.89 from 13 to 81 Total Balance: 100000.00
. . .
Thread[Thread-36,5,main]
401.71 from 36 to 73 Total Balance: 99291.06
Thread[Thread-35,5,main]
691.46 from 35 to 77 Total Balance: 99291.06
```
As you can see, something is very wrong. For a few transactions, the bank balance remains at \$100,000, which is the correct total for 100 accounts of \$1,000 each. But after some time, the balance changes slightly. When you run this program, errors may happen quickly, or it may take a very long time for the balance to become corrupted. This situation does not inspire confidence, and you would probably not want to deposit your hard-earned money in such a bank.

### 14.5.2 The Race Condition Explained
In the previous section, we ran a program in which several threads updated bank account balances. After a while, errors crept in and some amount of money was either lost or spontaneously created. This problem occurs when two threads are simultaneously trying to update an account. Suppose two threads simultaneously carry out the instruction
```java
accounts[to] += amount;
```
The problem is that these are not atomic operations. The instruction might be processed as follows:
1. Load accounts[to] into a register.
2. Add amount.
3. Move the result back to accounts[to].

Now, suppose the first thread executes Steps 1 and 2, and then it is preempted. Suppose the second thread awakens and updates the same entry in the account array. Then, the first thread awakens and completes its Step 3. That action wipes out the modification of the other thread. As a result, the total is no longer correct.\
The real problem is that the work of the transfer method can be interrupted in the middle. If we could ensure that the method runs to completion before the thread loses control, the state of the bank account object would never be corrupted.

### 14.5.3 Lock Objects
There are two mechanisms for protecting a code block from concurrent access. The Java language provides a synchronized keyword for this purpose, and Java SE5.0 introduced the `ReentrantLock` Class. The synchronized keyword automatically provides a lock as well as an associated "condition," which makes it powerful and convenient for most cases that require explicit locking. However, we believe that it is easier to understand the synchronized keyword after you have seen locks and conditions in isolation.\
The basic outline for protecting a code block with a `ReentrantLock` is:
```java
myLock.lock(); // a ReentrantLock object
try {
    // critical section
} finally {
    myLock.unlock(); // make sure the lock is unlocked even if an exception is thrown
}
```
This construct guarantees that only one thread at a time can enter the critical section. As soon as one thread locks the lock object, no other thread can get past the lock statement. When other threads call lock, they are deactivated until the first thread unlocks the lock object.
> Caution: It is critically important that the `unlock` operation is enclosed in a finally clause. If the code in the critical section throws an exception, the lock must be unlocked. Otherwise, the other threads will be blocked forever.\
** The lock is called reentrant because a thread can repeatedly acquire a lock that it already owns. The lock has a hold count that keeps track of the nested calls to the lock method. The tread has to call unlock for every call to lock in order to relinquish the lock. Because of this feature, code protected by a lock can call another method that uses the same locks. **\
For example, the transfer method calls the `getTotalBalance` method, which also locks the `bankLock` object, which now has a hold count of 2. When the `getTotalBalance` method exits, the hold count is back to 1. When the transfer method exists, the hold count is 0, and the thread relinquishes the lock.\
In general, you will want to protect blocks of code that update or inspect a shared object, so you can be assured that these operations run to completion before another thread can use the same object.
> Caution: Be careful to ensure that the code in a critical section is not bypassed by throwing an exception. If an exception is thrown before the end of the section, the finally clause will relinquish the lock, but the object may be in a damaged state.

### 14.5.4 Condition Objects
Often, a thread enters a critical section only to discover that it can't proceed until a condition is fulfilled. Use a condition object to manage threads that have acquired a lock but cannot do useful work. In this section, we introduce the implementation of condition objects in the Java library.\
Let us refine our simulation of the bank. We do not want to transfer money out of an account that does not have the funds to cover the transfer. Note that we cannot use code like
```java
if (bank.getBalance(from) >= amount)
    bank.transfer(from, to, amount);
```
It is entirely possible that the current thread will be deactivated between the successful outcome of the test and the call to transfer .
```java
if (bank.getBalance(from) >= amount)
    // thread might be deactivated at this point
    bank.transfer(from, to, amount);
```
By the time the thread is running again, the account balance may have fallen below the withdrawal amount. You must make sure that no other thread can modify the balance between the test and the transfer action. You do so by protecting both the test and the transfer action with a lock:
public void transfer(int from, int to, int amount) {
    bankLock.lock();
    try {
        while (accounts[from] < amount) {
            // wait
            // . . .
        }
        // transfer funds
        // . . .
    } finally {
        bankLock.unlock();
    }
}
Now, what do we do when there is not enough money in the account? We wait until some other thread has added funds. But this thread has just gained exclusive access to the bankLock , so no other thread has a chance to make a deposit. This is where condition objects come in.\
A lock object can have one or more associated condition objects. You obtain a condition object with the `newCondition` method. It is customary to give each condition object a name that evokes the condition that it represents. For example, here we set up a condition object to represent the “sufficient funds” condition.
```java
class Bank {
    private Condition sufficientFunds;
    // . . .
    public Bank() {
        // . . .
        sufficientFunds = bankLock.newCondition();
    }
}
```
If the transfer method finds that sufficient funds are not available, it calls
```java
sufficientFunds.await();
```
The current thread is now deactivated and gives up the lock. This lets in another thread that can, we hope, increase the account balance.\
There is an essential difference between a thread that is waiting to acquire a lock and a thread that has called `await`. Once a thread calls the `await` method, it enters a wait set for that condition. The thread is not made runnable when the lock is available. Instead, it stays deactivated until another thread has called the `signallAll` method on the same condition.\
When another thread has transferred money, it should call
```java
sufficientFunds.signalAll();
```
This call reactivates all threads waiting for the condition. When the threads are removed from the wait set, they are gain runnable and the scheduler will eventually activate them again. At that time, they will attempt to reenter the object. As soon as the lock is available, one of them will acquire the lock and continue where it left off, returning from the call to await.\
At this time, the thread should test the condition again. There is no guarantee that the condition is now fulfilled--the `singalAll` method merely signals to the waiting threads that is may be fulfilled at this time and that it is worth checking for the condition again.
> Note: In general, a call to `await` should be inside a loop of the form
```java
while(!(ok to proceed))
    condition.await();
```

It is crucially important that some other thread calls the `signalAll` method eventually. When a thread calls `await`, it has no way of reactivating itself. It puts its faith in the other threads. If none of them bother to reactivate the waiting thread, it will never run again. This can lead to unpleasant deadlock situations. If all other threads are blocked and the last activate thread calls `await` without unblocking the others, and the program hangs.\
When should you call `signalAll`? The rule of thumb is to call `signalAll` whenever the state of an object changes in a way that might be advantageous to waiting threads. For example, whenever an account balance changes, the waiting threads should be given another chance to inspect the balance. In our example, we call `signalAll` when we have finished the funds transfer.\
Note that the call to `signalAll` does not immediately activate a waiting thread. It only unblocks the waiting threads so that they can compete for entry into the object after the current thread has relinquished the lock.\
Another method, signal, unblocks only a single thread from the wait set, chosen at random. That is more efficient than unblocking all threads, but there is a danger. If the randomly chosen thread finds that it still cannot proceed, it becomes blocked again. If no other thread calls signal again, then the system deadlocks.
> Caution: A thread can only call `await`, `signalAll`, or `signal` on a condition if it owns the lock of the condition.


### 14.5.5 `synchronized` keyword
In the preceding sections, you saw how to use Lock and Condition objects. Before going any further, let us summarize the key points about locks and conditions:
- A lock protects sections of code, allowing only one thread to execute the code at a time.
- A lock manages threads that are trying to enter a protected code segment.
- A lock can have one or more associated condition objects.
- Each condition object manages threads that have entered a protected code section but that cannot proceed.

The Lock and Condition interfaces give programmers a high degree of control over locking. However, in most situations, you don't need that control--you can use a mechanism that is built into the Java language. Ever since version 1.0, every object in Java has an intrinsic lock. If a method is declared with the synchronized keyword the object's lock protects the entire method. That is, to call the method, a thread must acquire the intrinsic object lock. In other words,
```java
public synchronized void method(){
    // method body
}
```
is the equivalent of
```java
public void method(){
    this.intrinsicLock.lock();
    try {
        // method body
    } finally {
        this.intrinsicLock.unlock();
    }
}
```
The intrinsic object lock has a single associated condition. The `wait` method adds a thread to the wait set, and the `notifyAll`/`notify` methods unblock waiting threads. In other words, calling wait of `notifyAll` is the equivalent of
```java
intrinsicCondition.await();
intrinsicCondition.signalAll();
```
> Note: The `wait`, `notifyAll`, and `notify` methods are final methods of the Object class. The Condition methods had to be named `await`, `signalAll`, and `signal`, so that they don't conflict with those methods.

It is also legal to declare static methods as synchronized. If such a method is called, it acquires the intrinsic lock of the associated class object. As a result, no other thread can call this or any other syncrhonized static method of the same class.\
The intrinsic locks and conditions have some limitations. Among them:
- You cannot interrupt a thread that is trying to acquire a lock.
- You cannot specify a timeout when trying to acquire a lock.
- Having a single condition per lock can be inefficient.

What should you use in your code— Lock and Condition objects or synchronized methods? Here is our recommendation:
- It is best to use neither Lock / Condition nor the synchronized keyword. In many situations, you can use one of the mechanisms of the `java.util.concurrent` package that do all the locking for you. For example, "Blocking Queues", you will see how to use a blocking queue to synchronize threads that work on a common task. You should also explore parallel streams—see Volume II, Chapter 1.
- If the synchronized keyword works for your situation, by all means, use it. You’ll write less code and have less room for error.
- Use Lock / Condition if you really need the additional power that these constructs give you.

### 14.5.6 Synchronized Blocks
As we just discussed, every Java object has a lock. A thread can acquire the lock by calling a synchronized method. There is a second mechanism for acquiring the lock: by entering a synchronized block. When a thread enters a block of the form
```java
synchronized (obj) { // this is the syntax for a synchronized block
    // critical section
}
```
then it acquires the lock for obj. You will sometimes find adhoc locks, such as
```java
public class Bank {
    private double[] accounts;
    private Object lock = new Object();

    public void transfer(int from, int to, int amount) {
        synchronized (lock) { // an ad-hoc lock
            accounts[from] -= amount;
            accounts[to] += amount;
        }
        System.out.println(. . .);
    }
}
```
Here, the lock object is created only to use the lock that every Java object possesses.\
Sometimes, programmers use the lock of an object to implement additional atomic operations—a practice known as client-side locking. Consider, for example, the Vector class, which is a list whose methods are synchronized. Now suppose we stored our bank balances in a `Vector<Double>`. Here is a naive implementation of a transfer method:
```java
// Error
public void transfer(Vector<Double> accounts, int from, int to, int amount) {
    accounts.set(from, accounts.get(from) - amount);
    accounts.set(to, accounts.get(to) + amount);
}
```
The get and set methods of the Vector class are synchronized, but that doesn’t help us. It is entirely possible for a thread to be preempted in the transfer method after the first call to get has been completed. Another thread may then store a different value into the same position. However, we can hijack the lock:
```java
public void transfer(Vector<Double> accounts, int from, int to, int amount) {
    synchronized (accounts) {
        accounts.set(from, accounts.get(from) - amount);
        accounts.set(to, accounts.get(to) + amount);
    }
}
```
This approach works, but it is entirely dependent on the fact that the Vector class uses the intrinsic lock for all of its mutator methods. However, is this really a fact? The documentation of the Vector class makes no such promise. You have to carefully study the source code and hope that future versions do not introduce unsynchronized mutators. As you can see, client-side locking is very fragile and not generally recommended.

### 14.5.7 The monitor Concept
skipped

### 14.5.8 Volatile field
Sometimes, it seems excessive to pay the cost of synchronization just to read or write an instance field or two. After all, what can go wrong? Unfortunately, with modern processors and compilers, there is plenty of room for error.
- Computers with multiple processors can temporarily hold memory values in registers or local memory caches. As a consequence, threads running in different processors may see different values for the same memory location!
- Compilers can reorder instructions for maximum throughput. Compilers won't choose an ordering that changes the meaning of the code, but they make the assumption that memory values are only changed when there are explicit instructions in the code. However, a memory value can be changed by another thread!

If you use locks to protect code that can be accessed by multiple threads, you won't have these problems. Compilers are required to respect locks by flushing local caches as necessary and not inappropriately reordering instructions. The details are explained in the Java Memory Model and Thread Specification developed by JSR 133. Much of the specification is highly complex and technical, but the document also contains a number of clearly explained examples.
> Note: Brain Goetz coined the following "synchronization motto": "If you write a variable which may next be read by another thread, or you read a variable which may have last been written by another thread, you must use synchronization."

The `volatile` keyword offers a lock-free mechanism for synchronizing access to an instance field. If you declare a field as volatile, then the compiler and the virtual machine take into account that the field may be concurrently updated by another thread.\
For example, suppose an object has a boolean flag done that is set by one thread and queried by another thread. As we already discussed, you can use a lock:
```java
private boolean done;
public synchronized boolean isDone() { return done; }
public synchronized void setDone() { done = true; }
```
Perhaps it is not a good idea to use the intrinsic object lock. The `isDone` and `setDone` methods can block if another thread has locked the object. If that is a concern, one can use a separate lock just for this variable. But this is getting to be a lot of trouble.\
In this case, it is reasonable to declare the field as volatile:
```java
private volatile boolean done;
public boolean isDone() { return done; }
public void setDone() { done = true; }
```
The compiler will insert the appropriate code to ensure that a change to the done variable in on thread is visible from any other thread that reads the variable.\
> Caution: Volatile variables do not provide any atomicity. For example, the method
```java
public void flipDone() { done = !done; } // not atomic
```
is not guaranteed to flip the value of the field. There is not guarantee that the reading, flipping, and writing is uninterrupted.

### 14.5.9 final variable
skipped

### 14.5.10 Atomicity
You can declare shared variables as volatile provided you perform no operations other than assignment.\
There are a number of classes in the `java.util.concurrent.atomic` package that use efficient machine-level instructions to guarantee atomicity of other operations without using locks. For example, you can safely generate a sequence of numbers like this:
```java
public static AtomicLong nextNumber = new AtomicLong();
// In some thread...
long id = nextNumber.incrementAndGet();
```
The `incrementAndGet` method atomically increments the `AtomicLong` and returns the post-increment value. That is, the operations of getting the value, adding 1, setting it, and producing the new value cannot be interrupted. It is guaranteed that the correct value is computed and returned, even if multiple threads access the same instance concurrently.\
There are methods for atomically setting, adding, and subtracting values, but if you want to make a more complex update, you have to use the `compareAndSet` method.  For example, suppose you want to keep track of the largest value that is observed by different threads. The following won't work:
```java
public static AtomicLong largest = new AtomicLong();
// In some thread...
largest.set(Math.max(largest.get(), observed)); // Error--race condition!
```
This update is not atomic. Instead, compute the new value and use `compareAndSet` in a loop:
```java
do {
    oldValue = largest.get();
    newValue = Math.max(oldValue, observed);
} while (!largest.compareAndSet(oldValue, newValue));
```
If another thread is also updating largest , it is possible that it has beat this thread to it. Then `compareAndSet` will return false without setting the new value. In that case, the loop tries again, reading the updated value and trying to change it. Eventually, it will succeed replacing the existing value with the new one. This sounds tedious, but the `compareAndSet` method maps to a processor operation that is faster than using a lock.\
In Java SE 8, you don’t have to write the loop boilerplate any more. Instead, you provide a lambda expression for updating the variable, and the update is done for you. In our example, we can call
```java
largest.updateAndGet(x -> Math.max(x, observed));
```
or
```java
largest.accumulateAndGet(observed, Math::max);
```
The `accumulateAndGet` method takes a binary operator that is used to combine the atomic value and the supplied argument.\
When you have a very large number of threads accessing the same atomic values, performance suffers because the optimistic updates require too many retries. Java SE 8 provides classes `LongAdder` and `LongAccumulator` to solve this problem. A `LongAdder` is composed of multiple variables whose collective sum is the current value. Multiple threads can update different summands, and new summands are automatically provided when the number of threads increases. This is efficient in the common situation where the value of the sum is not needed until after all work has been done. The performance improvement can be substantial.

### 14.5.11 DeadLock
Locks and conditions cannot solve all problems that might arise in multithreading. Consider the following situation:
1. Account 1: $200
2. Account 2: $300
3. Thread 1: Transfer $300 from Account 1 to Account 2
4. Thread 2: Transfer $400 from Account 2 to Account 1

Threads 1 and 2 are clearly blocked. Neither can proceed because the balances in Accounts 1 and 2 are insufficient. It is possible that all threads get blocked because each is waiting for more money. Such a situation is called a deadlock.

### 14.5.12 ThreadLocal
In the preceding sections, we discussed the risks of sharing variables between threads. Sometimes, you can avoid sharing by giving each thread its own instance, using the `ThreadLocal` helper class. For example, the `SimpleDateFormat` class is not thread safe. Suppose we have a static variable
```java
public static final SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
```
If two threads execute an operation such as
```java
String dataStamp = dateFormat.format(new Date());
```
then the result can be garbage since the internal data structures used by the `dateFormat` can be corrupted by concurrent access. You could use synchronization, which is expensive, or you could construct a local `SimpleDateFormat` object whenever you need it, but that is also wasteful.\
To construct one instance per thread, use the following code:
```java
public static final ThreadLocal<SimpleDateFormat> dateFormat =
    ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd"));
```
The first time you call get in a given thread, the lambda in the constructor is called. From then on, the get method returns the instance belonging to the current thread.\
A similar problem is the generation of random numbers in multiple threads. The `java.util.Random` class is thread safe. But it is still inefficient if multiple threads need to wait for a single shared generator. Java SE 7 provides a convenience class for you. Simply make a call such as
```java
int random = ThreadLocalRandom.current().nextInt(upperBound);
```
The call `ThreadLocalRandom.current()` returns an instance of the Random class that is unique to the current thread.

### 14.5.13 Lock Testing and Timeouts
A thread blocks indefinitely when it calls the lock method to acquire a lock that is owned by another thread. You can be more cautious about acquiring a lock. The `tryLock` method tries to acquire a lock and returns true if it was successful. Otherwise, it immediately returns false, and the thread can go off and do something else.
```java
if (myLock.tryLock()) {
    // now the thread owns the lock
    try {
        // . . .
    } finally { myLock.unlock(); }
} else {
    // do something else
}
```
The lock method cannot be interrupted. If a thread is interrupted while it is waiting to acquire a lock, the interrupted thread continues to be blocked until the lock is available. If a deadlock occurs, then the lock method can never terminate.\
However, if you can `tryLock` with a timeout, an `InterruptedException` is thrown if the thread is interrupted while it is waiting. This is clearly a useful feature because it allows a program to break up deadlocks.\
You can also call the `lockInterruptibly` method. It has the same meaning as `tryLock` with an infinite timeout.\
When you wait on a condition, you can also supply a timeout:
```java
myCondition.await(100, TimeUnit.MILLISECONDS))
```
The await method returns if another thread has activated this thread by calling `signalAll` or `signal`, or if the timeout has elapsed, or if the thread was interrupted.\
The await methods throw an `InterruptedException` if the waiting thread is interrupted.  In the (perhaps unlikely) case that you’d rather continue waiting, use the `awaitUninterruptibly` method instead.

### 14.5.14 Read/Write Locks
The `java.util.concurrent.locks` package defines two lock classes, the `ReentrantLock` that we already discussed and the `ReentrantReadWriteLock`. The latter is useful when there are many threads that read from a data structure and fewer threads that modify it In that situation, it makes sense to allow shared access for the readers. Of course a writer must still have exclusive access.
Here are the steps that are necessary to use a read/write lock:
1. Construct a `ReentrantReadWriteLock` object:
    ```java
    private ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    ```
2. Extract the read and write locks:
    ```java
    private Lock readLock = rwl.readLock();
    private Lock writeLock = rwl.writeLock();
    ```
3. Use the read lock in all accessors:
    ```java
    public double getTotalBalance() {
        readLock.lock();
        try {
            // . . .
        } finally { readLock.unlock(); }
    }
    ```
4. Use the write lock in all mutators:
    ```java
    public void transfer(. . .) {
        writeLock.lock();
        try {
            // . . .
        } finally { writeLock.unlock(); }
    }
    ```

### 14.5.15 Why the `stop` and `suspend` Methods Are Deprecated
The initial release of Java defined a stop method that simply terminates a thread, and a suspend method that blocks a thread until another thread calls resume. The stop and suspend methods have something in common: Both attempt to control the behavior of a given thread without the tread's cooperation.\
The stop, suspend, and resume methods have been deprecated. The stop method is inherently unsafe, and experience has shown that the suspend method frequently leads to deadlocks. In this section, you will see why these methods are problematic and that you can do to avoid problems.\
Let us turn to the stop method first. This method terminates all pending methods, including the run method. When a thread is stopped, it immediately gives up the locks on all objects that it has locked. This can leave objects in an inconsistent state. For example, suppose a `TranferRunnable` is stopped in the middle of moving money from one account to another, after the withdrawal and before the deposit. Now the bank object is damaged. Since the lock has been relinquished, the damage is observable from the other threads that have not been stopped.\
When a thread wants to stop another thread, it has no way of knowing when the stop method is safe and when it leads to damaged object. Therefore, the method has been deprecated. You should interrupt a thread when you want it to stop. The interrupted thread can then stop when it is safe to do so.\
> Note: Some authors claim that the stop method has been deprecated because it can cause objects to be permanently locked by a stopped thread. However, that claim is not valid. A stopped thread exits all synchronized methods it has called--technically, by throwing a `ThreadDeath` exception. As a consequence, the thread relinquishes the intrinsic object locks that it holds.\
Next, let us see what is wrong with the suspend method. Unlike stop, suspend won't damage objects. However, if you suspend a thread that owns a lock, then the lock is unavailable until the thread is resumed. If the thread that calls the suspend method tries to acquire the same lock, the program deadlocks: The suspended thread waits to be resumed, and the suspending thread waits for the lock.\
If you want to safely suspend a thread, introduce a variable `suspendRequested` and test it in a safe place of your run method--in a place where your thread doesn't lock objects that other threads need. When your thread finds that the `suspendRequested` variable has been set, it should keep waiting until it becomes available again.

## 14.6 Blocking Queues
A blocking queue causes a thread to block when you try to add an element when the queue is currently full or to remove an element when the queue is empty. Blocking queues are a useful tool for coordinating the work of multiple threads. Worker threads can periodically deposit intermediate results into a blocking queue. Other worker threads remove the intermediate results and modify them further. The queue automatically balances the workload. If the first set of threads runs slower than the second, the second set blocks while waiting for the results. If the first set of threads runs faster, the queue fills up until the second set catches up.\
The blocking queue methods fall into three categories that differ by the action they perform when the queue is full or empty. If you use the queue as a thread management tool, use the put and take methods. The add, remove, and element operations throw an exception when you try to add to a full queue or get the head of an empty queue. Of course, in a multithreaded program, the queue might become full or empty at any time, so you will instead want to use the off, poll, and peek methods. These methods simply return with a failure indicator instead of throwing an exception if they cannot carry out their tasks.
> Note: The poll and peek methods return null to indicate failure. Therefore, it is illegal to insert null values into these queues.

The put method blocks if the queue is full, and the take method blocks if the queue is empty. These are the equivalents of offer and poll with no timeout.\
The `java.util.concurrent` package supplies several variations of blocking queues. By default, the `LinkedBlockingQueue` has no upper bound on its capacity, but a maximum capacity can be optionally specified. The `LinkedBlockingDeque` is a double-ended version. The `ArrayBlockingQueue` is constructed with a given capacity and an optional parameter to require fairness. If fairness is specified, then the longest-waiting threads are given preferential treatment. As always, fairness exacts a significant performance penalty, and you should only use it if your problem specifically requires it.\
The `PriorityBlockingQueue` is a priority queue, not a first-in/first-out queue. Elements are removed in order of their priority. The queue has unbounded capacity, but retrieval will block if the queue is empty.\
A `DelayQueue` contains objects that implement the Delayed interface, the `getDelay` methods returns the remaining delay of the object. A negative value indicates that the delay has elapsed. Elements can only be removed from a `DelayQueue` if their delay has elapsed.\
Java SE 7 adds a `TransferQueue` interface that allows a producer thread to wait until a consumer is ready to take on an item. When a producer calls
```java
q.transfer(item);
```
the call blocks until another thread removes it. The `LinkedTransferQueue` class implements this interface.

## 14.7 Thread-Safe Collections
If multiple threads concurrently modify a data structure, such as a hash table, it is easy to damage that data structure. You can protect a shared data structure by supplying a lock, but it is usually easier to choose a thread-safe implementation instead. In the following sections, we discuss the other thread-safe collections that the Java library provides.

### 14.7.1 Efficient Maps, Sets, and Queues
The `java.util.concurrnet` package supplies efficient implementations for maps, sorted sets, and queues: `ConcurrentHashMap`, `ConcurrentSkipListMap`, `ConcurrentSkipListSet`, and `ConcurrentLinkedQueue`.\
These collections sue sophisticated algorithms that minimize contention by allowing concurrent access to different parts of the data structure.\
Unlike most collections, the size method of these classes does not necessarily operate in constant time. Determining the current size of one of these collections usually requires traversal.\
The collections return weakly consistent iterators. That means that the iterators may or may not reflect all modifications that are made after they were constructed, but they will not return a value twice and they will not throw a `ConcurrentModificationException`.
> In contrast, an iterator of a collection in the `java.util` package throws a `ConcurrentModificationException` when the collection has been modified after construction of the iterator.

The concurrent hash map can efficiently support a large number of readers and a fixed number of writers. By default, it is assumed that there are up to 16 simultaneous writer threads. There can be many more writer threads, but if more than 16 write at the same time, the others are temporarily blocked. You can specify a higher number in the constructor, but it is unlikely that you will need to.

### 14.7.2 Atomic Update of Map Entries
The original version of `ConcurrentHashMap` only had a few methods for atomic updates, which made for somewhat awkward programming. Suppose we want to count how often certain features are observed. As a simple example, suppose multiple threads encounter words, and we want to count their frequencies. Can we use a `ConcurrentHashMap<String, Long>`? Consider the code for incrementing a count. Obviously, the following is not thread safe:
```java
Long oldValue = map.get(word);
Long newValue = oldValue == null ? 1 : oldValue + 1;
map.put(word, newValue); // Error--might not replace oldValue
```
Another thread might be updating the exact same count at the same time.
> Note: Some programmers are surprised that a supposedly thread-safe data structure permits operations that are not thread safe. But there are two entirely different considerations. If multiple threads modify a plain `HashMap`, they can destroy the internal structure (an array of linked lists). Some of the links may go missing, or even go in circles, rendering the data structure unusable. That will never happen with a `ConcurrentHashMap`. In the example above, the code for get and put will never corrupt the data structure. But, since the sequence of operations is not atomic, the result is not predictable.

A classic trick is to use the replace operation, which atomically replaces an old value with a new one, provided that no other thread has come before and replaced the old value with something else. You have to keep doing it until replace succeeds:
```java
do {
    oldValue = map.get(word);
    newValue = oldValue == null ? 1 : oldValue + 1;
} while (!map.replace(word, oldValue, newValue));
```
Alternatively, you can use a `ConcurrentHashMap<String, AtomicLong>` or, with Java SE 8, a `ConcurrentHashMap<String, LongAdder>`. Then the update code is:
```java
map.putIfAbsent(word, new LongAdder());
map.get(word).increment();
```
The first statement ensures that there is a `LongAdder` present that we can increment atomically. Since `putIfAbsent` returns the mapped value (either the existing one or the newly put one), you can combine the two statements:
```java
map.putIfAbsent(word, new LongAdder()).increment();
```
Java SE 8 provides methods that make atomic updates more convenient. The compute method is called with a key and a function to compute the new value. That function receives the key and the associated value, or null if there is none, and it computes the new value. For example, here is how we can update a map of integer counters:
```java
map.compute(word, (k, v) -> v == null ? 1 : v + 1);
```
You often need to do something special when a key is added for the first time. The merge method makes this particularly convenient. It has a parameter for the initial value that is used when the key is not yet present. Otherwise, the function that you supplied is called, combining the existing value and the initial value. (Unlike compute , the function does not process the key.)
```java
map.merge(word, 1L, (existingValue, newValue) -> existingValue + newValue);
```
or, more simply,
```java
map.merge(word, 1L, Long::sum);
```
It doesn’t get more concise than that.
> Caution: When you use compute or merge, keep in mind that the function that you supply should not do a lot of work. While that function runs, some other updates to the map may be blocked. Of course, that function should also not update other parts of the map.

### 14.7.3 Bulk Operations on Concurrent Hash Maps
Java SE 8 provides bulk operations on concurrent hash maps that can safely execute even while other threads operate on the map. The bulk operations traverse the map and operate on the elements they find as they go along. No effort is made to freeze a snapshot of the map in time. Unless you happen to know that the map is not being modified while a bulk operations runs, you should treat its result as an approximation of the map's state.\
There are three kinds of operations:
- `search` applies a function to each key and/or value, until the function yields a non-null result. Then the search terminates and the function's result is returned.
- `reduce` combines all keys and/or values, using a provided accumulation function.
- `forEach` applies a function to all keys and/or values.

Each operation has four versions:
- operationKeys: operates on keys.
- operationValues: operates on values.
- operation: operates on keys and values.
- operationEntries: operates on `Map.Entry` objects.

With each of the operations, you need to specify a parallelism threshold. If the map contains more elements than the threshold, the bulk operation is parallelized. If you want the bulk operation to run in a single thread, use a threshold of `Long.MAX_VALUE`. If you want the maximum number of threads to be made available for the bulk operation, use a threshold of 1.

### 14.7.4 Concurrent Set Views
Suppose you want a large, thread-safe set instead of a map. There is no `ConcurrentHashSet` class, and you know better than trying to create your own. Of course, you can use a `ConcurrentHashMap` with bogus values, but then you get a map, not a set, and you can't apply operations of the `Set` interface.\
The static `newKeySet` method yields a `Set<K>` that is actually a wrapper around a `ConcurrentHashMap<K, Boolean>`. (All map values are Boolean.TRUE, but you don't actually care since you just use it as a set.)
```java
Set<String> words = ConcurrentHashMap.<String>newKeySet();
```
Of course, if you have an existing map, the `keySet` method yields the set of keys. That set is mutable. If you remove the set's elements, the keys (and their values) are removed from the map. But it doesn't make sense to add elements to the key set, because there would be no corresponding values to add. Java SE 8 adds a second `keySet` method to `ConcurrentHashMap`, with a default value, to be used when adding elements to the set.
```java
Set<String> words = map.keySet(1L);
words.add("Java");
```
If "Java" wasn't already present in words , it now has a value of one.

### 14.7.5 Copy on Write Arrays
The `CopyOnWriteArrayList` and `CopyOnWriteArraySet` are thread-safe collections in which all mutators make a copy of the underlying array. This arrangement is useful if the threads that iterate over the collection greatly outnumber the threads that mutate it. When you construct an iterator, it contains a reference to the current array. If the array is later mutated, the iterator still has the old array, but the collection's array is replaced. As a consequence, the older iterator has a consistent (but potentially outdated) view that it can access without any synchronization expense.

### 14.7.6 Parallel Array Algorithms
As of Java SE 8, the Arrays class has a number of parallelized operations. The static `Arrays.parallelSort` method can sort an array of primitive values or objects.\
The `parallelSetAll` method fills an array with values that are computed from a function. The function receives the element index and computes the value at that location.\
Finally, there is a `parallelPrefix` method that replaces each array element with the accumulation of the prefix for a given associative operations. Huh? Here is an example. Consider the array `[1, 2, 3, 4, . . .]` and the × operation. After executing `Arrays.parallelPrefix(values, (x, y) -> x * y)`, the array contains `[1, 1 × 2, 1 × 2 × 3, 1 × 2 × 3 × 4, . . .]`.
> Note: At first glance, it seems a bit odd that these methods have parallel in their name, since the user should't care how the sorting happens. However, the API designers wanted to make it clear that the sorting is parallelized. That way, users are on notice to avoid comparators with side effects.

### 14.7.6 Older Thread-Safe Collections
Ever since the initial release of Java, the `Vector` and `Hashtable` classes provided thread-safe implementations of a dynamic array and a hash table. These classes are now considered obsolete, having been replaced by the `ArrayList` and `HashMap` classes. Those classes are not thread safe. Instead, a different mechanism is supplied in the collections library. Any collection class can be made thread safe by means of a synchronization wrapper:
```java
List<E> synchArrayList = Collections.synchronizedList(new ArrayList<E>());
Map<K, V> synchHashMap = Collections.synchronizedMap(new HashMap<K, V>());
```
The methods of the resulting collections are protected by a lock, providing thread safe access.\
You should make sure that no thread accesses the data structure through the original unsynchronized methods. The easiest way to ensure this is not to save any reference to the original object. Simply construct a collection and immediately pass it to the wrapper, as we did in our examples.\
You still need to use "client-side" locking if you want to iterate over the collection while another thread has the opportunity to mutate it.\
You are usually better off using the collections defined in the `java.util.concurrent` package instead of the synchronization wrappers. In particular, the `ConcurrentHashMap` map has been carefully implemented so that multiple threads can access it without blocking each other, provided they access different buckets. One exception is an array list that is frequently mutated. In that case, a synchronized `ArrayList` can outperform a `CopyOnWriteArrayList`.

## 14.8 Callable and Future
A Runnable encapsulates a task that runs asynchronously; you can think of it as an asynchronous method with no parameters and no return value. A Callable is similar to a Runnable, but it returns a value. The Callable interface is a parameterized type, with a single method call.
```java
public interface Callable<V> {
    V call() throws Exception;
}
```
A Future holds the result of an asynchronous computation. You can start a computation, give someone the Future object, and forget about it. The owner of the Future object can obtain the result when it is ready. The Future interface has the following methods:
```java
public interface Future<V> {
    V get() throws InterruptedException, ExecutionException;
    V get(long timeout, TimeUnit unit) throws InterruptedException, TimeoutException, ExecutionException;
    void cancel(boolean mayInterrupt);
    boolean isCancelled();
    boolean isDone();
}
```
A call to the first get method blocks until the computation is finished. The second method throws a `TimeoutException` if the call timed out before the computation finished. If the thread running the computation is interrupted, both methods throw an `InterruptedException`. If the computation has already finished, get returns immediately.
 The `isDone` method returns false if the computation is still in progress, true if it is finished.\
You can cancel the computation with the cancel method. If the computation has not yet started, it is canceled and will never start. If the computation is currently in progress, it is interrupted if the `mayInterrupt` parameter is true.\
The `FutureTask` wrapper is a convenient mechanism for turning a Callable into both a Future and a Runnable--it implements both interfaces. For example:
```java
Callable<Integer> myComputation = . . .;
FutureTask<Integer> task = new FutureTask<Integer>(myComputation);
Thread t = new Thread(task); // it's a Runnable
t.start();
// . . .
Integer result = task.get(); // it's a Future
```

## 14.9 Executors
Constructing a new thread is somewhat expensive because it involves interaction with the operating system. If your program creates a large number of short-lived threads, it should use a thread pool instead. A thread pool contains a number idle threads that are ready to run. You give a Runnable to the pool, and one of the threads calls the run method. When the run method exits, the thread doesn't die but stays around to serve the next request.\
Another reason to use a thread pool is to throttle the number of concurrent threads. Creating a huge number of threads can greatly degrade performance and even crash the virtual machine. If you have an algorithm that creates lots of threads, you should use a "fixed" thread pool that bounds the total number of concurrent threads.\
The `Executors` class has a number of static factory methods for constructing thread pools; see the following table for a summary.

| Method                             | Description                                                                                                                |
| -                                  | -                                                                                                                          |
| `newCachedThreadPool`              | New threads are created as needed; idle threads are kept for 60 seconds.                                                   |
| `newFixedThreadPool`               | The pool contains a fixed set of threads; idle threads are kept indefinitely.                                              |
| `newSingleThreadExecutor`          | A “pool” with a single thread that executes the submitted tasks sequentially (similar to the Swing event dispatch thread). |
| `newScheduledThreadPool`           | A fixed-thread pool for scheduled execution; a replacement for `java.util.Timer`.                                          |
| `newSingleThreadScheduledExecutor` | A single-thread “pool” for scheduled execution.                                                                            |
#### 14.9.1 Thread Pools
You can submit a Runnable or Callable to an ExecutorService with one of the following methods:
```java
Future<?> submit(Runnable task)
Future<T> submit(Runnable task, T result)
Future<T> submit(Callable<T> task)
```
The pool will run the submitted task at its earliest convenience. When you call submit, you get back a Future object that you can use to query the state of the task. The first submit method returns an odd-looking Future<?> . You can use such an object to call `isDone`, cancel, or `isCancelled`, but the get method simply returns null upon completion.\
The second version of submit also submits a Runnable, and the get method of the Future returns the given result object upon completion.\
The third version submits a Callable, and the returned Future gets the result of the computation when it is ready.\
When you are done with a thread pool, call `shutdown`. This method initiates the shutdown sequence for the pool. An executor that is shut down accepts no new tasks. When all tasks are finished, the threads in the pool die. Alternatively, you can call `shutdownNow`. The pool then cancels all tasks that have not yet begun and attempts to interrupt the running threads.\
Here, in summary, is what you do to use a thread pool:
1. Call the static `newCachedThreadPool` or `newFixedThreadPool` method of the Executors class.
2. Call submit to submit Runnable or Callable objects.
3. If you want to be able to cancel a task, or if you submit Callable objects, hang on to the returned Future objects.
4. Call shutdown when you no longer want to submit any tasks.

### 14.9.2 Scheduled Execution
The `ScheduledExecutorService` interface has methods for scheduled or repeated execution of tasks. It is a generalization of `java.util.Timer` that allows for thread pooling. The`newScheduledThreadPool` and `newSingleThreadScheduledExecutor` methods of the Executors class return objects that implement the `ScheduledExecutorService` interface.\
You can schedule a Runnable or Callable to run once, after an initial delay. You can also schedule a Runnable to run periodically.

### 14.9.3 Controlling Groups
You have seen how to use an executor service as a thread pool to increase the efficiency of task execution. Sometimes, an executor is used for a more tactical reason, simply to control a group of related tasks. For example, you can cancel all tasks in an executor with the `shutdownNow` method.\
The `invokeAny` method submits all objects in a collection of Callable objects and returns the result of a completed task. You don’t know which task that is—presumably, it is the one that finished most quickly. Use this method for a search problem in which you are willing to accept any solution. For example, suppose that you need to factor a large integer—a computation that is required for breaking the RSA cipher. You could submit a number of tasks, each attempting a factorization with numbers in a different range. As soon as one of these tasks has an answer, your
computation can stop.\
The `invokeAll` method submits all objects in a collection of Callable objects, blocks until all of them complete, and returns a list of Future objects that represent the solutions to all tasks.\
A disadvantage of this approach is that you may wait needlessly if the first task happens to take a long time. It would make more sense to obtain the results in the order in which they are available. This can be arranged with the `ExecutorCompletionService`.\
Start with an executor, obtained in the usual way. Then construct an `ExecutorCompletionService`. Submit tasks to the completion service. The service manages a blocking queue of Future objects, containing the results of the submitted tasks as they become available.

### 14.9.4 The Fork-Join Framework
Some applications use a large number of threads that are mostly idle. An example would be a web server that uses one thread per connection. Other applications use one thread per processor core, in order to carry out computationally intensive tasks, such as image or vidwo processing. The fork-join framework, which appeared in Java SE 7, is designed to support the latter. Suppose you have a processing task that naturally decomposes into subtasks, like this:
```java
if (problemSize < threshold) {
    // solve problem directly
} else {
    // break problem into subproblems
    // recursively solve each subproblem
    // combine the results
}
```
Here, we will discuss a simpler example. Suppose we want to count how many elements of an array fulfill a particular property. We cut the array in half, compute the counts of each half, and add them up.\
To put the recursive computation in a form that is usable by the framework, supply a class that extends `RecursiveTask<T>` (if the computation produces a result of type T) or `RecursiveAction` (if it doesn’t produce a result). Override the compute method to generate and invoke subtasks, and to combine their results.
```java
class Counter extends RecursiveTask<Integer> {
    // . . .
    protected Integer compute() {
        if (to - from < THRESHOLD) {
            // solve problem directly
        } else {
            int mid = (from + to) / 2;
            Counter first = new Counter(values, from, mid, filter);
            Counter second = new Counter(values, mid, to, filter);
            invokeAll(first, second);
            return first.join() + second.join();
        }
    }
}
```
Behind the scenes, the fork-join framework uses an effective heuristic for balancing the workload among available threads, called work stealing. Each worker thread has a deque (double-ended queue) for tasks. A worker thread pushes subtasks onto the head of its own deque. (Only one thread accesses the head, so no locking is required.) When a worker thread is idle, it "steals" a task from the tail of another deque. Since large subtasks are at the tail, such stealing is rare.

### 14.9.5 Completable Futures
The traditional approach for dealing with nonblocking calls is to use event handlers, where the programmer registers a handler for the action that should occur after a task completes. Of course, if the next action is also asynchronous, the next action after that is in a different event handler. Even though the programmer thinks in terms of "first do step 1, then step 2, then step 3," the program logic becomes dispersed in different handlers. It gets worse when one has to add error handling.\
The `CompletableFuture` class of Java SE 8 provides an alternative approach. Unlike event handlers, completable futures can be composed.\
For example, suppose we want to extract all links from a web page in order to build a web crawler. Let’s say we have a method
```java
public void CompletableFuture<String> readPage(URL url)
```
that yields the text of a web page when it becomes available. If the method
```java
public static List<URL> getLinks(String page)
```
yields the URLs in an HTML page, you can schedule it to be called when the page is available:
```java
CompletableFuture<String> contents = readPage(url);
CompletableFuture<List<URL>> links = contents.thenApply(Parser::getLinks);
```
The `thenApply` method doesn’t block either. It returns another future. When the first future has completed, its result is fed to the `getLinks` method, and the return value of that method becomes the final result.\
With completable futures, you just specify what you want to have done and in which order. It won't all happen right away, of course, but what is important is that all the code is in one place.

| Method         | Parameter                   | Description                                                        |
| -              | -                           | -                                                                  |
| `thenApply`    | `T -> U`                    | Apply a function to the result.                                    |
| `thenCompose`  | `T -> CompletableFuture<U>` | Invoke the function on the result and execute the returned future. |
| `handle`       | `(T, Throwable) -> U`       | Process the result or error.                                       |
| `thenAccept`   | `T -> void`                 | Like `thenApply` , but with void result.                           |
| `whenComplete` | `(T, Throwable) -> void`    | Like `handle` , but with void result.                              |
| `thenRun`      | `Runnable`                  | Execute the Runnable with void result.                             |

Now let us turn to methods that combine multiple futures (see the following table).\
The first three methods run a `CompletableFuture<T>` and a `CompletableFuture<U>` action in parallel and combine the results.\
The next three methods run two `CompletableFuture<T>` actions in parallel. As soon as one of them finishes, its result is passed on, and the other result is ignored.\
Finally, the static `allOf` and `anyOf` methods take a variable number of completable futures and yield a `CompletableFuture<Void>` that completes when all of them, or any one of them, completes. No results are propagated.

| Method           | Parameter                              | Description                                                                      |
| -                | -                                      |
| `thenCombine`    | `CompletableFuture<U>, (T, U) -> V`    | Execute both and combine the results with the given function.                    |
| `thenAcceptBoth` | `CompletableFuture<U>, (T, U) -> void` | Like `thenCombine` , but with void result.                                       |
| `runAfterBoth`   | `CompletableFuture<?>, Runnable`       | Execute the runnable after both complete.                                        |
| `applyToEither`  | `CompletableFuture<T>, T -> V`         | When a result is available from one or the other, pass it to the given function. |
| `acceptEither`   | `CompletableFuture<T>, T -> void`      | Like `applyToEither` , but with void result.                                     |
| `runAfterEither` | `CompletableFuture<?>, Runnable`       | Execute the runnable after one or the other completes.                           |
| `static allOf`   | `CompletableFuture<?>...`              | Complete with void result after all given futures complete.                      |
| `static anyOf`   | `CompletableFuture<?>...`              | Complete with void result after any of the given futures completes.              |

> Note: Technically speaking, the methods in this section accept parameters of type `CompletionStage` , not `CompletableFuture`. That is an interface with almost forty abstract methods, implemented only by `CompletableFuture` .The interface is provided so that third-party frameworks can implement it.

## 14.10 Synchronizers
The `java.util.concurrent` package contains several classes that help manage a set of collaborating threads—see the following Table. These mechanisms have "canned functionality" for common rendezvous patterns between threads. If you have a set of collaborating threads that follow one of these behavior patterns, you should simply reuse the appropriate library class instead of trying to come up with a handcrafted collection of locks and conditions.

| Class              | What It Does                                                                                                                                  | Notes                                                                                                                                                           |
| -                  | -                                                                                                                                             | -                                                                                                                                                               |
| `CyclicBarrier`    | Allows a set of threads to wait until a predefined count of them has reached a common barrier, and then optionally executes a barrier action. | Use when a number of threads need to complete before their results can be used. The barrier can be reused after the waiting threads have been released.         |
| `Phaser`           | Like a cyclic barrier, but with a mutable party count.                                                                                        | Introduced in Java SE 7.                                                                                                                                        |
| `CountDownLatch`   | Allows a set of threads to wait until a count has been decremented to 0.                                                                      | Use when one or more threads need to wait until a specified number of events have occurred.                                                                     |
| `Exchanger`        | Allows two threads to exchange objects when both are ready for the exchange.                                                                  | Use when two threads work on two instances of the same data structure, with the first thread filling one instance and the second thread emptying the other.     |
| `Semaphore`        | Allows a set of threads to wait until permits are available for proceeding.                                                                   | Use to restrict the total number of threads that can access a resource. If the permit count is one, use to block threads until another thread gives permission. |
| `SynchronousQueue` | Allows a thread to hand off an object to another thread.                                                                                      | Use to send an object from one thread to another when both are ready, without explicit synchronization.                                                         |

### 14.10.1 Semaphores
Conceptually, a semaphore manages a number of permits. The number is supplied in the constructor. To proceed past the semaphore, a thread requests a permit by calling acquire. (There are no actual permit objects. The semaphore simply keeps a count.) Since only a fixed number of permits is available, a semaphore limits the number of threads that are allowed to pass. Other threads may issue permits by calling release. Moreover, a permit doesn't have to be released by the thread that acquires it. Any thread can release any number of permits, potentially increasing the number of permits beyond the initial count.\
Semaphores were invented by Edsger Dijkstra in 1968, for use as a synchronization primitive. Dijkstra showed that semaphores can be efficiently implemented and that they are powerful enough to solve many common thread synchronization problems. In just about any operating systems textbook, you will find implementations of bounded queues using semaphores.\
Of course, application programmers shouldn't reinvent bounded queues. Usually semaphores do not map directly to common application situations.

### 14.10.2 Countdown Latches
A `CountDownLatch` lets a set of threads wait until a count has reached zero. The countdown latch is one-time only. Once the count has reached 0, you cannot increment it again.\
A useful special case is a latch with a count of 1. This implements a one-time gate. Threads are held at the gate until another thread sets the count to 0.\
Imagine, for example, a set of threads that need some initial data to do their work. The worker threads are started and wait at the gate. Another thread prepares the data. When it is ready, it calls `countDown`, and all worker threads proceed.\
You can then use a second latch to check when all worker threads are done. Initialize the lath with the number of threads. Each worker thread counts down that latch just before it terminates. Another thread that harvests the work results waits on the latch, and proceeds as soon as all workers have terminated.

### 14.10.3 Barriers
The `CyclicBarrier` class implements a rendezvous called a barrier. Consider a number of threads that are working on parts of a computation. When all parts are ready, the results need to be combined. When a thread is done with its part, we let it run against the barrier. Once all threads have reached the barrier, the barrier gives way and the threads can proceed.\
Here are the details. First, construct a barrier, giving the number of participating threads:
```java
CyclicBarrier barrier = new CyclicBarrier(nthreads);
```
Each thread does some work and calls await on the barrier upon completion:
```java
public void run() {
    doWork();
    barrier.await();
    // . . .
}
```
The await method takes an optional timeout parameter:
```java
barrier.await(100, TimeUnit.MILLISECONDS);
```
If any of the threads waiting for the barrier leaves the barrier, then the barrier breaks. (A thread can leave because it called await with a timeout of because it was interrupted.) In that case, the `await` method for all other threads throws a `BrokenBarrierException`. Threads that are already waiting have their await call terminated immediately.\
You can supply an optional barrier action that is executed when all threads have reached the barrier:
```java
CyclicBarrier barrier = new CyclicBarrier(nthreads, barrierAction);
```
The barrier is called cyclic because it can be reused after all waiting threads have been released. In this regard, it differs from a `CountdownLatch` which can only be used once.\
The `Phaser` class adds more flexibility, allowing you to vary the number of participating threads between phases.

### 14.10.4 Exchangers
An exchanger is used when two threads are working on two instances of the same data buffer. Typically, one thread fills the buffer, and the other consumes its contents. When both are down, they exchange their buffers.

### 14.10.5 Synchronous Queues
A synchronous queue is a mechanism that pairs up producer and consumer threads. When a thread calls put on a `SynchronousQueue`, it blocks until another threads calls take, and vice versa. Unlike the case with exchanger, data are only transferred in one direction, from the producer to the consumer.\
Even though the `SynchronousQueue` class implements the `BlockingQueue` interface, it is not conceptually a queue. It does not contain any elements--its size method always returns 0.

## 14.11 Threads and Swing
skipped
