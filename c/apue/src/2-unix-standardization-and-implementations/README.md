# Chapter02 UNIX Standardization and Implementations

## 1. Introduction
## 2. UNIX Standardization
### ISO C
- ANSI(American National Standards Institute)
- ISO(International Organization for Standardization)
- IEC(International Electrotechnical Commission)
### IEEE POSIX
- IEEE(Institute of Electrical and Electronics Engineers)
- POSIX(Portable Operating System Interface)
### The Single UNIX Specification
### FIPS
- FIPS(Federal Information Processing Standard)
## 3. UNIX System Implementations
### UNIX System V Release 4
### 4.4BSD
### FreeBSD
### Linux
### Max OS X
### Solaris
### Other UNIX Systems
## 4. Relationship of Standards and Implementations
## 5. Limits
three types of limits are provided:
1. Compile-time limits(headers)
2. Runtime limits not associated with a file or directory(the sysconf function)
3. Runtime limits that are associated with a file or a directory(the pathconf and fpathconf functions)
### ISO C Limits
- one's-complement arithmetic
- two's-complement arithmetic

Although the ISO C standard specifies minimum acceptable values for integral data types, POSIX.1 makes extensions to the C standard. To conform to POSIX.1, an implementation must suppor a minimum value of 2147,483,647 for INT_MAX, -2147,483,647 for INT_MIN, and 4,294,967,295 for UINT_MAX.\
Although ISO C defines the constant FILENAME_MAX, we avoid using it, because POSIX.1 provides better alternatives(NAME_MAX and PATH_MAX).
### POSIX Limits
### XSI Limits
### sysconf, pathconf, and fpathconf Functions
The runtime limits are obtained by calling one of the following three functions.
```c
#include <unistd.h>
long sysconf(int name);
long pathconf(const char *pathname, int name);
long fpathconf(int fd, int name);
```
The difference between the last two functions is that one takes a pathname as its argument and the other takes a file descriptor argument.\
We need to look in more detail at the different return values from these three functions.
1. All three functions return -1 and set errno to EINVAL if the name isn't one of the appropriate constants.
2. Some names can return either the value of the variable (a return value >= 0) or an indication that the value is indeterminate. An indeterminate value is indicated by return -1 and not changing the value of errno.

### Indeterminate Runtime Limits
#### Pathnames
If the constant PATH_MAX is defined in <limits.h>, then we're all set. If it's not, then we need to call pathconf. The value returned by pathconf is the maximum size of a relative pathname when the first argument is the working directory, so we specify the root as the first argument and add 1 to the result. If pathconf indicates that PATH_MAX is indeterminate, we have to punt and just guess a value.
#### Maximum Number of Open Files
We would hope to use the POSIX.1 value OPEN_MAX to determine this value portably, but if the value is indeterminate, we still have a problem.\
Our best option in this case is just to close all descriptors up to some arbitrary limit--say, 256. We show this technique in Figure 2.17. As with our pathname example, this strategy is not guaranteed to work for all cases, but it's the best we can do without using a more exotic approach.\
The OPEN_MAX value is called runtime invariant by POSIX, meaning that its value should not change during the lifetime of a process.

## 6. Options
If we are to write portable applications that depend on optionally supported features, we need a portable way to determine whether an implementation supports a given option.\
Just as with limits(Section 2.5), POSIX.1 defines three ways to do this.
1. Compile-time options are defined in <unistd.h>.
2. Runtime options that are not associated with a file or a directory are identified with the sysconf function.
3. Runtime options that are associated with a file or a directory are discovered by calling either the pathconf or the fpathconf function.

## 7. Feature Test Macros
## 8. Primitive System Data Types
## 9. Differences Between Standards
## 10. Summary


