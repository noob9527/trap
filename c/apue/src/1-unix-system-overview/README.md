# Chapter01 UNIX System Overview

## 1. Introduction
## 2. UNIX Architecture
- kernel
- system calls
- shell
- library routines
- applications
## 3. Logging in
### Login Name
### Shells
A shell is a command-line interpreter that reads user input and execute commands.
## 4. Files and Directories
### File System
### Filename
POSIX.1 recommends restricting filenames to consist of the following characters: letters(a-zA-Z), numbers(0-9), period, dash and underscore.
### Pathname
### Working Directory
Every process has a working directory, sometimes called the current working directory.
### Home Directory
When we log in, the working directory is set to our home directory. Our home directory is obtained from our entry in the password file.

## 5. Input and Output
### File Descriptors
File descriptors are normally small non-negative integers that the kernel uses to identify the files accessed by a process. Whenever it opens an existing file or creates a new file, the kernel returns a file descriptor that we use when we want to read or write the file.
### Standard Input, Standard Output, and Standard Error
By convention, all shells open three descriptors whenever a new program is run. If nothing special is done, all three are connected to the terminal. Most shells provide a way to redirect any or all of these three descriptors to any file.
### Unbuffered I/O
The constants STDIN_FILENO and STDOUT_FILENO are defined in <unistd.h> and specify the file descriptors for standard input and standard output. These values are 0 and 1, respectively, as required by POSIX.1.
### Standard I/O
The standard I/O functions provide a buffered interface to the unbuffered I/O functions. Using standard I/O relieves us from having to choose optimal buffer sized.\
The standard I/O constants stdin and stdout are also defined in the <stdio.h> header and refer to the standard input and standard output.

## 6. Programs and Processes
### Program
### Processes and Process ID
An executing instance of a program is called a process. The UNIX System guarantees that every process has a unique numeric identifier called the process ID. The process ID is always a non-negative integer.
### Process Control
There are three primary functions for process control: fork, exec, and waitpid.\
example:
```c
printf("%% ");	/* print prompt (printf requires %% to print %) */
while (fgets(buf, MAXLINE, stdin) != NULL) {
    if (buf[strlen(buf) - 1] == '\n')
        buf[strlen(buf) - 1] = 0; /* replace newline with null */

    if ((pid = fork()) < 0) {
        err_sys("fork error");
    } else if (pid == 0) {		/* child */
        execlp(buf, buf, (char *)0);
        err_ret("couldn't execute: %s", buf);
        exit(127);
    }

    /* parent */
    if ((pid = waitpid(pid, &status, 0)) < 0)
        err_sys("waitpid error");
    printf("%% ");
}
exit(0);
```
- We call fork to create a new process, which is a copy of the caller. We say that the caller is the parent and that the newly created process is the child. Then fork returns the non-negative process ID of the new child process to the parent, and returns 0 to the child. Because fork creates a new process, we say that it is called once--by the parent--but returns twice--in the parent and in the child.
- The combination of fork followed by exec is called spawning a new process on some operating systems. In the UNIX System, the two parts are separated into individual functions.
- Because of the child calls execlp to execute the new program file, the parent wants to wait for the child to terminate. This is done by calling waitpid, specifying which process to wait for: the pid argument, which is the process ID of the child. The waitpid function also returns the termination status of the child--the status variable--but in this simple program, we don't do anything with this value. Wecould examine it to determine how the child terminated.
### Threads and Thread IDs
All threads within a process share the same address space, file descriptors, stacks, and process-related attributes. Each thread executes on its own stack, although any thread can access the stacks of other threads in the same process. Because they can access the same memory, the threads need to synchronize access to shared data among themselves to avoid inconsistencies.
Like processes, threads are identified by IDs. Thread IDs, however, are local to a process. A thread ID from one process has no meaning in another process. We use thread IDs to refer to specific threads as we manipulate the threads within a process.
## 7. Error handling
On Linux, the error constants are listed in the errno(3) manual page.\
POSIX and ISO C define errno as a symbol expanding into a modifiable lvalue of type integer. This can be either an integer that contains the error number or a function that returns a pointer to the error number.\
There are two rules to be aware of with respect to errno. First, its value is never cleared by a routine if an error does not occur. Therefore, we should examine its value only when the return value from a function indicates that an error occurred. Second, the value of errno is never set to 0 by any of the functions, and none of the constants defined in <errno.h> has a value of 0.
### Error Recovery
The errors defined in <errno.h> can be divided into two categories: fatal and nonfatal. A fatal error has no recovery action. The best we can do is print an error message on the use's screen or to a log file, and then exit. Nonfatal errors, on the other hand, can sometimes be dealt with more robustly. Most nonfatal errors are temporary, such as a resource shortage, and might not occur when there is less activity on the system.\
Ultimately, it is up to the application developer to determine the cases where an application can recover from an error. If a reasonable recovery strategy can be used, we can improve the robustness of our application by avoiding an abnormal exit.

## 8. User Identification
### User ID
### Group ID
### Supplementary Group IDs
## 9. Signals
## 10. Time Values
Historically, UNIX systems have maintained two different time values:
1. Calendar time. This value counts the number of seconds since the Epoch(1970-1-1 00:00:00, Coordinated Universal Time(UTC)). (Older manuals refer to UTC as Greenwich Mean Time.) These time values are used to record the time when a file was loast modified. The primitive system data type time_t holds these time values.
2. Process time. This is also called CPU time and measures the central processor resources used by a process. Process time is measured in clocl ticks, which have historically been 50,60, or 1000 ticks per second. The primitive system data type clock_t holds these time values.
When we measure the execution time of a process, we'll see that the UNIX system maintains three values for a process:
- Clock time
- User CPU time
- System CPU time

The clock time, sometimes called wall clock time, is the amount of time the process takes to run, and its value depends on the number of other processes being run on the system. Whenever we report the clock time, the measurements are made with no other activities on the system.\
The user CPU time is the CPU time attributed to user instructions. The system CPU time is the CPU time attributed to the kernel when it executes on behalf of the process.
## 11. System Calls and Library Functions
## 12. Summary

