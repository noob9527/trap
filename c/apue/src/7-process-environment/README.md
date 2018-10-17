# Chapter07 Process Environment

## 1. Introduction
skipped

## 2. `main` Function
A C program starts execution with a function called `main`. The prototype for the main function is
```c
int main(int argc, char *argv[]);
```
where `argc` is the number of command-line arguments, and `argv` is an array of pointers to the arguments.\
When a C program is executed by the kernel--by one of the `exec` functions, a special start-up routine is called before the `main` function is called. The executable program file specifies this routine as the starting address for the program; this is set up by the link editor when it is invoked by the C compiler. This start-up routine takes values from the kernel--the command-line arguments and the environment--and sets things up so that the main function is called as shown earlier.

## 3. Process Termination
There are eight ways for a process to terminate. Normal termination occurs in five ways:
1. Return from `main`
2. Calling `exit`
3. Calling `_exit` or `_Exit`
4. Return of the last thread from its start routine
5. Calling `pthread_exit` from the last thread

Abnormal termination occurs in three ways
6. Calling `abort`
7. Receipt of a signal
8. Response of the last thread to a cancellation request

The start-up routine that we mentioned in the previous section is also written so that if the `main` function returns, the `exit` function is called. If the start-up routine were coded in C (it is often coded in assembly language) the call to `main` could look like
```c
exit(main(argc, argv));
```

### Exit Functions
Three functions terminate a program normally: `_exit` and `_Exit`, which return to the kernel immediately, and `exit`, which performs certain cleanup processing and then returns to the kernel.
```c
#include <stdlib.h>

void exit(int status);
void _Exit(int status);

#include <unistd.h>

void exit(int status);
```
Historically, the `exit` function has always performed a clean shutdown of the standard I/O library: the `fclose` function is called for all open streams. Recall from previous sections that this causes all buffered output data to be flushed(written to the file).\
All three exit function expect a single integer argument, which we call the exit status. Most UNIX System shells provide a way to examine the exit status of a process. If
- Any of these functions is called without an exit status
- `main` does a return without a return value
- the `main` function is not declared to return an integer.

The exit status of the process is undefined. However, if the return type of main is an integer and main "falls off the end"(an implicit return), the exit status of the process is 0(This behavior is new with the 1999 version of the ISO C standard.).\
Returning an integer value from the `main` function is equivalent to calling `exit` with the same value. Thus `exit(0)` is the same as `return(0);` from the `main` function.

#### `atexit` Functions
With ISO C, a process can register at least 32 functions that are automatically called by `exit`. These are called exit handlers and are registered by calling the `atexit` function.
```c
#include <stdlib.h>

int atexit(void (* func)(void));

// Returns: 0 if OK, nonzero on error
```
This declaration says that we pass the address of a function as the argument to `atexit`. When this function is called, it is not passed any arguments and is not expected to return a value. The `exit` function calls these functions in reverse order of their registration. Each function is called as many times as it was registered.\
With ISO C and POSIX.1, `exit` first calls the exit handlers and then closes(via `fclose`) all open streams. POSIX.1 extends the ISO C standard by specifying that any exit handlers installed will be cleared if the program calls any of the `exec` family of functions.\
The only way a program can be executed by the kernel is if one of the `exec` functions is called. The only way a process can voluntarily terminate is if `_exit` or `_Exit` is called, either explicitly or implicitly (by calling exit). A process can also be involuntarily terminated by a signal.

## 4. Command-Line Arguments
skipped

## 5. Environment List
Each program is also passed an environment list. Like the argument list, the environment list is an array of character pointers, with each pointer containing the address of a null-terminated C string. The address of the array of pointers is contained in the global variable environ:
```c
extern char **environ;
```
We'll cal `environ` the environment pointer, the array of pointers the environment list, and the strings they point to the environment strings.\
Access to specific environment variables is normally through the `getenv` and `putenv` functions, instead of through the `environ` variable. But to go through the entire environment, the `environ pointer must be used.`

## 6. Memory Layout of a C Program
Historically, a C program has been composed of the following pieces:
- Text segment, consisting of the machine instructions that the CPU executes. Usually, the text segment is shareable so that only a single copy needs to be in memory for frequently executed programs, such as text editors, the C compiler the shells, and so on. Also, the text segment is often read-only, to prevent a program from accidentally modifying its instructions.
- Initialized data segment, usually called simply the data segment, containing variables that are specifically initialized in the program. For example, the C declaration
```c
int maxcount = 99;
```
appearing outside any function causes this variable to be stored in the initialized data segment with its initial value.
- Uninitialized data segment, often called the "bss" segment, named after an ancient assembler operator that stood for "block started by symbol." Data in this segment is initialized by the kernel to arithmetic 0 or null pointers before the program starts executing. The C declaration
```c
long sum[1000];
```
appearing outside any function causes this variable to be stored in the uninitialized data segment.
- Stack, where automatic variables are stored, along with information that is saved each time a function is called. Each time a function is called, the address of where to return to and certain information about the caller's environment, such as some of the machine registers, are saved on the stack. The newly called function then allocates room on the stack for its automatic and temporary variables. This is how recursive function in C can work. Each time a recursive function calls itself, a new stack frame is used, so one set of variables doesn't interfere with the variables from another instance of the function.
- Heap, where dynamic memory allocation usually takes place. Historically, the heap has been located between the uninitialized data and the stack.

Note that the contents of the uninitialized data segment are not stored in the program file on disk, because the kernel sets the contents to 0 before the program starts running. The only portions of the program that need to be saved in the program file are the text segment and the initialized data.\
The size(1) command reports the sizes(in bytes) of the text, data, and bss segments.

## 7. Shared Libraries
Most UNIX systems today support shared libraries. Shared libraries remove the common library routines from the executable file, instead maintaining a single copy of the library routine somewhere in memory that all processes reference. This reduces the size of each executable file but may add some runtime overhead, either when the program is first executed or the first time each shared library function is called. Another advantage of shared libraries is that library functions can be replaced with new versions without having to relink edit every program that used the library.

## 8. Memory Allocation
ISO C specifies three functions for memory allocation:
1. `malloc`, which allocates a specified number of bytes of memory. This initial value of the memory is indeterminate.
2. `calloc`, which allocates space for a specified number of objects of a specified size. The space is initialized to all 0 bits.
3. `realloc`, which increases or decreases the size of a previously allocated area. When the size increases, it may involve moving the previously allocated area somewhere else, to provide the additional room at the end. Also, when the size increases, the initial value of the space between the old contents and the end of the new area ins indeterminate

```c
include <stdlib.h>
void *malloc(size_t size);
void *calloc(size_t nobj, size_t size);
void *realloc(void *ptr, size_t newsize);

// All three return: non-null pointer if OK, NULL on error

void free (void *ptr);
```
The pointer returned by the three allocation functions is guaranteed to be suitably aligned so that it can be used for any data object. For example, if the most restrictive alignment requirement on a particular system requires that `doubles` must start at memory locations that are multiples of 8, then all pointers returned by these three functions would be so aligned.\
The function `free` causes the space pointed to by `ptr` to be  deallocated. This freed space is usually put into a poll of available memory and can be allocated in a later call to one of the three `alloc` functions.\
The allocation routines are usually implemented with the `sbrk(2)` system call. This system call expands (or contracts) the heap of the process. Although `sbrk` can expand or contract the memory of a process, most versions of `malloc` and `free` never decrease their memory size. The space that we free is available for a later allocation, but the freed space is not usually returned to the kernel; instead, that space is kept in the `malloc` pool.\
Most implementations allocate more space than requested and use the additional space for record keeping--the size of the block, a pointer to the next allocated block, and the like. As a consequence, writing past the end or before the start of an allocated area could overwrite this record-keeping information in another block. These types of errors are often catastrophic, but difficult to find, because the error may not show up until much later.\
Writing past the end or before the beginning of a dynamically allocated buffer can corrupt more than internal record-keeping information. The memory before and after a dynamically allocated buffer can potentially be used for other dynamically allocated object. These objects can be unrelated to the code corrupting them, making it even more difficult to find the source of the corruption.\
Other possible error that can be fatal are freeing a block that was already freed and calling free with a pointer that was not obtained from one of the three `alloc` functions. If a process calls `malloc` but forgets to call `free`, its memory usage will continually increase; this is called leakage. If we do not call `free` to return unused space, the size of a process's address space will slowly increase until no free space is left During this time, performance can degrade from excess paging overhead.

### Alternate Memory Allocators
- `libmalloc`
    skipped
- `vmalloc`
    skipped
- quick-fit
    skipped
- `jemalloc`
    skipped
- `TCMalloc`
    skipped
- `alloca`
    One additional function is also worth mentioning. The function `alloca` has the same calling sequence as `malloc`; however, instead of allocating memory from the heap, the memory is allocated from the stack frame of the current function. The advantage is that we don't have to free the space; it goes away automatically when the function returns. The `alloca` function increases the size of the stack frame. The disadvantage is that some systems can't support `alloca`, if it's impossible to increase the size of the stack frame after the function has been called. Nevertheless, many software packages use it, and implementations exist for a wide variety of systems.

## 9. Environment Variables
As we mentioned earlier, the environment strings are usually of the form
```
name=value
```
The UNIX kernel never looks at these strings; their interpretation is up to the various applications. The shells, for example, use numerous environment variables. Some, such as HOME and USER, are set automatically at login; others are left for us to set. We normally set environment variables in a shell start-up file to control the shell's actions.\
ISO C defines a function that we can use to fetch values from the environment, but this standard says that then contents of the environment are implementation defined.
```c
#include <stdlib.h>

char *getenv(const char *name);

// Returns: pointer to value associated with name, NULL if not found
```
Note that this function returns a pointer to the value of a new-value string. We should always use `getenv` to fetch a specific value from the environment, instead of accessing `environ` directly.\
In addition to fetching the value of an environment variable, sometimes we may want to set an environment variable. We may want to change the value of an existing variable or add a new variable to the environment. (In the next chapter, we'll see that we can affect the environment of only the current process and any child processes that we invoke. We cannot affect the environment of the parent process, which is often a shell. Nevertheless, it is still useful to be able to modify the environment list.) Unfortunately, not all systems support this capability. The following table shows the functions that are supported by the various standards and implementations.
| Function   | ISO C | POSIX.1 | Linux | Mac OS X |
| ---------- | - | - | - | - |
| `getenv`   | - | - | - | - |
| `putenv`   |   |XSI| - | - |
| `setenv`   |   | - | - | - |
| `unsetenv` |   | - | - | - |
| `clearenv` |   |   | - |   |

The prototypes for the middle three functions are
```c
#include <stdlib.h>

int putenv(char *str);
// Returns: 0 if OK, nonzero on error

int setenv(const char *name, const char *value, int rewrite);

int unsetenv(const char *name);

// Both return: 0 if OK, -1 on error
```
The operation of these three functions is as follows:
- the `putenv` function takes a string of the form name-value and places it in the environment list. If name already exists, its old definition is first removed.
- The `setenv` function sets name to value. If name already exists in the environment, then if `rewrite ` is nonzero, the existing definition for name is first removed; or if `rewrite` is 0, an existing definition for name is not removed, name is not set to the new value, and no error occurs.
- The `unsetenv` function removes any definition of name. It is not an error if such a definition does not exist.

Note the difference between `putenv` and `setenv`. Whereas `setenv`must allocate memory to create the name-value string from its arguments, `putenv` is free to place the string passed to it directly into the environment. Indeed, many implementations do exactly this, so it would be an error to pass `putenv` a string allocated on the stack, since the memory would be reused after we return from the current function.

## 10. `setjmp` and `longjmp` Functions
```c
#include <setjmp.h>

int setjmp(jmp_buf env);

// Returns: 0 if called directly, nonzero if returning from a call to longjmp

void longjmp(jmp_buf env, int val);
```
We call `setjmp` from the location that we want to return to, the argument `env` is of the special type `jmp_buf`. This data type is some form of array that a capable of holding all the information required to restore the status of the stack to the state when we call `longjmp`. Normally, the `env` variable is a global variable, since we'll need to reference it from another function.

### Automatic, Register, and Volatile Variables
We've seen what the stack looks like after calling `longjmp`. The next question is, "What are the states of the automatic variables and register variables in the main function?" the answer is "It depends." Most implementations do not try to roll back these automatic variables and register variables, but the standards say only that their values are indeterminate. If you have an automatic variable that you don't want rolled back, define it with the `volatile` attribute. Variables that are declared as global or static are left alone when `longjmp` is executed.

### Potential Problem with Automatic Variables
skipped

## 11. `getrlimit` and `setrlimit` Functions
Every process has a set of resource limits, some of which can be queried and changed by the `getrlimit` and `setrlimit` functions
```
#include <sys/resource.h>

int getrlimit(int resource, struct rlimit *rlptr);
int setrlimit(int resource, const  struct rlimit *rlptr);

// Both return: 0 if OK, -1 on error
```
Each call to these two functions specifies a single resource and a pointer to the following structure:
```c
struct rlimit {
    rlim_t rlim_cur;    // soft limit: current limit
    rlim_t rlim_max;    // hard limit: maximum value for rlim_cur
}
```
Three rules govern the changing of the resource limits
1. A process can change its soft limit to a value less than or equal to its hard limit.
2. A process can lower its hard limit to a value greater than or equal to its soft limit. This lowering of the hard limit is irreversible for normal users.
3. Only a superuser process can raise a hard limit.

An infinite limit is specified by the constant `RLIM_INFINITY`.\
The resource limits affect the calling process and are inherited by any of its children. This means that the setting of resource limits needs to be built into the shells to affect all our future processes. Indeed, the Bourne shell, the GNU Bourne-again shell, and the Korn shell have the built-in `ulimit` command, and the C shell has the built-in limit command.

## 12. Summary
skipped
