# Chapter12 Thread Control

## 1. Introduction
skipped

## 2. Thread limits
The Single UNIX Specification defines several limits associated with the operation of threads, use of these limits is intended to promote application portability among different operating system implementations. As with other system limits, the thread limits can be queried using `sysconf`.

## 3. Thread Attributes
skipped

## 4. Synchronization Attributes
skipped
### Mutex Attributes
skipped
### Reader-Writer Lock Attributes
skipped
### Condition Variable Attributes
skipped
### Barrier Attributes
skipped

## 5. Reentrancy
Threads are similar to signal handlers when it comes to reentrance. In both cases, multiple threads of control can potentially call the same function at the same time.\
If a function can be safely called by multiple threads at the same time, we say that the function is thread-safe.\
If a function is reentrant with respect to multiple threads, we say that it is thread-safe. This doesn't tell us, however, whether the function is reentrant with respect to signal handlers. We say that a function that is safe to be reentered from an asynchronous signal handler is async-signal safe.\
POSIX.1 provides a way to manage FILE objects in a thread-safe way. You can use `flockfile` and `ftrylockfile` to obtain a lock associated with a given FILE object. This lock is recursive: you can acquire it again, while you already hold it, without deadlocking. Although the exact implementation of the lock is unspecified, all standard I/O routines that manipulate FILE objects are required to behave as if they call `flockfile` and `funlockfile` internally.
```c
#include <stdio.h>

int ftrylockfile(FILE *fp);

// Returns: 0 if OK, nonzero if lock can't be acquired

int flockfile(FILE *fp);
int funlockfile(FILE *fp);
```
Although the standard I/O routines might be implemented to be thread-safe from the perspective of their own internal data structures, it is  still useful to expose the locking to applications. This allows applications to compose multiple calls to standard I/O functions into atomic sequences. Of course, when dealing with multiple FILE objects, you need to beware of potential deadlocks and to order your locks carefully.\
If the standard I/O routines acquire their own locks, then we can run into serious performance degradation when doing character-at-a-time I/O. In this situation, we end up acquiring and releasing a lock for every character read or written. To avoid this overhead, unlocked versions of the character-based standard I/O routines are available.
```c
#include <stdio.h>

int getchar_unlocked(void);
int getc_unlocked(FILE *fp);

// Both return: the next character if OK, EOF on end of file or error

int putchar_unlocked(int c);
int putc_unlocked(int c, FILE *fp);

// Both return: c if OK, EOF on error
```
These four functions should not be called unless they are surrounded by calls to `flockfile` and `funlockfile`. Otherwise, unpredictable results can occur.\
Once you lock the FILE object, you can make multiple calls to these functions before releasing the lock. This amortizes the locking overhead across the amount of data read or written.

## 6. Thread-Specific Data
Thread-specific data, also known as thread-private data, is a mechanism for storing and finding data associated with a particular thread. The reason we call the data thread-specific, or thread-private, is that we'd like each thread to access its own separate copy of the data, without worrying about synchronizing access with other threads.\
Many people went to a lot of trouble designing a threads model that promotes sharing process data and attributes. So why would anyone want to promote interfaces that prevent sharing in this model? There are two reasons.\
First, sometimes we need to maintain data on a per-thread bases. Since there is no guarantee that thread IDs are small, sequential integers, we can't simply allocate an array of per-thread data and use the thread ID as the index. Even if we could depend on small,sequential thread IDs, we'd like a little extra protection so that on thread can't mess with another's data.\
The second reason for thread-private data is to provide a mechanism for adapting process-based interfaces to a multithreaded environment. An obvious example of this is `errno`. Older interfaces (before the advent of threads) defined `errno` as an integer that is accessible globally within the context of a process. System calls and library routines set `errno` as a side effect of failing. To make it possible for threads to use these same system calls and library routines, `errno` is redefined as thread-private data. Thus one thread making a call that sets `errno` doesn't affect the value of `errno` for the other threads in the process.\
Recall that all threads in a process have access to the entire address space of the process. Other than using registers, there is no way for one thread to prevent another from accessing its data. This is true even for thread-specific data. Even though the underlying implementation doesn't prevent access, the functions provided to manage thread-specific data promote data separation among threads by making it more difficult for threads to gain access to thread-specific data from other threads.\
Before allocating thread-specific data, we need to create a key to associate with the data. The key will be used to gain access to the thread-specific data.
```c
#include <pthread.h>

int pthread_key_create(pthread_key_t *keyp, void (*desctructor)(void *));

// Returns: 0 if OK, error number on failure
```
The key created is stored in the memory location pointed to by `keyp`. The same key can be used by all threads in the process, but each thread will associate a different thread-specific data address with the key. When the key is created, the data address for each thread is set to a null value.\
In addition to creating a key, `pthread_key_create` associates an optional destructor function with the key. When the thread exits, if the data address has been set to a non-null value, the destructor is called with the data address as the only argument. If destructor is null, then no destructor function is associated with the key. When the thread exits normally, either by calling `pthread_exit` or by returning, the destructor is called. Also, if the thread is canceled, the destructor is called, but only after the last cleanup handler returns. But if the thread calls `exit`, `_exit`, `_Exit`, or `abort`, or otherwise exits abnormally, the destructor is not called.\
Threads typically use `malloc` to allocate memory for their thread-specific data. The destructor function usually frees the memory that was allocated. If the thread exited without freeing the memory, then the memory would be lost--leaked by the process.\
We can break the association of a key with the thread-specific data values for all threads by calling `pthread_key_delete`.\
```c
#include <pthread.h>

int pthread_key_delete(pthread_key_t key);

// Returns: 0 if OK, error number on failure
```
Depending on how the system schedules threads, some threads might see one key value, whereas other threads might see a different value. The way to solve this race is to use `pthread_once`.
```
#include <pthread.h>

pthread_once_t initflag = PTHREAD_ONCE_INIT;

int pthread_once(pthread_once_t *initflag, void (*initfn)(void));

// Returns: 0 if OK, error number on failure
```
If each thread calls `pthread_once`, the system guarantees that the initialization routine, `initfn`, will be called only once.\
Once a key is created, we can associate thread-specific data with the key by calling `pthread_setspecific`. We can obtain the address of the thread-specific data with `pthread_getspecific`.
```
#include <pthread.h>

int pthread_getspecific(pthread_key_t key);

// Returns: thread-specific data value or NULL if no value has been associated with the key

int pthread_setspecific(pthread_key_t key, const void *value);

// Returns: 0 if OK, error number on failure
```

## 7. Cancel Options
Two thread attributes that are not included in the `pthread_attr_t` structure are the cancelability state and the cancelability type. These attributes affect the behaviour of a thread in response to a call to `pthread_cancel`.\
The cancelability state attribute can be either `PTHREAD_CANCEL_ENABLE` or `PTHREAD_CANCEL_DISABLE`. A thread can change its cancelability state by calling `pthread_setcancelstate`.
```c
#include <phtread.h>

int pthread_setcancelstate(int state, int *oldstate);

// Returns: 0 if OK, error number on failure
```
In one atomic operation, `pthread_setcancelstate` sets the current cancelability state to state and stores the previous cancelability state in the memory location pointed to by `oldstate`.\
A call to `pthread_cancel` doesn't wait for a thread to terminate. In the default case, a thread will continue to execute after a cancellation request is made until the thread reaches a cancellation point. A cancellation point is a place where the thread checks whether it has been canceled, and if so, acts on the request.\
A thread starts with a default cancelability state of `PTHREAD_CANCEL_ENABLE`. When the state is set to `PTHREAD_CANCEL_DISABLE`, a cal to `pthread_cancel` will not kill the thread. Instead, the cancellation request remains pending for the thread. When the state is enabled again, the thread will act on any pending cancellation requests at the next cancellation point.\
If your application doesn't call one of the cancellation point functions for a long period of time, then you can call `pthread_testcancel` to add your own cancellation points to the program.
```c
#include <phtread.h>

void pthread_testcancel(void);
```
When you can `pthread_testcancel`, if a cancellation request is pending and if cancellation has not been disabled, the thread will be canceled. When cancellation is disabled, however, calls to `pthread_testcancel` have no effect.\
The default cancellation type we have been describing is known as deferred cancellation. After a call to `pthread_testcancel`, the actual cancellation doesn't occur until the thread hits a cancellation point. We can change the cancellation type by calling `pthread_setcanceltype`.
```c
#include <phtread.h>

int pthread_setcanceltype(int type, int *oldtype);

// Returns: 0 if OK, error number on failure
```
The `pthread_setcanceltype` function sets the cancellation type to type (either `PTHREAD_CANCEL_DEFERRED` or `PTHREAD_CANCEL_ASYNCHRONOUS`) and returns the previous type in the integer pointed to by `oldtype`.\
Asynchronous cancellation differs from deferred cancellation in that the thread can be canceled at any time. The thread doesn't necessarily need to hit a cancellation point for it to be canceled.

## 8. Threads and Signals
Dealing with signals can be complicated even with a process-based paradigm. Introducing threads into the picture makes things even more complicated.\
Each thread has its own signal mask, but the signal disposition is shared by all threads in the process. As a consequence, individual threads can block signals, but when a thread modifies the action associated with a given signal, all threads share the action. Thus, if one thread chooses to ignore a given signal, another thread can undo that choice by restoring the default disposition or installing a signal handler for that signal.\
Signals are delivered to a single thread in the process. If the signal is related to a hardware fault, the signal is usually sent to the thread whose action caused the event. Other signals, on the other hand, are delivered to an arbitrary thread.\
In previous section, we discussed how processes can use the `sigprocmask` function to block signals from delivery. However, the behavior of `sigprocmask` is undefined in a multithreaded process. Threads have to use the `pthread_sigmask` function instead.
```c
#include <signal.h>

int pthread_sigmask(int how, const sigset_t *restrict set, sigset_t *restrict oset);

// Returns: 0 if OK, error number on failure
```
A thread can wait for one or more signals to occur by calling `sigwait`.
```c
#include <signal.h>

int sigwait(const sigset_t *restrict set, int *restrict signop);

// Returns: 0 if OK, error number on failure
```
To send a signal to a process, we call `kill`. To send a signal to a thread, we call `pthread_kill`.
```c
#include <signal.h>

int pthread_kill(pthread_t thread, int signo);

// Returns: 0 if OK, error number on failure
```
We can pass a `signo` value of 0 to check for existence of the thread. If the default action for a signal is to terminate the process, then sending the signal to a thread will still kill the entire process.

## 9. Threads and `fork`
When a thread calls fork, a copy of the entire process address space is made for the child. Recall the discussion of copy-on-write in previous sections. This child is an entirely different process from the parent, and as long as neither one makes changes to its memory contents, copies of the memory pages can be shared between parent and child.\
By inheriting a copy of the address space, the child also inherits the state of every mutex, reader-writer lock, and condition variable from the parent process. If the parent consists of more than one thread, the child will need to clean up the lock state if it isn't going to call `exec` immediately after `fork` returns.\
Inside the child process, only one thread exists. It is made from a copy of the thread that called `fork` in the parent. If the threads in the parent process hold any locks, the same locks will also be held in the child process. The problem is that the child process doesn't contain copies of the threads holding the locks, so there is no way for the child to know which locks are held and need to be unlocked.\
This problem can be avoided if the child calls one of the `exec` function directly after returning from `fork`. In this case, the old address space is discarded, so the lock state doesn't matter. This is not always possible, however, so if the child needs to continue processing, we need to use a different strategy.\
To avoid problems with inconsistent state in a multithreaded process, POSIX.1 states that only async-signal safe functions should be called by a child process between the time that `fork` returns and the time that the child calls one of the `exec` functions. This limits what the child can do before calling `exec`, but doesn't address the problem of lock state in the child process.\
To clean up the lock state, we can establish `fork` handlers by calling the function `pthread_atfork`.
```c
#include <pthread.h>

int phtread_atfork(void (*prepare)(void), void (*parent)(void), void (*child)(void));

// Returns: 0 if OK, error number on failure
```
With `pthread_atfork`, we can install up to three functions to help clean up the locks. The prepare fork handler is called in the parent before fork creates the child process. This fork handler's job is to acquire all locks defined by the parent. The parent fork handler is called in the context of the parent after fork has created the child process, but before fork returned. This fork handler's job is to unlock all the locks acquired by the prepare fork handler. The child fork handler is called in the context of the child process before returning from fork. Like the parent fork handler, the child fork handler must release all the locks acquired by the prepare fork handler.\
Although the `pthread_atfork` mechanism is intended to make locking state consistent after a `fork`, it ha has several drawbacks that make it usable in only limited circumstances:
- There is no good way to reinitialize the state for more complex synchronization objects such as condition variables and barriers.
- Some implementations of error-checking mutexes will generate errors when the child fork handler tries to unlock a mutex that was locked by the parent
- Recursive mutexes can't be cleaned up in the child fork handler, because there is no way to determine the number of times one has been locked.
- If the child processes are allowed to call only async-signal safe functions, then the child fork handler shouldn't even be able to clean up synchronization objects, because none of the functions that are used to manipulate them are async-signal safe. The practical problem is that a synchronization object might be in an intermediate sate when one thread calls fork, but the synchronization object can't be cleaned up unless it is in a consistent state.
- If an application calls fork in a signal handler(which is legal, because fork is async-signal safe), then the fork handlers registered by `pthread_atfork` can call only async-signal safe functions, or else the results are undefined.

## 10. Threads and I/O
skipped

## 11. Summary
skipped
