## Chapter6: Enums and Annotations

### Use enums instead of int constants
In summary, the advantages of enum types over int constants are compelling. Enums are for more readable, safer, and more powerful. Many enums require no explicit constructors or members, but many others benefit from associating data with each constant and providing methods whose behavior is affected by this data. For fewer enums benefit from associating multiple behaviors with a single method. In this relatively rare case, prefer constant-specific methods to enums that switch on their own values. Consider the strategy enum pattern if multiple enum constants share common behaviors.

### Use instance fields instead of ordinals
The Enum specification has this to say about ordinal: "Most programmers will have no use for this method. It is designed for use by general-purpose enum-based data structures such as EnumSet and EnumMap." Unless you are writing such a data structure, you are bast off avoiding the ordinal method entirely.

### Use EnumSet instead of bit fields
In summary, just because an enumerated type will be used in sets, there is no reason to represent it with bit fields. The EnumSet class combines the conciseness and performance of bit fields with all the many advantages of enum types described in Item 30. The one real disadvantage of EnumSet is that it is not, as of release 1.6, possible to create an immutable EnumSet, but this will likely be remedied in an upcoming release, In the meantime, you can wrap an EnumSet with Collections.unmodifiableSet, but conciseness and performance will suffer.

### Use EnumMap instead of ordinal indexing
In summary, it is rarely appropriate to use ordinals to index arrays: use EnumMap instead. If the relationship that you are representing is multidimensional, use EnumMap<..., EnumMap<...>>. This is a special case of the general principle that application programmers should rarely, if ever, use Enum.ordinal(item 31).

### Emulate extensible enums with interfaces
In summary, while you cannot write an extensible enum type, you can emulate it by writing an interface to go with a basic enum type that implements the interface. This allows clients to write their own enums that implement the interface. These enums can then be used wherever the basic enum type can be used, assuming APIs are written in terms of the interface.

### Prefer annotations to naming patterns
skipped

### Consistently use the Override annotation
skipped

### Use marker interfaces to define types
If you find yourself writing a marker annotation type whose target is ElementType.TYPE, take the time to figure out whether it really should be an annotation type, or whether a marker interface would be more appropriate.
In a sense, this item is the inverse of Item 19, which says, "if you don't want to define a type, don't use an interface." To a first approximation, this item says, "if you do want to define a type, do use an interface."


