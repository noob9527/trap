# Chapter08 Process Control

## 1. Introduction
skipped

## 2. Process Identifiers
Every process has a unique process ID, a non-negative integer. Because the process ID is the only well-known identifier of a process that is always unique, it is often used as a piece of other identifiers, to guarantee uniqueness.\
Although unique, process IDs are reused. As processes terminate, their IDs become candidates for reuse. Most UNIX systems implement algorithms to delay reuse, however, so that newly created processes are assigned IDs different from those used by processes that terminated recently. This prevents a new process from being mistaken for the previous process to have used the same ID.\
There are some special processes, but the details differ from implementation to implementation. Process ID 0 is usually the scheduler process and is often known as the swapper. No program on disk corresponds to this process, which is part of the kernel and is known as a system process. Process ID 1 is usually the `init` process and is invoked by the kernel at the end of the bootstrap procedure. The program file for this process was `/etc/init` in older versions of the UNIX System and is `/sbin/init` in newer versions. This process is responsible for bringing up a UNIX system after the kernel has been bootstrapped. `init` usually reads the system-dependent initialization files--the `/etc/rc*` files or `/etc/inittab` and the files in `/etc/init.d`--and brings the system to a certain state, such as multiuser. The `init` process never fies. It is a normal user process, not a system process within the kernel, like the swapper, although it does run with super user privileges.\
In addition to  the process ID, there are other identifiers for every process. The following functions return these identifiers.
```c
#include <unistd.h>

pid_t getpid(void);

// Returns: process ID of calling process

pid_t getppid(void);

// Returns: parent process ID of calling process

uid_t getuid(void);

// Returns: real user ID of calling process

uid_t geteuid(void);

// Returns: effective user ID of calling process

gid_t getgid(void);

// Returns: real group ID of calling process

gid_t getegid(void);

// Returns: effective group ID of calling process

```
Note that none of these functions has an error return.

## 3. `fork` Function
An existing process can create a new one by calling the `fork` function.
```c
#include <unistd.h>

pid_t fork(void);

// Returns: 0 in child, process ID of child in parent, -1 on error
```
The new process created by `fork` is called the child process. This function is called once but returns twice. The only difference in the returns is that the return value in the child is 0, whereas the return value in the parent is the process ID of the new child. The reason the child's process ID is returned to the parent is that a process can have more than one child, and there is no function that allows a process to obtain the process IDs of its children. The reason `fork` returns 0 to the child is that a process can have only a single parent, and the child can always call `getppid` to obtain the process ID of its parent. (Process ID 0 is reserved for use by the kernel, so it's not possible for 0 to be the process ID of a child)\
Both the child and the parent continue executing with the instruction that follows the call to `fork`. The child is a copy of the parent. For example, the child gets a copy of the parent's data space, heap, and stack. Note that this is a copy for the child; the parent and the child do not share these portions of memory. This parent and the child do share the text segment, however.\
Modern implementations don't perform a complete copy of the parent's data, stack, and heap, since a `fork` is often followed by an `exec`. Instead, a technique called copy-on-write(COW) is used. These regions are shared by the parent and the child and have their protection changed by the kernel to read-only. If either process tries to modify these regions, the kernel then makes a copy of that piece of memory only, typically a "page" in a virtual memory system.\
In general , we never know whether the child starts executing before the parent, or vice versa. The order depends on the scheduling algorithm used by the kernel. If it's required that the child and parent synchronize their actions, some for of interprocess communication is required.\
### File Sharing
One characteristic of fork is that all file descriptors that are open in the parent are duplicated in the child. We say "duplicated" because it's as if the `dup` function had been called for each descriptor. The parent and the child share a file table entry for every open descriptor.\
It is important that the parent and the child share the same file offset. Consider a process that forks a child, then waits for the child to complete. Assume that both processes write to standard output as part of their normal processing. If the parent has its standard output redirected(by a shell, perhaps), it is essential that the parent's file offset be updated by the child when the child writes to standard output. In this case, the child can write to standard output while the parent is waiting for it; on completion of a child, the parent can continue writing to standard output, knowing that its output will be appended to whatever the child wrote. If the parent and the child did not share the same file offset, this type of interaction would be more difficult to accomplish and would require explicit actions by the parent.\
If both parent and child write to the same descriptor, without any form of synchronization, such as having the parent wait for the child, their output will be intermixed (assuming it's a descriptor that was open before the fork). There are two normal cases for handling the descriptors after a `fork`.
1. The parent waits for the child to complete. In this case, the parent does not need to do anything with its descriptors. When the child terminates, any of the shared descriptors that the child read from or wrote to will have their file offsets updated accordingly.
2. Both the parent and the child go their own ways. Here, after the fork, the parent closes the descriptors that it doesn't need, and the child does the same thing. This way, neither interferes with the other's open descriptors. This scenario is often found with network servers.

The two main reasons for `fork` to fail are:
- If too many processes are already in the system, which usually means that something else is wrong
- If the total number of processes for this real user ID exceeds the system's limit.

There are two uses for fork:
1. When a process wants to duplicate itself so that the parent and the child can each execute different sections of code at the same time. This is common for network servers--the parent waits for a service request from a client. When the request arrives, the parent calls fork and lets the child handle the request. The parent goes back to waiting for the next service request to arrive.
2. When a process wants to execute a different program. This is common for shells. In this case, the child does an `exec` right after it returns from the `fork`.

Some operating systems combine the operations from step 2--a `fork` followed by an `exec`--into a single operation called a `spawn`. The UNIX System separates the two, as there are numerous cases where it is useful to `fork` without doing an `exec`. Also, separating the two operations allows the child to change the per-process attributes between the `fork` and the `exec`, such as I/O redirection, user ID, signal disposition, and so on.

## 4. `vfork` Function
The function `vfork` has the same calling sequence and same return values as `fork`, but the semantics of the two functions differ.\
The `vfork` function was intended to create a new process for the purpose of executing a new program. The `vfork` function creates the new process, just like `fork`, without copying the address space of the parent into the child, as the child won't reference that address space; the child simply calls `exec`(or `exit`) right after the `vfork`. Instead, the child runs in the address space of the parent until it calls either `exec` or `exit`. This optimization is more efficient on some implementations of the UNIX System, but leads to undefined results if the child modifies any data(except the variable used to hold the return value from `vfork`), makes function calls, or returns without calling `exec` or `exit`.\
Another difference between the two functions is that `vfork` guarantees that the child runs first, until the child calls `exec` or `exit`. When the child calls either of these functions, the parent resumes. (This can lead to deadlock if the child depends on further actions of the parent before calling either of these two functions.)

## 5. `exit` Functions
As we described in previous section, a process can terminate normally in five ways:
1. Executing a return from the main function.
2. Calling the `exit` function. This function is defined by ISO C and includes the calling of all exit handlers that have been registered by calling `atexit` and closing all standard I/O streams.
3. Calling the `_exit` or `_Exit` function. ISO C defines `_Exit` to provide a way for a process to terminate without running exit handlers or signal handlers. Whether standard I/O streams are flushed depends on the implementation. On UNIX systems, `_Exit` and `_exit` are synonymous and do not flush standard I/O streams. The `_exit` function is called by `exit` and handles the UNIX system-specific details; `_exit` is specified by POSIX.1.
4. Executing a return from the start routine of the last thread in the process. The return value of the thread is not used as the return value of the process, however. When the last thread returns from its start routine, the process exits with a termination status of 0.
5. Calling the `pthread_exit` function from the last thread in the process. As with the previous case, the exit status of the process in this situation is always 0, regardless of the argument passed to `pthread_exit`.

The three forms of abnormal termination are as follows:
1. Calling `abort`. This is a special case of the next item, as it generates the `SIGABRT` signal.
2. When the process receives certain signals. The signal can be generated by the process itself(e.g. by calling the `abort` function), by some other process, or by the kernel.
3. The last thread responds to a cancellation request. By default, cancellation occurs in a deferred manner: one thread request that another be canceled, and sometime later the target thread terminates.

Regardless of how a process terminates, the same code in the kernel is eventually executed. This kernel code closes all the open descriptors for the process, releases the memory that it was using, and so on.\
For any of the preceding case, we want the terminating process to be able to notify its parent how it terminated. For the three exit functions, this is done by passing an exit status as the argument to the function. In the case of an abnormal termination, however, the kernel generates a termination status to indicate the reason for the abnormal termination. In any case, the parent of the process can obtain the termination status from either the `wait` or the `waitpid` function.\
Note that we differentiate between the exit status, which is the argument to one of the three exit functions or the return value from main, and the termination status. The exit status is converted into a termination status by the kernel when `_exit` is finally called.\
When we described the `fork` function, it was obvious that the child has a parent process after the call to `fork`. Now we're talking about returning a termination status to the parent. But what happens if the parent terminates before the child? The answer is that the `init` process becomes the parent process of any process whose parent terminates. In such a case, we say that the process has been inherited by `init`. What normally happens is that whenever a process terminates, the kernel goes through all active processes to see whether the terminating process is the parent of any process that still exists. If so, the parent process ID of the surviving process is changed to be 1. This way, we're guaranteed that every process has a parent.\
Another condition we have to worry about is when a child terminates before its parent. If the child completely disappeared, the parent wouldn't be able to fetch its termination status when and if the parent was finally ready to check if the child had terminated. The kernel keeps a small amount of information for every terminating process, so that the information is available when the parent of the terminating process calls `wait` or `waitpid`. Minimally, this information consists of the process ID, the termination status of the process, and the amount of CPU time taken by the process. The kernel can discard all the memory used by the process and close its open files. In UNIX System terminology, a process that has terminated, but whose parent has not yet waited for it, is called a **zombie**. The `ps` command prints the state of a zombie process as Z. If we write a long-running program that forks many child processes, they become zombies unless we wait from them and fetch their termination status.\
The final condition to consider is this: What happens when a process that has been inherited by `init` terminates? Does it become a zombie? The answer is "no", because `init` is written so that whenever one of its children terminates, `init` calls one of the `wait` functions to fetch the terminations status. By doing this, `init` prevents the system from being clogged by zombies. When we say "one of init's children," we mean either a process that `init` generates directly or a process whose parent has terminated and has been subsequently inherited by `init`.

## 6. `wait` and `waitpid` Functions
When a process terminates, either normally or abnormally, the kernel notifies the parent by sending the `SIGCHLD` signal to the parent. Because the termination of a child is an asynchronous event--it can happen at any time while the parent is running--this signal is the asynchronous notification from the kernel to the parent. The parent can choose to ignore this signal, or it can provide a function that is called when the signal occurs: a signal handler. The default action for this signal is to be ignored. For now, we need to be aware that a process that calls `wait` or `waitpid` can
- Block, if all of its children are still running
- Return immediately with the termination status of a child, if a child has terminated and is waiting for its termination status to be fetched
- Return immediately with an error, if it doesn't have any child processes

If the process is calling `wait` because it received the `SIGCHLD` signal, we expect `wait` to return immediately. But if we call it at any random point in time, it can block.
```c
#include <sys/wait.h>

pid_t wait(int *statloc);
pid_t waitpid(pid_t pid, int *statloc, int options);

// Both return: process ID if OK, 0(see later), or -1 one error
```
The differences between these two functions are as follows:
- The `wait` function can block the caller until a child process terminates, whereas `waitpid` has an option that prevents it from blocking.
- The `waitpid` function doesn't wait for the child that terminates first; it has a number of options that control which process it waits for.

If a child has already terminated and is a zombie, `wait` returns immediately with that child's status. Otherwise, it blocks the caller until a child terminates. If the caller blocks and has multiple children, `wait` returns when one terminates. We can always tell which child terminated, because the process ID is returned by the function.\
For both functions, the argument `statloc` is a pointer to an integer. If this argument is not a null pointer, the termination status of the terminated process is stored in the location pointed to by the argument. If we don't care about the termination status, we simply pass a null pointer as this argument.\
Traditionally, the integer status that these two functions return has been defined by the implementation, with certain bits indicating the exit status (for a normal return), other bits indicating the signal number (for an abnormal return), one bit indicating whether a core file was generated, and so on. POSIX.1 specifies that the termination status is to be looked at using various macros that are defined in `<sys/wait.h>`. Four mutually exclusive macros tell us how the process terminated, and they all begin with `WIF`. Based on which of these four macros is true, other macros are used to obtain the exit status, signal number, and the like.\
The interpretation of the `pid` argument for `waitpid` depends on its value:
- pid == -1, Waits for any child process. In the respect, `waitpid` is equivalent to wait
- pid > 0, Waits for the child whose process ID equals `pid`
- pid == 0, Waits for any child whose process group ID equals that of the calling process.
- pid < -1, Waits for any child whose process group ID equals the absolute value of `pid`.

The `waitpid` function returns the process ID of the child that terminated and stores the child's termination status in the memory location pointed to by `statloc`. With `wait`, the only real error is if the calling process has no children. (Another error return is possible, in case the function call is interrupted by a signal.) With `waitpid`, however, it's also possible to get an error if the specified process or process group does not exist or is not a child of the calling process.\
The options argument lets us further control the operation of `waitpid`. This argument either is 0 or is constructed from the bitwise OR of the constants in `WCONTINUED`, `WNOHANG`, `WUNTRACED`.\
The `waitpid` function provides three features that aren't provided by the wait function.
1. The `waitpid` function lets us wait for one particular process, whereas the `wait` function returns the status of any terminated child.
2. The `waitpid` function provides a nonblocking version of `wait`. There are times when we want fetch a child's status, but we don't want to block.
3. The `waitpid` function provides support for job control with the `WUNTRACED` and `WCONTINUED` options.\

## 7. `waitid` Function
The single UNIX Specification includes an additional function to retrieve the exit status of a process. The `waitid` function is similar to `waitpid`, but provides extra flexibility.
```c
#include <sys/wait.h>

int waitid(idtype_t idtype, id_t id, siginfo_t *infop, int options);

// Returns: 0 if OK, -1 on error
```
like `waitpid`, `waitid` allows a process to specify which children to wait for. Instead of encoding this information in a single argument combined with the process ID or process group ID, two separate arguments are used. The id parameter is interpreted based on the value of `idtype`. The options argument is a bitwise OR of the several flags. These flags indicate which state changes the caller is interested in. At least one of `WCONTINUED`, `WEXITED`, or `WSTOPPED` must be specified in the options argument. The `infop` argument is a pointer to a `siginfo` structure. This structure contains detailed information about the signal generated that caused the state change in the child process.

## 8. `wait3` and `wait4` Functions
Most UNIX system implementations provide two additional functions: `wait3` and `wait4`. Historically, these two variants descend from the BSD branch of the UNIX System. The only feature provided by these two functions that isn't provided by the `wait`, `waitid` and `waitpid` functions is an additional argument that allows the kernel to return a summary of the resources used by the terminated process and all its child processes.
```c
#include <sys/types.h>
#include <sys/wait.h>
#include <sys/time.h>
#include <sys/resource.h>

pid_t wait3(int *statloc, int options, struct rusage *rusage);
pid_t wait4(pid_t pid, int *statloc, int options, struct rusage *rusage);
```
The resource information includes such statistics as the amount of user CPU time, amount of system CPU time, number of page faults, number of signals received, and the like. Refer to the `getrusage(2)` manual page for additional details.

## 9. Race Conditions
For our purposes, a race condition occurs when multiple processes are trying to do something with shared data and the final outcome depends on the order in which the processes run. The `fork` function is a lively breeding ground for race conditions, if any of the logic after the fork either explicitly or implicitly depends on whether the parent or child runs first after the `fork`. In general, we cannot predict which process runs first, what happens after that process tarts running depends on the system load and the kernel's scheduling algorithm.\
A process that wants to wait for a child to terminate must call one of the `wait` functions. If a process wants to wait for its parent to terminate, a loop of the following form could be used:
```c
while (getppid() != 1)
    sleep(1);
```
The problem with this type of loop, called polling, is that it wastes CPU time, as the caller is awakened every second to test the condition.\
To avoid race conditions and to avoid polling, some form of signaling is required between multiple processes. Signals can be used for this purpose, Various forms of interprocess communication can also be used.

## 10. `exec` Functions
When a process calls one of the `exec` functions, that process is completely replaced by the new program, and the new program starts executing at its `main` function. The process ID does not change across an `exec`, because a new process is not created; `exec` merely replaces the current process--its text, data, heap, and stack segments--with a brand-new program from disk.\
There are seven different `exec` functions, but we'll often simply refer to "the exec function," which means that we could use any of the seven functions. These seven functions round out the UNIX System process control primitives. With `fork`, we can create new processes; and with the `exec` functions, we can initiate new programs. The `exit` function and the `wait` functions handle termination and waiting for termination. These are the only process control primitives we need.
```c
#indlude <unistd.h>

int execl(const char *pathname, const char *arg0, ... /* (char *)0 */);
int execv(const char *pathname, char *const argv[]);
int execle(const char *pathname, const char *arg0, ... /* (char *)0 */, char *const envp[]);
int execve(const char *pathname, char *const argv[], char *const envp[]);
int execlp(const char *filename, const char *arg0, ... /* (char *)0 */);
int execvp(const char *filename, char *const argv[]);
int fexecve(int fd, char *const argv[], char *const envp[]);

// All seven return: -1 on error, no return on success
```
The first difference in these functions is that the first four take a pathname argument, the next two take a filename argument, and the last one takes a file descriptor argument. When a filename argument is specified,
- If filename contains a slash, it is taken as a pathname.
- Otherwise, the executable file es searched for in the directories specified by the PATH environment variable.

The PATH variable contains a list of directories, called path prefixes, that are separated by colons. For example, the name-value environment string
```bash
PATH=/bin:/usr/bin/:/usr/local/bin/:.
```
If either `execlp` or `execvp` finds an executable file using one of the path prefixes, but the file isn't a machine executable that was generated by the link editor, the function assumes that the file is a shell script and tries to invoke `/bin/sh` with the filename as input to the shell.\
With `fexecve`, we avoid the issue of finding the correct executable file altogether and rely on the caller to do this. Bu using a file descriptor, the caller can verify the file is in fact the intended file and execute it without a race. Otherwise, a malicious user with appropriate privileges could replace the executable file(or a portion of the path to the executable file) after it has been located and verified, but before the caller can execute it.
The next difference concerns the passing of the argument list(l stands for list and v stands for vector).a The functions `execl`, `execlp`, and `execle` require each of the command-line arguments to the new program to be specified as separate arguments. We mark the end of the arguments with a null pointer. For the other four functions(`execv`, `execvp`, `execve`, and `fexecve`), we have to build an array of pointers to the arguments, and the address of this array is the argument to these three functions.\
Before using ISO C prototypes, the normal way to show the command-line arguments for the three functions `execl`, `execle`, and `execlp` was
```
char *arg0, char *arg1, ..., char *argn, (char *)0
```
This syntax explicitly shows that the final command-line argument is followed by a null pointer. If this null pointer is specified by the constant 0, we we must cast it to a pointer; if we don't, it's interpreted as an integer argument. If the size of an integer is different from the size of a `char *`, the actual arguments to the `exec` function will be wrong.\
The final difference is the passing of the environment list to the new program. The three functions whose names end in an e (`execle`, `execve`, and `fexecve`) allow us to pass a pointer to an array of pointers to the environment strings. The other four functions, however, use the `environ` variable in the calling process to copy the existing environment for the new program. Normally, a process allows its environment to be propagated to its children, but in some cases, a process wants to specify a certain environment for a child. One example of the latter is the `login` program when a new login shell is initiated. Normally, `login` creates a specific environment with only a few variables defined and lets us, through the shell start-up file, add variables to the environment when we log in.\
The arguments for these seven `exec` functions are difficult to remember. The letters in the function names help somewhat. The letter p means that the function takes a filename argument and uses the PATH environment variable to find the executable file. The letter l means that the function takes a list of arguments and is mutually exclusive with the letter v, which means that it takes an `argv[]` vector. Finally, the letter e means that the function takes an `envp[]` array instead of using the current environment.\

## 11. Changing User IDs and Group IDs
skipped
### `setreuid` and `setregid` Functions
skipped
### `seteuid` and `setegid` Functions
skipped
### `Group IDs`
skipped
## 12. Interpreter Files
All contemporary UNIX systems support interpreter files. These files are text files that begin with a line of the form
```bash
#! pathname [optional-argument]
```
The space between the exclamation point and the pathname is optional. The most common of these interpreter files begin with the line
```bash
#!/bin/sh
```
The pathname is normally an absolute pathname, since no special operations are performed on it. The recognition of these files is done within the kernel as part of processing the `exec` system call. The actual file that gets executed by the kernel is not the interpreter file, but rather the file specified by the pathname on the first line of the interpreter file. Be sure to differentiate between the interpreter file--a text file that begins with #!--and the interpreter, which is specified by the pathname on the first line of the interpreter file.

## 13. `system` Functions
It is convenient to execute a command string from within a program. For example, assume that we want to put a time-and-data stamp into a certain file. We could use the functions described in previous section to do this: call `time` to get the current calendar time, then call `localtime` to convert it to a broken-down time, then call `strftime` to format the result, and finally write the result to the file. It is much easier, however, to say
```c
system("date > file");
```
ISO C defines the `system` function, but its operation is strongly system dependent. POSIX.1 includes the `system` interface, expanding on the ISO C definition to describe its behaviour in a POSIX environment.
```
#include <stdlib.h>

int system(const char *cmdstring);

// Returns: see below
```
If `cmdstring` is a null pointer, `system` returns nonzero only if a command processor is available. This feature determines whether the `system` function is supported on a given operating system. Under the UNIX System, `system` is always available.\
Because `system` is implemented by calling `fork`, `exec`, and `waitpid`, there are three types of return values:
1. If either the `fork` fails or `waitpid` returns an error other than `EINTR`, system returns -1 with `errno` set to indicate the error.
2. If the `exec` fails, implying that the shell can't be executed, the return value is as if the shell had executed `exit(127)`.
3. Otherwise, all three functions--`fork`, `exec`, and `waitpid`succeed, and the return value from `syustem` is the termination status of the shell, in the format specified for `waitpid`.

### Set-User-ID Programs
What happens if we call `system` from a set-user-ID program? Doing so creates a security hole and should never be attempted.

## 14. Process Accounting
skipped

## 15. User Identification
Any process can find out its real and effective user ID and group ID. Sometimes, however, we want to find out the login name of the user who's running the program. We could call `getpwuid(getuid())`, but what if a single user has multiple login names, each with the same user ID?(A person might have multiple entries in the password file with the same user ID to have a different login shell for each entry.) The system normally keeps track of the name we log in under, and the `getlogin` function provides a way to fetch that login name.
```c
#include<unistd.h>

char *getlogin(void);

// Returns: pointer to string giving login name if OK, NULL on orror
```
This function can fail if the process is not attached to a terminal that a user logged in to. We normally call these processes daemons.\
Given the login name, we can then use it to look up the user in the password file--to determine the login shell, for example--using `gwtpwnam`.

## 16. Process Scheduling
Historically, the UNIX System provided processes with only coarse control over their scheduling priority. The scheduling policy and priority were determined by the kernel. A process could choose to run with lower priority by adjusting its nice value(thus a process could be "nice" and reduce its share of the CPU by adjusting its nice value). Only a privileged process was allowed to increase its scheduling priority.\
In the Single UNIX Specification, nice values range from 0 to `(2 * NZERO)-1`, although some implementations support a range from 0 to `2*NZERO`. Lower nice values have higher scheduling priority. Although this might seem backward, it actually makes sense: the more nice you are, the lower your scheduling priority is. `NZERO` is the default nice value of the system.\
A process can retrieve and change its nice value with the `nice` function. With this function, a process can affect only its own nice value; it can't affect the nice value of any other process.
```c
#include <unistd.h>

int nice(int incr);

Returns: new nice value - NZERO if OK, -1 on error
```
The `incr` argument is added to the nice value of the calling process. If `incr` is too large, the system silently reduces it to the maximum legal value. Similarly, if `ince` is too small, the system silently increases it to the minimum legal value. Because -1 is a legal successful return value, we need to clear `errno` before calling `nice` and check its value if `nice` returns -1. If the call to `nice` succeeds and the return value is -1, then `errno` will still be zero. If `errno` is nonzero, it means that the all to `nice` failed.

## 17. Process Times
In previous section, we described three times that we can measure: wall clock time, user CPU time, and system CPU time. Any process can call the `times` function to obtain these values for itself and any terminated children.
```c
#include <sys/times.h>

clock_t times(struct tms *buf);

// Returns: elapsed wall clock time in clock ticks if OK, -1 on error
```
This function fills in the `tms` structure pointed to by `buf`.
Note that the structure does not contain any measurement for the wall clock time. Instead, the function returns the wall clock time as the value of the function, each time it's called. This value is measured from some arbitrary point in the past, so we can't use its absolute value; instead, we use its relative value. For example, we call `times` and save the return value. At some later time, we call `times` again and subtract the earlier return value from the new return value. The difference is the wall clock time.\
The two structure fields for child processes contain values only for children that we have waited for with one of the `wait` functions discusses earlier in this chapter.

## 18. Summary
skipped

