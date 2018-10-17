# Chapter06 System Data Files and Information

## 1. Introduction
skipped

## 2. Password File
The UNIX System's password file, called the user database by POSIX.1, contains the fields shown in below, These fields are contained in a `passwd` structure that is defined in `<pwd.h>`.

| Description | struct passwd member | POSIX.1 | Max OS X | Linux |
| ------ | ----------- | - | - | - |
| user name                     | `char   *pw_name`   | - | - | - |
| encrypted password            | `char   *pw_passwd` |   | - | - |
| numerical user ID             | `uid_t  *pw_uid`    | - | - | - |
| numerical group ID            | `gid_t  *pw_gid`    | - | - | - |
| comment field                 | `char   *pw_gecos`  |   | - | - |
| initial working directory     | `char   *pw_dir`    | - | - | - |
| initial shell (user program)  | `char   *pw_shell`  | - | - | - |
| user access class             | `char   *pw_class`  |   | - |   |
| next time to change password  | `time_t *pw_change` |   | - |   |
| account expiration time       | `time_t *pw_expire` |   | - |   |

Historically, the password file has been stored in `/etc/passwd` and has been an ASCII file. Each line contains the fields described in the table, separated by colons. For example, four lines from the `/etc/passwd` file on Linux could be
```
root:x:0:0:root:/root:/bin/bash
squid:x:23:23::/var/spool/squid:/dev/null
nobody:x:65534:65534:nobody:/nonexistent:/usr/sbin/nologin
```
Note the following points about the entries.
- There is usually an entry with the user name root. This entry has a user ID of -(the superuser).
- The encrypted password field contains a single character as a placeholder where older versions of the UNIX System used to store the encrypted password. Because it is a security hole to store the encrypted password in a file that is readable by everyone, encrypted passwords are now kept elsewhere.
- Some fields in a password file entry can be empty. If the encrypted password fields is empty, it usually means that the user does not have a password. The entry for `squid` has one blank field: the comment field. An empty comment field has no effect.
- The shell field contains the name of the executable program to be used as the login shell for the user. The default value for an empty shell field is usually `/bin/sh`. Note, however, that the entry for `squid` has `/dev/null` as the login shell. Obviously, this is a device and cannot be executed, so its use here is to prevent anyone from logging in to our system as user `squid`.\
Many Services have separate user IDs for the daemon processes that help implement the service. The `squid` entry is for the processes implementing the `squid` proxy cache service.
- There are several alternatives to using `/dev/null` to prevent a particular user from logging in to a system. For example, `/bin/false` is often used as the login shell. It simply exits with an unsuccessful (nonzero) status; the shell evaluates the exit status as false. It is also common to see `/bin/true` used to disable an account; it simply exits with a successful (zero) status. Some systems provide the `nologin` command, which prints a customizable error message and exits with a nonzero exit status.
- The `nobody` user name can be used to allow people to log in to a system, but with a user ID (65534) and group ID (65534) that provide no privileges. The only files that this user ID and group ID can access are those that are readable or writable by the world.
- Some systems that provide the `finger(1)` command support additional information in the comment field. Each of these fields is separated by a comma:the user's name, office location, office phone number, and home phone number. Additionally, an ampersand in the comment field is replaced with the login name (capitalized) by some utilities. Event if your system doesn't support the `finger` command, these fields can still go into the comment field, since that field is simply a comment and not interpreted by system utilities.

Some systems provide the `vipw` command to allow administrators to edit the password file. The `vipw` command serializes changes to the password file and makes sure that any additional files are consistent with the changes mad. It is also common for systems to provide similar functionality through graphical user interfaces.\
POSIX.1 defines two functions to fetch entries from the password file. These functions allow us to look up an entry given a user's login name or numerical user ID.
```c
#include <pwd.h>

struct passwd *getpwuid(uid_t uid);
struct passwd *getpwnam(const char *name);

// Both return: pointer if OK, NULL on error
```
The `getpwuid` function is used by the `ls(1)` program to map the numerical user ID contained in an i-node into a user's login name. The `getpwnam` function is used by the `login(1)` program when we enter our login name.\
Both functions return a pointer to a `passwd` structure that the functions fill in. This structure is usually a static variable within the function, so its contents are overwritten each time we call either of these functions.\
These two POSIX.1 functions are fine if we want to look up either a login name or a user ID, but some programs need to go through the entire password file. Three functions can be used for this purpose:
```c
#include <pwd.h>

struct passwd *getpwent(void);

// Returns: pointer if OK, NULL on error or end of file

void setpwent(void);
void endpwent(void);
```
We call `getpwent` to return the next entry in the password file. As with the two POSIX.1 functions, `getpwent` returns a pointer to a structure that it has filled in. This structure is normally overwritten each time we call this function. If this is the first call to this function, it opens whatever files it uses. There is no order implied when we use this function; the entries can be in any order, because some systems use a hashed version of the file `/etc/passwd`.\
The function `setrpwent` rewinds whatever files it uses, and `endpwent` closes these files. When using `getpwent`, we must always be sure to close these files by calling `endpwent` when we're through. Although `getpwent` is smart enough to know when it has to open its files, it never knows when we're through.

## 3. Shadow Passwords
To make it more difficult to obtain the raw materials(the encrypted passwords), systems now store the encrypted password in another file, often called the shadow password file. Minimally, this file has to contain the user name and the encrypted password. Other information relating to the password is also stored here.
| Description | struct `spwd` member |
| ------ | ----------- |
| user login name                          | `char  *sp_namp`       |
| encrypted password                       | `char  *sp_pwdp`       |
| days since Epoch of last password change | `int    sp_lstchg`     |
| days until change allowed                | `int    sp_min`        |
| days before change required              | `int    sp_max`        |
| days warning for expiration              | `int    sp_warn`       |
| days before account inactive             | `int    sp_inact`      |
| days since Epoch when account expires    | `int    sp_expire`     |
| reserved                                 | `unsigned int sp_flag` |

The only two mandatory fields are the user's login name and encrypted password. The other fields control how often the password is to change--known as "password aging"--and how long an account is allowed to remain active.

## 4. Group File
| Description | struct group member | POSIX.1 | Max OS X | Linux |
| ------ | ----------- | - | - | - |
| group name                                 | `char   *gr_name`   | - | - | - |
| encrypted password                         | `char   *gr_passwd` |   | - | - |
| numerical group ID                         | `int     gr_gid`    | - | - | - |
| array of pointers to individual user names | `char  **gr_mem`    | - | - | - |

The UNIX System's group file, called the group database by POSIX.1, contains the fields shown in the table. These fields are contained in a `group` structure that is defined is `<grp.h>`.

## 5. Supplementary Group IDs
skipped
## 6. Implementation Differences
skipped
## 7. Other Data Files
We've discussed only two of the system's data files so far: the password file and the group file. Numerous other files are used by UNIX systems in normal day-to-day operation. Fortunately, the interfaces to these various files are like the ones we've already described for the password and group files.\
The general principle is that every data file has at least three functions:
1. A `get` function that reads the next record, opening the file if necessary. These functions normally return a pointer to a structure. A null pointer is returned when the end of file is reached. Most of the `get` functions return a pointer to a static structure, so we always have to copy the structure if we want to save it.
2. A `set` function that opens the file, if not already open, and rewinds the file. We use this function when we know we want to start again at the beginning of the file.
3. An `end` entry that closes the data file. As we mentioned earlier, we always have to call this function when we're done, to close all the files.

Additionally, if the data file supports some form of keyed lookup, routines are provided to search for a record with a specific key. For example, two keyed lookup routines are provided for the password file: `getpwnam` looks for a record with a specific user name, and `getpwuid` looks for a record with a specific user ID.
## 8. Login Accounting
skipped
## 9. System Identification
POSIX.1 defines the `uname` function to return information on the current host and operating system.
```c
#include <sys/utsname.h>

int uname(struct tsname *name);

// Returns: non-negative value if OK, -1 on error
```
We pass the address of a `utsname` structure to this function, and the function then fills it in.
## 10. Time and Date Routines
The basic time service provided by the UNIX kernel counts the number of seconds that have passed since the Epoch: 00:00:00 January 1, 1970, Coordinated Universal Time(UTC). In previous section, we said that these seconds are represented in a `time_t` data type, and we call then calendar times. These calendar times represent both the time and the data. The UNIX System has always differed from other operating systems in:
- keeping time in UTC instead of the local time
- automatically handling conversions, such as daylight saving time
- keeping the time and date as a single quantity

The time function returns the current time and data.
```c
#include <time.h>

time_t time(time_t *calcptr);

// Returns: value of time if OK, -1 on error
```
The time value is always returned as the value of the function. If the argument is non null, the time value is also stored as at the location pointed to by `calptr`.

## 11. Summary
skipped
