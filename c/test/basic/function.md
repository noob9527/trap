# function

### memset
memset() is used to fill a block of memory with a particular value.
The syntax of memset() function is as follows :
```c
// ptr ==> Starting address of memory to be filled
// x   ==> Value to be filled
// n   ==> Number of bytes to be filled starting
//         from ptr to be filled
void *memset(void *ptr, int x, size_t n);
```
Note that ptr is a void pointer, so that we can pass any type of pointer to this function.
Let us see a simple example in C to demonstrate how memset() function is used:
```c
// C program to demonstrate working of memset()
#include <stdio.h>
#include <string.h>

int main()
{
    char str[50] = "GeeksForGeeks is for programming geeks.";
    printf("\nBefore memset(): %s\n", str);

    // Fill 8 characters starting from str[13] with '.'
    memset(str + 13, '.', 8*sizeof(char));

    printf("After memset():  %s", str);
    return 0;
}
```
`memset` is preferable to `bzero` due to `bzero` is deprecated and reduces portability.

Reference:
- [memset-c-example](https://www.geeksforgeeks.org/memset-c-example/)
- [why-use-bzero-over-memset](https://stackoverflow.com/questions/17096990/why-use-bzero-over-memset)

### pause

#### synopsis
```c
#include <unistd.h>

int pause(void);
```
#### description
The pause() library function causes the invoking process (or thread) to sleep until a signal is received that either terminates it or causes it to call a signal-catching function.

#### return value
The pause() function only returns when a signal was caught and the signal-catching function returned. In this case pause() returns -1, and errno is set to EINTR.

Reference:
- [pause](http://www.tutorialspoint.com/unix_system_calls/pause.htm)
