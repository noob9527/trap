# NIO

### Scatter and Gather
Java NIO comes with built-in scatter/gather support. Scatter/gather are concepts used in reading from, and writing to channels.\
A scattering read from a channel is a read operation that reads data into more than one buffer. Thus, the channel "scatters" the data from the channel into multiple buffers.\
A gathering write to a channel is a write operation that writes data from more than one buffer into a single channel. Thus, the channel "gathers" the data from multiple buffers into one channel.

#### Scatter reads
Here is a code example that shows how to perform a scattering read:
```java
ByteBuffer header = ByteBuffer.allocate(128);
ByteBuffer body   = ByteBuffer.allocate(1024);

ByteBuffer[] bufferArray = { header, body };

channel.read(bufferArray);
```
The fact that scattering reads fill up one buffer before moving on to the next, means that it is not suited for dynamically sized message parts. In other words, if you have a header and a body, and the header is fixed size (e.g. 128 bytes), then a scattering read works fine.

#### Gather writes
Here is a code example that shows how to perform a gathering write:
```java
ByteBuffer header = ByteBuffer.allocate(128);
ByteBuffer body   = ByteBuffer.allocate(1024);

//write data into buffers

ByteBuffer[] bufferArray = { header, body };

channel.write(bufferArray);
```
The array of buffers are passed into the write() method, which writes the content of the buffers in the sequence they are encountered in the array. Only the data between position and limit of the buffers is written. Thus, if a buffer has a capacity of 128 bytes, but only contains 58 bytes, only 58 bytes are written from that buffer to the channel. Thus, a gathering write works fine with dynamically sized message parts, in contrast to scattering reads.

### Reference
- [Java NIO Tutorial](http://tutorials.jenkov.com/java-nio/index.html)