# Chapter11 Threads

## 1. Introduction
skipped

## 2. Thread Concepts
A typical UNIX process can be thought of as having a single thread of control: each process is doing only one thing at a time. With multiple threads of control, we can design our programs to do more than one thing at a time within a single process, with each thread handling a separate task. This approach can have several benefits.
- We can simplify code that deals with asynchronous events by assigning a separate thread to handle each event type. Each thread can then handle its event using a synchronous programming model. A synchronous programming model is much simpler than an asynchronous one.
- Multiple processes have to use complex mechanisms provided by the operating system to share memory and file descriptors, as we will see in following chapters. Threads, in contrast, automatically have access to the same memory address space and file descriptors.
- Some problems can be partitioned so that overall program throughput can be improved. A single-threaded process with multiple tasks to perform implicitly serializes those tasks, because there is only one thread of control. With multiple threads of control, the processing of independent tasks can be interleaved by assigning a separate thread per task. Two tasks can be interleaved only if they don't depend on the processing performed by each other.
- Similarly, interactive programs can realize improved response time by using multiple threads to separate the portions of the program that deal with user input and output from the other parts of the program.

Some people associate multithreaded programming with multiprocessor or multicore systems. The benefits of a multithreaded programming model can be realized even if your program is running on a uniprocessor. A program can be simplified using threads regardless of the number of processors, because the number of processors doesn't affect the program structure. Furthermore, as long as your program has to block when serializing tasks, you can still see improvements in response time and throughput when running on a uniprocessor, because some threads might be able to run while others are blocked.\
A thread consists of the information necessary to represent an execution context within a process. This includes a thread ID that identifies the thread within a process, a set of resister values, a stack, a scheduling priority and policy, a signal mask, an `errno` variable, and thread-specific data. Everything within a process is sharable among the threads in a process, including the text of the executable program, the program's global and heap memory, the stacks, and the file descriptors.

## 3. Thread Identification
Just as every process has a process ID, every thread has a thread ID, Unlike the process ID, which is unique in the system, the thread ID has significance only within the context of the process to which it belongs.\
Recall that a process ID, represented by the `pid_t` data type, is a non-negative integer. A thread ID is represented by the `pthread_t` data type, so portable implementations can't treat them as integers. Therefore, a function must be used to compare two thread IDs.
```c
#include <pthread.h>

int pthread_equal(pthread_t tid1, pthread_t tid2);

// Returns: nonzero if equal, 0 otherwise
```
A thread can obtain its own thread ID by calling the `pthread_self` function.
```c
#include <pthread.h>

pthread_t pthread_self(void);

// Returns: the thread ID of the calling thread
```

## 4. Thread Creation
With pthreads, when a program runs, it also starts out as a single process with a single thread of control. AS the program runs, its behavior should be indistinguishable from the traditional process, until it creates more threads of control.\
Additional threads can be created by calling the `pthread_create` function.
```c
#include <pthread.h>

int pthread_create(phtread_t *restrict tidp, const pthread_attr_t *restrict attr, void *(*start_rtn)(void *), void *restrict arg);

// Returns: 0 if OK, error number on failure
```
When a thread is created, there is no guarantee which will run first: the newly created thread or the calling thread. The newly created thread has access to the process address space and inherits the calling thread's floating-point environment and signal mask; however, the set of pending signals for the thread is cleared.\
Note that the pthread functions usually return an error code when they fail. They don't set `errno` like the other POSIX functions. The per-thread copy of `errno` is provided only for compatibility with existing functions that use it. With threads, it is cleaner to return the error code from the function, thereby restricting the scope of the error to the function that caused it, instead of relying on some global state that is changed as a side effect of the function.

## 5. Thread Termination
If any thread within a process calls `exit`, `_Exit`, or `_exit`, then the entire process terminates. Similarly, when the default action is to terminate the process, a signal sent to a thread will terminate the entire process.\
A single thread can exit in three ways, thereby stopping its flow of control, without terminating the entire process.
1. The thread can simply return from the start routine. The return value is the thread's exit code.
2. The thread can be canceled by another thread in the same process.
3. The thread can call `pthread_exit`

```c
#include <pthread.h>

void pthread_exit(void *rval_ptr);
```
The `rval_ptr` argument is a typeless pointer, similar to the single argument passed to the start routine. This pointer is available to other threads in the process by calling the `pthread_join` function.
```c
#include <pthread.h>

void pthread_join(pthread_t thread, void **rval_ptr);

// Returns: 0 if OK, error number on failure
```
The calling thread will block until the specified thread calls `pthread_exit`, returns from its start routine, or is canceled. If the thread simply returned from its start routine, `rval_ptr` will contain the return code. If the thread was canceled, the memory location specified by `rval_ptr` is set to `PTHREAD_CANCELED`.\
By calling `pthread_join`, we automatically place the thread with which we're joining in the detached state so that its resources can be recovered. If the thread was already in the detached state, `pthread_join` can fail, returning `EINVAL`, although this behavior is implementation-specific.\
If we're not interested in a thread's return value, we can set `rval_ptr` to NULL. In this case, calling `pthread_join` allows us to wait for the specified thread, but does not retrieve the thread's termination status.\
One thread can request that another in the same process be canceled by calling the `pthread_cancel` function
```c
#include <pthread.h>

void pthread_cancel(pthread_t tid);

// Returns: 0 if OK, error number on failure
```
In the default circumstances, `pthread_cancel` will cause the thread specified by `tid` to behave as if it had called `pthread_exit` with an argument of `PTHREAD_CANCELED`. However, a thread can elect to ignore or otherwise control how it is canceled. Note that `pthread_cancel` doesn't wait for the thread to terminate; it merely makes the request.\
A thread can arrange for functions to be called when it exits, similar to the way that the `atexit` function can be used by a process to arrange that functions are to be called when the process exits. The functions are known as thread cleanup handlers. More than one cleanup handler can be established for a thread. The handlers are recorded in a stack, which means that they are executed in the reverse order from that with which they were registered.
```c
#include <pthread.h>

void pthread_cleanup_push(void (*rtn)(void *), void *arg);
void pthread_cleanup_pop(int execute);
```
The `pthread_cleanup_push` function schedules the cleanup function, `rtn`, to be called with the single argument, `arg`, when the thread performs one of the following actions:
- Makes a call to `pthread_exit`
- Responds to a cancellation request
- Makes a call to `pthread_cleanup_pop` with a nonzero execute argument

If the `execute` argument is set to zero, the cleanup function is not called. In either case, `pthread_cleanup_pop` removes the cleanup handler established by the last call to `pthread_cleanup_push`.\
By now, you should begin to see similarities between the thread functions and the process functions.
| Process primitive | Thread primitive       | Description                                                 |
| ----------------- | ---------------------- | ----------------------------------------------------------- |
| `fork`            | `pthread_create`       | create a new flow of control                                |
| `exit`            | `pthread_exit`         | exit from an existing flow of control                       |
| `waitpid`         | `pthread_join`         | get exit status from flow of control                        |
| `atexit`          | `pthread_cleanup_push` | register function to be called at exit from flow of control |
| `getpid`          | `pthread_self`         | get ID for flow of control                                  |
| `abort`           | `pthread_cancel`       | request abnormal termination of flow of control             |

By default, a thread's termination status is retained until we call `pthread_join` for the thread. A thread's underlying storage can be reclaimed immediately on termination if the thread has been detached. After a thread is detached, we can't use the `pthread_join` function to wait for its termination states, because calling `pthread_join` for a detached thread results in undefined behavior. We can detach a thread by calling `pthread_detach`.
```c
#include <pthread.h>

void pthread_detach(pthread_t tid);

// Returns: 0 if OK, error number on failure
```

## 6. Thread Synchronization
skipped

### Mutexes(Mutual-exclusion)
We can protect our data and ensure access by only one thread at a time by using the pthreads mutual-exclusion interfaces. A mutex is basically a lock that we set before accessing a shared resource and release when we're done. While it is set, any other thread that tries to set it will block until we release it. If more than one thread is blocked when we unlock the mutex, then all threads blocked on the lock will be made runnable, and the first one to run will be able to set the lock. The others will see that the mutex is still locked and go back to waiting for it to become available again. In this way, only one thread will proceed at a time.\
The mutual-exclusion mechanism works only if we design our threads to follow the same data-access rules. The operating system doesn't serialize access to data for us. If we allow one thread to access a shared resource without first acquiring a lock, then inconsistencies can occur even though the rest of our threads do acquire the lock before attempting to access the shared resource.\
A mutex variable is represented by the `pthread_mutex_t` data type. Before we can use a mutex variable, we must first initialize it by either setting it to the constant `PTHREAD_MUTEX_INITIALIZER`(for statically allocated mutexes only) or calling `pthread_mutex_init`. If we allocation the mutex dynamically(by calling `malloc`, for example), then we need to call `pthread_mutex_destroy` before freeing the memory.
```c
#include <pthread.h>

int pthread_mutex_init(pthread_mutex_t *restrict mutex, const pthred_mutexattr_t *restrict attr);
int pthread_mutex_destroy(pthread_mutex_t *restrict mutex);

// Both return: 0 if OK, error number on failure
```
To initialize a mutex with the default attributes, we set `attr` to NULL.\
To lock a mutex, we call `phtead_mutex_lock`. If the mutex is already locked, the calling thread will block until the mutex is unlocked. To unlock a mutex, we call `pthread_mutex_unlock`.
```c
#include <pthread.h>

int pthread_mutex_lock(pthread_mutex_t *restrict mutex);
int pthread_mutex_trylock(pthread_mutex_t *restrict mutex);
int pthread_mutex_unlock(pthread_mutex_t *restrict mutex);

// All return: 0 if OK, error number on failure
```
If a thread can't afford to block, it can use `pthread_mutex_trylock` to lock the mutex conditionally. If the mutex is locked at the time `pthread_mutex_trylock` is called, then `pthread_mutex_trylock` will fail, returning `EBUSY` without locking the mutex.

### Deadlock Avoidance
A thread will deadlock itself if it tries to lock the same mutex twice, but there are less obvious ways to create deadlocks with mutexes. For example, when we use more than one mutex in our programs, a deadlock can occur if we allow one thread to hold a mutex and block while trying to lock a second mutex at the same time that another thread holding the second mutex tries to lock the first mutex. Neither thread can proceed, because each needs a resource that is held by the other, so we have a deadlock.

### `pthread_mutex_timedlock` Function
One additional mutex primitive allows us to bound the time that a thread blocks when a mutex it is trying to acquire is already locked. The `pthread_mutex_timedlock` function is equivalent to `pthread_mutex_lock`, but if the timeout value is reached, `pthread_mutex_timedlock` will return the error code `ETIMEDOUT` without locking the mutex.
```c
#include <pthread.h>
#include <time.h>

int pthread_mutex_timedlock(pthread_mutex_t *restrict mutex, const struct timespec *restrict tsptr);

// Returns: 0 if OK, error number on failure
```

### Read-Writer Locks
Reader-writer locks are similar to mutexes, except that they allow for higher degrees of parallelism. With a mutex, the state is either locked or unlocked, and only one thread can lock it at a time. Three states are possible with a reader-writer lock: locked in read mode, locked in write mode, and unlocked. Only one thread at a time can hold a reader-writer lock in write mode, but multiple threads can hold a reader-writer lock in read mode at the same time.\
When a reader-writer lock is writer locked, all threads attempting to lock it block until it is unlocked. When a reader-writer lock is read locked, all threads attempting to lock it in read mode are given access, but any threads attempting to lock it in writer mode block until all the threads have released their read locks. Although implementations vary, reader-writer locks usually block additional readers if a lock i already held in read mode and a thread is blocked trying to acquire the lock in writer mode. This prevents a constant stream of readers from starving waiting writers.\
Reader-writer locks are well suited for situations in which data structures are read more often than they are modified. When a reader-writer lock is held in write mode, the data structure it protects can be modified safely, since only one thread at a time can hold the lock in write mode. When the reader-writer lock is held in read mode, the data structure it protects  can be read by multiple threads, as long as the threads first acquire the lock in read mode.\
Reader-writer locks are also called shared-exclusive locks. When a reader-writer lock is read locked, it is said to be locked in shared mode. When it is write locked, it is said to be locked in exclusive mode.
```c
#include <pthread.h>

int pthread_rwlock_init(pthread_rwlock_t *restrict rwlock, const pthread_rwlockattr_t *restrict attr);
int pthread_rwlock_destroy(pthread_rwlock_t *rwlock);

int pthread_rwlock_rdlock(pthread_rwlock_t *rwlock);
int pthread_rwlock_wrlock(pthread_rwlock_t *rwlock);
int pthread_rwlock_unlock(pthread_rwlock_t *rwlock);

int pthread_rwlock_tryrdlock(pthread_rwlock_t *rwlock);
int pthread_rwlock_trywrlock(pthread_rwlock_t *rwlock);

// All return: 0 if OK, error number on failure
```

### Read-Writer Locking with Timeouts
Just as with mutexes, the Single UNIX Specification provides functions to lock reader-writer locks with a timeout to give applications a way to avoid blocking indefinitely while trying to acquire a reader-writer lock.
```c
#include <pthread.h>
#include <time.h>

int pthread_rwlock_timedrdlock(pthread_rwlock_t *restrict rwlock, const struct timespec *restrict tsptr);
int pthread_rwlock_timedwrlock(pthread_rwlock_t *restrict rwlock, const struct timespec *restrict tsptr);

// Both return: 0 if OK, error number on failure
```

### Condition Variables
Condition variables are another synchronization mechanism available to threads. These synchronization objects provide a place for threads to rendezvous. When used with mutexes, condition variables allow threads to wait in a race-free way for arbitrary conditions to occur.\
The condition itself is protected by a mutex. A thread must first lock the mutex to change the condition state. Other threads will not notice the change until they acquire the mutex, because the mutex must be locked to be able to evaluate the condition.
```c
#include <pthread.h>
#include <time.h>

int pthread_cond_init(pthread_cond_t *restrict cond, const pthread_condattr_t *restrict attr);
int pthread_cond_destory(pthread_cond_t *cond);

int phtread_cond_wait(pthread_cond_t *restrict cond, pthread_mutex_t *restrict mutex);
int phtread_cond_timedwait(pthread_cond_t *restrict cond, pthread_mutex_t *restrict mutex, const struct timespec *restrict tsptr);

// Both return: 0 if OK, error number on failure
```
The mutex passed to `pthread_cond_wait` protects the condition. The caller passes it locked to the function, which then atomically places the calling thread on the list of threads waiting for the condition and unlocks the mutex. This closes the window between the time that the condition is checked and the time that the thread goes to sleep waiting for the condition to change, so that the thread doesn't miss a change in the condition. When `pthread_cond_wait` returns, the mutex is again locked.\
There are two functions to notify threads that a condition has been satisfied. The `pthread_cond_signal` function will wake up at least one thread waiting on a condition, whereas the `pthread_cond_broadcast` function will wake up all threads waiting on a condition.
```c
#include <pthread.h>

int pthread_cond_signal(pthread_cond_t *cond);
int pthread_cond_broadcast(pthread_cond_t *cond);

// Both return: 0 if OK, error number on failure
```
When we call `pthread_cond_signal` or `pthread_cond_broadcast`, we are said to be signaling the thread or condition. We have to be careful to signal the threads only after changing the state of the condition.

### Spin Locks
A spin lock is like a mutex, except that instead of blocking a process by sleeping, the process is blocked by busy-waiting (spinning) until the lock can be acquired. A spin lock could be used in situations where locks are held for short periods of times and threads don't want to incur the cost of being descheduled.\
Spin locks are often used as low-level primitives to implement other types of locks. Depending on the system architecture, they can be implemented efficiently using test-and-set instructions. Although efficient, they can lead to wasting CPU resources: while a thread is spinning and waiting for a lock to became available, the CPU can't do anything else. This is why spin locks should be held only for short periods of time.\
Spin locks are useful when used in a nonpreemptive kernel: besides providing a mutual exclusion mechanism, they block interrupts so an interrupt handler can't deadlock the system by trying to acquire a spin lock that is already locked(think of interrupts as another type of preemption). In these types of kernels, interrupt handlers can't sleep, so the only synchronization primitives they  can use are spin locks.\
However, at user level, spin locks are not as useful unless you are running in a real-time scheduling class that doesn't allow preemption. User-level threads running in a time-sharing scheduling class can be descheduled when their time quantum expires or when a thread with a higher scheduling priority becomes runnable. In these cases, if a thread is holding a spin lock, it will be put to sleep and other threads blocked on the lock will continue spinning longer than intended.\
Many mutex implementations are so efficient that the performance of applications using mutex locks is equivalent to their performance if they had used spin locks. In fact, some mutex implementations will spin for a limited amount of time trying to acquire the mutex, and only sleep when the spin count threshold is reached. These factors, combined with advances in modern processors that allow them to context switch at faster and faster rates, make spin locks useful only in limited circumstances.

### Barriers
Barriers are a synchronization mechanism that can be used to coordinate multiple threads working in parallel. A barrier allows each thread to wait until all cooperating threads have reached the same point, and then continue executing from there. We've already seen one form of barrier--the `pthread_join` function acts as a barrier to allow one thread to wait until another thread exits.\
Barrier objects are more general than this, however. They allow an arbitrary number of threads to wait until all of the threads have completed processing, but the threads don't have to exit. They can continue working after all threads have reached the barrier.

## 7. Summary
skipped
