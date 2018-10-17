# Chapter14 Advanced I/O

## 1. Introduction
skipped

## 2. Nonblocking I/O
In previous section, we said that system calls are divided into two categories: the "slow" ones and all the others. The slow system calls are those that can block forever. They include
- Reads that can block the caller forever if data isn't present with certain file types
- Writes that can block the caller forever if the data can't be accepted immediately by these same file types.
- Opens that block until some condition occurs on certain file types(such as an open of a FIFO for writing only, when no other process has the FIFO open for reading).
- Reads and writes of files that have mandatory record locking enabled.
- Certain `ioctl` operations.
- Some of the interprocess communication functions.

We also said that system calls related to disk I/O are not considered slow, even though the read or write of a disk file can block the caller temporarily.\
Nonblocking I/O lets us issue an I/O operation, such as an `open`, `read` or `write`, and not have it block forever. If the operation cannot be completed, the call returns immediately with an error noting that the operation would have blocked.\
There are two ways to specify nonblocking I/O for a given descriptor.
1. If we call `open` to get the descriptor, we can specify the `O_NONBLOCK` flag.
2. For a descriptor that is already open, we call `fcntl` to turn on the `O_NONBLOCK` file status flag.

Sometimes, we can avoid using nonblocking I/O by designing our applications to use multiple threads. We can allow individual threads to block in I/O calls if we can continue to make progress in other threads. This can sometimes simplify the design, at other times, however, the overhead of synchronization can add more complexity than is saved from using threads.

## 3. Record Locking
Record locking is the term normally used to describe the ability of a process to prevent other processes from modifying a region of a file while the first process is reading or modifying that portion of the file. Under the UNIX System, "record" is a misnomer; the UNIX kernel does not have a notion of records in a file. A better term is byte-range locking, given that it is a range of a file(possibly the entire file) that is locked.

### `fcntl` Record Locking
```c
#include <fcntl.h>

int fcntl(int fd, int cmd, ... /* struct flock *flockptr */)

// Returns: depends on cmd if OK, -1 on error
```

For record locking, `cmd` is `F_GETLK`, `F_SETLK`, or `F_SETLKW`. The third argument is a pointer to an `flock` structure.\
We previously mentioned two types of locks: a shared read lock and an exclusive write lock. The basic rule is that any number of processes can have a shared read lock on a given byte, but only one process can have an exclusive write lock on a given byte. Furthermore, if there are one or more read locks on a byte, there can't be any write locks on that byte; if there is an exclusive write lock on a byte, there can't be any read locks on that byte.\
The compatibility rule applies to lock requests made from different processes, not to multiple lock requests made by a single process If a process has an existing lock on a range of a file, a subsequent attempt to place a lock on the same range by the same process will replace the existing lock with the new one.

### Implied inheritance and Release of Locks
Three rules govern the automatic inheritance and release of record locks.
1. Locks are associated with a process and a file. This has two implications. The first is obvious: when a process terminates, all its locks are released. The second is far from obvious: whenever a descriptor is closed, any locks on the file referenced by that descriptor for that process are released. This means that if we make the calls
```c
fd1 = open(pathname, ...);
read_lock(fd1, ...);
fd2 = dup(fd1);
close(fd2);
```
after the `close(fd2)`, the lock that was obtained on `fd1` is released. The same thing would happen if we replaced the `dup` with `open`, as in
```c
fd1 = open(pathname, ...);
read_lock(fd1, ...);
fd2 = open(pathname, ...);
close(fd2);
```
to open the same file on another descriptor.
2. Locks are never inherited by the child across a fork. This means that if a process obtains a lock and then calls fork, the child is considered another process with regard to the lock that was obtained by the parent. The child has to call `fcntl` to obtain its own locks on any descriptors that were inherited across the fork. This constraint makes sense because locks are meant to prevent multiple processes from writing to the same file at the same time. If the child inherited locks across a fork, both the parent and the child could write to the same file at the same time.
3. Locks are inherited by a new program across an `exec`. Note, however, that if the `close-on-exec` flag is set for a file descriptor, all locks for the underlying file are released when the descriptor is closed as part of an `exec`.

### FreeBSD Implementation
skipped

### Locks at End of File
skipped

### Advisory versus Mandatory Locking
Mandatory locking causes the kernel to check every `open`, `read`, and `write` to verify that the calling process isn't violating a lock on the file being accessed. Mandatory locking is sometimes called enforcement-mode locking.\
Mandatory locking is enabled for a particular file by turning on the set-group-ID bit and turning off the group-execute bit. Since the set-group-ID bit makes no sense when the group-execute bit is off, the designer of SVR3 chose this way to specify that the locking for a file is to be mandatory locking and not advisory locking.

## 4. I/O Multiplexing
What if we have to read from two descriptors? In this case, we can't do a blocking read on either descriptor, as data may appear on one descriptor while we're blocked in a `read` on the other. A different technique is required to handle this case.\
We could use nonblocking I/O in a single process by setting both descriptors to be nonblocking and issuing a read on the first descriptor. If data is present, we read it and process it. If there is no data to read, the call returns immediately. We then do the same thing with the second descriptor. After this, we wait for some amount of time and then try to read from the first descriptor again. This type of loop is called polling. The problem is that it wastes CPU time. Most of the time, there won't be data to read, so we waste time performing the read system calls. We also have to guess how long to wait each time around the loop. Although it works on any system that supports nonblocking I/O, polling should be avoided on a multitasking system.\
Another technique is called asynchronous I/O. With this technique, we tell the kernel to notify us with a signal when a descriptor is ready for I/O. There are two problems with this approach. First, although systems provide their own limited forms of asynchronous I/O, POSIX chose to standardize a different set of interfaces, so portability can be an issue. System V provides the `SIGPOLL` signal to support a limited form of asynchronous I/O, but this signal works only if the descriptor refers to a STREAMS device. BSD has a similar signal, `SIGIO`, but it has similar limitations: it works only on descriptors that refer to terminal devices or networks. The second problem with this technique is that the limited forms use only one signal per process (`SIGPOLL` or `SIGIO`). If we enable this signal for two descriptors, the occurrence of the signal doesn't tell us which descriptor is ready. Although the POSIX.1 asynchronous I/O interfaces allow us to select which signal to use for notification, the number of signals we can use is still far less than the number of possible open file descriptors. To determine which descriptor is ready, we would need to set each file descriptor to nonblocking mode and try the descriptors in sequence.\
A better technique is to use I/O multiplexing. To do this, we build a list of the descriptors that we are interested in and call a function that doesn't return until one of the descriptors is ready for I/O. Three functions--`poll`, `pselect`, and `select`--allow us to perform I/O multiplexing. On return from these functions, we are told which descriptors are ready for I/O.

### `select` and `pselect` Functions
The `select` function lets us do I/O multiplexing under all POSIX-compatible platforms. The arguments we pass to `select` tell the kernel
- Which descriptors we're interested in.
- Which conditions we're interested in for each descriptor.
- How long we want to wait.(We can wait forever, wait a fixed amount of time, or not wait at all.)

On the return from `select`, the kernel tells us
- The total count of the number of descriptors that are ready
- Which descriptors are ready for each of the three conditions(read, write, or exception condition)

With this return information, we call call the appropriate I/O function and know that the function won't block.
```c
#include <sys/select.h>

int select(
    int maxfdp1,
    fd_set *restirct readfds,
    fd_set *restirct writefds,
    fd_set *restirct exceptfds,
    struct timeval *restrict tvptr
        );

Returns: count of ready descritpros, 0 on timeout, -1 on error
```
Let's look at the lart argument first. It specifies how long we want to wait interms of seconds and microseconds. There are three conditions
- `typtr == NULL`
Wait forever. This infinite wait can be interrupted if we catch a signal. Return is made when one of the specified descritprs is ready or when a signal is caught. If a signal is caught, `select` returns -1 with `errno` set to `EINTR`.
- `typtr->tv_sec == 0 && tvptr->tv_usec == 0`
Don't wait at all. All the specified descritprs are tested, and return is made immediately. This is a way to poll the system to find out the status of multiple descriptrs without blocking in the `select` funciton.
- `typtr->tv_sec != 0 || tvptr->tv_usec != 0`
Wait the specified number of seconds and microseconds. Return is made when one of the specified descritpors is rady or when the timeout value expires. If the timeout expires before any of the descriptors is ready, the return value is 0. As with the first condition, this wait can also be interrupted by a caught signal.

The middle three arguments--`readfds`, `writefds`, and `exceptfds`--are pointers to descriptor sets. These three sets specify which descriptors we're interested in and for which condition. A descriptor set is stored in an `fd_set` data type.  This data type is chosen by the implementation so that it can hold one bit for each possible descriptor. We an consider it to be just a big array of bits.\
Any(or all) of the middle three arguments to `select` can be null pointers if we're not interested in that condition. If all three pointers are NULL, then we have a higher-precision timer than is provided by `sleep`.\
The first argument to `select`, `maxfdp1`, stands for "maximum file descriptor plus 1." We calculate the highest descriptor that we're interested in, considering all three of the descriptor sets, add 1, and that's the first argument. We could just set the first argument to `FD_SETSIZE`, a constant in `<sys/select.h>` that specifies the maximum number of descriptors(often 1024), but this value is too large for most applications. Indeed, most applications probably use between 3 and 10 descriptors. By specifying the highest descriptor that we're interested in, we can prevent the kernel from going through hundreds of unused bits in the three descriptor sets, looking for bits that are turned on.\
There are three possible return values from `select`
1. A return value of -1 means that an error occurred. This can happen, for example, if a signal is caught before any of the specified descriptors are ready. In this case, none of the descriptor sets will be modified.
2. A return value of 0 means that no descriptors are ready. This happens if the time limit expires before any of the descriptors are ready. When this happens, all the descriptor sets will be zeroed out.
3. A positive return value specifies the number of descriptors that are ready. This value is the sum of the descriptors ready in all three sets, so if the same descriptor is ready to be read and written, it will be counted twice in the return value. The only bits left on in the three descriptor sets are the bits corresponding to the descriptors that are ready.

We now need to be more specific about what "ready" means.
- A descriptor in the read set is considered ready if a `read` from that descriptor won't block.
- A descriptor in the write set is considered ready if a `write` from that descriptor won't block.
- A descriptor in the exception set is considered ready if an exception condition is pending on that descriptor. Currently, an exception condition corresponds to either the arrival of out-of-band data on a network connection or certain conditions occurring on a pseudo terminal that has been placed into packet mode.
- File descriptors for regular files always return ready for reading, writing, and exception conditions.

It is important to realize that whether a descriptor is blocking or not doesn't affect whether `select` blocks. That is, if we have a nonblocking descriptor that we want to read from and we call `select` with a timeout value of 5 seconds, `select` will block for up to 5 seconds. Similarly, if we specify an infinite timeout, `select` blocks until data is ready for the descriptor or until a signal is caught.\
If we encounter the end of file on a descriptor, that descriptor is considered readable by `select`. We then call `read` and it returns 0.\
POSIX.1 also defines a variant of `select` called `pselect`
```c
#include <sys/select.h>

int pselect(
    int maxfdp1,
    fd_set *restirct readfds,
    fd_set *restirct writefds,
    fd_set *restirct exceptfds,
    const struct timespec *restrict tsptr,
    const sigset_t *restrict sigmask,
        );

Returns: count of ready descritpros, 0 on timeout, -1 on error
```
The `pselect` function is identical to `select`, with the following exceptions.
- The timeout value for `select` is specified by a `timeval` structure, but for `pselect`, a `timespec` is used. Instead of seconds and microseconds, the `timespec` structure represents the timeout value in seconds and nanoseconds. This provides a higher-resolution timeout if the platform supports that fine a level of granularity.
- The timeout value for `pselect` is declared const, and we are guaranteed that its value will not change as a result of calling `pselect`.
- An optional signal mask argument is available with `pselect`. If `sigmask` is NULL, `pselect` behaves as `select` does with respect to signal. Otherwise, `sigmask` points to a signal mask that is atomically installed when `pselect` is called. On return, the previous signal mask is restored.

### `poll` Function
The `poll` function is similar to `select`, but the programmer interface is different. This function was originally introduced in System V to support the STREAMS subsystem, but we are able to use it with any type of file descriptor.
```c
#include <poll.h>

int poll(struct pollfd fdarray[], nfds_t nfds, int timeout);

Returns: count of ready descritpros, 0 on timeout, -1 on error
```
With `poll`, instead of building a set of descriptor for each condition as we did with `select`, we build an array of `pollfd` structures, with each array element specifying a descriptor number and the conditions that we're interested in for that descriptor.
```c
struct pollfd{
    int fd;             // file descriptor to check, or <0 to ignore
    short events;       // events of interest on fd
    short revents;      // events of occurred on fd
}
```
The number of elements in the `fdarray` array is specified by `nfds`. The final argument to `poll` specifies how long we want to wait.  As with `select`, whether a descriptor is blocking doesn't affect whether `poll` blocks.

### Interruptibility of `select` and `poll`
skipped

## 5. Asynchronous I/O
Before we look at the different ways to use asynchronous I/O, we need to discuss the costs. When we decide to use the asynchronous I/O, we complicate the design of our application by choosing to juggle multiple concurrent operations. A simpler approach may be to use multiple threads, which would allow us to write program using a synchronous model, and let the threads run asynchronous to each other.\
We incur additional complexity when we use the POSIX asynchronous I/O interfaces:
- We have to worry about three different sources of errors for every asynchronous operation: one associated with the submission of operation,  one associated with the result of the operation itself, and one associated with the functions used to determine the status of the asynchronous operations.
- The interface themselves involve a lot of extra setup and processing rules compared to their conventional counterparts.
- Recovering from errors can be difficult. For example, if we submit multiple asynchronous writes and one fails, how should we proceed? If the writes are related, we might have to undo the ones that succeeded.

### System V Asynchronous I/O
System V provides a limited form of asynchronous I/O that works only with STREAMS devices and STREAMS pipes. The System V asynchronous I/O signal is `SIGPOLL`.\

Details is skipped

### BSD Asynchronous I/O
Asynchronous I/O in BSD-derived systems is a combination of two signals: `SIGIO` and `SIGURG`. The former is the general asynchronous signal, and the latter is used only to notify the process that out-of-band data has arrived on a network connection.

Details is skipped

### POSIX Asynchronous I/O
The POSIX asynchronous I/O interfaces give us a consistent way to perform asynchronous I/O, regardless of the type of file.

Details is skipped

## 6. `readv` and `writev` Functions
The `readv` and `writev` functions let us read into and write from multiple non-contiguous buffers in a single function call. These operations are called scatter read and gather write.
```c
#include <sys/uio.h>

ssize_t readv(int fd, const struct invec *iov, int iovcnt);
ssize_t writev(int fd, const struct invec *iov, int iovcnt);

// Both return: number of bytes read or written, -1 on error
```
The `writev` function gathers the output data from the buffers in order. `writev` returns the total number of bytes output, which should normally equal the sum of all the buffer lengths.\
The `readv` function scatters the data into the buffers in order, always filling one buffer before proceeding to the next. `readv` returns the total number of bytes that were read. A count of 0 is returned if there is no more data and the end of file is encountered.

## 7. `readn` and `writen` Functions
** Note that we define these functions as a convenience, The `readn` and `writen` functions are not part of any standard. **

Pipes, FIFOs, and some devices--notably terminals and networks--have the following properties.
1. A `read` operation may return the less than asked for, even though we have not encountered the end of file. This is not an error, and we should simply continue reading from the device.
2. A `write` operation can return less than we specified. This may be caused by kernel output buffers becoming full, for example. Again, it's not an error, and we should continue writing the remainder of the data. (Normally, this short return from `write` occurs only with a nonblocking descriptor or if a signal is caught.)

We'll never see this happen when reading or writing a disk file, except when the file system runs out of space or we hit our quota limit and we can't write all that we requested.
Generally, when we read from or write to a pipe, network device, or terminal, we need to take these characteristics into consideration. We can use the `readn` and `writen` functions to read and write N bytes of data, respectively, letting these functions handler a return value that's possibly less than requested. These two functions simply call `read` or `write` as many times as required to read or write the entire N bytes of data.
```c
#include "apue.h"

ssize_t readv(int fd, void *buf, size_t nbytes);
ssize_t writev(int fd, void *buf, size_t nbytes);

// Both return: number of bytes read or written, -1 on error
```

## 8. Memory-Mapped I/O
Memory-mapped I/O lets us map a file on disk into a buffer in memory so that, when we fetch bytes from the buffer, the corresponding bytes of the file are read. Similarly, when we store data in the buffer, the corresponding bytes are automatically written to the file. This lets us perform I/O without `read` or `write`.\
To use this feature, we have to tell the kernel map a given file to a region in memory. This task is handled by the `mmap` function.
```c
#include <sys/mman.h>

void *mmap(void *addr, size_t len, int prot, int flag, int fd, off_t off);

// Returns: starting address of mapped region if OK, MAP_FAILED on error
```
Depending on the system, memory-mapped I/O can be more efficient when copying one regular file to another. There are limitations. We can't use this technique to copy between certain devices(such as a network device or a terminal device), and we have to be careful if the size of the underlying file could change after we map it. Nevertheless, some applications can benefit from memory-mapped I/O, as it can often simplify the algorithms, since we manipulate memory instead of reading and writing a file.

## 9. Summary
skipped
