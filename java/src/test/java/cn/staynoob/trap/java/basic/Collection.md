# Collection

### `Hashtable`

> Java Collections Framework.  Unlike the new collection
implementations, {@code Hashtable} is synchronized.  If a
thread-safe implementation is not needed, it is recommended to use
{@link HashMap} in place of {@code Hashtable}.  If a thread-safe
highly-concurrent implementation is desired, then it is recommended
to use {@link java.util.concurrent.ConcurrentHashMap} in place of
{@code Hashtable}.

Hashtable is synchronized, but in practice, you may still need some additional synchronization. Consider a very common idiom "check then put". HashTable's internal synchronization mechanism does nothing in this case, you still need a way to make the two operations atomic, as the following code exhibits:
```java
// method1
synchronized (collection) {
    if (!collection.containsKey(key))
        collection.put(key, value);
}

// method2
collection.putIfAbsent(key, value);
```
Also, note that null keys and null values are not allowed in a `Hashtable`.

##### `Hashtable` vs `HashMap`
Hashtable and HashMap provide very similar functionality. Both of them provide:
- Fail-fast iteration(Fail-fast iteration means that if a Hashtable is modified after its Iterator is created, then the `ConcurrentModificationException` will be thrown)
- Unpredictable iteration order

But there are some differences too:
- HashMap doesn't provide any Enumeration, while Hashtable provides not fail-fast Enumeration
- Hashtable doesn't allow null keys and null values, while HashMap do allow one null key and any number of null values
- Hashtable's methods are synchronized while HashMaps's methods are not
