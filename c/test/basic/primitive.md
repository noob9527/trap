# 基础数据类型

### keywords
- int
- long
- short
- char
- float
- double
- _Bool
- _Complex
- _Imaginary
- unsigned
- signed

### truthy, falsy
c 认为所有的非零值都是 truthy 值

### string
Strings are defined as an array of characters. The difference between a character array and a string is the string is terminated with a special character ‘\0’.\
Declaration of strings: Declaring a string is as simple as declaring a one dimensional array. Below is the basic syntax for declaring a string.
```c
char str_name[size];
```
In the above syntax str_name is any name given to the string variable and size is used define the length of the string, i.e the number of characters strings will store. Please keep in mind that there is an extra terminating character which is the Null character (‘\0’) used to indicate termination of string which differs strings from normal character arrays.\
Initializing a String: A string can be initialized in different ways. We will explain this with the help of an example. Below is an example to declare a string with name as str and initialize it with “GeeksforGeeks”.
```c
char str[] = "GeeksforGeeks";
char str[50] = "GeeksforGeeks";
char str[] = {'G','e','e','k','s','f','o','r','G','e','e','k','s','\0'};
char str[14] = {'G','e','e','k','s','f','o','r','G','e','e','k','s','\0'};
```
