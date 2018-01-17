## Chapter2: Creating and Destroying Objects

### Consider static factory methods instead of constructors
advantages:
1. they have names.
    ex: the constructor BigInteger(...) which returns a BigInteger that is probably prime, would have been better expressed as a static factory method named BigInteger.probablePrime(...).
2. they are not required to create a new object each time they're invoked.
    ex: Boolean.valueOf(boolean), which never creates new object
3. they can return an object of any subtype of their return type.
    ex: java.util.Collections, java.util.EnumSet

disadvantages:
1. classes without public or protected constructors cannot be subclassed.
2. they are not readily distinguishable from other static methods.
    you can reduce this disadvantage by drawing attention to static factories in class or interface comments, and by adhering to common naming conventions Here are some common names for static factory methods:
    - valueOf
        returns an instance that has, loosely speaking, the same value as its
    parameters. Such static factories are effectively type-conversion methods.
    - of
        A concise alternative to valueOf, popularized by EnumSet.
    - getInstance
        Returns an instance that is described by the parameters but cannot be said
    to have the same value. In the case of a singleton, getInstance takes
    no parameters and returns the sole instance.
    - newInstance
        Like getInstance, except the newInstance guarantees that each instance
    return is distinct from all others.
    - getType
        Like getInstance, but used when the factory method is in a different class.
    Type indicates the type of object returned by the factory method.
    - newType
        Like newInstance, but used when the factory method is in a different class.
    Type indicates the type of object returned by the factory method.

### Consider a builder when faced with many constructor parameters
1. Telescoping constructor pattern
    the telescoping constructor pattern works, but it is hard to write client code when there are many parameters, and harder still to read it.
2. JavaBean pattern
    - JavaBean may be in an inconsistent state partway through its construction.
    - JavaBeans pattern precludes the possibility of making a class immutable.
3. Builder pattern is a good choice when designing classes whose constructors or static factories would have more than a handful of parameters.

### Enforce the singleton property with a private constructor or an enum type
A single-element enum type is the best way to implement a singleton
```java
public enum Elvis {
    INSTANCE;
    public void leaveTheBuilding() {
        System.out.println("Whoa baby, I'm outta here!");
    }
}
```

### Enforce noninstantiability with a private constructor
```java
public class UtilityClass {
	// Suppress default constructor for noninstantiability
	private UtilityClass() {
		throw new AssertionError();
	}
}
```

### Avoid creating unnecessary objects
Prefer primitives to boxed primitives, and watch out for unintentional autoboxing.

### Eliminate obsolete object reference
Whenever a class manages its own memory, the programmer should be alert for memory leaks.

### Avoid finalizers
Finalizers are unpredictable, often dangerous, and generally unnecessary.
- There is no guarantee they'll be executed promptly. It can take arbitrarily long between the time that an object becomes unreachable and the time that its finalizer is executed.
- Providing a finalizer for a class can, under rare conditions, arbitrarily delay reclamation of its instances.
- Not only does the language specification provide no guarantee that finalizers will get executed promptly; it provides no guarantee that they'll get executed at all.
