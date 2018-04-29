# 4 Networking

## 1 Connecting to a server

### Using Telnet

### Connecting to a Server with Java
Our first network [program](./socket/SocketTest.java) will do the same thing we did using telnet--connect to a port and print out what it finds. The key statements of this program are these:
```java
Socket s = new Socket("time-a.nist.gov", 13);
InputStream inStream = s.getInputStream();
```
The first line opens a socket, which is a network software abstraction that enables communication out of and into this program. We pass the remote address and the port number to the socket constructor. If the connection fails, an UnknownHostException is thrown. If there is another problem, an IOException occurs. Since UnknownHostException is a subclass of IOException and this is a sample program, we just catch the superclass.\
Once the socket is open, the getInputStream method in java.net.Socket returns an InputStream object that you can use just like any other stream. Once you have grabbed the stream, this program simply prints each input line to standard output. This process continues until the stream is finished and the server disconnects.\
### Socket Timeouts
skipped

### Internet Addresses
You can use the InetAddress class if you need to convert between host names and Internet addresses. The java.net package supports IPv6 Internet addresses, provided the host operating system does.\
Some host names with a lot of traffic correspond to multiple Internet addresses, to facilitate load balancing. For example, at the time of this writing, the host name google.com corresponds to twelve different Internet addresses. One of them is picked at random when the host is accessed. You can get all hosts with the getAllByName method.\
Finally, you sometimes need the address of the local host. If you simply ask for the address of localhost, you always get the local loopback address 127.0.0.1, which cannot be used by others to connect to your computer. Instead, use the static getLocalHost method to get the address of your local host.

## 2 Implementing Servers

### Server Sockets
A server program, when started, waits for a client to attach to its port. For our example program, we chose port number 8189, which is not used by any of the standard services. The ServerSocket class establishes a socket. In our case, the command
```java
ServerSocket s = new ServerSocket(8189);
```
establishes a server that monitors port 8189. The command
```java
Socket incoming = s.accept();
```
tells the program to wait indefinitely until a client connects to that port. Once someone connects to this port by sending the correct request over the network, this method returns a Socket object that represents the connection that was made. You can use this object to get input and output streams.\
Every server program, such as an HTTP web sever, continues performing this loop:
1. It receives a command from the client through an incoming data stream.
2. It decodes the client command.
3. It gathers the information that the client requested.
4. It sends the information to the client through the outgoing data stream.

### Serving Multiple Clients
Rejecting multiple connections allows any one client to monopolize the service by connecting to it for a long time. We can do much better through the magic of threads. Every time we know the program has established a new socket connection--that is, every time the call to accept() returns a socket--we will launch a new thread to take care of the connection between the server and that client. The main program will just go back and wait for the next connection. Note that this approach is not satisfactory for high-performance servers.

### Half-Close
The half-close allows one end of a socket connection to terminate its output while still receiving data from the other end.\
Here is a typical situation. Suppose you transmit data to the server but you don't know at the outset how much data you have. With a file, you'd just close the file at the end of the data. However, if you close a socket, you immediately disconnect from the server and cannot read the response. The half-close overcomes this problem. You can close the output stream of a socket, thereby indicating to the sever the end of the requested data, but keep the input stream open. The server side simply reads input until the end of the input stream is reached. Then sends the response.\
Of course, this protocol is only useful for one-shot services such as HTTP where the client connects, issues a request, catches the response, and then disconnects.

### Interruptible Sockets
When you connect to a socket, the current thread blocks until the connection has been established or a timeout has elapsed. Similarly, when you read or write data through a socket, the current thread blocks until the operation is successful or has timed out.\
In interactive applications, you would like to give users an option to simply cancel a socket connection that does not appear to produce results. However, if a thread blocks on an unresponsive socket, you cannot unblock it by calling interrupt. To interrupt a socket operation, use a SocketChannel, a feature of the java.nio package. Open the SocketChannel like this:
```java
SocketChannel channel = SocketChannel.open(new InetSocketAddress(host, port));
```
A channel does not have associated streams. Instead, it has read and write methods that make use of Buffer objects. These methods are declared in the interfaces ReadableByteChannel and WritableByteChannel. If you don't want to deal with buffers, you can use the Scanner class to read from a SocketChannel because Scanner has a constructor with a ReadableByteChannel parameter:
```java
Scanner in = new Scanner(channel, "UTF-8");
```
To turn a channel into an output stream, use the static Channels.newOutputStream method.
```java
OutputStream outStream = Channels.newOutputStream(channel);
```
That's all you need to do. Whenever a thread is interrupted during an open, read, or write operation, the operation does not block, but is terminated with an exception.

## Getting Web Data
### URLs and URIs
The URL and URLConnection classes encapsulate much of the complexity of retrieving information from a remote site. You can construct a URL object from a string. If you simply want to fetch the contents of the resource, use the openStream method of the URL class. This methods yields an InputStream object. Use it in the usual way, for example, to construct a Scanner.\
The java.net package makes a useful distinction between URLs(uniform resource locators) and URIs(uniform resource identifiers). A URI is a purely syntactical construct that contains the various parts of the string specifying a web resource. A URL is a special kind of URI, namely, one with sufficient information to locate a resource. Other URIs, such as `mailto:cay@hostmann.com` are not locators--there is no data to locate from this identifier. Such a URI is called a URN(uniform resource name).\
In the Java library, the URI class has no methods for accessing the resource that the identifier specifies--its sole purpose is parsing. In contrast, the URL class can open a stream to the resource. For that reason, the URL class only works with schemes that the Java library knows how to handle, such as http:, https:, ftp:, the local file system (file:), and JAR files (jar:).\
To see why parsing is not trivial, consider how complex URIs can be. For example:
```
http://google.com?q=Beach+Chalet
ftp://username:password@ftp.yourserver.com/pub/file.txt
```
The URI specification gives the rules for the makeup of these identifiers. A URI has the syntax
```
[scheme:]schemeSpecificPart[#fragment]
```
Here, the "..." denotes an optional part, and the ":" and "#" are included literally in the identifier. If the scheme: part is present, the URI is called absolute. Otherwise, it is called relative.\
An absolute URI is opaque if the schemeSpecificPart does not begin with a / such as `mailto:cay@hostmann.com`, All absolute nonopaque URIs and all relative URIs are hierarchical. Examples are
```
http://hostmann.com/index.html
../../java/net/Socket.html#Socket()
```
The schemeSpecificPart of a hierarchical URI has the structure
```
[//authority][path][?query]
```
where again, `[...]` denotes optional parts.\
For server-based URIs, the authority part has the form `[user-info@]host[:port]`, the port must be an integer.\
One of the purposes of the URI class is to parse an identifier and break it up into its components. You can retrieve them with the methods
```
getScheme
getSchemeSpecificPart
getAuthority
getUserInfo
getHost
getPort
getPath
getQuery
getFragment
```
The other purpose of the URI class is the handling of absolute and relative identifiers. If you have an absolute URI and a relative URI then you can combine the two into an absolute URI. This process is called resolving a relative URL. The opposite process is called relativization. The URI class supports both of these operations.

### Using a URLConnection to Retrieve Information
skipped
### Posting Form Data
Two commands, called GET and POST, are commonly used to send information to a web server.\
In the GET command, you simply attach query parameters to the end of the URL. The URL has the form
```
http://host/path?query
```
Each parameter has the form name=value. Parameters are separated by & characters. Parameter values are encoded using the URL encoding scheme, following these rules:
1. Leave the characters A through Z, a through z, 0 through 9, and `.-~_` unchanged.
2. Replace all spaces with + chacters
3. Encode all other character into UTF-8 and encode each byte by a %, followed by a two-digit hexadecimal number.
For example, to transmit San Francisco, CA, you use San+Francisco%2c+CA, as the hexadecimal number 2x is the UTF-8 code of the ',' character.\
This encoding keeps any intermediate programs from messing with spaces and interpreting other special characters.\
Very long query strings can look unattractive in browsers, and older browsers and proxies have a limit on the number of characters that you can include in a GET request. For that reason, a POST request is oftern used for forms with a lot of data. In a POST request, you do not attach parameters to a URL. Instead, you get an output stream from the URLConnection and write name/value pairs to the output stream. You still have to URL-encode the values and separate them with '&' character.

## 5 Sending E-Mail
skipped
