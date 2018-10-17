# Chapter09 Process Relationships

## 1. Introduction
skipped

## 2. Terminal Logins
skipped
### BSD Terminal Logins
skipped
### Mac OS X Terminal Logins
skipped
### Linux Terminal Logins
skipped

## 3. Network Logins
skipped
### BSD Network Logins
skipped
### Mac OS X Network Logins
skipped
### Linux Network Logins
skipped

## 4. Process Groups
A process group is a collection of one or more processes, usually associated with the same job, that can receive signals from the same terminal. Each process group has a unique process group ID. Process group IDs are similar to process IDs: they are positive integers and can be stored in a `pid_t` data type. The function `getpgrp` returns the process group ID of the calling process.
```c
#include <unistd.h>

pid_t getpgrp(void);

// Returns: process group ID of calling process
```
In older BSD-derived systems, the `getpgrp` function took a `pid` argument and returned the process group for that process. The Single UNIX Specification defines the `getpgid` function that mimics this behavior.
```c
#include <unistd.h>

pid_t getpgid(pid_t pid);

// Returns: process group ID if OK, -1 on error
```
If `pid`is 0，the process group ID of the calling process is returned. Thus `getpgid(0)` is equivalent to `getpgrp()`. Each process group can have a process group leader. The leader is identified by its process group ID being equal to its process ID.\
It is possible for a process group leader to create a process group, create processes in the group, and then terminate. The process group still exists, as long as at least one process is in the group, regardless of whether the group leader terminates. This is called the process group lifetime--the period of time that begins when the group is created and ends when the last remaining process leaves the group. The last remaining process in the process group can either terminate or enter some other process group.\
A process joins an existing process group or creates a new process group by calling `setpgid`. (In the next section, we'll see that `setsid` also creates a new process group.)
```c
#include <unistd.h>

int setpgid(pid_t pid, pid_t pgid);

// Returns: 0 if OK, -1 on error
```
This function sets the process group ID to `pgid` in the process whose process ID equals `pid`. If the two arguments are equal, the process specified by `pid` becomes a process group leader. If `pid` is 0, the process ID of the caller is used. Also, if `pgid` is 0, the process ID specified by `pid` is used as the process group ID.
A process can set the process group ID of only itself or any of its children. Furthermore, it can't change the process group ID of one of its children after that child has called on of the `exec` functions.\
In most job-control shells, this function is called after a `fork` to have the parent set the process group ID of the child, and to have the child set its own process group ID. One of these calls is redundant, but by doing both, we are guaranteed that the child is placed into its own process group before either process assumes that this has happened. If we didn't do this, we would have a race condition, since the child's process group membership would depend on which process executes first.\
When we discuss signals, we'll see how we can send a signal to either a single process or a process group. Similarly, the `waitpid` function from previous section lets us wait for either a single process or one process from a specified process group.

## 5. Sessions
A session is a collection of one or more process groups. The processes in a process group are usually placed there by a shell pipeline.\
A process establishes a new session by calling the `setsid` function
```c
#include <unistd.h>

pid_t setsid(void);

// Returns: process group ID if OK, -1 on error
```
If the calling process is not a process group leader, this function creates a new session. Three things happen.
1. The process becomes the session leader of this new session. (A session leader is the process that creates a session.) The process is the only process in this new session.
2. The process becomes the process group leader of a new process group. The new process group ID is the process ID of the calling process.
3. The process has no controlling terminal. If the process had a controlling terminal before calling `setsid`, that association is broken.

This function returns an error if the caller is already a process group leader. To ensure this is not the case, the usual practice is to call `fork` and have the parent terminate and the child continue. We are guaranteed that the child is not a process group leader, because the process group ID of the parent is inherited by the child, but the child gets a new process ID. Hence, it is impossible for the child's process ID to equal its inherited process group ID.\
The single UNIX Specification talks only about a "session leader"; there is no "session ID" similar to a process ID or a process group ID. Obviously, a session leader is a single process that has a unique process ID, so we could talk about a session ID that is the process ID of the session leader. This concept of a session ID was introduced in SVR4. Historically, BSD-based systems didn't support this notion, but have since been updated to include it. The `getsid` function returns the process group ID of a process's session leader.
```c
#include <unistd.h>

pid_t getsid(pid_t pid);

Returns: session leader's process group ID if OK, -1 on error
```
If `pid` is 0, `getsid` returns the process group ID of the calling process's session leader. For security reasons, some implementations may restrict the calling process from obtaining the process group ID of the session leader if `pid` doesn't belong to the same session as the caller.

## 6. Controlling Terminal
Sessions and process groups have a few other characteristics.
- A session can have a single controlling terminal. This is usually the terminal device or pseudo terminal device on which we log in.
- The session leader that establishes the connection to the controlling terminal is called the controlling process.
- The process groups within a session can be divided into a single foreground process group and one or more background process groups.
- If a session has a controlling terminal, it has a single foreground process group and all other process groups in the session a re background process groups.
- Whenever we press the terminal's interrupt key(often DELETE or Control-C), the interrupt signal i s sent to all processes in the foreground process group.
- Whenever we press the terminal's quit key(often Control-backslash), the quit signal is sent to all processes in the foreground precess group.
- If a modern disconnect is detected by the terminal interface, the hang-up signal is sent to the controlling process(the session leader).

Usually, we don't have to worry about the controlling terminal; it is established automatically when we log in. There are times when a program wants to talk to the controlling terminal, regardless of whether the standard input or standard output is redirected. The way a program guarantees that it is talking to the controlling terminal is to open the file `/dev/tty`. This special file is a synonym within the kernel for the controlling terminal. Naturally, if the program doesn't have a controlling terminal, the `open` of this device will fail.

## 7. `tcgetpgrp`, `tcsetpgrp`, and `tcgetsid` Functions
We need a way to tell the kernel which process group is the foreground process group, so that the terminal device driver knows where to send the terminal input and the terminal-generated signals.
```c
#include <unistd.h>

pid_t tcgetpgrp(int fd);

// Returns: process group ID of foreground process group if OK, -1 on error

int tcsetpgrp(int fd, pid_t pgrpid);

// Returns: 0 if OK, -1 on error
```
The function `tcgetpgrp` returns the process group ID of the foreground process group associated with the terminal open on `fd`.\
If the process has a controlling terminal, the process can call `tcsetpgrp` to set the foreground process group ID to `pgrpid`. The value of `pgrpid` must be the process group ID of a process group in the same session, and `fd` must refer to the controlling terminal of the session.\
Most applications don't call these two functions directly. Instead, the functions are normally called by job-control shells.

## 8. Job Control
Job control is a feature that was added to BSD around 1980. This feature allows us to start multiple jobs(groups of processes) from a single terminal and to control which jobs can access the terminal and which jobs are run in the background. Job control requires three forms of support:
1. A shell that supports job control
2. The terminal driver in the kernel must support job control
3. The kernel must support certain job-control signals

The interaction with the terminal driver arises because a special terminal character affects the foreground job: the suspend key(typically Control-Z). Entering this character causes the terminal driver to send the `SIGTSTP` signal to all processes in the foreground process group. The jobs in any background process groups aren't affected. The terminal driver looks for three special characters, which generate signals to the foreground process group.
- The interrupt character (typically DELETE or Control-C) generates `SIGINT`.
- The quit character (typically Control-backslash) generates `SIGQUIT`.
- The suspend character (typically Control-Z) generates `SIGSTP`.

Another job control condition can arise that must be handled by the terminal driver. Since we can have a foreground job and one or more background jobs, which of these receives the characters that we enter at the terminal? Only the foreground job receives terminal input. It is not an error for a background job to try to read from the terminal, but the terminal driver detects this and sends a special signal to the background job: `SIGTTIN`. This signal normally stops the background job; by using the shell, we are notified of this event and can bring the job into the foreground so that it can read from the terminal.\
What happens if a background job sends its output to the controlling terminal? This is an option that we can allow or disallow. Normally, we use the `stty(1)` command to change this option.\
Is job control necessary or desirable? Job control was originally designed and implemented before windowing terminals were widespread. Some people claim that a well-designed windowing system removes any need for job control. Some complain that the implementation of job control--requiring support from the kernel, the terminal driver, the shell, and some applications--is a hack. Some use job control with a windowing system, claiming a need for both. Regardless of your opinion, job control is a required feature of POSIX.1.

## 9. Shell Execution of Programs
skipped

## 10. Orphaned Process Groups
skipped

## 11. FreeBSD Implementation
skipped

## 12. Summary
skipped
