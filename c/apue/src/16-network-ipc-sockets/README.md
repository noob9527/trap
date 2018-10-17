# Chapter16 Network IPC: Sockets

## 1. Introduction
## 2. Socket Descriptors
A socket is an abstraction of a communication endpoint. Just as they would use file descriptors to access files, applications use socket descriptors to access sockets. Socket descriptors are implemented as file descriptors in the UNIX System. Indeed, many of the functions that deal with file descriptors, such as `read` and `write`, will work with a socket descriptor.\
To create a socket, we call the socket function.
```c
#include <sys/socket.h>
int socket(int domain, int type, int protocal);
// returns: file(socket) descriptor if OK, -1 on error
```
The domain argument determines the nature of the communication, including the address format(described in more detail in the next section). The constants start with `AF_`(for address family) because each domain has its own format for representing an address.
| Domain | Description |
| ------ | ----------- |
| `AF_INET` | IPv4 Internet domain |
| `AF_INET6` | IPv6 Internet domain |
| `AF_UNIX` | UNIX domain |
| `AF_UNSPEC` | unspecified |

Most systems define the `AF_LOCAL` domain also, which is an alias for `AF_UNIX`. The `AF_UNSPEC` domain is a wildcard that represents "any" domain, Historically, some platforms provide support for additional network protocols, such as `AF_IPX` or the NetWare protocol family, but domain constants for these protocols are not defined by the POSIX.1 standard.\
The type argument determines the type of the socket, which further determines the communication characteristics. The socket types defined by POSIX.1 are summarized in the Figure below, but implementations are free to add support for additional types.
| Type | Description |
| ------ | ----------- |
| `SOCK_DGAM` | fixed-length, connectionless, unreliable messages |
| `SOCK_RAW` | datagram interface to IP(optional in POSIX.1) |
| `SOCK_SEQPACKET` | fixed-length, sequenced, reliable, connection-oriented messages |
| `SOCK_STREAM` | sequenced, reliable, bidirectional, connection-oriented byte streams |

The protocol argument is usually zero, to select the default protocol for the given domain and socket type. When multiple protocols are supported for the same domain and socket type, we can use the protocol argument to select a particular protocol. The default protocol for a SOCK_STREAM socket in the `AF_INET` communication domain is TCP(Transmission 
Control Protocol). The default protocol for a `SOCK_DGRAM` socket in the `AF_INET` communication domain is UDP(User Datagram Protocol). The figure below lists the protocols defined for the Internet domain sockets.
| Type | Description |
| ------ | ----------- |
| `IPPROTO_IP` | IPv4 Internet Protocol |
| `IPPROTO_IPV6` | IPv6 Internet Protocol(optional in POSIX.1) |
| `IPPROTO_ICMP` | Internet Control Message Protocol |
| `IPPROTO_RAW` | Raw IP packets protocol(optional in POSIX.1) |
| `IPPROTO_TCP` | Transmission Control Protocol |
| `IPPROTO_UDP` | User Datagram Protocol |

With a datagram (`SOCK_FGRAM`) interface, no logical connection needs to exist between peers for them to communicate. All you need to do is send a message addressed to the socket being used by the peer process.\
A datagram, therefore, provides a connectionless service. A byte stream(`SOCK_STREAM`), in contrast, requires that, before you can exchange data, you set up a logical connection between your socket and the socket belonging to the peer with which you wish to communicate.\
A datagram is a self-contained message. Sending a datagram is analogous to mailing someone a letter. You can mail many letters, but you can't guarantee the order of delivery, and some might get lost along the way. Each letter contains the address of the recipient, making the letter independent from all the others. Each letter can even go to different recipients.\
In contrast, using a connection-oriented protocol for communicating with a peer is like making a phone call, First, you need to establish a connection by placing a phone call, but after the connection is in place, you can communicate bidirectionally with each other. The connection is a peer-to-peer communication channel over which you talk. Your words contain no addressing information, as a point-to-point virtual connection exists between both ends of the call, and the connection itself implies a particular source and destination.\
A `SOCK_STREAM` socket provides a byte-stream service; applications are unaware of message boundaries. This means that when we read data from a `SOCK_STREAM` socket, it might not return the same number of bytes written by the sender. We will eventually get everything sent to us, but it might take several function calls.\
A `SOCK_SEQPACKET` socket is just like a `SOCK_STREAM` socket except that we get a message-based service instead of a byte-stream service. This means that the amount of data received from a `SOCK_SEQPACKET` socket is the same amount as was written. The Stream Control Transmission Protocol(SCTP) provides a sequential packet service in the Internet domain.
A `SOCK_RAW` socket provides a datagram interface directly to the underlying network layer (which means IP in the Internet domain). Applications are responsible for building their own protocol headers when using this interface, because the transport protocols(TCP and UDP, for example) are bypassed. Superuser privileges are required to create a raw socket to prevent malicious applications from creating packets that might bypass established security mechanisms.\
Calling socket is similar to calling `open`. In both cases, you get a file descriptor that can be used for I/O. When you are done using the file descriptor, you call `close` to relinquish access to the file or socket and free up the file descriptor for reuse.\
Although a socket descriptor is actual a file descriptor, you can't use a socket descriptor with every function that accepts a file descriptor argument. APUE Figure 16.4(P592) summarizes most of the functions we've described so far that are used with file descriptors and describes how they behave when used with socket descriptors.\
Communication on a socket is bidirectional. We can disable I/O on a socket with the shutdown function.
```c
#include <sys/socket.h>
int shutdown(int sockfd, int how);
// returns: 0 if OK, -1 on error
```
If `how` is `SHUT_RD`, then reading from the socket is disabled. If `how` is `SHUT_WR`, then we can't use the socket for transmitting data. We can use `SHUT_WDWR` to disable both data transmission and reception.\
Given that we can `close` a socket, why is shutdown needed? There are several reasons. First, `close` will deallocate the network endpoint only when the last active reference is closed. If we duplicate the socket (with `dup`, for example), the socket won't be deallocated until we close the last file descriptor referring to it. The `shutdown` function allows us to deactivate a socket independently of the number of active file descriptors referencing it. Second, it is sometimes convenient to shut a socket down in one direction only. For example, we can shut a socket down for writing if we want the process we are communicating with to be able to tell when we are done transmitting data, while still allowing us to use the socket to receive data sent to us by the process.

## 3. Addressing
Before we learn to do something useful with a socket, we need to learn how to identify the process with which we wish to communicate. Identifying the process has two components. The machines network address helps us identify the computer on the network we wish to contact, and the service, represented by a port number, helps us identify the particular process on the computer.

### Byte Ordering
When communicating with processes running on the same computer, we generally don't have to worry about byte ordering. The byte order is a characteristic of the processor architecture, dictating how bytes are ordered within larger data types, such as integers.\
Network protocols specify a byte ordering so that heterogeneous computer systems can exchange protocol information without confusing the byte ordering. The TCP/IP protocol suite uses `big-endian` byte order. The byte ordering becomes visible to applications when they exchange formatted data. With TCP/IP., addresses are presented in network byte order, so applications sometimes need to translate them between the processor's byte order and the network byte order.\
Four functions are provided to convert between the processor byte order and the network byte order for TCP/IP applications.
```c
#include <arpa/inet.h>

uint32_t htonl(uint32_t hostint32);
// returns: 32-bit integer in network byte order

uint16_t htons(uint16_t hostint16);
// returns: 16-bit integer in network byte order

uint32_t ntohl(uint32_t netint32);
// returns: 32-bit integer in network byte order

uint16_t ntohs(uint16_t netint16);
// returns: 16-bit integer in network byte order
```
The h is for "host" byte order, and the n is for "network" byte order. The l is for "long" integer, and the s is for "short" integer.

### Address Formats
An address identifies a socket endpoint in a particular communication domain. The address format is specific to the particular domain. So that addresses with different formats can be passed to the socket functions, the addresses are cast to a generic `sockaddr` address structure:
```c
struct sockaddr {
    sa_family_t sa_family;  // address family (e.g. AF_INET)
    char sa_data[];         // variable-length address
    // ...
};
```
Implementations are free to add more members and define a size for the `sa_data` member. For example, on Linux, the structure is defined as
```c
struct sockaddr {
    sa_family_t sa_family;
    char sa_data[14];
};
```
In the IPv4 domain(`AF_INET`), a socket address is represented by a `sockaddr_in` structure:
```c
struct in_addr {
    in_addr_t s_addr;   // IPv4 address
};
struct socktaddr_in {
    sa_family_t sin_family;     // address family
    in_port_t sin_port;         // port number
    struct in_addr sin_addr;    // IPv4 address
};
```
The `in_port_t` data type is defined to be a `uint16_t`. The `in_addr_t` data type is defined to be a `uint32_t`.
> Note: `sockaddr_in` can be casted to a `sockaddr`
In contrast to the `AF_INET` domain, the IPv6 domain(`AF_INET6`) socket address is represented by a `sockaddr_in6` structure.
> Note: although the `sockaddr_in` and `socktaddr_in6` structures are quite different, they are both passed to the socket routines cast to a `sockaddr` structure.
It is sometimes necessary to print an address in a format that is understandable by a person instead of a computer.
```c
#include <arpa/inet.h>

const char *inet_ntop(int domain, const void *restrict addr, char *restrict str, socklen_t size);
// returns: pointer to address string on success, NULL on error
int inet_pton(int domain, const char *restrict str, void *restrict addr);
// returns: 1 on success, 0 if the format is invalid, or -1 on error
```
The `inet_ntop` function converts a binary address in network byte order into a text string; `inet_pton` converts a text string into a binary address in network byte order. Only two domain values are supported: `AF_INET` and `AF_INET6`.\
For `inet_ntop`, the size parameter specifies the size of the buffer(`str`) to  hold the text string. Two constants are defined to make our job easier: `INET_ADDRSTRLEN` is large enough to hold a text string representing an IPv4 address, and `INET6_ADDRSTRLEN` is large enough to hold a text string representing an IPv6 address. For `inet_pton`, the `addr` buffer needs to be large enough to hold a 32-bit address if domain is `AF_INET` or large enough to hold a 128-bit address if domain is `AF_INET6`. Example:
```c
#include <sys/types.h>
#include <sys/socket.h>
#include <netdb.h>
#include <stdio.h>
#include <arpa/inet.h>

#define SERVER_PORT 22000

int main(int argc, char *argv[]) {
    struct sockaddr_in server_address;
    char buffer[INET_ADDRSTRLEN];

    inet_pton(AF_INET, "127.0.0.1", &server_address);
    inet_ntop(AF_INET, &server_address, buffer, INET_ADDRSTRLEN);

    printf("buffer:%s\n", buffer);
    printf("addr:%u\n", server_address.sin_addr);
}
```

### Address Lookup
skipped

### Associating Addresses with Sockets
We need to associate a will-known address with the server's socket on which client requests will arrive. Clients need a way to discover the address to use to contact a server, and the simplest scheme is for a server to reserve an address and register it in `/etc/services` or with a name service.\
We use the `bind` function to associate an address with a socket.
```c
#include <sys/socket.h>
int bind(int sockfd, const struct sockaddr *addr, socklen_t len);
// returns: 0 if OK, -1 on error
```
There are several restrictions on the address we can use:
- The address we specify must be valid for the machine on which the process is running; we can't specify an address belonging to some other machine.
- The address must match the format supported by the address family we used to create the socket.
- The port number in the address cannot be less than 1024 unless the process has the appropriate privilege.(i.e., is the superuser).
- Usually, only one socket endpoint can be bound to a given address, although some protocols allow duplicate bindings.
For the Internet domain, if we specify the special IP address `INADDR_ANY`(defined in `netinet/in.h`), the socket endpoint will be bound to all the system's network interfaces. This means that we can receive packets from any of the network interface cards installed in the system. We'll see in the next section that the system will choose an address and bind it to our socket for us if we call connect or listen without first binding an address to the socket.

## 4. Connection Establishment
If we're dealing with a connection-oriented network service(`SOCK_STREAM` or `SOCK_SEQPACKET`), then before we can exchange data, we need to create a connection between the socket of the process requesting the service (the client) and the process providing the service (the server). We use the `connect` function to create a connection.
```c
#include <sys/socket.h>
int connect(int sockfd, const struct sockaddr *addr, socklen_t len);
// returns: 0 if OK, -1 on error
```
The address we specify with `connect` is the address of the server with which we wish to communicate. If `sockfd` is not bound to an address, `connect` will bind a default address for the caller.\
When we try to connect to a server, the connect request might fail for several reasons. For a connect request to succeed, the machine to which we are trying to connect must be up and running, the server must be bound to the address we are trying to contact, and there must be room in the server's pending connect queue(we'll learn more about this shortly). Thus, applications must be able to handle `connect` error returns that might be caused by transient conditions.\
If the `socket` is in nonblocking mode, which we discuss further in Section 16.8, `connect` will return -1 with `error` set to the special error code `EINPROGRESS` if the connection can't be established immediately. The application can use either `poll` or `select` to determine when the file descriptor is writeable. At this point, the connection is complete.\
The `connect` function can also be used with a connectionless network service(`SOCK_DGRAM`). This might seem like a contradiction, but it is an optimization instead. If we call `connect` with a `SOCK_DGRAM` socket, the destination address of all messages we send is set to the address we specified in the `connect` call, relieving us from having to provide the address every time we transmit a message. In addition, we will receive datagrams only from the address we've specified.\
A server announces that iti is willing to accept connect requests by calling the `listen` function.
```c
#include <sys/socket.h>
int listen(int sockfd, int backlog);
// returns: 0 if OK, -1 on error
```
The `backlog` argument provides a hint to the system regarding the number of outstanding connect requests that it should enqueue on behalf of the process. The actual value is determined by the system, but the upper limit is specified as `SOMAXCONN` in `<sys/socket.h>`.\
Once the queue is full, the system will reject additional connect requests, so the `backlog` value must be chosen based on the expected load of the server and the amount of processing it must do to accept a connect request and start the service.\
Once a server has called `listen` the socket used can receive connect requests. We use the accept function to retrieve a connect request and convert it into into a connection.
```c
#include <sys/socket.h>
int accept(int sockfd, struct sockaddr *restrict addr, socklen_t *restrict len);
// returns: file (socket) descriptor if OK, -1 on error
```
The file descriptor returned by `accept` is a socket descriptor that is connected to the client that called `connect`. This new socket descriptor has the same socket type and address family as the original socket (`sockfd`). The original socket passed to `accept` is not associated with the connection, but instead remains available to receive additional connect requests.\
If we don't care about the client's identity, we can set the `addr` and `len` parameters to NULL, Otherwise, before calling `accept`, we need to set the `addr` parameter to a buffer large enough to hold the address and set the integer pointed to by `len` to the size of the buffer in bytes. On return, `accept` will fill in the client's address in the buffer and update the integer pointed to by `len` to reflect the size of the address.\
If no connect requests are pending, `accept` will block until one arrives. If `sockfd` is in nonblocking mode, `accept` will return -1 and set `errno` to either `EAGAIN` or `EWOULDBLOCK`.\
If a server calls `accept` and no connect request is present, the server will block until one arrives. Alternatively, a server can use either `poll` or `select` to wait for a connect request to arrive. In this case, a socket with pending connect requests will appear to be readable.

## 5. Data Transfer
Since a socket endpoint is represented as a file descriptor, we can use `read` and `write` to communicate with a socket, as long as it is connected. Recall that a datagram socket can be "connected" if we set the default peer address using the `connect` function. Using `read` and `write` with socket descriptors is significant, because it means that we can pass socket descriptors to functions that were originally designed to work with local files. We can also arrange to pass the socket descriptors to child processes that execute programs that know nothing about sockets.\
Although we can exchange data using `read` and `write`, that is about all we can do with these two functions. If we want to specify options, receive packets from multiple clients, or send out-of-band data, we need to use one of the six socket functions designed for data transfer.\
The simplest one is send. Unlike `write` however, `send` supports a fourth `flags` argument. Three flags are defined by the Single UNIX Specification, but it is common for implementations to support additional ones. If send returns success, it doesn't necessarily mean that the process at the other end of the connection receives the data. All we are guaranteed is that when `send` succeeds, the data has been delivered to the network without error.\
The `sendto` function is similar to `send`. The difference is that `sendto` allows us to specify a destination address to be used with connectionless sockets.\
With a connection-oriented socket, the destination address is ignored, as the destination is implied by the connection. With a connectionless socket, we can't use `send` unless the destination address is first set by calling `connect`, so `sendto` give us an alternate way to send a message.\
The `recv` function is similar to `read`, but allows us to specify some options to control how we receive the data. If the sender has called `shutdown` to end transmission, or if the network protocol supports orderly shutdown by default and the sender has closed the socket, then `recv` will return 0 when we have received all the data.\
If we are interested in the identity of the sender, we can use `recvfrom` to obtain the source address from which the data was sent. Because it allows us to retrieve the address of the sender, `recvfrom` is typically used with connectionless sockets. Otherwise, `recvfrom` behaves identically to `recv`.

## 6. Socket Options
skipped
## 7. Out-of-Band Data
skipped
## 8. Nonblocking and Asynchronous I/O
skipped
## 9. Summary
skipped
