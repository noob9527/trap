# Thread

### InterruptedException
When the interrupt method is called on a thread, the interrupted status of the thread is set. This is a boolean flag that is present in every thread. Each thread should occasionally check whether it has been interrupted. However, if a thread is blocked, it cannot check the interrupted status. This is where the InterruptedException comes in. When the interrupt method is called on a thread that blocks on a call such as sleep or wait, the blocking call is terminated by an InterruptedException.\
Usually, a thread will clear the interrupted status after throwing an InterruptedException. so, in order to test the flag correctly, you may want to reset the status after catching an InterruptedException as following.
```java
try {
    Thread.sleep(1L);
} catch (InterruptedException e) {
    Thread.currentThread().interrupt();
}
```

### UncaughtExceptionHandler
A thread can be terminated by an uncaught exception, however, there is no catch clause to which the exception can be propagated. As following code shows, you cannot catch an exception thrown by another thread directly.
```java
Runnable runnable = () -> {
    throw new RuntimeException("gocha!");
};

Thread thread = new Thread(runnable);

try {
    thread.start();
    thread.join();
} catch (RuntimeException e) {
    throw new RuntimeException("this line won't be executed");
}
```
Instead, you need to set up an UncaughtExceptionHandler.
```java
Runnable runnable = () -> {
    throw new RuntimeException("gocha!");
};

Thread thread = new Thread(runnable);
thread.setUncaughtExceptionHandler((thread, exception) -> {
    assertThat(exception).hasMessage("gocha!");
});

thread.start();
thread.join();
```
