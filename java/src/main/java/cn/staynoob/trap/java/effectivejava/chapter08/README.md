## Chapter8: General Programming

### Minimize the scope of local variables
The most powerful technique for minimizing the scope of a local variable is to declare it where it is first used. Nearly every local variable declaration should contain an initializer. If you don't yet have enough information to initialize a variable sensibly, you should postpone the declaration until you do. One exception to this rule concerns try-catch statements.
A final technique to minimize the scope of local variables is to keep methods small and focused.

### Prefer for-each loops to traditional for loops
In summary, the for-each loop provides compelling advantages over the traditional for loop in clarity and bug prevention, with no performance penalty. You should use it wherever you can. Unfortunately, there are three common situations where you can't use a for-each loop:
1. Filtering
    If you need to traverse a list or array and replace some or all of the values of its elements, then you need the list iterator or array index in order to set the values of an element.
2. Transforming
    If you need to traverse a list or array and replace some or all of the values of its elements, then you need the list iterator or array index in order to set the values of an element.
3. Parallel iteration
    If you need to traverse multiple collections in parallel, then you need explicit control over the iterator or index variable, so that all iterators or index variables can be advanced in lockstep(as demonstrated unintentionally in the buggy card ann dice examples above).

If you find yourself in any of these situations, use an ordinary for loop, be wary of the traps mentioned in this item, and know that you're doing the best you can.

### Know and use the libraries
skipped

### Avoid float and double if exact answers are required
In summary, don't use float or double for any calculations that require an exact answer. Use BigDecimal if you want the system to keep track of the decimal point and you don't mind the inconvenience and cost of not using a primitive type. Using BigDecimal has the added advantage that it gives you full control over rounding, letting you select from eight rounding mods whenever an operation that entails rounding is performed. This comes in handy if you're performing business calculations with legally mandated rounding behavior. If performance is of the essence, you don't mind keeping track of the decimal point yourself, and the quantities aren't too big, use int or long. If the quantities don't exceed nine decimal digits, you can use int; if they don't exceed eighteen digits, you can use long. If the quantities might exceed eighteen digits, you must use BigDecimal.

### Prefer primitive types to boxed primitives
In summary, use primitives in preference to boxed primitives whenever you have the choice. Primitive types are simpler and faster. If you must use boxed primitives, be careful! Autoboxing reduces the verbosity, but not the danger, of using boxed primitives. When your program compares two boxed primitives with the == operator, it does an identity comparison, which is almost certainly not what you want. When your program does mixed-type computations involving boxed and unboxed primitives, it does unboxing, and when your program does unboxing, it can result in costly and unnecessary object creations.

### Avoid strings where other types are more appropriate
To summarize, avoid the natural tendency to represent objects as strings when better data types exist or can be written. Used inappropriately, strings are more cumbersome, less flexible, slower, and more error-prone than other types. Types for which strings are commonly misused include primitive types, enums, and aggregate types.

### Beware the performance of string concatenation
skipped

### Refer to objects by the interfaces
skipped

### Prefer interfaces to reflection
In summary, reflection is a powerful facility that is required for certain sophisticated system programming tasks, but it has many disadvantages. If you are writing a program that has to work with classes unknown at compile time, you should, if at all possible, use reflection only to instantiate objects, and access the objects using some interface or superclass that is known at compile time.

### Use native methods judiciously
In summary, think twice before using native methods. Rarely, if ever, use them for improved performance. If you must use native methods to access low-level resources or legacy libraries, use as little native code as possible and test it thoroughly. A single bug in the native code can corrupt your entire application.

### Optimize judiciously
Don't sacrifice sound architectural principles for performance. Strive to write good programs rather than fast ones. If a good program is not fast enough, its architecture will allow it to be optimized. Good programs embody the principle of information hiding: where possible, they localize design decisions within individual modules, so individual decisions can be changed without affecting the remainder of the system(item 13).
Luckily, it is generally the case that good API design is consistent with good performance. It is a very to warp an API to achieve good performance. The performance issue that caused you to warp the API may go away in a future release of the platform or other underlying software, bug the warped API and the support headaches that come with it will be with you for life.
To summarize, do not strive to write fast programs--strive to write good ones; speed will follow. Do think about performance issues while you're designing systems and especially while you're designing APIs, wire-level protocols, and persistent data formats. When you've finished building the system, measure its performance. If it's fast enough, you're done. If not, locate the source of the problems with the aid of a profiler, and go to work optimizing the relevant parts of the system. The first step is to examine your choice of algorithms: no amount of low-level optimization can make up for a poor choice of algorithm. Repeat this process as necessary, measuring the performance after every change, until you're satisfied.

### Adhere to generally accepted naming conventions
Package names should be hierarchical with the components separated by periods. Components should consist of lowercase alphabetic characters and, rarely, digits. The name of any package that will be used outside your organization should begin with your organization's Internet domain name with the top-level domain first, for example, edu.cmu, com.sun, gov.nsa. The standard libraries and optional packages, whose names begin with java and javax, are exceptions to this rule. Users must not create packages whose names begin with java or javax. Detailed rules for converting Internet domain names to package name prefixes can be found in The Java Language Specification.
The remainder of a package name should consist of one or more components describing the package. Components should be short, generally eight or fewer characters. Meaningful abbreviations are encouraged, for example, util rather than utilities. Acronyms are acceptable, for example, awt. Components should generally consist of a single word or abbreviation.
Method and field names follow the same typographical conventions as class and interface names, except that the first letter of a method or field name should be lowercase, for example, remove or ensureCapacity. If an acronym occurs as the first word of a method or field name, it should be lowercase.
Type parameter names usually consist of a single letter. Most commonly it is one of these five: T for an arbitrary type, E for the element type of a collection, K and V for the key and value types of a map, and X for an exception. A sequence of arbitrary types can be T, U, V or T1, T2, T3.
The form beginning with get is mandatory if the class containing the method is a Bean, and it's advisable if you're considering turning the class into a Bean at a later time. Also, there is strong precedent for this form if the class contains a method to set the same attribute. In this case, the two methods should be named getAttribute and setAttribute.
A few method names deserve special mention. Methods that convert the type of an object, returning an independent object of a different type, are often called toType, for example, toString, toArray. Methods that return a view (item 5) whose type differs from that of the receiving object are often called asType, for example, asList. Methods that return a primitive with the same value as the object on which they're invoked are often called typeValue, for example, intValue. Common names for static factories are valueOf, of, getInstance, newInstance, getType, and newType(Item1, page10).
To summarize, internalize the standard naming conventions are learn to use them as second nature. The typographical conventions are straightforward and largely unambiguous; the grammatical conventions are more complex and looser. To quote from The Java Language Specification, "These conventions should not be followed slavishly if long-held conventional usage dictates otherwise." Use common sense.


