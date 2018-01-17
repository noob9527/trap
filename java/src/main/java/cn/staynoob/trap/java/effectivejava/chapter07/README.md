## Chapter7: Methods

### Check parameters for validity
For public methods, use the Javadoc @throws tag to document the exception that will be thrown if a restriction on parameter values is violated(Item 62). Typically the exception will be IllegalArgumentException, IndexOutOfBoundsException, or NullPointerException(Item 60). Once you've documented the restrictions on a method's parameters and you've documented the exceptions that will be thrown if these restrictions are violated, it is a simple matter to enforce the restrictions.
For an unexported method, you as the package author control the circumstances under which the method is called, so you can and should ensure that only valid parameter values are ever passed in. Therefor, nonpublic methods should generally check their parameters using assertions
In essence, these assertions are claims that the asserted condition will be true, regardless of how the enclosing package is used by its clients. Unlike normal validity checks, assertions throw AssertionError if they fail. And unlike normal validity checks, they have no effect and essentially no cost unless you enable them, which you do by passing the -ea(or -enableassertions) flag to the java interpreter.
To summarize, each time you write a method or constructor, you should think about what restrictions exist on its parameters. You should document these restrictions and enforce them with explicit checks at the beginning of the method body. It is important to get into the habit of doing this. The modest work that it entails will be paid back with interest the first time a validity check fails.

### Make defensive copies when needed
In summary. if a class has mutable components that it gets from or returns to its clients, the class must defensively copy these components. If the cost of the copy would be prohibitive and the class trusts its clients not to modify the components inappropriately, then the defensive copy may be replaced by documentation outlining the client's responsibility not to modify the affected components.

### Design method signatures carefully
- Choose method names carefully
- Don't go overboard in providing convenience methods
- Avoid long parameter lists
    There are three techniques for shortening overly long parameter lists:
    1. break the method up into multiple methods, each of which requires only a subset of the parameters.
    2. create helper classes to hold groups of parameters.
    3. adapt the Builder pattern(item 2) from object construction to method invocation.
- For parameter types, favor interfaces over classes
- Prefer tow-element enum types to boolean parameters

### Use overloading judiciously
selection among overloaded methods is static, while selection among overridden methods is dynamic. The correct version of an overridden method is chosen at runtime, based on the runtime type of the object on which the method is invoked. As a reminder, a method is overridden when a subclass contains a method declaration with the same signature as a method declaration in an ancestor. If an instance method is overridden in a subclass and this method is invoked on an instance of the subclass, the subclass's overriding method execute, regardless of the compile-time type of the subclass instance. 
Exactly what constitutes a confusing use of overloading is open to some debate. A safe, conservative policy is never to export two overloadings with the same number of parameters. If a method uses varargs, a conservative policy is not to overload it at all, except as described in Item 42. If you adhere to these restrictions, programmers will never be in doubt as to which overloading applies to any set of actual parameters. The restrictions are not terribly onerous because you can always give methods different names instead of overloading them.
Exporting multiple overloadings with the same number of parameters is unlikely to confuse programmers if it is always clear which overloading will apply to any given set of actual parameters. This is the case when at least one corresponding formal parameter in each pair of overloadings has a "radically different" type in the two overloadings. Two types are radically different if it is clearly impossible to cast an instance of either type to the other. Under these circumstances, which overloading applies to a given set of actual parameters is fully determined by the runtime types of the parameters and cannot be affected by their compile-time types, so the major source of confusion goes away.
Array types and classes other than Object are radically different, Also array types and interfaces other than Serializable and Cloneable are radically different. Two distinct classes are said to be unrelated if neither class is a descendant of the other. For Example, String and Throwable are unrelated. It is impossible for any object to be an instance of two unrelated classes, so unrelated classes are radically different.
To summarize, just because you can overload methods doesn't mean you should. You should generally refrain from overloading methods with multiple signatures that have the same number of parameters. In some cases, especially where constructors are involved, it may be impossible to follow this advice. In that case, you should at least avoid situations where the same set of parameters can be passed to different overloadings by the addition of casts. If such a situation cannot be avoided, for example, because you are retrofitting an existing class to implement a new interface, you should ensure that all overloadings behave identically when passed the same parameters. If you fail to do this, programmers will be hard pressed to make effective use of the overloaded method or constructor, and they won't understand why it doesn't work.

### Use varargs judiciously
In summary, varargs methods are a convenient way to define methods that require a variable number of arguments, but they should not be overused. They can produce confusing results if used inappropriately.

### Return empty arrays or collections, not nulls
skipped

### Write doc comments for all exposed API elements
The doc comment for a method should describe succinctly the contract between the method and its client. With the exception of methods in classes designed for inheritance(Item 17), the contract should say what the method does rather than how it does its job. The doc comment should enumerate all of the methods's preconditions, which are the things that have to be true in order for a client to invoke it, and its postconditions, which are the things that will be true after the invocation has completed successfully. Typically, preconditions are described implicitly by the @throws tags for unchecked exceptions; each unchecked exception corresponds to a precondition violation. Also, preconditions can be specified along with the affected parameters in their @param tags.
In addition to preconditions and postconditions, methods should document any side effects. A side effect is an observable change in the state of the system that is not obviously required in order to achieve the postcondition. For example, if a method starts a background thread, the documentation should make note of it. Finally, documentation comments should describe the thread safety of a class or method as discussed in Item 70.
To describe a method's contract fully, the doc comment should have an @param tag for every parameter, an @return tag unless the method has a void return type, and an @throws tag for every exception thrown by the method, whether checked or unchecked(item 62). By convention, the text following an @param tag or @return tag should be a noun phrase describing the value represented by the parameter or return value. The text following an @throws tag should consist of the word "if," followed by a clause describing the conditions under which the exception is thrown. Occasionally, arithmetic expressions are used in place of noun phrases. By convention, the phrases or clause following an @param, @return, or @throws tag is not terminated by a period.
The first "sentence" of each doc comment (as defined below) becomes the summary description of the element to which the comment pertains. For example, the summary description in the doc comment on page 204 is "Returns the element at the specified position in this list." The summary description must stand on its own to describe the functionality of the element it summarizes. To avoid confusion, no two members or constructors in a class or interface should have the same summary description. Pay particular attention to overloadings, for which it is often natural to use the same first sentence in a prose description (but unacceptable in doc comments).
It is somewhat misleading to say that the summary description is the first sentence in a doc comment. Convention dictates that it should seldom be a complete sentence. For methods and constructors, the summary description should be a full verb phrase(including any object) describing the action performed by the method. For example,
- ArrayList(int initialCapacity) -- Constructs an empty list with ehe specified initial capacity.
- Collection.size() -- Returns the number of elements in this collection.
For classes, interfaces, and fields, the summary description should be a noun phrase describing the thing represented by an instance of the class or interface or by the field itself. For example,
- TimerTask -- A task that can be scheduled for one-time or repeated execution by a Timer.
- Math.PI -- The double value that is closer than any other to pi, the ratio of the circumference of a circle to its diameter.



