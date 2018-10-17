# io
## system calls
### create
Used to Create a new empty file.\
syntax:
```c
int creat(char *filename, mode_t mode)
```
### open
Used to Open the file for reading, writing or both.
Syntax:
```c
#include<sys/types.h>
#includ<sys/stat.h>
#include <fcntl.h>
int open (const char* Path, int flags [, int mode ]);
```
### close
Tells the operating system you are done with a file descriptor and Close the file which pointed by fd.
```c
#include <fcntl.h>
int close(int fd);
```
### read
From the file indicated by the file descriptor fd, the read() function reads cnt bytes of input into the memory area indicated by buf. A successful read() updates the access time for the file.
```c
#include <fcntl.h>
size_t read (int fd, void* buf, size_t cnt);
```
### write
Writes cnt bytes from buf to the file or socket associated with fd. cnt should not be greater than INT_MAX (defined in the limits.h header file). If cnt is zero, write() simply returns 0 without attempting any other action.
```c
#include <fcntl.h>
size_t write (int fd, void* buf, size_t cnt);
```

Reference:
- [io-system-calls](https://www.geeksforgeeks.org/input-output-system-calls-c-create-open-close-read-write/)

## standard functions
### gets, fgets
`gets` reads characters from the standard input (stdin) and stores them as a C string into str until a newline character or the end-of-file is reached.\
syntax:
```c
char *gets(char *str)
// str :Pointer to a block of memory (array of char)
// where the string read is copied as a C string.
// returns : the function returns str
```
- It is not safe to use because it does not check the array bound.
- It is used to read string from user until newline character not encountered.

Example : Suppose we have a character array of 15 characters and input is greater than 15 characters, gets() will read all these characters and store them into variable.Since, gets() do not check the maximum limit of input characters, so at any time compiler may return buffer overflow error.
```c
// C program to illustrate
// gets()
#include <stdio.h>
#define MAX 15

int main()
{
    char buf[MAX];

    printf("Enter a string: ");
    gets(buf);
    printf("string is: %s\n", buf);

    return 0;
}
```

`fgets` reads a line from the specified stream and stores it into the string pointed to by str. It stops when either (n-1) characters are read, the newline character is read, or the end-of-file is reached, whichever comes first.
Syntax:
```c
char *fgets(char *str, int n, FILE *stream)
// str : Pointer to an array of chars where the string read is copied.
// n : Maximum number of characters to be copied into str
// (including the terminating null-character).
// *stream : Pointer to a FILE object that identifies an input stream.
// stdin can be used as argument to read from the standard input.
//
// returns : the function returns str
```
- It follow some parameter such as Maximum length, buffer, input device reference.
- It is safe to use because it checks the array bound.
- It keep on reading until new line character encountered or maximum limit of character array.
Example : Let’s say the maximum number of characters are 15 and input length is greater than 15 but still fgets() will read only 15 character and print it.
```c
// C program to illustrate
// fgets()
#include <stdio.h>
#define MAX 15
int main()
{
    char buf[MAX];
    fgets(buf, MAX, stdin);
    printf("string is: %s\n", buf);

    return 0;
}
```

Reference:
- [fgets-gets-c-language](https://www.geeksforgeeks.org/fgets-gets-c-language/)

### puts, fputs
- puts copies the null-terminated string s to the standard output stream stdout and appends a new-line character.
- fputs copies the null-terminated string s to the named output stream.
Neither routine copies the terminal null character.

reference:
- [puts-fputs](https://www.sas.upenn.edu/~saul/parasite/man/man3/puts.3.html)


