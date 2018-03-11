# Chapter03 File I/O

## 1. Introduction
## 2. File Descriptors
To the kernel, all open files are referred to by file descriptors. A file descriptor is a non-negative integer. When we open an existing file or create a new file, the kernel returns a file descriptor to the process. When we want to read or write a file, we identify the file with the file descriptor that was returned by `open` or `creat` as an argument to either read or write.\
Although their values are standardized by POSIX.1, the magic numbers 0,1,2 should be replaced in POSIX-compliant applications with the symbolic constants STDIN_FILENO, STDOUT_FILENO, and STDERR_FILENO to improve readability. these constants are defined in the <unistd.h> header.
## 3. open and `openat` Functions
The file descriptor returned by open and openat is guaranteed to be the lowest numbered unused descriptor. This fact is used by some applications to open a new file on standard input, standard output, or standard error. For example, an application might close standard output--normally, file descriptor 1--and then open another file, knowing that it will be opened on file descriptor 1. We'll see a better way to guarantee that a file is open on a given descriptor in Section 3.12, when we explore the dup2 function.\
The fd parameter distinguishes the `openat` function from the open function. There are three possibilities:
1. The path parameter specifies an absolute pathname. In this case, the `fd` parameter is ignored and the `openat` function behaves like the open function.
2. The path parameter specifies a relative pathname and the fd parameter is a file descriptor that specifies the starting location in the file system where the relative pathname is to be evaluated. The fd parameter is obtained by opening the directory where the relative pathname is to be evaluated.
3. The path parameter specifies a relative pathname and the fd parameter has the special value `AT_FDCWD`. In this case, the pathname is evaluated starting in the current working directory and the `openat` function behaves like the open function.

The `openat` function is one of a class of functions added to the latest version of POSIX.1 to address two problems. First, it gives threads a way to use relative pathnames to open files in directories other than the current working directory. As we'll see in Chapter 11, all threads in the same process share the same current working directory, so this makes it difficult for multiple threads in the same process to work in different directories at the same time. Second, it provides a way to avoid time-of-check-to-time-of-use(TOCTTOU) errors.
The basic idea behind TOCTTOU errors is that a program is vulnerable if it makes two file-based function calls where the second call depends on the results of the first call. Because the two calls are not atomic, the file can change between the two calls, thereby invalidating the results of the first call, leading to a program error. TOCTTOU errors in the file system namespace generally deal with attempts to subvert file system permissions by tricking a privileged program into either reducing permissions on a privileged file or modifying a privileged file to open up a security hole.
### Filename and Pathname Truncation
With POSIX.1, ths constant _POSIX_NO_TRUNC determines wheter long filenames and long components of pathnames are truncated or an error is returned. Linux always return an error.
## 4. `creat` Function
```c
#include <fcntl.h>
int creat(const char *path, mode_t mode);
// returns: file descriptor opened for write-only if OK, -1 on error
```
Note that this function is equivalent to
```c
open(path, O_WRONLY | O_CREAT | O_TRUNC, mode);
```
One deficiency with `creat` is that the file is opened only for writing. Before the new version of open was provided, if we were creating a temporary file that we wanted to write and then read back, we had to call `creat`, `close`, and then `open`. A better way is to use the open function, as in
```c
open(path, O_RDWR | O_CREAT | O_TRUNC, mode);
```
## 5. `close` Function
When a process terminates, all of its open files are closed automatically by the kernel. Many programs take advantage of this fact and don't explicitly close open files.

## 6. `lseek` Function
> The character l in the name `lseek` means "long integer", Before the introduction of the off_t data type, the offset argument and the return value were long integers.

Every open file has an associated "current file offset", normally a non-negative integer that measures the number of bytes from the beginning of the file. (We describe some exceptions to the "non-negative" qualifier later in this section.) Read and write operations normally start at the current file offset and cause the offset to be incremented by the number of bytes read or written. By default, this offset is initialized to 0 when a file is opened, unless the O_APPEND options is specified.\
An open file's offset can be set explicitly by calling `lseek`.
```c
#include <unistd.h>
off_t lseek(int fd, off_t offset, int whence);
```
The interpretation of the offset depends on the value of the whence argument.
- If whence is SEEK_SET, the file's offset is set to offset bytes from the beginning of the file.
- If whence is SEEK_CUR, the file's offset is set to its current value plus the offset. The offset can be positive or negative.
- If whence is SEEK_END, the file's offset is set to the size of the file plus the offset. The offset can be positive or negative.
Because a successful call to lseek returns the new file offset, we can seek zero bytes from the current position to determine the current offset:
```c
off_t curr = lseek(fd, 0, SEEK_CUR);
```
This technique can also be used to determine if a file is capable of seeking. If the file descriptor refers to a pipe, FIFO, or socket, lseek sets errno to ESPIPE and returns -1.\
For example, if we invoke [this program](./seek.c) interactively, we get
```bash
./a.out < /etc/passwd
seek OK
cat < /etc/passwd | ./a.out
cannot seek
./a.out < /var/spool/cron/FIFO
cannot seek
```
The file's offset can be greater than the file's current size, in which case the next write to the file will extend the file. This is referred to as creating a hole in a file and is allowed. Any bytes in a file that have not been written are read back as 0.\
A hole in a file isn't required to have storage backing it on disk. Depending on the file system implementation, when you write after seeking past the end of a file, new disk blocks might be allocated to store the data, but there is no need to allocate disk blocks for the data between the old end of file and the location where you start writing.\
Note that if `off_t` is a 32-bit integer, the maximum file size is 2^31 -1 bytes. Although you might enable 64-bit file offsets, your ability to create a file larger than 2GB(2^31 - 1 bytes) depends on the underlying file system type.
## 7. `read` Function
Data is read from an open file with the read function.
```c
#include <unistd.h>
ssize_t read(int fd, void *buf, size_t nbytes);
// Returens: number of bytes read, 0 if end of file, -1 on error
```
There are several cases in which the number of bytes actually read is less than the amount requested:
- When reading from a regular file, if the end of file is reached before the requested number of bytes has been read. For example, if 30 bytes remain until the end of file and we try to read 100 bytes, read returns 30. The next time we call read, it will return 0(end of file)
- When reading from a terminal device. Normally, up to one line is read at at time
- When reading from a network. Buffering within the network may cause less than the requested amount to be returned
- When reading from a pipe of FIFO. If the pipe contains fewer bytes than requested, read will return only what is available.
- When reading from a record-oriented device. Some record-oriented devices, such as magnetic tape, can return up to a single record at a time.
- When interrupted by a signal and a partial amount of data has already been read.
The read operation starts at the file's current offset. Before a successful return, the offset is incremented by the number of bytes actually read.
## 8. `write` Function
Data is written to an open file with the write function
```c
#include <unistd.h>
ssize_t write(int fd, const void *buf, size_t nbytes);
// Returns: number of bytes written if OK, -1 on error
```
The return value is usually equal to the nbytes argument; otherwise, an error has occurred. A common cause for a write error is either filling up a disk or exceeding the file size limit for a given process.
For a regular file, the write operation starts at the file's current offset. If the O_APPEND option was specified when the file was opened, the file's offset is set to the current end of file before each write operation. After a successful write, the file's offset is incremented by the number of bytes actually written.
## 9. I/O efficiency
Most file systems support some kind of read-ahead to improve performance. When sequential reads are detected, the system tries to read in more data than an application requests, assuming that the application will read it shortly.
## 10. File Sharing
The kernel uses three data structures to represent an open file, and the relationships among them determine the effect one process has on another with regard to file sharing.
1. Every process has an entry in the process table. Within each process table entry is a table of open file descriptors, which we can think of as a vector, with one entry per descriptor. Associated with each file descriptor are
    1. The file descriptor flags
    2. A pointer to a file table entry
2. The kernel maintains a file table for all open files. Each file table entry contains
    1. The file status flags for the file, such as read, write, append, sync, and nonblocking;
    2. The current file offset
    3. A pointer to the v-node table entry for the file
3. Each open file(or device) has a v-node structure that contains information about the type of file and pointers to functions that operate on the file. For most files, the v-node also contains the i-node for the file. This information is read from disk when the file is opened, so that all the pertinent information about the file is readily available. For example, the i-node contains the owner of the file, the size of the file, pointers to where the actual data blocks for the file are located on disk, and so on. (Linux has no v-node. Instead, a generic i-node structure is used. Although the implementations differ, the v-node is conceptually the same as a generic i-node. Both point to an i-node structure specific to the file system).
> The v-node was invented to provide support for multiple file system types on a single computer system. Sun called this the Virtual File System and called the file system-independent portion of the i-node the v-node. The v-node propagated through various vendor implementations as support for Sun's Network File System(NFS) was added. Instead of splitting the data structures into a v-node and an i-node, Linux uses a file system-independent i-node and a file system-dependent i-node.

If two independent processes have the same file open, Each process that opens the file gets its own file table entry, but only a single v-node table entry is required for a given file. One reason each process gets its own file table entry is so taht each process has its own current offset for the file.\
Given these data structures, we now need to be more specific about what happens with certain operations that we've already described:
- After each write is complete, the current file offset in the file table entry is incremented by the number of bytes written. If this causes the curren file offset to exceed the current file size, the current file size in the i-node table entry is set to the current file offset.
- If a file is opened with the O_APPEND flag, a corresponding flag is set in the file status flags of the file table entry. Each time a write is performed for a file with this append flag set, the current file offset in the file table entry is first set to the current file size from the i-node table entry. This forces every write to be appended to the current end of file.
- If a file is positioned to its current end of file using `lseek`, all that happens is the current file offset in the file table entry is set to the current file size from the i-node table entry. (Note that this is not the same as if the file was opened with the O_APPEND flag)
- The `lseek` function modifies only the current file offset in the file table entry. no I/O takes place.

It is possible for more than one file descriptor entry to point to the same file table entry, as we'll see when we discuss the dup function in Section 3.12. This also happens after a fork when the parent and the child share the same file table entry for each open descriptor.\
Note the difference in scope between the file descriptor flags and the file status flags. The former apply only to a single descriptor in a single process, whereas the latter apply to all descriptors in any process that point to the given file table entry. When we describe the `fcntl` function in Section 3.14, we'll see how to fetch and modify both the file descriptor flags and the fil status flags.\
Everything that we've described so far in this section works fine for multiple processes that are reading the same file. Each process has its own file table entry with its own current file offset. Unexpected results can arise, however, when multiple processes write to the same file. To see how to avoid some surprises, we need to understand the concept of atomic operations.
## 11. Atomic Operations
### Appending to a File
Consider a single process that wants to append to the end of a file. Older versions of the UNIX System didn't support the O_APPEND option to open, so the program was coded as follows:
```c
if (lseek(fd, 0L, SEEK_END) < 0)
    err_sys("lseek error");
if (write(fd, buf, 100) != 100)
    err_sys("write wrror");
```
The problem here is that our logical operation of "position to the end of file and write" requires two separate function calls(as we've shown it). The solution is to have the positioning to the current end of file and the write be an atomic operation with regard to other processes.\
Any operation that requires more that one function call cannot be atomic, as there is always the possibility that the kernel might temporarily suspend the process between the two function calls.
### `pread` and `pwrite` Functions
The Single UNIX Specification includes two functions taht allow applications to seek and perform I/O atomically: `pread` and `pwrite`.
Calling `pread` is equivalent to calling `lseek` followed by a call to read, with the following exceptions.
- There is no way to interrupt the two operations that occur when we call `pread`.
- The current file offset is not updated
Calling `pwrite` is equivalent to calling `lseek` followed by a call to write, with similar exceptions.
### Creating a File
## 12. `dup` and `dup2` Functions (TODO)
An existing file descriptor is duplicated by either of the following functions:
```c
#include <unistd.h>
int dup(int fd);
int dup2(int fd, int fd2);
// Both return: new file descritor if OK, -1 on error
```
The new file descriptor returned by dup is guaranteed to be the lowest-numbered available file descriptor. With dup2, we specify the value of the new descriptor with the fd2 argument. If fd2 is already open, it is first closed. If fd equals fd2, then dup2 returns fd2 without closing it. Otherwise, the FD_CLOEXEC file descriptor flag is cleared for fd2. so that fd2 is left open if the process calls exec.\
The new file descriptor that is returned as the value of the functions shares the same file table entry as the fd argument.\
Another way to duplicate a descriptor is with the `fcntl` function, Indeed, the call
```c
dup(fd);
```
is equivalent to
```c
fcntl(fd, F_DUPFD, 0);
```
Similarly, the call
```c
dup2(fd, fd2);
```
is equivalent to
```c
close(fd2);
fcntl(fd, F_DUPFD, fd2);
```
In ths last case, the dup2 is not exactly the same as a close followed by an `fcntl`. The differences are as follows:
1. dup2 is an atomic operation, whereas the alternate from involves two function calls. It is possible in the latter case to have a signal catcher called between the close and the `fcntl` that could modify the file descriptors. The same problem could occur if a different thread changes the file descriptors.
2. There are some errno differences between dup2 and `fcntl`.
## 13. `sync` `fsync` and `fdatasync` Functions
Traditional implementations of the UNIX System have a buffer cache or page cache in the kernel through which most disk I/O passes. When we write data to a file, the data is normally copied by the kernel into one of its buffers and queued for writing to disk at some later time. This is called delayed write.\
The kernel eventually writes all the delayed-write blocks to disk, normally when it needs to reuse the buffer for some other disk block. To ensure consistency of the file system on disk with the contents of the buffer cache, the `sync`, `fsync`, and `fdatasync` functions are provided.
```c
#include <unistd.h>
int fsync(int fd);
int fdatasync(int fd);
// Returns: 0 if OK, -1 on error
void sync(void);
```
The sync function simply queues all the modified block buffers for writing and returns; it does not wait for the disk writes to take place.\
The function sync is normally called periodically (usually every 30 seconds) from a system daemon, often called update. This guarantees regular flushing of the kernel's block buffers. The command sync also calls the sync function.\
The function `fsync` refers only to a single file, specified by the file descriptor fd, and waits for the disk writes to complete before returning. This function is used when an application, such as a database, needs to be sure that the modified blocks have been written to the disk.\
The `fdatasync` function is similar to `fsync`, but it affects only the data portions of a file. With `fsync`, the file's attributes are also updated synchronously.
## 14. `fcntl` Functions
The `fcntl` function can change the properties of a file that is already open.
```
#include <fcntl.h>
int fcntl(int fd, int cmd, .../* int arg */);
// Returns depends on cmd if OK, -1 on error
```
In the examples in this section, the third argument is always an integer, corresponding to the comment in the function prototype just shown. When we describe record locking, however, the third argument becomes a pointer to a structure.\
The `fcntl` function is used for five different purposes.
 function is used for five different purposes.
1. duplicate an existing descriptor (cmd=F_DUPFD or F_DUPFD_COLEXEC)
2. get/set file descriptor flags (cmd=F_GETFD or F_SETFD)
3. get/set file status flags (cmd=F_GETFL or F_SETFL)
4. get/set asynchronous I/O ownership (cmd=F_GETOWN or F_SETOWN)
5. get/set record locks (cmd=F_GETLK, F_SETLK or F_SETLKW)

## 15. `ioctl` Functions
The `ioctl` function has always been the catchall for I/O operations. Anything that couldn't be expressed using one of the other functions in this chapter usually ended up being specified with an `ioctl`. Terminal I/O was the biggest user of this function.
```c
#include <unistd.h> // System V
#include <sys/ioctl.h> // BSD and Linux
int ioctl(int fd, int request, ...);
// Returns: -1 on error, something else if OK
```
For the ISO C prototype, an ellipsis is used for the remaining arguments. Normally, however, there is only one more argument, and it's usually a pointer to a variable or a structure.\
In this prototype, we show only the headers required for the function itself. Normally, additional device-specific headers are required. For example the `ioctl` commands for terminal I/O, beyond the basic operations specified by POSIX.1, all require the <termios.h> header.\
Each device driver can define its own set of `ioctl` commands. The system, however, provides generic `ioctl` commands for different classes of devices.
## 16. /dev/fd
Newer systems provide a directory named /dev/fd whose entries are files named 0,1,2, and so on. Opening the file /dev/fd/n is equivalent to duplicating descriptor n, assuming that descriptor n is open.\
Some systems provide the pathnames /dev/stdin, /dev/stdout, and /dev/stderr. These pathnames are equivalent to /dev/fd/0, /dev/fd/1, and /dev/fd/2 respectively.
The main use of the /dev/fd files is from the shell. for example:
```c
# antipattern
filter file2 | cat file1 - file3 | lpr
# recomendation
filter file2 | cat file1 /dev/fd/0 file3 | lpr
```
First, cat reads file1, then its standard input (the output of the filter program on file2), and then file3.
> The special meaning of - as a command-line argument to refer to the standard input or the standard output is a kludge that has crept into many programs. There are also problems if we specify - as the first file, as it looks like the start of another command-line option. Using /dev/fd is a step toward uniformity and cleanliness.

## 17. Summary
This chapter has described the basic I/O functions provided by the UNIX System. These are often called the unbuffered I/O functions because each read or write invokes a system call into the kernel.

