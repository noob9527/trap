# Chapter05 Standard I/O Library

## 1. Introduction
skipped

## 2. Streams and FILE Objects
In previous chapters, all the I/O routines centered on file descriptors, When a file is opened, a file descriptor is returned, and that descriptor is then used for all subsequent I/O operations. With the standard I/O library, the discussion centers on streams. When we open or create a file with the standard I/O library, we say that we have associated a stream with the file.\
When we open a stream, the standard I/O function `fopen` returns a pointer to a FILE object. This object is normally a structure that contains all the information required by the standard I/O library to manage the stream: the file descriptor used for actual I/O, a pointer to a buffer for the stream, the size of the buffer, a count of the number of characters currently in the buffer, an error flag, and the like.\
Application software should never need to examine a FILE object. To reference the stream, we pass its FILE pointer as an argument to each standard I/O function.

## 3. Standard Input, Standard Output, and Standard Error
Three streams are predefined and automatically available to a process: standard input, standard output, and standard error. These streams refer to the same files as the file descriptors `STDIN_FILENO`, `STDOUT_FILENO`, and `STDERR_FILENO`, respectively.\
These three standard I/O streams are referenced through the predefined file pointers `stdin`, `stdout`, and `stderr`. The file pointers are defined in the `<stdio.h>` header.

## 4. Buffering
The goal of the buffering provided by the standard I/O library is to use the minimum number of `read` and `write` calls. Also, this library tries to do its buffering automatically for each I/O stream, obviating the need for the application to worry about it.\
Three types of buffering are provided:
1. Fully buffered. In this case, actual I/O takes place when the standard I/O buffer is filled. Files residing on disk are normally fully buffered by the standard I/O library. The buffer used is usually obtained by one of the standard I/O functions calling `malloc` the first time I/O is performed on a stream.\
The term `flush` describes the writing of a standard I/O buffer. A buffer can be flushed automatically by the standard I/O routines, such as when a buffer fills, or we can call the function `fflush` to flush a stream.
2. Line buffered. In this case, the standard I/O library performs I/O when a newline character is encountered on input or output. This allows us to output a single character at a time(with the standard I/O `fputc` function), knowing that actual I/O will take place only when we finish writing each line. Line buffering is typically used on a stream when it refers to a terminal--standard input and standard output, for example.
3. Unbuffered. The standard I/O library does not buffer the characters. If we write 15 characters with the standard I/O `fputs` function, for example, we expect these 15 characters to be output as soon as possible, probably with the `write` function.\
The standard error stream, for example, is normally unbuffered so that any error messages are displayed as quickly as possible, regardless of whether they contain a newline.

ISO C requires the following buffering characteristics:
- Standard input and standard output are fully buffered, if and only if they do not refer to an interactive device.
- standard error is never fully buffered.

This, however, doesn't tell us whether standard input and standard output are unbuffered or line buffered if they refer to an interactive device and whether standard error should be unbuffered or line buffered. Most implementations default to the following types of buffering:
- Standard error is always unbuffered.
- All other streams are line buffered if they refer to a terminal device; otherwise, they are fully buffered.

If we don't like these defaults for any given stream, we can change the buffering by calling either the `setbuf` or `setvbuf` function.\
In general, we should let the system choose the buffer size and automatically allocate the buffer. When we do this, the standard I/O library automatically releases the buffer when we close the stream.\
At any time, we can force a stream to be flushed.
```c
#include <stdio.h>
int fflush(FILE *fp);
// return: 0 if OK, EOF on error
```
The `fflush` function causes any unwritten data for the stream to be passed to the kernel. As a special case, if `fp` is NULL, `fflush` causes all output streams to be flushed.

## 5. Opening a Stream
The `fopen`, `freopen`, and `fdopen` functions open a standard I/O stream.
```c
#include <stdio.h>
FILE *fopen(const char *restrict pathname, const char *restrict type);
FILE *freopen(const char *restrict pathname, const char *restrict type, FILE *restrict fp);
FILE *fdopen(int fd, const char *type);
// All three return: file pointer if OK, NULL on error
```
The differences in these three functions are as follows:
1. The `fopen` function opens a specified file.
2. The `freopen` function opens a specified file on a specified stream, closing the stream first if it is already open. If the stream previously had an orientation, `freopen` clears it. This function is typically used to open a specified file as one of the predefined streams: standard input, standard output, or standard error.
3. The `fdopen` function takes an existing file descriptor, which we could obtain from the `open`, `dup`, `dup2`, `fcntl`, `pipe`, `socket`, `socketpair`, or `accept` functions, and associates a standard I/O stream with the descriptor. This function is often used with descriptors that are returned by the function that create pipes and network communication channels. Because these special types of files cannot be opened with the standard I/O `fopen` function, we have to call the device-specific function to obtain a file descriptor, and then associate this descriptor with a standard I/O stream using `fdopen`

By default, the stream that is opened is fully buffered, unless it refers to a terminal device, in which case it is line buffered. Once the stream is opend, but before we do any other operation on the stream, we can change the buffering if we want to, with the `setbuf` or `setvbuf` functions from the previous section.\
An open stream is closed by calling `fclose`.
```c
#include <stdio.h>
int fclose(FILE *fp);
// 0 if OK, EOF on error
```
Any buffered output data is flushed before the file is closed. Any input data that may be buffered is discarded. If the standard I/O library had automatically allocated a buffer for the stream, that buffer is released.\
When a process terminates normally, either by calling the `exit` function directly or by returning from the `main` function, all standard I/O streams with unwritten buffered data are flushed and all open standard I/O streams are closed.

## 6. Reading and Writing a Stream
Once we open a stream we can choose from among three types of unformatted I/O:
1. Character-at-a-time I/O. We can read or write one character at a time, with the standard I/O functions handling all the buffering, if the stream is buffered.
2. Line-at-a-time I/O. If we want to read or write a line at a time, we use `fgets` and `fputs`. Each line is terminated with a newline character, and we have to specify the maximum line length that we can handle when we call `fgets`.
3. Direct I/O. This type of I/O is supported by the `fread` and `fwrite` functions. For each I/O operation, we read or write some number of objects, where each object is of a specified size. These two functions are often used for binary files where we read or write a structure with each operation.

### Input functions
Three functions allow us to read one character at a time
```c
#include <stdio.h>
int getc(FILE *fp);
int fgetc(FILE *fp);
int getchar(void);
// All three return: next character if OK, EOF on end of file or error
```
The function `getchar` is defined to be equivalent to `getc(stdin)`. The difference between `getc` and `fgetc` is that `getc` can be implemented as a macro, whereas `fgetc` cannot be implemented as a macro. This means three things.
1. The argument to `getc` should not be an expression with side effects, because it could be evaluated more than once.
2. Since `fgetc` is guaranteed to be a function, we can take its address. This allows us to pass the address of `fgetc` as an argument to another function.
3. Calls to `fgetc` probably take longer than calls to `getc`, as it usually takes more time to call a function.

These three functions return the next character as an unsigned char converted to an int. The reason for specifying unsigned is so that the high-order bit, if set, doesn't cause the return value to be negative. The reason for requiring an integer return value is so that all possible character values can be returned, along with an indication that either an error occurred or the end of file has been encountered. The constant `EOF` in `<stdio.h>` is required to be a negative value. Its value is often -1. This representation also means that we cannot store the return value from these three functions in a character variable and later compare this value with the constant `EOF`.\
Note that these functions return the same value whether an error occurs or the end of file is reached. To distinguish between the two, we must call either `ferror` or `feof`.
```c
#include <stdio.h>
int ferror(FILE *fp);
int feof(FILE *fp);
// Both return: nonzero (true) if condition is true, 0(false) otherwise

void clearerr(FILE *fp);
```
In most implementations, two flags are maintained for each stream in the FILE object:
- An error flag
- An end-of-file flag

Both flags are cleared by calling `clearerr`.\
After reading from a stream, we can push back characters by calling `ungetc`.
```c
#include <stdio.h>
int ungetc(int c, FILE *fp);

// returns: c if OK, EOF on error.
```
The characters that are pushed back are returned by subsequent reads on the stream in reverse order of their pushing. Be aware, however, that although ISO C allows an implementation to support any amount of pushback, an implementation is required to provide only a single character of pushback. We should not count on more than a single character.\
The character that we push back does not have to be the same character that was read. We are not able to push back EOF. When we reach the end of file, however, we can push back a character. The next read will return that character, and the read after that will return EOF. This works because a successful call to `ungetc` clears the end-of-file indication for the stream.\
Pushback is often used when we're reading an input stream and breaking the input into words or tokens of some form. Sometimes we need to peek at the next character to determine how to handle the current character. It's then easy to push back the character that we peeked at, for the next call to `getc` to return. If the standard I/O library didn't provide this pushback capability, we would have to store the character in a variable of our own, along with a flag telling us to use this character instead of calling `getc` the next time we need a character.

### Output functions
Output functions are available that correspond to each of the input functions we've already described.
```c
#include <stdio.h>

int putc(intc, FILE *fp);
int fputc(intc, FILE *fp);
int putchar(intc);

// All three returns: c if OK, EOF on error.
```
As with the input functions, `putchar(c)` is equivalent to `putc(c, stdout)`, and `putc` can be implemented as a macro, whereas `fputc` cannot be implemented as a macro.

## 7. Line-at-a-Time I/O
Line-at-a-time input is provided by the two functions, `fgets` and `gets`.
```c
#include <stdio.h>

char *fgets(char *restreact buf, int n, FILE *restrict fp);
char *gets(char *buf);

// Both return: buf if OK, NULL on end of file or error
```
Both specify the address of the buffer to read the line into. The `gets` function reads from standard input, whereas `fgets` reads from the specified stream.\
With `fgets`, we have to specify the size of the buffer, n. This function reads up through and including the next newline, but no more that n-1 characters, into the buffer. The buffer is terminated with a null byte. If the line, including the terminating newline, is longer than n-1, only a partial line is returned, but the buffer is always null terminated. Another call to `fgets` will read what follows on the line.\
The `gets` function should never be used. The problem is that it doesn't allow the caller to specify the buffer size. This allows the buffer to overflow if the line is longer than the buffer, writing over whatever happens to follow the buffer in memory. An additional difference with `gets` is that it doesn't store the newline in the buffer, as `fgets` does. Even though ISO C requires an implementation to provide `gets`, you should use `fgets` instead. In fact, `gets` is marked as an obsolescent interface in SUSv4 and has been omitted from the latest version of the ISO C standard.\
Line-at-a-time output is provided by `fputs` and `puts`.
```c
#include <stdio.h>

char *fputs(const char *restrict str, int n, FILE *restrict fp);
char *puts(const char *str);

// Both return: non-negative value if OK, EOF on error
```
The function `fputs` writes the null-terminated string to the specified stream. The null byte at the end is not written. Not that this need not be line-at-a-time output, since the string need not contain a newline as the last non-null character. Usually, this is the case--the last non-null character is a newline--but it's not required.\
The `puts` function writes the null-terminated string to the standard output, without writing the null byte. But `puts` then writes a newline character to the standard output.\
The `puts` function is not unsafe, like its counterpart `gets`. Nevertheless, we'll avoid using it, to prevent having to remember whether it appends a newline. If we always use `fgets` and `fputs`, we know that we always have to deal with the newline character at the end of each line.

## 8. Standard I/O Efficiency
skipped
## 9. Binary I/O
If we're doing binary I/O, we often would like to read or write an entire structure at a time. To do this using `getc` or `putc`, we have to loop through the entire structure, one byte at a time, reading or writing each byte. We can't use the line-at-a-time functions, since `fputs` strops writing when it hits a null byte, and there might be null bytes within the structure. Similarly, `fgets` won't work correctly on input if any of the data bytes are nulls ore newline. Therefore, the following two functions are provided for binary I/O.
```c
#include <stdio.h>
size_t fread(void 8restrict ptr, size_t size, size_t nobj, FILE *restrict fp);
size_t fwrite(const void 8restrict ptr, size_t size, size_t nobj, FILE *restrict fp);

// Both return: number of objects read or written
```
These functions have two common uses:
1. Read or write a binary array. For example, to write elements 2 through 5 of a floating-point array, we could write
```c
float data[10];

if(fwrite(&data[2], sizeof(float), 4, fp)!=4)
    err_sys("fwrite error");
```
Here, we specify size as the size of each element of the array and `nobj` as the number of elements.
2. Read or write a structure. FOr example, we could write
```c
struct {
    short count;
    long total;
    char name[NAMESIZE];
} item;

if(fwrite(&item, sizeof(item), 1, fp)!=1)
    err_sys("fwrite error");
```
Here, we specify size as the size of structure and `nobj` as 1(the number of objects to write).

The obvious generalization of these two cases is to read or write an array of structures. To do this, size would be the `sizeof` the structure, and `nobj` would be the number of elements in the array.\
Both `fread` and `fwrite` return the number of objects read or written. For the read case, this number can be less than `nobj` if an error occurs or if the end of file is encountered. In this situation, `ferror` or `feof` must be called. For the write case, if the return value is less than the requested `nobj` , an error has occurred.\
A fundamental problem with binary I/O is that it can be used to read only data that has been written on the same system, but the norm today is to have heterogeneous systems connected together with networks. It is common to want to write data on one system and process it on another. These two functions won't work. The real solution for exchanging binary data among different systems is to use an agreed-upon canonical format.

## 10. Positioning a Stream
skipped

## 11. Formatted I/O
### Formatted Output
Formatted output is handled by the five `printf` functions.
```c
int printf(const char *restrict format, ...);

int fprintf(FILE *restrict fp, const char *restrict format, ...);

int dprintf(int fd, const char *restrict format, ...);
// All three return: number of characters output if OK, negative value if output error

int sprintf(char *restrict buf, const char *restrict format, ...);
// Returns: number of characters stored in array if OK, negative value if encoding error

int snprintf(char *restrict buf, size_t n, const char *restrict format, ...);
// Returns: number of characters that would hvae been stored in array if buffer was large enough, negative value if encoding error
```
The `printf` function writes to the standard output, `fprintf` writes to the specified stream, `dprintf` writes to the specified file descriptor, and `sprintf` places the formatted characters in the array `buf`. The `sprintf` function automatically appends a null byte at the end of the array, but this null byte is not included in the return value.\
Note that it's possible for `sprintf` to overflow the buffer pointed to by `buf`. The caller is responsible fore ensuring that the buffer is large enough. Because buffer overflows can lead to program instability and even security violations, `snprintf` was introduced. With it, the size of the buffer is an explicit parameter; any characters that would have been written past the end of the buffer are discarded instead. The `snprintf` function returns the number of characters that would have been written to the buffer had it been big enough. As with `sprintf`, the return value doesn't include the terminating null byte. If `snprintf` returns a positive value less than the buffer size n, then the output was not truncated. If an encoding error occurs, `snprintf` returns a negative value.\
Although `dprintf` doesn't deal with a file pointer, we include it with the rest of the related functions that handle formatted output. Note that using `dprintf` removes the need to call `fdopen` to convert a file descrptor into a file pointer for use with `fprintf`.\
The format specification controls how the remainder of the arguments will be encoded and ultimately displayed. Each argument is encoded according to a conversion specification that starts with a percent sign(%). Except for the conversion specifications, other characters in the format are copied unmodified. A conversion specification has four optional components, shown in square brackets below:
```
%[flags][fldwidth][precision][lenmodifier]convtype
```
The following five variants of the `printf` family are similar to the previous five but the variable argument list is replaced with `arg`.
```c
#include <stdarg.h>
#include <sdtio.h>
int vprintf(const char *restrict format, va_list arg);

int vfprintf(FILE *restrict fp, const char *restrict format, va_list arg);

int vdprintf(int fd, const char *restrict format, va_list arg);
// All three return: number of characters output if OK, negative value if output error

int vsprintf(char *restrict buf, const char *restrict format, va_list arg);
// Returns: number of characters stored in array if OK, negative value if encoding error

int vsnprintf(char *restrict buf, size_t n, const char *restrict format, va_list arg);
// Returns: number of characters that would hvae been stored in array if buffer was large enough, negative value if encoding error
```
### Formatted Input
Formatted input is handled by the three `scanf` functions
```c
#include <sdtio.h>

int scanf(const char *restrict format, ...);
int fscanf(FILE *restrict fp, const char *restrict format, ...);
int sscanf(const char * restrict buf, const char *restrict format, ...);

// All three return: number of input items assigned, EOF if input error or end of file before any conversion
```
The `scanf` family is used to pars an input string and convert character sequences into variables of specified types. The arguments following the format contain the addresses of the variables to initialize with the results of the conversions.\
Like the `printf` family, the `scanf` family supports functions that use variable argument lists as specified by `<stdarg.h>`.

## 12. Implementation Details
As we've mentioned, under the UNIX System, the standard I/O library ends up calling the I/O routines that we described in Chapter 3. Each standard I/O stream has an associated file descriptor, ans we can obtain the descriptor for a stream by calling `fileno`.
```c
#include <stdio.h>
int fileno(FILE *fp);

// Returns: the file descriptor associated with the stream
```
We need this function if we want to call the `dup` or `fcntl` functions.
## 13. Temporary Files
The ISO C standard defined two functions that are provided by the standard I/O library to assist in creating temporary files.
```
#include <stdio.h>

char *tmpnam(char *ptr);
// Returns: pointer to unique pathname

FILE *temfile(void);
// Returns: file pointer if OK, NULL on error
```
The `tmpnam` function generates a string that is a valid pathname and that does not match the name of any existing file. This function generates a different pathname each time it is called, up to `TMP_MAX` times. `TMP_MAX` is defined in `<stdio.h>`.\
If `ptr` is NULL, the generated pathname is stored in a static area, and a pointer to this area is returned as the value of the function. Subsequent calls to `tmpnam` can overwrite this static area. (Thus, if we call this function more than once and we want to save the pathname, we have to save a copy of the pathname, not a copy of the pointer). If `ptr` is not NULL, it is assumed that it points to an array of at least `L_tmpnam` characters. The generated pathname is stored in this array, and `ptr` is returned as the value of the function.\
The `tmpfile` function creates a temporary binary file that is automatically removed when it is closed or on program termination. Under the UNIX System, it makes no difference that this file is a binary file.\
The standard technique often used by the `tmpfile` function is to create a unique pathname by calling `tmpnam`, then create the file, and immediately unlink it. Recall from previous section that unlinking a file does not delete its contents until the file is closed. This way, when the file is closed, either explicitly or on program termination, the contents of the fire are deleted.\
The single UNIX Specification defines two additional functions as part of the XSI option for dealing with temporary files:
```c
#include <stdlib.h>

char *mkdtemp(char *template);
// Returns: pointer to directory name if OK, NULL on error

int mkstemp(char *template);
// Returns: file descriptor if OK, -1 on error
```
The `mkdtemp` function creates a directory with a unique name, and the `mkstemp` function creates a regular file with a unique name. The name is selected using the template string. This string is a pathname whose last six characters are set to XXXXXX. The function replaces these placeholders with different characters to create a unique pathname. If successful, these functions modify the template string to reflect the name of the temporary file.\
Unlike `tmpfile`, the temporary file created by `mkstemp` is not removed automatically for us. If we want to remove it from the file system namespace, we need to unlink it ourselves.\
Use of `tmpnam` and `tmpfile` does have at least one drawback: a window exists between the time that the unique pathname is returned and the time that an application creates a file with that name. During this timing window, another process can create a file of the same name. The `mkstemp` functions should be used instead, as they don't suffer from this problem.

## 14. Memory Streams
skipped
## 15. Alternatives to Standard I/O
skipped
## 16. Summary
skipped
