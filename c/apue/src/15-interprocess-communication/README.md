# Chapter15 Interprocess Communication

## 1. Introduction
skipped

## 2. Pipes
Pipes are the oldest form of UNIX System IPC and are provided by all UNIX systems, Pipes have two limitations.
1. Historically, they have been half duplex. Some systems now provide full-duplex pipes, but for maximum portability, we should never assume that this is the case.
2. Pipes can be used only between processes that have a common ancestor. Normally, a pipe is created by a process, that process calls `fork`, and the pipe is used between the parent and the child.

We'll see that FIFOs get around the second limitation, and that UNIX domain sockets get around both limitations.\
Despite these limitations, half-duplex pipes are still the most commonly used form of IPC. Every time you type a sequence of commands in a pipe line for the shell to execute, the shell creates a separate process for each command and links the standard output of one process to the standard input of the next using a pipe.\
A pipe is created by calling the `pipe` function.
```c
#include <unistd.h>

int pipe(int fd[2]);

// Returns: 0 if OK, -1 on error
```
Two file descriptors are returned through the `fd` argument: `fd[0]` is open for reading, and `fd[1]` is open for writing. The output of `fd[1]` is the input for `fd[0]`.\
A pipe in a single process is next to useless. Normally, the process that calls `pipe` then calls `fork`, creating an IPC channel from parent to the child, or vice versa. What happens after the `fork` depends on which direction of data flow we want. For a pipe from the parent to the child, the parent closes the read end of the pipe, and the child closes the write end.

## 3. `popen` and `pclose` Functions
Since a common operation is to create a pipe to another process to either read its output or send it input, the standard I/O library has historically provided the `popen` and `pclose` functions. These two functions handle all the dirty work that we've been doing ourselves: creating a pipe, forking a child, closing the unused ends of the pipe, executing a shell to run the command, and waiting for the command to terminate.
```c
#include <stdio.h>

FILE *popen(const char *cmdstring, const char *type);

// Returns: file pointer if OK, NULL on error

int pclose(FILE *fp);

// Returns: termination status of cmdstring, or -1 on error
```
The function `popen` does a fork and `exec` to execute the `cmdstring` and returns a standard I/O file pointer. If type is "r", the file pointer is connected to the standard output of `cmdstring`. If type is "w", the file pointer is connected to the standard input of `cmdstring`. One way to remember the final argument to `popen` is to remember that, like `fopen`, the returned file pointer is readable if type is "r" or writable if type is "w".\
The `pclose` function closes the standard I/O stream, waits for the command to terminate, and returns the termination status of the shell. If the shell cannot be executed, the termination status returned by `pclose` is as if the shell had executed `exit(127)`. The `cmdstring` is executed by the Bourne shell, as in
```bash
sh -c cmdstring
```

## 4. Coprocesses
A UNIX system filter is a program that reads from standard input and writes to standard output. Filters are normally connected linearly in shell pipelines. A filter becomes a coprocess when the same program generates the filter's input ands reads from the filter's output.

Detail is skipped

## 5. FIFOs
FIFOs are sometimes called named pipes. Unnamed pipes can be used only between related processes when a common ancestor has created the pipe. With FIFOs, however, unrelated processes can exchange data.
```c
#include <sys/stat.h>

int mkfifo(const char *path, mode_t mode);

int mkfifoat(int fd, const char *path, mode_t mode);

// Both return: 0 if OK, -1 on error
```
The specification of the `mode` argument is the same as for the `open` function.\
Once we have used `mkfifo` or `mkfifoat` to create a FIFO, we open it using `open`. Indeed, the normal file I/O functions(e.g. close, read, write, unlink) all work with FIFOs.\
When we open a FIFO, the nonblocking flag (`O_NONBLOCK`) affects what happens.
- In the normal case(without `O_NONBLOCK`), an open for read-only blocks until some other process opens the FIFO for writing. Similarly, an open for write-only blocks until some other process opens the FIFO for reading.
- If `O_NONBLOCK` is specified, an open for read-only returns immediately. But an open for write-only returns -1 with `errno` set to `ENXIO` if no process has the FIFO open for reading.

As with a pipe, if we write to a FIFO that no process has open for reading, the signal `SIGPIPE` is generated. When the last write for a FIFO closes the FIFO, an end of file is generated for the reader of the FIFO.\
It is common to have multiple writers for a given FIFO. This means that we have to worry about atomic writes if we don't want the writes from multiple processes to be interleaved. As with pipes, the constant `PIPE_BUF` specifies the maximum amount of data that can be written atomically to a FIFO.\
There are two uses for FIFOs.
1. FIFOs are used by shell commands to pass data from one shell pipeline to another without creating intermediate temporary files.
2. FIFOs are used as rendezvous points in client-server applications to pass data between the clients and the servers.

### Example--Using FIFOs to duplicate Output Streams
FIFOs can be used to duplicate an output stream in a series of shell commands. This prevents writing the data to an intermediate disk file(similar to using pipes to avoid intermediate disk files). But whereas pipes can be used only for linear connections between processes, a FIFO has a name, so it can be used for nonlinear connections.

Details is skipped

### Example--Client-Server Communication Using a FIFO
Another use for FIFOs is to send data between a client and a server. If we have a server that is contacted by numerous clients, each client can write its request to a well-known FIFO that the server creates.\
Since there are multiple writers for the FIFO, the requests sent by the clients to the server need to be less than `PIPE_BUF` bytes in size. This prevents any interleaving of the client writes.

Details is skipped

## 6. XSI IPC
The three types of IPC that we call XSI IPC--message queues, semaphores, and shared memory--have many similarities.

### Identifiers and Keys
Each IPC structure(message queue, semaphore, or shared memory segment) in the kernel is refereed to by a non-negative integer identifier. To send a message to or fetch a message from a message queue, for example, all we need know is the identifier of the queue. Unlike file descriptors, IPC identifiers are not small integer. Indeed, when a given IPC structure is created and then removed, the identifier associated with that structure continually increases until it reaches the maximum positive value for an integer, and then wraps around to 0.\
The identifier is an internal name for an IPC object. Cooperating processes need an external naming scheme to be able to rendezvous using the same IPC object. For this purpose, an IPC object is associated with a key that acts as an external name.\
Whenever an IPC structure is created, a key must be specified. The data type of this key is the primitive system data type `key_t`, which is often defined as a long integer in the header `<sys/types.h>`. This key is converted into an identifier by the kernel.\
There are various ways for a client and a server to rendezvous at the same IPC structure.
1. The server can create a new IPC structure by specifying a key of `IPC_PRIVATE` and stored the returned identifier somewhere for the client to obtain. The key `IPC_PRIVATE` guarantees that the server creates a new IPC structure. The disadvantage of this technique is that file system operations are required for the server to write the integer identifier to a file, and then for the client to retrieve this identifier later.\
The `IPC_PRIVATE` is also used in a parent-child relationship. The parent creates a new IPC structure by specifying `IPC_PRIVATE`, and the resulting identifier is then available for the child after the `fork`. The child can pass the identifier to a new program as an argument to one of the `exec` functions.
2. The client and the server can agree on a key by defining the key on a common header, for example. The server can then creates a new IPC structure specifying this key. The problem of this approach is that it's possible for the key to already be associated with an IPC structure, in which case the get function returns an error. The server must handle this error, deleting the existing structure, and try to create it again.
3. The client and the server can agree on a pathname and project ID(the project ID is a character value between 0 to 255) and call the function `ftok` to convert these two values to a key. This key is then used in step 2. The only service provided by `ftok` is a way of generating a key from a pathname and project ID.
    ```c
    #include <sys/ipc.h>

    key_t ftok(const char *path, int id);

    // Returns: key if OK, (key_t)-1 on error
    ```
the path argument must refer to an existing file. Only the lower 8 bit of id are used when generating the key.

If we want to create a new IPC structure, making sure that we don't reference an existing one with the same identifier, we must specify a flag with both the `IPC_CREAT` and `IPC_EXCL` bits set. Doing this causes an error return of `EEXIST` if the IPC structure already exists.

### Permission structure
skipped

### Configuration limits
skipped

### Advantages and Disadvantages
A fundamental problem with XSI IPC is that the IPC structures are system wide and do not have a reference count. For example, if we create a message queue, place some messages on the queue, and then terminate, the message queue and its contents are not deleted. They remain in the system until specifically read or deleted by some process calling `msgrcv` or `msgctl`, by someone executing the `ipcrm` command, or by the system being rebooted. Compare this with a pipe, which is completely removed when the last process to reference it terminates. With a FIFO, although the name stays in the file system until explicitly removed, any data left in a FIFO is removed when the last process to reference the FIFO terminates.\
Another problem with XSI IPC is that these IPC structures are not known by names in the file system, We can't see the IPC objects with an `ls` command, we can't remove them with the `rm` command, and we can't change their permissions with the `chmod` command.\
Since these forms of IPC don't use file descriptors, we can't use the multiplexed I/O functions with them. This makes it harder to use more than one of these IPC strctures at a time or to use any of these IPC structures with file or device I/O. For example, we can't have a server wait for a message to be placed on one or two message queues without some for of busy-wait loop.

## 7. Message Queues
A message queue is a linked list of messages stored within the kernel and identified by a message queue identifier. We'll call the message queue just a queue and its identifier a queue ID.\
Nowadays, Message queues, which originally implemented to provide higher-than-normal-speed IPC, are no longer that much faster than other forms of IPC(UNIX domain socket). When we consider the problems in using message queues, we come to the conclusion that we shouldn't use them for new applications.

Details is skipped

## 8. Semaphores
A semaphore isn't a form of IPC similar to the others that we've described(pipes, FIFOs, and message queues). A semaphore is a counter used to provide access to a shared data object for multiple processes.

Details is skipped

## 9. Shared Memory
Shared memory allows two or more processes to share a given region of memory. This is the fastest form of IPC, because the data does not need to be copied between the client and the server. This only trick in using shared memory is synchronizing access to a given region among multiple processes. If the server is placing data into a shared memory region, the client shouldn't try to access the data until the server is done. Often, semaphores are used to synchronize shared memory access.(record locking or mutexes can also be used).\
We've already seen one form of shared memory when multiple processes map the same file into their spaces. The XSI shared memory differs from memory-mapped files in that there is no associated file. The XSI shared memory segments are anonymous segments of memory.

Details is skipped

## 10. POSIX Semaphores
The POSIX semaphore interfaces were meant to address several deficiencies with the XSI semaphore interfaces:
1. The POSIX semaphore interfaces allow for higher-performance implementations compared to XSI semaphores.
2. The POSIX semaphore interfaces are simpler to use: there are no semaphore sets, and several of the interfaces are patterned after familiar file system operations. Although there is no requirement that they be implemented in the file system, some systems do take this approach.
3. The POSIX semaphores behave more gracefully when removed. Recall that when an XSI semaphore is removed, operations using the same semaphore identifier fail with `errno` set to `EIDRM.` With POSIX semaphores, operations continue to work normally until the last reference to the semaphore is release.

Details is skipped

## 11. Client-Server Properties
skipped

## 12. Summary
skipped
