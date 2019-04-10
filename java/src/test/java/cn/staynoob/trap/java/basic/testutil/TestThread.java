package cn.staynoob.trap.java.basic.testutil;

/**
 * if any exceptions is thrown by a new thread
 * throw the same exception in main thread so that the junit
 * will be able to capture them
 */
public class TestThread {

    private final Thread thread;
    private Throwable throwable;

    public TestThread(final Runnable runnable) {
        thread = new Thread(runnable);
        thread.setUncaughtExceptionHandler((t, e) -> throwable = e);
    }

    public void start() {
        thread.start();
    }

    /**
     * wait for the thread to terminate
     * if any uncaught exception occurs, throw it to main thread
     */
    public void join() {
        try {
            thread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        if (throwable == null) return;

        if (throwable instanceof RuntimeException)
            throw (RuntimeException) throwable;
        else if (throwable instanceof Error)
            throw (Error) throwable;
        else
            throw new RuntimeException("An exception is thrown in test thread", throwable);
    }

    public Thread getThread() {
        return thread;
    }

    public Thread.State getState() {
        return thread.getState();
    }
}
