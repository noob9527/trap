# Chapter04 Files and Directories

## 1. Introduction
## 2. `stat`, `fstat`, `fstatat`, and `lstat` Functions
The discussion in this chapter centers on the four `stat` functions and the information they return.
```c
#include <sys/stat.h>
int stat(const char *restrict pathname, struct stat *restrict buf) ;
int stat(int fd, struct stat *buf) ;
int lstat(const char *restrict pathname, struct stat *restrict buf) ;
int stat(int fd, const char *restrict pathname, struct stat *restrict buf, int flag) ;
// All four return: 0 if OK, -1 on error
```
Given a pathname, the `stat` function returns a structure of information about the named file. The `fstat` function obtains information about the file that is already open on the descriptor `fd`. The `lstat` function is similar to `stat`, but when the named file is a symbolic link, `lstat` returns information about the symbolic link, not the file referenced by the symbolic link.\
The `fstatat` function provides a way to return the file statistics for a pathname relative to an open directory represented by the `fd` argument. The `flat` argument controls whether symbolic links are followed; when the `AT_SYMLINK_NOFOLLOW` flag is set, `fstatat` will not follow symbolic links, but rather returns information about the link itself. Otherwise, the default is to follow symbolic links, returning information about the file to which the symbolic link points. If the `fd` argument has the value `AT_FDCWD` and the pathname argument is a relative pathname, then `fstatat` evaluates the pathname argument relative to the current directory. If the pathname argument is an absolute pathname, then the `fd` argument is ignored. In these two cases, `fstatat` behaves like either `stat` or `lstat`, depending on the value of `flag`.\
The `buf` argument is a pointer to s structure that we must supply. The functions fill in the structure. The definition of the structure can differ among implementations.\
Note that most members of the `stat` structure are specified by a primitive system data type. We'll go through each member of this structure to examine the attributes of a file.\
The biggest user of the `stat` functions is probably the ls -l command, to learn all the information about a file.

## 3. File Types
We've talked about two different types of files so far: regular files and directories. Most files on a UNIX system are either regular files or directories, but there are additional types of files. The types are:
1. Regular file. The most common type of file, which contains data of some form. There is no distinction to the UNIX kernel whether this data is text or binary. Any interpretation of the contents of a regular file is left to the application processing the file.
2. Directory file. A file that contains the names of other files and pointers to information on these files. Any process that has read permission for a directory file can read the contents of the directory, but only the kernel can write directly to a directory file. Processes must use the functions described in this chapter to make changes to a directory.
3. Block special file. A type of file providing buffered I/O access in fixed-size units to devices such as disk drives.
4. Character special file. A type of file providing unbuffered I/O access in variable-sized units to devices. All devices on a system are either block special files or character special files.
5. FIFO. A type of file used for communication between processes. It's sometimes called a named pipe.
6. Socket. A type of file used for network communication between processes. A socket can also be used for non-network communication between processes on a single host.
7. Symbolic link. A type of file that points to another file.
The type of a file is encoded in the `st_mode` member of the `stat` structure. We can determine the file type with the macros shown below. The argument to each of these macros is the `st_mode`member from the `stat` structure.
| Macro | Type of file |
| ------ | ----------- |
| `S_ISREG()` | regular file |
| `S_ISDIR()` | directory file |
| `S_ISCHR()` | character special file |
| `S_ISBLK()` | block special file |
| `S_ISFIFO()` | pipe or FIFO |
| `S_ISLNK()` | symbolic link |
| `S_ISSOCK()` | socket |

## 4. Set-User-ID and Set-Group-ID
Every process has six or more IDs associated with it.
- The real user ID and real group ID identify who we really are. These two fields are taken from our entry in the password file when we log in. Normally, these values don't change during a login session, although there are ways for a superuser process to change them.
- The effective user ID, effective group ID, and supplementary group IDs determine our file access permissions.
- The saved set-user-ID and saved set-group-ID contain copies of the effective user ID and the effective group ID, respectively, when a program is executed.
Every file has an owner and a group owner. The owner is specified by the `st_uid` member of the `stat` structure; the group owner, by the `st_gid` member.\
When we execute a program file, the effective user ID of the process is usually the real user ID, and the effective group ID is usually the real group ID. However, we can also set a special flag in the file's mode word(`st_mode`) that says, "When this file is executed, set the effective user ID of the process to be the owner of the file(`st_uid`)." Similarly, we can set another bit in the file's mode word that causes the effective group ID to be the group owner of the file(`st_gid`). These two bits in the file's mode word are called the set-user-ID bit and the set-group-ID bit.\
For example, if the owner of the file is the superuser and if the file's set-user-ID bit is set, then while that program file is running as a process, it has superuser privileges. This happens regardless of the real user ID of the process that executes the file. As an example, the UNIX System program that allows anyone to change his or her password, `passwd`, is a set-user-ID program. This is required so that the program can write the new password to the password file, typically either `/etc/passwd` or `/etc/shadow`, files that should be writable only by the superuser. Because a prcess that is running set-user-ID to some other user usually assumes extra permissions, it must be written carefully.\
Returning to the `stat` function, the set-user-ID bit and the set-group-ID bit are contained in the file's `st_mode` value. These two bits can be tested against the constants `S_ISUID` and `S_ISGID`, respectively.

## 5 File Access Permissions
There are nine permission bits for each file, divided into three categories. They are shown in below.
| Type | Description |
| ------ | ----------- |
| `S_IRUSR` | user-read |
| `S_IWUSR` | user-write |
| `S_IXUSR` | user-execute |
| `S_IRGRP` | group-read |
| `S_IWGRP` | group-write |
| `S_IXGRP` | group-execute |
| `S_IROTH` | other-read |
| `S_IWOTH` | other-write |
| `S_IXOTH` | other-execute |

## 6 Ownership of New Files and Directories
The user ID of a new file is set to the effective user ID of the process. POSIX.1 allows an implementation to choose one of the following options to determine the group ID of a new file:
1. The group ID of a new file can be the effective group ID of the process.
2. The group ID of a new file can be the group ID of the directory in which the file is being created.

## 7 `access` and `faccessat` Functions
As we described earlier, when we open a file, the kernel performs its access tests based on the effective user and group IDs. Sometimes, however, a process wants to test accessibility based on the real user and group IDs. This is useful when a process is running as someone else, using either the set-user-ID or the set-group-ID feature. Event though a process might be set-user-ID to root, it might still want to verify that the real user can access and group IDs.
```c
#include <unistd.h>
int access(const char *pathname, int mode);
int faccessat(int fd, const char *pathname, int mode, int flat);
// Both returns: 0 if OK, -1 on error
```

## 8 `umask` Function
The `umask` function sets the file mode creation mask for the process and returns the precious value.
```c
#include <sys/stat.h>
mode_t umask(mode_t cmask);
// returns: previous file mode creation mask
```
The `cmask` is formed as the bitwise OR of any of the nine constants from `S_IRUSR`, `SIWUSR`, and so on.\
The file mode creation mask is used whenever the process creates a new file or a new directory. Any bits that are on in the file mode creation mask are turned off in the file's mode.\

## 9 `chmod`, `fchmod`, and `fchmodat` Functions
The `chmod`, `fchmod`, and `fchmodat` functions allow us to change the file access permissions for an existing file.
```c
#include <sys/stat.h>
int chmod(const char *pathname, mode_t mode);
int fchmod(int fd, mode_t mode);
int fchmodat(int fd, const char *pathname, mode_t mode, int flag);

// All three return: 0 if OK, -1 on error
```
To change the permission bits of a file, the effective user ID of the process must be equal to the owner ID of the file, or the process must have superuser permissions.

## 10 Sticky Bit
TBD
## 11 `chown`, `fchown`, `fchownat`, and `lchown` Functions
TBD
## 12 File Size
TBD
## 13 File Truncation
TBD
## 14 File Systems
TBD
## 15 `link`, `linkat`, `unlink`, `unlinkat`, and `remove` Functions
TBD
## 16 `rename` and `renameat` Functions
TBD
## 17 Symbolic Links
TBD
## 18 Creating and Reading Symbolic Links
TBD
## 19 File Times
TBD
## 20 `futimens`, `utimensat`, and `utimes` Functions
TBD
## 21 `mkdir`, `mkdirat`, and `rmdir` Functions
TBD
## 22 Reading Directories
TBD
## 23 `chdir`, `fchdir`, and `getcwd` Functions
TBD
## 24 Device Special Files
TBD
## 25 Summary of File Access Permission Bits
TBD
## 26 Summary
TBD
