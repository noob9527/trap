# main function
### definition
The function called at program startup is named main. The implementation declares no prototype for this function. It shall be defined with a return type of int and with no parameters, or with two parameters (referred to here as argc and argv, though any names may be used, as they are local to the function in which they are declared)
```c
int main(void) { /* ... */ }
// or
int main(int argc, char *argv[]) { /* ... */ }
```
### arguments
The arguments argc and argv of main is used as a way to send arguments to a program, the possibly most familiar way is to use the good ol' terminal where an user could type cat file. Here the word cat is a program that takes a file and outputs it to standard output (stdout).\
The program receives the number of arguments in argc and the vector of arguments in argv, in the above the argument count would be two (The program name counts as the first argument) and the argument vector would contain [cat,file,null]. While the last element being a null-pointer.
```c
#include <stdio.h>

int main(int argc, char *argv[]) {
    printf("argc:%d\n", argc);
    for (int i = 0; i < 5; i++) {
        printf("argv[%d]:%s\n", i, argv[i]);
    }
}
```

### return value
The return value indicates the result of the program. Usually 0 indicates success while other values indicates different kinds of failure.

### reference
- [main-function-in-c](https://stackoverflow.com/questions/18446686/main-function-in-c)
- [regarding-mainint-argc-char-argv](https://stackoverflow.com/questions/3898021/regarding-mainint-argc-char-argv)