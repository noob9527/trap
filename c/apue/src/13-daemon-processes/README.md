# Chapter13 Daemon Processes

## 1. Introduction
skipped

## 2. Daemon Characteristics
skipped

## 3. Coding Rules
Some basic rules to coding a daemon prevent unwanted interactions from happening. We state these rules here and then show a function, `daemonize`, that implements them.
1. Call `umask` to set the file mode creation mask to a known value, usually 0. The inherited file mode creation mask could be set to deny certain permissions. If the daemon process creates files, it may want to set specific permissions. For example, if it creates files with group-read and group-write enabled, a file mode creation mask that turns off either of these permissions would undo its efforts. On the other hand, if the daemon calls library functions that result in files being created, then it might make sense to set the file mode create mask to a more restrictive value, since the library functions might not allow the caller to specify the permissions through an explicit argument.
2. Call `fork` and have the parent `exit`. This does several things. First, if the daemon was started as a simple shell command, having the parent terminate makes the shell think the command is done. Second, the child inherits the process group ID of the parent but gets a new process ID, so we're guaranteed that the child is not a process group leader. This is a prerequisite for the call to `setsid` that is done next.
3. Call `setsid` to create a new session. The following three steps occur.
    - The process becomes the leader of a new session
    - The process becomes the leader of a new process group
    - The process is disassociated from its controlling terminal.
4. Change the current working directory to the root direct. The current working directory inherited from the parent could be on a mounted file system. Since daemons normally exist until the system is rebooted, if the daemon stays on a mounted file system, that file system cannot be unmounted. Alternatively, some daemons might change the current working directory to a specific location where they will do all their work. For example, a line printer spooling daemon might change its working directory to its spool directory.
5. Unneeded file descriptors should be closed. This prevents the daemon from holding open any descriptors that it may have inherited from its parent(which could be a shell or some other process). We can use our `open_max` function or the `getrlimit` function to determine the highest descriptor and close all descriptors up to that value.
6. Some daemons open file descriptors 0, 1, and 2 to `/dev/null` so that any library routines that try to read from standard input or write to standard output or standard error will have no effect. Since the daemon is not associated with a terminal device, there is nowhere for output to be displayed, nor is there anywhere to receive input from an interactive user. Even if the daemon was started from an interactive session, the daemon runs in the background, and the login session can terminate without affecting the daemon. If other users log in on the same terminal device, we wouldn't want output from the daemon showing up on the terminal, and the users wouldn't expect their input to be read by the daemon.

## 4. Error Logging
The BSD `syslog` facility has been widely used since 4.2BSD. Most daemons use this facility.\
There are three ways to generate log messages:
1. Kernel routines can call the `log` function. These messages can be read by any user process that `opens` and `reads` the `/dev/klog` service.
2. Most user processes(daemons) call the `syslog(3)` function to generate log messages. This causes the message to be sent to the UNIX domain datagram socket `/dev/log`.
3. A user process on this host, or on some other host that is connected to this host by a TCP/OP network, can send log messages to UDP port 514. Note that the `syslog` function never generates these UDP datagrams: they require explicit network programming by the process generating the log message.

Normally, the `syslogd` daemon reads all three forms of log messages. On start-up this daemon reads a configuration file, usually `/etc/syslog.conf`, which determines where different classes of messages are to be sent.

## 5. Single-Instance Daemons
The file- and record-locking mechanism provides the basis for one way to ensure that only one copy of a daemon is running. If each daemon creates a file with a fixed name and places a write lock on the entire file, only one such write lock will be allowed to be created. Successive attempts to create write locks will fail, serving as an indication to successive copies of the daemon that another instance is already running.\
File and record locking provides a convenient mutual-exclusion mechanism. If the daemon obtains a write-lock on an entire file, the lock will be removed automatically if the daemon exits. This simplifies recovery, eliminating the need for us to clean up from the previous instance of the daemon.

## 6. Daemon Conventions
Several common conventions are followed by daemons in the UNIX System.
- If the daemon uses a lock file, the file is usually stored in `/var/run`. Note, however, that the daemon might need superuser permissions to create a file here. The name of the file is usually `name.pid`, where name is the name of the daemon or the service.
- If the daemon supports configuration options, they are usually stored in `/etc`. The configuration file is named `name.conf`, where name is the name of the daemon or the name of the service.
- Daemons can be started from the command line, but they are usually started from one of the system initialization scripts.
- If a daemon has a configuration file, the daemon reads the file when it starts, but usually won't look at it again. If an administrator changes the configuration, the daemon would need to be stropped and restarted to account for the configuration changes.

## 7. Client-Server Model
skipped

## 8. Summary
skipped
