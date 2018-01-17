## Chapter3: Methods Common to All Objects

### Obey the general contract when overriding equals

#### General contract of equals method
When you override the equals method, you must adhere to its general contract.
Here is the contract, copied from the specification for Object(JavaSE6).
The equals method implements an equivalence relation. It is:
- Reflexive(自反性)
    For any non-null reference value x, x.equals(x) must return true.
- Symmetric(对称性)
    For any non-null reference value x and y, e.equals(y) must return true if and only if y.equals(x) returns true.
- Transitive(传递性)
    For any non-null reference value x,y,z, if x.equals(y) returns true and y.equals(z) returns true, then x.equals(z) must return true.
- Consistent(一致性)
    For any non-null reference value x and y, multiple invocations of x.equals(y) consistently return true or consistently return false, provided no information used in equals comparisons on the objects is modified.
- Non-nullity
    For any non-null reference value x, x.equals(null) must return false.

Let's examine the five requirements in turn:
1. Reflexivity(skip)
2. Symmetry(skip)
3. Transitivity
    There is no way to extend an instantiable class and a value component while preserving the equals contract. unless you are willing to forgo the benefits of object-oriented abstraction(using a getClass test in place of the instanceof test in the equals method, witch violates Liskov substitution).
    There are some classes in the Java platform libraries that do extend an instantiable class and add a value component. For example, `java.sql.Timestamp`extends `java.util.Date` and adds a nanoseconds field. The equals implementation for Timestamp does violate symmetry and can cause erratic behavior if Timestamp and Date objects are used in the same collection or are otherwise intermixed.
    Note that you can add a value component to a subclass of an abstract class without violating the equals contract. since it is impossible to create a superclass instance directly.
4. Consistency
    Whether or not a class is immutable, do not write an equals method that depends on unreliable resources. It's extremely difficult to satisfy the consistency requirement if you violate this prohibition. For example, `java.net.URL`'s equals method relies on comparison of the IP addresses of the hosts associated with the URLs. Translating a host name to an IP address can require network access. and it isn't guaranteed to yield the same results over time. This can cause the URL equals methods to violate the equals contract and has caused problems in practice. (Unfortunately, this behavior cannot be changed dur to compatibility requirements.) With very few exceptions, equals methods should perform deterministic computations on memory-resident objects.
5. Non-nullity
    You don't need a separate null check if you already have a instanceof check. ** because the `instanceof` operator is specified to return false if its first operand is null. **

Putting it all together, here's a recipe for a high-quality equals method:
1. using the == operator to check if the argument is a reference to this object.
    If so, return true. This is just a performance optimization, but one that is worth doing if the comparision is potentially expensive.
2. Use the instanceof operator to check if the argument has the correct type.
    If not, return false. Typically, the correct type is the class in which the method occurs. Occasionally, it is some interface implemented by this case. Use an interface if the class implements an interface that refines the equals contract to permit comparisons across classes that implement the interface. Collection interfaces such as Set,LIst, Map, and Map.Entry have this property.
3. Cast the argument to the correct type.
    Because this cast was preceded by an instanceof test, it is guaranteed to succeed.
4. For each "significant" field in the class, check if that field of the argument matches the corresponding field of this object.
    - for primitive fields whose type is not float or double, use the == operator for comparisons; 
    - for `float` fields, use Float.compare method. The special treatment of float in made necessary by the existence of Float.NaN, -0.0f. and so is `double`.
    - for object reference fields, invoke the equals method recursively, some object reference fields may legitimately contain null. To avoid the posibility of a NullPointerException, use this idiom to compare such fields:
        ```java
        field == null ? o.field == null : field.equals(o.field)
        ```
        This alternative may be faster if field and o.field are often identical:
        ```java
        field == o.field || (field != null && field.equals(o.field))
        ```
    The performance of the equals method may be affected by the order in which fields are compared. For best performance, you should first compare fields that are most likely to differ, less expensive to compare, or, ideally, both.
5. When you are finished writing your equals method, ask yourself three questions: Is it symmetric? Is it transitive? Is it consistent?
    Of course your equals method also has to satisfy the other two properties (reflexivity and "non-nullity"), but these two usually take care of themselves.

### Always override hashCode when you override equals
You must override hashCode in every class that override equals. Failure to do so will result in a violation of the general contract for `Object.hashCode`. Here is the contract,  copied from the Object specification(JavaSE6):
- Whenever it is invoked on the same object more than once during an execution of an application, the hashCode method must consistently return the same integer, provided no information used in equals comparisons on the object is modified. This integer need not remain consistent from one execution of ana application to another execution of the same application.
- If two objects are equal according to the equals(Object) method, the calling the hashCode method on each of the two objects must produce the same integer result.
- It is not required that if two objects are unequal according to the equals(Object) method, then calling the hashCode method on each of the two objects must produce distinct integer result. However, the programmer should be aware that producing distinct integer results for unequal objects may improve the performance of hash tables.
The key provision that is violated when you fail to override hashCode is the second one: equal objects must have equal hash code.
A good hash function tends to produce unequal hash code for unequal objects. THis is exactly what is meant by the third provision of the hashCode contract. Ideally, a hash function should distribute any reasonable collection of unequal instances uniformly across all possible hash values. Achieving this ideal can be difficult. Luckily it's not too difficult to achieve a fair approximation. Here is a simple recipe:
1. Store some constant nonzero value, say, 17, in an int variable called result.
2. For each significant field f in your object(each field taken into account by the equals method, that is), do the following:
    1. Compute an int hash code c for the field:
        1. If the field is a boolean, compute `(f ? 1 : 0)`.
        2. If the field is a byte, char, short or int, compute `(int)f`.
        3. If the field is a long, compute `(int)(f ^ (f >>> 32))`
        4. If the field is a float, compute `Float.floatToIntBits(f)`
        5. If the field is a double, compute `Double.doubleToLongBits(f)`, and then hash the resulting long.
        6. If the field is an object reference and this class's equals method compares the field by recursively invoking equals, recursively invoke hashCode on the filed. If a more complex comparison is required, compute a "canonical representation" for this field and invoke hashCode on the canonical representation. If the value of the field is null, return 0(or some other constant, but 0 is traditional).
        7. If the field is an array. treat it as if each element were a separate field. That is, compute a hash code for each significant element by applying these rule recursively, and combine these values per step. If every element in an array field is significant, you can use one of the Arrays.hashCode methods added in release 1.5.
    2. Combine the hash code c computed in previous step into result as follows: `result = 31 * result + c`
3. Return result.
4. When you are finished writing the hashCode method, ask yourself whether equal instances have equal hash codes.

You may exclude redundant fields from the hash code computation. In other words, you may ignore any field whose value can be computed from fields included in the computation. You must exclude any fields that are not used in equals comparisons, or you risk violating the second provision of the hashCode contract.
A non zero initial value is used in step 1 so the hash value will be affected by initial fields whose hash value, as computed in step 2.1, is zero. If zero were used as the initial value in step 1, the overall hash value would be unaffected by any such initial fields, which could increase collisions. The value 17 is arbitrary.
The multiplication in step 2.2 makes the result depend on the order of the fields, yielding a much better hash function if the class has multiple similar fields. For example, if the multiplication were omitted from a String hash function, all anagrams would have identical hash codes. The value 31 was chosen because it is an odd prime. If it were even and the multiplication overflowed, information would be lost, as multiplication by 2 is equivalent to shifting. The advantage of using a prime is less clear, but it is traditional. A nice property of 31 is that the multiplication can be replaced by a shift and a subtraction for better performance: `31 * i == (i << 5) - i`. Modern VMs do this sort of optimization automatically.
```java
@Override 
public int hashCode() {
    int result = 17;
    result = 31 * result + areaCode;
    result = 31 * result + prefix;
    result = 31 * result + lineNumber;
    return result;
}
```
Many classed in the Java platform libraries, such as String, Integer, and Date, include in their specifications the exact value returnd by their hashCode method as a function of the instance value. This is generally not a good idea, as it severely limits your ability to improve the hash function in future released. If you leave the details of a hash function unspecified and a flaw is found or a better hash function discovered, you can change the hash function in a subsequent release, confident that no clients depend on the exact values returned by the hash function.

### Always override toString
Providing a good toString implementation makes your class much more pleasant to use.

### Override clone judiciously
If implementing the cloneable interface is to have any effect on a class, the class and all of its superclasses must obey a fairly complex, unenforceable, and thinly documented protocol. The resulting mechanism is extralinguistic: it creates an object without calling a constructor.
The general contract for the clone method is weak. Here it is, copied from the specification for java.lang.Object(JavaSE6):
- `x.clone() != x` will be true
- `x.clone().getClass() == x.getClass()`will be true, but these are not absolute requirements.
- `x.clone().equals(x)`will be true, this is not an absolute requirement.
There are a number of problems with this contract. The provision that "no constructors are called" is too strong. A well-behaved clone method can call constructors to create objects internal to the clone under construction. If the class is final, clone can even return an object created by a constructor.
The provision that `x.clone().getClass()` should generally be identical to `x.getClass()`, however, is too weak. In practice, programmers assume that if they extend a class and invoke `super.clone` from the subclass, the returned object will be an instance of the subclass. The only way a superclass can provide this functionality is to return an object obtained by calling `super.clone()`. If a clone method returns an object created by a constructor, it will have the wrong class. Therefor, if you override the clone method in a nonfinal class, you should return an object obtained by invoking `super.clone`. If all of a class's superclasses obey this rule, then invoking `super.clone` will eventually invoke Object's clone method, creating an instance of the right class. This mechanism is vaguely similar to automatic constructor chaining, except that it isn't enforced.
Like a constructor, a clone method should not invoke any nonfinal methods on the clone under construction(item 17). If clone invokes an overridden method, this method will execute before the subclass in which it is defined has had a chance to fix its state in the clone, quite possibly leading to corruption in the clone and the original.
Object's clone method is declared to throw CloneNotSupportedException, but overriding clone methods can omit this declaration. Public clone methods should omit it because methods that don't throw checked exceptions are easier to use(Item59). If a class that is designed for inheritance(Item17) overrides clone, the overriding method should mimic the behavior of Object.clone: it should be declared protected, it should be declared to throw CloneNotSupportedException, and the class should not implement Cloneable. This gives subclasses the freedom to implement Cloneable or not, just as if they extended Object directly.
Is all this complexity really necessary? Rarely. If you extend a class that implements Cloneable, you have little choice but to implement a well-behaved clone method. Otherwise, you are better off providing an alternative means of object copying, or simply not providing the capability. For example, it doesn't make sense for immutable classes to support object copying, because copies would be virtually indistinguishable from the original.

### Consider implementing Comparable
The general contract of the compareTo method is similar to that of equals:
> In the following description, the notation sgn(expression) designates the mathematical signum function, which is defined to return -1, 0, or 1, according to whether the value of expression is negative, zero, or positive.
- The implementor must ensure sgn(x.compareTo(y)) == -sgn(y.compareTo(x)) for all x and y.(This implies that x.compareTo(y) must throw an exception if and only if y.compareTo(x) throws an exception.)
- The implementor must also ensure that the relation is transitive: (x.compareTo(y) > 0 && y.compareTo(z) > 0) implies x.compareTo(z) > 0.
- Finally, the implementor must ensure that x.compareTo(y) == 0 implies that sgn(x.compareTo(z)) == sgn(y.compareTo(z)), for all z.
- It is strongly recommended, bug not strictly required, that (x.compareTo(y) == 0) == (x.equals(y)). Generally speaking, any class that implements the Comparable interface and violates this condition should clearly indicate this fact. The recommended language is "Not: This class has a natural ordering tht is inconsistent with equals." 

One consequence of these three provisions is that the equality test imposed by a compareTo method must obey the same restrictions imposed by the equals contract: reflexivity, symmetry, and transitivity. Therefore the same caveat applies: there is no way to extend an instantiable class with a new value component while preserving the compareTo contract, unless you are willing to forgo the benefits of object-oriented abstraction(item8). The same workaround applies, too. If you want to add a value component to a class that implements Comparable, don't extend it; write an unrelated class containing an instance of the first class. Then provide a "view" method that returns this instance. This frees you to implement whatever compareTo method you like on the second class, while allowing its client to view an instance of the second class as an instance of the first class when needed.
The final paragraph of the compareTo contract, which is a strong suggestion rather that a true provision, simply states that the equality test imposed by the compareTo method should generally return the same results as the equals method. If this provision is obeyed, the ordering imposed by the compareTo method is said to be consistent with equals. If it's violated, the ordering is said to be inconsistent with equals. A class whose compareTo method imposes an order that is inconsistent with equals will still work, but sorted collections containing elements of the class may not obey the general contract of the appropriate collection interfaces (Collection, Set, or Map). This is because the general contract for these interfaces are defined in terms of the equals method, but sorted collections use the quality test imposed by compareTo in place of equals. It is not a catastrophe if this happens, but it's something to be aware of.
For example, consider the BigDecimal class, whose compareTo method is inconsistent with equals. If you create a HashSet instance and add new BigDecimal("1.0") and new BigDecimal("1.00"), the set will contain two elements because the two BigDecimal instances added to the set qre unequals ehn compared using the equals method. If, however, you perform the same procedure using a TreeSet instead of a HashSet, the set will contain only one element because the two BigDecimal instances are equal when compared using the compareTo method.(See the BigDecimal documentation for details.)
Writing a compareTo method is similar to writing an equals method:
- Compare object reference fields by invoking the compareTo method recursively.
- Compare integral primitive fields using the relational operators < and >.
- For floating-point fields, use Double.compare or Float.compare in place of the relational operators, which do not obey the general contract for compareTo when applied to floating point values.
- For array fields, apply these guidelines to each element.
- If a class has multiple significant fields, the order in which you compare them is critical. You must start with the most significant field and work your way down. If a comparison results in anything other than zero(which represents equality), you're done; just return the result. If the most significant fields are equal, go on to compare the next-most-significant fields, and so on.

