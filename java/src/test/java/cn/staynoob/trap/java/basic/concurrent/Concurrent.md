# Concurrent

### Mutable vs Immutable
Note the differences between "instance immutable" and "variable immutable"
```java
public class Demo {
    // the instance of Boolean is immutable whereas flag variable is mutable
    public Boolean flag = true;

    // now variable is immutable whereas the instance of Date class is mutable
    public final Date now = new Date();
}
```