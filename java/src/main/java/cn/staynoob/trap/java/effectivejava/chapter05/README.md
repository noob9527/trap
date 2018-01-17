## Chapter5: Generics

### Don't use raw types in new code
There are two minor exceptions to the rule that you should not use raw types in new code, both of which stem from the fact that generic type information is erased at runtime(item25). You must use row types in class literals. The specification does not permit the use of parameterized types (though it does permit array types and primitive types). In other words, List.class, String[].class, and int.class are all legal, but List<String>.class and List<?>.class are not.
The second exception to the rule concerns the instanceof operator. Because generic type information is erased at runtime, it is illegal to use the instanceof operator on parameterized types other than unbounded wildcard types. The use of unbounded wildcard types in place of raw types does not affect the behavior of the instanceof operator in any way. In this case, the angle brackets and question marks are just noise. This is the preferred way to use the instanceof operator with generic types:
```java
// Legitimate use of raw type - instanceof operator
if (o instanceof Set) {
    Set<?> m = (Set<?>) o;
}
```
Note that once you've determined that o is a Set, you must cast it to the wildcard type Set<?>, not the raw type Set. This is a checked cast, so it will not cause a compiler warning.

### Eliminate unchecked warnings
In summary, unchecked warnings are important. Don't ignore them. Every unchecked warning represents the potential for a ClassCastException at runtime. Do your best to eliminate these warnings. If you can't eliminate an unchecked warning and you can prove that the code that provoked it is typesafe, suppress the warning with an @SuppressWarnings("unchecked") annotation in the narrowest possible scope. Record the rationale for your decision to suppress the warning in a comment.

### Prefer lists to arrays
In summary, arrays and generics have very different type rules. Arrays are covariant and reified; generics are invariant and erased. As a consequence, arrays provide runtime type safety but not compile-time type safety and vice versa for generics. Generally speaking, arrays and generics don't mix well. If you find yourself mixing them and getting compile-time errors or warnings, your first impulse should be to place the arrays with lists.

### Favor generic types
skipped

### Favor generic methods
skipped

### Use bounded wildcards to increase API flexibility
For maximum flexibility, use wildcard types on input parameters that represent producers or consumers. If an input parameter is both a producer and a consumer, then wildcard types will do you no good: you need an exact type match, which is what you get without any wildcards.
Here is a mnemonic to help you remember which wildcard type to use:
> PECS stands for producer-extends, consumer-super.

In other words, if a parameterized type represents a T producer, use <? extends T>; if it represents a T consumer, use <? super T>. The PECS mnemonic captures the fundamental principal that guides the use of wildcard types.
Do not use wildcard types as return types. Rather than providing additional flexibility for your users, it would force them to use wildcard types in client code.
Properly used, wildcard types are nearly invisible to users of a class. They cause methods to accept the parameters they should accept and reject those they should reject. If the user of a class has to think about wildcard types, there is probably something wrong with the class's API.
If a type parameter appears only once in a method declaration, replace it with a wildcard.
In summary, using wildcard types in your APIs, while tricky, makes the APIs for more flexible. If you write a library that will be widely used, the proper use of wildcard types should be considered mandatory. Remember the basic rule: producer-extends, consumer-super(PECS). And remember that all comparables and comparators are consumers.

### Consider typesafe heterogeneous containers
In summary, the normal use of generics, exemplified by the collections APIs, restricts you to a fixed number of type parameters per container. You can get around this restriction by placing the type parameter on the key rather than the container. You can use Class objects as keys for such typesafe heterogeneous containers. A Class object used in this fashion is called a type token. You can also use a custom key type. For example, you could have a DatabaseRow type representing a database row (the container), and a generic type Column<T> as its key.
