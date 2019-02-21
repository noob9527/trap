# Input and Output
## 1 Input/Output Streams
In the Java API, an object from which we can read a sequence of bytes is called an input stream. An object to which we can write a sequence of bytes is called an output stream. These sources and destinations of byte sequences can be--and often are--files, but they can also be network connections and even blocks of memory. The abstract classes `InputStream` and `OutputStream` form the basis for a hierarchy of input/output(I/O) classes
### Reading and Writing Bytes
The InputStream class has an abstract method:
```java
abstract int read()
```
This method reads one byte and returns the byte that was read, or -1 if it encounters the end of the input source. The designer of a concrete input stream class overrides this method to provide useful functionality. For example, in the FileInputStream class, this method reads one byte from a file. System.in is a predefined object of a subclass of InputStream that allows you to read information from "standard input," that is, the console or a redirected file.\
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
int b = pbin.read();
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
The OutputStreamWriter class turns an output stream of Unicode unites into a stream of bytes, using a chosen character encoding. Conversely, the InputStreamReader class turns an input stream that contains bytes(specifying characters in some character encoding) into a reader that emits Unicode code units. For example, here is how you make an input reader that reads strokes from the console and converts them to Unicode:
```java
Reader in = new InputStreamReader(System.in);
```
This input stream reader assumes the default character encoding used by the host system. On desktop operating systems, that can be an archaic encoding such as Windows 1252 or MacRoman. You should always choose a specific encoding in the constructor for the InputStreamReader, for example:
```java
Reader in = new InputStreamReader("data.txt", StandardCharsets.UTF_8);
```

### How to Write Text Output
For text output, use a PrintWriter. That class has methods to print strings to numbers in text format. To write to a print writer, use the same print, println, and printf methods that you used with System.out. The characters are then converted to bytes and end up in the file. The print methods don't throw exceptions. You can call the checkError method to see if something went wrong with the output stream.

> Note: Java veterans might wonder whatever happened to the PrintStream class and to System.out. In java 1.0, the PrintStream class simply truncated all Unicode characters to ASCII characters by dropping the top byte.(At the time, Unicode was still a 17-bit encoding.) Clearly, that was not a clean or portable approach, and it was fixed with the introduction of readers and writers in Java 1.1. For compatibility with existing code, System.in, System.out, and System.err are still input/output streams, not readers and writers. But now the PrintStream  class internally converts Unicode characters to the default host encoding in the same way the PrintWriter does. Objects of type PrintStream act exactly like print writers when you use the print and println methods, but unlike print writers they allow you to output raw bytes with the write(int) and write(byte[]) methods.

### How to Read Text Input
The easiest way to process arbitrary text is the Scanner class that we used extensively to Volume 1. You can construct a Scanner from any input stream. Alternatively, you can read a short text file into a string like this:
```java
String content = new String(Files.readAllBytes(path),  charset);
```
But if you want the file as a sequence of lines, call
```java
List<String> lines = Files.readAllLines(path, charset);
```
If the file is large, process the lines lazily as a `Stream<String>`;
```java
try (List<String> lines = Files.lines(path, charset)) {
    // ...
}
```
### Saving Objects in Text Format
skipped
### Character Encoding
Input and output streams are for sequences of bytes, but in many cases you will work with texts--that is, sequences of characters. It then matters how characters are encoded into bytes.\
Java uses the Unicode standard for characters. Each character or "code point" has a 21-bit integer number. There are different character encodings--methods for packaging those 21--bit numbers into bytes.\
The most common encoding is UTF-8, which encodes each Unicode code point into a sequence of one to four bytes. UTF-8 has the advantage that the characters of the traditional ASCII character set, which contains all characters used in English, only take up one byte each.\
Another common encoding is UTF-16, which encodes each Unicode code point into one or two 16-bit values. This is the encoding used in Java strings.\
There is no reliable way to automatically detect the character encoding from a stream of bytes. Some API methods let you use the "default charset"--the character encoding preferred by the operating system of the computer. Is that the same encoding that is used by your source of bytes? These bytes may well originate from a different part of the world. Therefore, you should always explicitly specify the encoding. For example, when reading a web page, check the Content-Type header.\

> Caution: The Oracle implementation of Java has a system property file.encoding for overriding the platform default. This is not an officially supported property, and it is not consistently followed by all parts of Oracle's implementation of the Java library. You should not set it.

> Caution: Some methods(such as the String(byte[]) constructor) use the default platform encoding if you don't specify any; others(such as Files.readAllLines) use UTF-8

> Hint: UTF-8 is a variable width character encoding capable of encoding all 1,112,064 valid code points in Unicode using one to four 8-bit bytes. The encoding is defined by the Unicode standard, and was originally designed by Ken Thompson and Rob Pike. **The name is derived from Unicode Transformation Format – 8-bit.**
It was designed for backward compatibility with ASCII. Code points with lower numerical values, which tend to occur more frequently, are encoded using fewer bytes. The first 128 characters of Unicode, which correspond one-to-one with ASCII, are encoded using a single octet with the same binary value as ASCII, so that valid ASCII text is valid UTF-8-encoded Unicode as well. Since ASCII bytes do not occur when encoding non-ASCII code points into UTF-8, UTF-8 is safe to use within most programming and document languages that interpret certain ASCII characters in a special way, such as "/" in filenames, "\" in escape sequences, and "%" in printf.

> UTF-16 (16-bit Unicode Transformation Format) is a character encoding capable of encoding all 1,112,064 valid code points of Unicode. The encoding is variable-length, as code points are encoded with one or two 16-bit code units (also see Comparison of Unicode encodings for a comparison of UTF-8, -16 & -32).
UTF-16 arose from an earlier fixed-width 16-bit encoding known as UCS-2 (for 2-byte Universal Character Set) once it became clear that 16 bits were not sufficient for Unicode's user community.
While UTF-16 is used internally by systems such as Windows and Java, and often for plain text and for word-processing data files on Windows, it never gained popularity on the web, where UTF-8 is dominant: UTF-16 is used by under 0.01% of web pages, and is rarely used for files on Unix/Linux or OS/X.
UTF-16 and UCS-2 produce a sequence of 16-bit code units. Since most communication and storage protocols are defined for bytes, and each unit thus takes two 8-bit bytes, the order of the bytes may depend on the endianness (byte order) of the computer architecture.
To assist in recognizing the byte order of code units, UTF-16 allows a Byte Order Mark (BOM), a code point with the value U+FEFF, to precede the first actual coded value. (U+FEFF is the invisible zero-width non-breaking space/ZWNBSP character.) If the endian architecture of the decoder matches that of the encoder, the decoder detects the 0xFEFF value, but an opposite-endian decoder interprets the BOM as the non-character value U+FFFE reserved for this purpose. This incorrect result provides a hint to perform byte-swapping for the remaining values.

> Caution: Some programs, including Microsoft Notepad, add a byte order mark at the beginning of UTF-8 encoded files. Clearly, this is unnecessary since there are no byte ordering issues in UTF-8. But the Unicode standard allow it, and even suggests that it's a pretty good idea since it leaves little doubt about the encoding. It is supposed to be removed when reading a UTF-8 encoded file. Sadly. Java does not do that, and bug reports against this issue are closed as "will not fix." Your best bet is to strip out any leading \uFEFF that you find in your input.

Reference:
- [UTF-8](https://en.wikipedia.org/wiki/UTF-8)
- [UTF-16](https://en.wikipedia.org/wiki/UTF-16)

## 3 Reading and Writing Binary Data
### The DataInput and DataOutput interfaces
The DataOutput interface defines the methods for writing a number, a character, a boolean value, or a string in binary format, For example, writeInt always writes an integer as a 4-byte binary quantity regardless of the number of digits, and writeDouble always writes a double as an 80byte binary quantity. The resulting output is not human-readable, but the space needed will be the same for each value of a given type and reading it back in will be faster than parsing text.\
The DataInputStream class implements the DataInput interface. To read binary data from a file combine a DataInputStream with a source of bytes such as a FileInputStream:
```java
DataInputStream in = new DataInputStream(new FileInputStream("employee.dat"));
```
Similarly, to write binary data, use the DataOutputStream class that implements the DataOutput Interface:
```java
DataOutputStream in = new DataOutputStream(new FileOutputStream("employee.dat"));
```
### Random-Access Files
The RandomAccessFile class lets you read or write data anywhere in a file. Disk files are random-access, but input/output streams that communicate with a network socket are not. You can open a random-access file either for reading only or for both reading and writing; specify the option by using the string "r" (for read access) or "rw"(for read/write access) as the second argument in the constructor.\
A random-access file has a file pointer that indicates the position of the next byte to be read or written. The seek method can be used to set the file pointer to an arbitrary byte position within the file. The argument to seek is a long integer between zero and the length of the file in bytes.\
The RandomAcessFile class implements both the DataInput and DataOutput interfaces. To read and write from a random-access file, use methods such as readInt/writeInt and readChar/writeChar that we discussed in the preceding section.

### ZIP Archives
ZIP archives store one or more files in a (usually) compressed format. Each ZIP archive has a header with information such as the name of each file and the compression method that was used. In Java, you can use a ZipInputStream to read a ZIP archive. You need to look at the individual entries in the archive. and you can use a ZipOutputStream to write a ZIP file.

> Note: JAR files(which were discussed in Volume I, Chapter 13) are simply ZIP files with a special entry --the so-called manifest. Use the JarInputStream and JarOutputStream classes to read and write the manifest entry.

## 4 Object Input/Output Streams and Serialization
### Saving and Loading Serializable Objects
to save and read the objects, using ObjectOutputStream and ObjectInputStream, There is, however, one change you need to make to any class that you want to save to an output stream and restore from an object input stream. The class must implement the Serializable interface. To make a class serializable, you do not need to do anything else.\
> Note: You can wirte and read only objects with the writeObject/readObject methods. For primitive type values, use methods such as writeInt/readInt or writeDouble/readDouble.(The object input/output stream classes implement the DataInput/DataOutput interfaces.)

Behind the scenes, an ObjectOutputStream looks at all the fields of the objects and saves their contents.\
However, there is one important situation that we need to consider: What happens when one object is shared by several objects as part of its state? Saving such a network of objects is a challenge. Of course, we cannot save and restore the memory addresses for the mutual object. it will likely occupy a completely different memory address than it originally did.\
Instead, each object is saved with the serial number, hence the name object serialization for this mechanism. Here is the algorithm:\
1. Associate a serial number with each object reference that you encounter.
2. When encountering an object reference for the first time, save the object data to the output stream.
3. If it has been saved previously, just write "same as the previously saved object with serial number x."
When reading back the objects, the procedure is reversed.
1. When an object is specified in an object input stream for the first time, construct it, initialize it with the stream data, and remember the association between the serial number and the object reference.
2. When the tag "same as the previously saved object with serial number x" is encountered, retrieve the object reference for the sequence number.
By replacing memory addresses with serial numbers, serialization permits the transport of object collections from one machine to another.

### Understanding the Object Serialization File Format
When an object is saved, the class of that object must be saved as well. The class description contains:
- The name of the class
- The serial version unique ID, which is a fingerprint of the data field types and method signatures
- A set of flags describing the serialization method
- A descrition of the data fields
The fingerprint is obtained by ordering the descriptions of the class, superclass, interfaces, field types, and method signatures in a canonical way, and then appliying the so-called Secure Hash Algorithm(SHA) to that data.\
When reading an object, its fingerprint is compared against the current fingerprint of the class. If they don't match, it means the class definition has changed after the object was written, and an exception is generated.\
Here is how a class identifier is stored:
- 72
- 2-byte length of class name
- Class name
- 8-byte fingerprint
- 1-byte flag
- 2-byte count of data field descriptors
- Data field descriptors
- 78(end marker)
- Superclass type(70 if none)
The flag byte is composed of thress bit masks, defined in java.io.ObjectStreamConstants:
```java
/**
 * Bit mask for ObjectStreamClass flag. Indicates a Serializable class
 * defines its own writeObject method.
 */
final static byte SC_WRITE_METHOD = 0x01;
/**
 * Bit mask for ObjectStreamClass flag. Indicates class is Serializable.
 */
final static byte SC_SERIALIZABLE = 0x02;

/**
 * Bit mask for ObjectStreamClass flag. Indicates class is Externalizable.
 */
final static byte SC_EXTERNALIZABLE = 0x04;
```
studying the serialized format can be about as exciting as reading a phone book. It is not important to know the exact file format (unless you are trying to create an evil effect by modifying the data), What you should remember is this:
- The serialized format contains the types and data fields of all objects.
- Each object is assigned a serial number
- Repeated occurrences of the same object are stored as references to that serial number.

### Modifying the Default Serialization Mechanism
Certain data fields should never be serialized. Java has an easy mechanism to prevent such fields from ever being serialized: Mark them with the keyword **transient**. You also need to tag fields as transient if they belong to nonserializable classes. Transient fields are always skipped when objects are serialized.\
The serialization mechanism provides a way for individual classes to add validation or any other desired action to the default read and write behavior. A serializable class can define methods with the signature
```java
private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException;
private void writeObject(ObjectOutputStream out) throws IOException;
```
Then, the data fields are no longer automatically serialized, and these methods are called instead.\
Here is a typical example. A number of classes in the java.awt.geom package, such as Point2D.Double, are not serializable. Now, suppose you want to serialize a class LabeledPoint that stores a String and a Point2D.Double. First, you need to mark the Point2D.Double field as transient to avoid a NotSerializableException. In the writeObject method, we first write the object descriptor and the String field, label, by calling the defaultWriteObject method. This is a special method of the ObjectOutputStream class that can only be called from within a writeObject method of a serializable class. Then we write the point coordinates, using the standard DataOutput calls.
```java
private void writeObject(ObjectOutputStream out) throws IOException {
    out.defaultWriteObject();
    out.writeDouble(point.getX());
    out.writeDouble(point.getY());
}
private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    in.defaultReadObject();
    double x = in.readDouble();
    double y = in.readDouble();
    point = new Point2D.Double(x, y);
};
```
The readObject and writeObject methods only need to save and load their data fields. Trey should not concern themselves with superclass data or any other class information.\
Unlike the readObject and writeObject methods that were described in the previous section, these methods are fully responsible for saving and restoring the entire object, including the superclass data. When writing an object, the serialization mechanism merely records the class of the object in the output stream. When reading an externalizable object, the object input stream creates an object with the no-argument constructor and then calls the readExternal method.
> Caution: Unlike the readObject and writeObject methods, which are private and can only be called by the serialization mechanism, the readExternal and writeExternal methods are public. In particular, readExternal potentially permits modification of the state of an existing object.

### Serializing Singletons and Typesafe Enumerations
You have to pay particular attention to serializing and deserializing objects that are assumed to be unique. This commonly happens when you are implementing singletons and typesafe enumerations. Even though their constructors is private, the serialization mechanism can create new objects!\
To solve this problem, you need to define another special serialization method, called readResolve. If the readResolve method is defined, it is called after the object is deserialized. It must return an object which then becomes the return value of the readObject method.

### Versioning
If you use serialization to save objects, you need to consider what happens when your program evolves. Clearly, it would be desirable if object files could cope with the evolution of classes. At first glance, it seems that this would not be possible. When a class definition changes in any way, its SHA fingerprint also changes, and you know that object input streams will refuse to read in objects with different fingerprints. However, a class can indicate that it is compatible with an earlier version of itself. To do this, you must first obtain the fingerprint of the earlier version of the class. Use the standalone serialver program that is part of the JDK to obtain this number. All later version of the class must define the serialVersionUID constant to the same fingerprint as the original. When a class has a static data member named serialVersionUID, it will not compute the fingerprint manually but will use that value instead.

### Using Serialization for Cloning
There is an amusing use for the serialization mechanism: It gives you an easy way to clone an object, provided the class is serailizable. Simply serialize it to an output stream and then read it back in. The result is a new object that is a deep copy of the existing object. You should be aware that this method, although clever, will usually be much slower than a clone method that explicitly constructs a new object and copies or clones the data fields.

## 5 Working with Files
The Path interface and Files class were added in Java SE 7. They are much more convenient to use than the File class which dates back all the way to JDK 1.0. We expect them to be very popular with Java programmers and discuss them in depth.

### Paths
A Path is a sequence of directory names, optionally followed by a file name. The first component of a path may be a root component such as / or C:\\. The permissible root components depend on the file system. A path that starts with a root component is absolute. Otherwise, it is relative. For example, here we construct an absolute and a relative path.
```java
Path absolute = Paths.get("/home", "harry");
Path relative = Paths.get("myprog", "conf", "user.properties");
```
The static Paths.get method receives one or more strings, which it joins with the path separator of the default file system. It then parses the result, throwing an InvalidPathException if the result is not a valid path in the given file system. The result is a Path object. The get method can get a single string containing multiple components.\
It is very common to combine or resolve paths. The call p.resolve(q) returns a path according to these rules:
- If q is absolute, them the result is q.
- Otherwise, the result is "p then q," according to the rules of the file system.
The opposite of resolve is relativize. The call p.relativize(r) yields the path q which, when resolved with p, yields r. For example, relativizing "/home/harry" against "/home/fred/input.txt" yields "../fred/input.txt". Here, we assume that .. denotes the parent directory in the file system.
The normalize method removes any redundant . and .. components(or whatever the file system may deem redundant). For example, normalizing the path /home/harry/../fred/./input.txt yields /home/fred/input.txt

### Reading and Writing Files
skipped(see `java.nio.file.Files` documentation)
### Creating Files and Directories
skipped(see `java.nio.file.Files` documentation)
### Copying, Moving, and Deleting Files
skipped(see `java.nio.file.Files` documentation)
### Getting File Information
skipped(see `java.nio.file.Files`, `java.nio.file.attribute` documentation)
### Visiting Directory Entries
skipped(see `java.nio.file.Files` documentation), especially `Files.walk` method
### Using directory Streams
skipped(see `java.nio.file.Files` documentation)
### ZIP File Systems
The Paths class looks up paths in the default file system--the files on the user's local disk. You can have other file systems. One of the more useful ones is a ZIP file system. If zipname is the name of a ZIP file, then the call
```java
FileSystem fs = FileSystems.newFileSystem(Paths.get(zipname), null);
```
establishes a file system that contains all files in the ZIP archive. It's an easy matter to copy a file out of that archive if you know its name:
```java
Files.copy(fs.getPath(sourceName), targetPath);
```
Here, fs.getPath is the analog of Paths.get for an arbitrary file system.

## 6 Memory-Mapped Files
Most operating systems can take advantage of a virtual memory implementation to "map" a file, or a region of a file, into memory. Then the file can be accessed as if it were an in-memory array, which is much faster than the traditional file operations.

### Memory-Mapped File Performance
skipped

### The Buffer Data Structure
In this section, we briefly describe the basic operations on Buffer objects. A buffer is an array of values of the same type. The Buffer class is an abstract class with concrete subclasses `ByteBuffer`, `CharBuffer` et cetera.\
In practice, you will most commonly use `ByteBuffer` and `CharBuffer`. A buffer has
- A capacity that never changes.
- A position at which the next value is read or written.
- A limit beyond which reading and writing is meaningless.
- Optionally, a mark for repeating a read or write operation.

The principal purpose of a buffer is a "write then read" cycle. At the outset, the buffer's position is 0 and the limit is the capacity. Keep calling put to add values to the buffer. When you run out of data or reach the capacity, it is time to switch to reading.\
Call flip to set the limit to the current position and the position to 0. Now keep calling get while the remaining method (which returns limit - position) is positive. When you have read all values in the buffer, call clear to prepare the buffer for the next writing cycle. The clear method resets the position to 0 and the limit to the capacity.\
If you want to reread the buffer, use rewind or mark/reset.\
To get a buffer, call a static method such as ByteBuffer.allocate or ByteBuffer.wrap.\
Then, you can fill a buffer from a channel, or write its contents to a channel.

### File Locking
When multiple simultaneously executing programs need to modify the same file, they need to communicate in some way, or the file can easily become damaged. File locks can solve this problem. A file lock controls access to a file or a range of bytes within a file.\
Suppose your application saves a configuration file with user preferences. If a user invokes two instances of the application, it could happen that both of them want to write the configuration file at the same time. In that situation, the first instance should lock the file. When the second instance finds the file locked, it can decide to wait until the file is unlocked or simply skip the writing process.\
To lock a file, call either the lock or tryLock methods of the FileChannel class.
```java
FileChannel = FileChannel.open(path);
FileLock lock = channel.lock();
// or
FileLock lock = channel.tryLock();
```
The first call blocks until the lock becomes available. The second call returns immediately, either with the lock or with null if the lock is not available. The file remains locked until the channel is closed or the release method is invoked on the lock.\
The shared flag is false to lock the file for both reading and writing. It is true for a shared lock, which allows multiple processes to read from the file, while preventing any process from acquiring an exclusive lock. Not all operating systems support shared locks. You may get an exclusive lock even if you just asked for a shared one. Call the isShared method of the FileLock class to find out which kind you have.
> Note If you lock the tail portion of a file and the file subsequently grows beyond the locked portion, the additional area is not locked. To lock all bytes, use a size of Long.MAX\_VALUE.

Be sure to unlock the lock when you are done. As always, this is best done with a try-with-resources statement.\
Keep in mind that file locking is system-dependent. Here are some points to watch for:
- On some systems, file locking is merely advisory. If an application fails to get a lock, it may still write to a file that another application has currently locked.
- On some systems, you cannot simultaneously lock a file and map it into memory.
- File locks are held by the entire Java virtual machine. If two programs are launched by the same virtual machine, they can't each acquire a lock on the same file. The lock and tryLock methods will throw an OverlappingFileLockException if the virtual machine already holds another overlapping lock on the same file.
- On some systems, closing a channel releases all locks on the underlying file held by the java virtual machine. You should therefore avoid multiple channels on the same locked file.
- Locking files on a networked file system is highly system-dependent and should probably be avoided.

## 7 Regular Expressions
The simplest use for a regular expression is to test whether a particular string matches it. Here is how you program that test in Java. First, construct a Pattern object fro m a string containing the regular expression. Then, get a Matcher object from the pattern and call its matches method:
```java
Pattern pattern = Pattern.compile(patternString);
Matcher matcher = pattern.matcher(input);
if(matcher.matches()) //...
```
The input of the matcher is an object of any class that implements the CharSequence interface, such as a String, StringBuilder, or CharBuffer.\
When compiling the pattern, you can set one or more flags, for example:
```java
Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE + Pattern.UNICODE_CASE);
```
Or you can specify them inside the pattern:
```java
String regex = "(?iU:expression)"
```
If the regular expression contains groups, the Matcher object can reveal the group boundaries. The methods
```java
int start(int groupIndex)
int end(int groupIndex)
```
yield the starting index and the past-the-end index of a particular group. You can simply extract the matched string by calling
```java
String group(int groupIndex)
```
Group 0 is the entire input; the group index for the first actual group is 1. Call the groupCount method to get the total group count. For named groups, use the methods
```java
int start(String groupName)
int end(String groupName)
String group(String groupName)
```
Nested groups are ordered by the opening parentheses.\
Usually, you don't want to match the entire input against a regular expression, but to find one or more matching substrings in the input. Use the find method of the Matcher class to find the next match. If it returns true, use the start and end methods to find the extent of the match or the group method without an argument to get the matched string.
```java
while(matcher.find()) {
    int start = matcher.start();
    int end = matcher.end();
    String match = input.group();
}
```

> Tips: By default, a quantifier matches the largest possible repetition that makes the overall match succeed. You can modify that behavior with suffixes ? (reluctant, or stingy, match: match the smallest repetition count) and + (possessive, or greedy, match: match the largest count even if that makes the overall match fail).



