## Chapter10: Serialization
Encoding an object as a byte stream is known as serializing the object; the revers process is known as deserializing it.

### Implement Serializable judiciously
Allowing a class's instances to be serialized can be as simple as adding the words "implements Serializable" to its declaration. Because this is so easy to do, there is a common misconception that serialization requires little effort on the part of the programmer. The truth is far more complex. While the immediate cost to make a class serializable can be negligible, the long-term costs are often substantial.\
A major cost of implementing Serializable is that it decreases the flexibility to change a class's implementation once it has been release. When a class implements Serializable, its byte-stream encoding(or serialized form) becomes part of its exprted API. Once you distribute a class widely, you are generally required to support the serialized from forever, just as you are required to support all other parts of the exported API. If you do not make the effort to design a custom serialized form, but merely accept the default, the serialized form will forever be tied to the class's original internal representation. In other words, if you accept the default serialized form, the class's private and package-private instance fields become part of its exported API, and the practice of minimizing access to fields(Item 13) loses its effectiveness as a tool for information hiding.\


