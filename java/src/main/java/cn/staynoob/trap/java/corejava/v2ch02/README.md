# Input and Output
## 1 Input/Output Streams
In the Java API, an object from which we can read a sequence of bytes is called an input stream. An object to which we can write a sequence of bytes is called an output stream. These sources and destinations of byte sequences can be--and often are--files, but they can also be network connections and even blocks of memory. The abstract classes `InputStream` and `OutputStream` form the basis for a hierarchy of input/output(I/O) classes
### Reading and Writing Bytes
The InputStream class has an abstract method:
```java
abstract int read()
```
This method reads one byte and returns the byte that was read, or -1 if it encounters the end of the input source. The designer of a concrete input stream class overrides this method to provide useful functionality.\
Similarly, the OutputStream class definers the abstract method
```java
abstract int write()
```
which writes one byte to an output location.\
Both the read and write methods block until the byte is actually read or written. This means that if the input stream cannot immediately be accessed (usually because of a busy network connection), the current thread blocks. This gives other threads the chance to do useful work while the method is waiting for the input stream to become available again. When you gave finished reading or writing to an input/output stream, close it by calling the close method. This call frees up the operating system resources that are in limited supply. If an application opens too many input/output streams without closing them, system resources can become depleted. Closing an output stream also flushes the buffer used for the output stream: Any bytes that were temporarily places in a buffer so that they could be delivered as a larger packet are sent off. In particular, if you do not close a file, the last packet of bytes might never be delivered. You can also manually flush the output with the flush method.\
Even if an input/output stream class provides concrete methods to work with the raw read and write functions, application programmers rarely use them. The data that you are interested in probably contain numbers, strings, and objects, not raw bytes.

### The Complete Stream Zoo
For unicode text, on the other hand, you can use subclasses of the abstract classes Reader and Writer. The basic methods of the Reader and Writer classes are similar to those of InputStream and OutputStream.
```java
abstract int read();
abstreact void write(int c);
```
The read method returns either a UTF-16 code unit (as an integer between 0 and 65535) or -1 when you have reached the end of the file. The write method is called with a Unicode code unit.
> Note: The Closeable interface extends the AutoCloseable interface. Therefore, you can use the try-with-resources statement with any Closeable. Why have two interfaces? The close method of the Closeable interface only throws an IOException, whereas the AutoCloseable.close method may throw any exception.

### Combining Input/Output Stream Filters
FileInputStream and FileOutputStream give you input and output streams attached to a disk file. You need to pass the file name or full path name of the file to the constructor. For example
```java
FileInputStream fin = new FileInputStream("employee.dat");
```
looks in the user directory for a file named employee.dat.
> TIP: All the classes in java.io interpret relative path names as starting from the user's working directory. You can get this directory by a call to System.getProperty("user.dir").

> CAUTION: Since the backslash character is the escape character in Java strings, be sure to use \\ for Windows-style path names. In windows, you can also use a single forward slash because most Windows file-handling system calls will interpret forward slashes as file separators. However, This is not recommended--the behavior of the Windows system functions is subject to change. Instead, for portable programs, use the file separator character for the platform on which you program runs. It is available as the constant string java.io.File.separator.

Like the abstract InputStream and OutputStream classes, these classes only support reading and writing at the byte level. That is, we can only read bytes and byte arrays from the object file.
```java
byte b = (byte) fin.read();
```
As you will see in the next section, if we just had a DataInputStream, we could read numeric types:
```java
DataInputStream din = ...;
double x = din.readDouble();
```
But just as the FileInputStream has no methods to read numeric types, the DataInputStream has no method to get data from a file.\
Java uses a clever mechanism to separate two kinds of responsibilities. Some input streams(such as the FileInputStream and the input stream returned by the openStream method of the URL class) can retrieve bytes from files and other more exotic locations. Other input streams(such as the DataInputStream) can assemble bytes into more useful data types. The Java programmer has to combine the two. For example, to be able to read numbers from a file, first create a FileInputStream and then pass it to the constructor of a DataInputStream.
```java
FileInputStream fin = new FileInputStream("emplyee.dat");
DataInputStream din = new DataInputStream(fin);
double x = din.readDouble();
```
You can add multiple capabilities by nesting the filters. For example, by default, input streams are not buffered. That is, every call to read asks the operating system to dole out yet another byte. It is more efficient to request blocks of data instead and store them in a buffer. If you want buffering and the data input methods for a file, you need to use the following rather monstrous sequence of constructors:
```java
DataInputStream din = new DataInputStream(new BufferedInputStream(new FileInputStream("employee.dat")));
```
Notice that we put the DataInputStream last in the chain of constructors because we want to use the DataInputStream methods, and we want them to use the buffered read method.\
Sometimes you'll need to keep track of the intermediate input streams when chaining them together. For example, when reading input, you  often need to peek at the next byte to see if it is the value that you expect. Java provides the PushbackInputStream for this purpose.
```java
PushbackInputStream pbin = new PushbackInputStream(new BufferedInputStream(new FileInputStream("emplyee.dat")));
```
Now you can speculatively read the next byte
```java
int b - pbin.read();
```
and throw it back if it isn't what you wanted.
```java
if (b != '<') pbin.unread(b);
```
However, reading and unreading are the only methods that apply to a pushback input stream. If you want to look ahead and also read numbers, then you need both a pushback input stream and a data input stream reference.
```java
PushbackInputStream pbin = new PushbackInputStream(new BufferedInputStream(new FileInputStream("emplyee.dat")));
DataInputStream din = new DataInputStream(pbin);
```
Of course, in the input/output libraries of other programming languages, niceties such as buffering and lookahead are automatically taken care of, so it is a bit of a hassle to resort, in Java, to combining stream filters. However, the ability to mix and match filter classes to construct truly useful sequences of input/output streams does give you an immense amount of flexibility. For example, you can read numbers from a compressed ZIP file by using the following sequence of input streams:
```java
ZipInputStream zin = new ZipInputStream(new FileInputStream("employee.zip"));
DataInputStream din = new DataInputStream(zin);
```

## 2 Text Input and Outputs
