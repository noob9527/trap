# Library

### Cmake
#### `add_library`
STATIC, SHARED, or MODULE may be given to specify the type of library to be created.\
STATIC libraries are archives of object files for use when linking other targets.\
SHARED libraries are linked dynamically and loaded at runtime.\
MODULE libraries are plugins that are not linked into other targets but may be loaded dynamically at runtime using dlopen-like functionality.

#### `target_link_libraries`

### Reference
- [difference-between-static-and-shared-libraries](https://stackoverflow.com/questions/2649334/difference-between-static-and-shared-libraries)
- [static-vs-dynamic-libraries](https://www.geeksforgeeks.org/static-vs-dynamic-libraries/)
- [static-vs-dynamic-libraries-set-2](https://www.geeksforgeeks.org/working-with-shared-libraries-set-2/)
