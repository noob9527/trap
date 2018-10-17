# Chapter17 Advanced IPC

## 1. Introduction
skipped

## 2. UNIX Domain Sockets
UNIX domain sockets are used to communicate with processes running on the same machine. Although Internet domain sockets can be used for this same purpose, UNIX domain sockets are more efficient. UNIX domain sockets only copy data; they have no protocol processing to perform, not network headers to add or remove, no checksums to calculate, no sequence numbers to generate, and no acknowledgements to send.\
UNIX domain sockets provide both stream and datagram interfaces. The UNIX domain datagram service is reliable, however. Messages are neither lost nor delivered out of order. UNIX domain sockets are like a cross between sockets and pipes. You can use the network-oriented sockets interface with them, or you can use the `socketpair` function to create a pair of unnamed, connected, UNIX domain sockets.\
```c
#include <sys/socket.h>

int socketpair(int domain, int type, int protocol, int sockfd[2]);

// Returns: 0 if OK, -1 on error
```

### Naming UNIX Domain Sockets
Although the `socketpaire` function creates sockets that are connected to each other, the individual sockets don't have names. This means that they can't be addressed by unrelated process.\
In previous sections, we learned how to bind an address to an Internet domain socket. Just as with Internet domain sockets, UNIX domain sockets can be named and used to advertise services. The address format used with UNIX domain sockets differs from that used with Internet domain sockets, however.\
n address for a UNIX domain socket is represented by a `sockaddr_un` structure. The `sun_path` member of the `sockaddr_un` structure contains a pathname. When we bind an address to a UNIX domain socket, the system creates a file of type `S_IFSOCK` with the same name.\
This file exists only as a means of advertising the socket name to clients. The file can't be opened or otherwise used for communication by applications.\
If the file already exists when we try to bind the same address, the `bind` request will fail. When we close the socket, this file is not automatically removed, so we need to make sure that we unlink it before our application exits.

## 3. Unique Connections
A server can arrange for unique UNIX domain connections to clients using the standard bind, listen, and accept functions. Clients use connect to contact the server; after the connect request is accepted by the server, a unique connection exists between the client and the server. This style of operation is the same that we illustrated with Internet domain sockets in previous sections.

Details is skipped

## 4. Passing File Descriptor
Passing an open file descriptor between processes is a powerful technique. It can lead to different ways of designing client-server applications. It allows one process to do everything that is required to open a file and simply pass back to the calling process a descriptor that can be used with all the I/O functions. All the details involved in opening the file or device are hidden from the client.\
When we pass an open file descriptor from one process to another, we want the passing process and the receiving process to share the same file table entry.\
Technically, we are passing a pointer to an open file table entry from one process to another. This pointer is assigned the first available descriptor in the receiving process.(Say that we are passing an open file descriptor mistakenly gives the impression that the descriptor number in the receiving process is the same as in the sending process, which usually isn't true). Having two processes share an open file table is exactly what happens after a `fork`.\
What normally happens when a descriptor is passed from one process to another is that the sending process, after passing the descriptor, then closes the descriptor. Closing the descriptor by the sender doesn't really close the file or device, since the descriptor is still considered open by the receiving process(even if the receiver hasn't specifically received the descriptor yet).

## 5. An Open Server, Version 1
skipped

## 6. An Open Server, Version 2
skipped

## 7. Summary
skipped
