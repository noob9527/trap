# type

### `size_t`,`ssize_t`
`size_t` is an unsigned integer data type which is used to represent sizes of objects in bytes, hence it can be returned by the sizeof operator. So it is guaranteed to be big enough to contain the size of the biggest object that system can handle. Basically the maximum permissible size is dependent on the compiler. If compiler is 32 bit then it is nothing other than typedef(i.e., alias) for unsigned int but if compiler is 64 bit then it would be a typedef for unsigned long long. In short size_t is never negative.\
Therefore many library function of C language declare their argument and return type as size_t like `malloc`, `memcpy` and `strlen`.\
In short, `ssize_t` is the same as `size_t`, but is a signed type - read `ssize_t` as “signed size_t”. ssize_t is able to represent the number -1, which is returned by several system calls and library functions as a way to indicate error. For example, the read and write system calls:
```c
#include <sys/types.h>
#include <unistd.h>

ssize_t read(int fildes, void *buf, size_t nbyte);
ssize_t write(int fildes, const void *buf, size_t nbyte);
```
If you know that `ssize_t` is non-negative, then you can safely cast `ssize_t` to `size_t`, as this will not change the value.

```c
size_t x = ...;
ssize_t y = ...;
printf("%zu\n", x);  // prints as unsigned decimal
printf("%zx\n", x);  // prints as hex
printf("%zd\n", y);  // prints as signed decimal
```
