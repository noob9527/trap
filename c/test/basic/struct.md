# struct

### 定义与声明
```c
// 定义结构
struct person {
    char firstname[100];
    char lastname[100];
    int age;
};

// 声明结构变量
struct person foo;

// 定义的同时声明变量
struct person {
    char firstname[100];
    char lastname[100];
    int age;
} foo;

// 匿名结构
struct {
    char firstname[100];
    char lastname[100];
    int age;
} foo;
```

### 初始化结构
```c
struct person foo {
    "stephen",
    "curry"
    30
};

// c99 命名初始化
struct person foo {
    .age=30,
    .lastname="curry",
    .firstname="stephen",
}
```

### 访问结构成员
```c
// 点运算符
struct person foo {
    "stephen",
    "curry"
    30
};

foo.firstname;  // "stephen"
foo.age;        // 30

// -> 运算符
struct person *ptr = &foo;
foo.firstname       // "stephen"
(*ptr) .firstnam    // "stephen"
ptr -> firstname    // "stephen"
```

