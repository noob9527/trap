# keyword

### restrict
> In the C programming language, as of the C99 standard, restrict is a keyword that can be used in pointer declarations. The restrict keyword is a declaration of intent given by the programmer to the compiler. It says that for the lifetime of the pointer, only the pointer itself or a value directly derived from it (such as pointer + 1) will be used to access the object to which it points. This limits the effects of pointer aliasing, aiding optimizations. If the declaration of intent is not followed and the object is accessed by an independent pointer, this will result in undefined behavior.
reference:

- restrict keyword is mainly used in pointer declarations as a type qualifier for pointers.
- It doesn’t add any new functionality. It is only a way for programmer to inform about an optimizations that compiler can make.
- When we use restrict with a pointer ptr, it tells the compiler that ptr is the only way to access the object pointed by it and compiler doesn’t need to add any additional checks.
- If a programmer uses restrict keyword and violate the above condition, result is undefined behavior.
- restrict is not supported by C++. It is a C only keyword.

```c
// C program to use restrict keyword.
#include <stdio.h>

// Note that the purpose of restrict is to
// show only syntax. It doesn't change anything
// in output (or logic). It is just a way for
// programmer to tell compiler about an
// optimization
void use(int* a, int* b, int* restrict c)
{
    *a += *c;

    // Since c is restrict, compiler will
    // not reload value at address c in
    // its assembly code. Therefore generated
    // assembly code is optimized
    *b += *c;
}

int main(void)
{
    int a = 50, b = 60, c = 70;
    use(&a, &b, &c);
    printf("%d %d %d", a, b, c);
    return 0;
}
```

reference:
- [restrict](https://en.wikipedia.org/wiki/Restrict)
- [restrict-keyword-c](https://www.geeksforgeeks.org/restrict-keyword-c/)