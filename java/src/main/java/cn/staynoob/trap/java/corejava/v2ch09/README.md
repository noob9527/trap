# Security
## 1 Class Loaders
### The Class Loading Process
The virtual machine loads only those class files that are needed for the execution of a program. For example, suppose program execution starts with MyProgram.class. Here are the steps that the virtual machine carries out:
1. The virtual machine has a mechanism for loading class files--for example, by reading the files from disk or by requesting them from the Web; it uses this mechanism to load the contents of the MyProgram class file.
2. If the MyProgram class has fields or superclasses of another class type, their class files are loaded as well.(The process of loading all the classes that a given class depends on is called resolving the class)
3. The virtual machine then executes the main method in MyProgram(which is static, so no instance of a class needs to by created)
4. If the main method or a method that main calls requires additional classes, these are loaded next.
The class loading mechanism doesn't just use a single class loader, however. Every Java program has at least three class loaders:
- The bootstrap class loader
- The extension class loader
- The system class loader(sometimes also called the application class loader)
The bootstrap class loader loads the system classes(typically, from the JAR file rt.jar). It is an integral part of the virtual machine and is usually implemented in C. There is no ClassLoader object corresponding to the bootstrap class loader. For example
```java
String.class.getClassLoader();
```
returns null.\
The extension class loader loads "standard extensions" from the *jre/lib/ext* directory. You can drop JAR files into that directory, and the extension class loader will find the classes in them, even without any class path. (Some people recommend this mechanism to avoid the "class path hell," but see the next cautionary note.)\
The system class loader loads the application classes. It locates classes in the directories and JAR/ZIP files on the class path, as set by the CLASSPATH environment variable or the -classpath command-line option.\

### The Class Loader Hierarchy
Class loaders have a parent/child relationship. Every class loader except for the bootstrap one has a parent class loader. A class loader is supposed to give its parent a chance to load any given class and to only load it if the parent has failed. For example, when the system class loader is asked to load a system class(say, java.util.ArrayList), it first asks the extension class loader. That class loader first asks the bootstrap class loader. The bootstrap class loader finds and loads the class in rt.jar, so neither of the other class loaders searches any further.\
Most of the time, you  don't have to worry about the class loader hierarchy. Generally, classes are loaded because they are required by other classes, and that process is transparent to you.\
Occasionally, however, you need to intervene and specify a class loader. Consider this example:
- Your application code contains a helper method that calls Class.forName(classNameString)
- That method is called from a plugin class.
- The classNameString specifies a class that is contained in the plugin JAR
The author of the plugin has reasons to expect that the class should be loaded. However, the helper method's class was loaded by the system class loader, and that is the class loader used by Class.forName. The classes in the plugin JAR are not visible. This phenomenon is called classloader inversion.\
To overcome this problem, the helper method needs to use the correct class loader. It can require the class loader as a parameter. Alternatively, it can require that the correct class loader is set as the context class loader of the current thread. This strategy is used by many frameworks(such as the JAXP and JNDI frameworks that we discussed in Chapters 3 and 5). \
Once the context class loader is set correctly. The helper method can then use it to load the class:
```java
Thread t = Thread.currentThread();
ClassLoader loader = t.getContextClassLoader();
Class cl = loader.loadClass(className);
```
Each thread has a reference to a class loader, called the context class loader. The main thread's context class loader is the system class loader. When a new thread is created, its context class loader is set to the creating thread's context class loader. Thus, if you don't do anything, all threads will have their context class loaders set to the system class loader.\
> Tip: If you write a method that loads a class by name, it is a good idea to offer the caller the choice between passing an explicit class loader and using the context class loader. Don't simply use the class loader of the method's class.

### Using Class Loaders as Namespaces
It might surprise you, however, that you can have two classes in the same virtual machine that have the same class and package name. A class is determined by its full name and the class loader. This technique is useful for loading code from multiple sources.

### Writing Your Own Class Loader
To write your own class loader, simply extend the ClassLoader class and override the method `findClass(String className)` The loadClass method of the ClassLoader superclass takes care of the delegation to the parent and calls findClass only if the class hasn't already been loaded and if the parent class loader was unable to load the class.\
Your implementation of this method must do the following:
1. Load the bytecodes for the class from the local file system or some other source.
2. Call the defineClass method of the ClassLoader superclass to present the bytecodes to the virtual machine.

### Bytecode Verification
When a class loader presents the bytecodes of a newly loaded Java platform class to the virtual machine, these bytecodes are first inspected by a verifier. The verifier checks that the instructions cannot perform actions that are obviously damaging. All classes except for system classes are verified.\
Here are some of the checks that the verifier carries out:
- Variables are initialized before they are used
- Method calls match the types of object references.
- Rules for accessing private data and methods are not violated.
- Local variables accesses fall within the runtime stack.
- The runtime stack does not overflow
You might wonder, however, why a special verifier checks all these features. After all, the compiler would never allow you to generate a class file in which an uninitialized variable is used or in which a private data field is accessed from another class. Indeed, a class file generated by a compiler for the Java programming language always passes verification. However, the bytecode format used in the class files is well documented, and it is an easy matter for someone with experience in assembly programming and a hex editor to manually produce a class file containing valid but unsafe instructions for the Java virtual machine. The verifier is always guarding against maliciously altered class files, not just checking the class files produced by a compiler.

## 2 Security Managers and Permissions
Once a class has been loaded into the virtual machine and checked by the verifier, the second security mechanism of the Java platform springs into action: the security manager.
### Permission Checking
The security manager controls whether a specific operation is permitted Operations checked by the security manager include the following:
- Creating a new class loader
- Exiting the virtual machine
- Accessing a field of another class by using reflection
- Accessing a file
- Opening a socket connection
- Starting a print job
- Accessing the system clipboard
- Accessing the AWT event queue
There are many other checks throughout the Java library.\
The default behavior when running Java applications is to install no security manager, so all these operations are permitted. The applet viewer, on the other hand, enforces a security policy that is quite restrictive.\
Clearly, the integrity of the security policy depends on careful coding. The providers of system services in the standard library must always consult the security manager before attempting any sensitive operation.
### Java Platform Security
JDK1.0 had a very simple security model: Local classes had full permissions, and remote classes were confined to the sandbox. JDK1.1 implemented a slight modification: Remote code that was signed by a trusted entity was granted the same permission as local classes. However, both versions of the JDK used an all-or-nothing approach. Programs either had full access or they had to play in the sandbox.\
Starting with Java SE 1.2, the Java platform has a much more flexible mechanism. A security policy maps code resource to permission sets.\
A permission is any property that is checked by a security manager. The java platform supports a number of permission classes, each encapsulating the details of a particular permission.\
Each class has a protection domain--an object that encapsulates both the code source and the collection of permissions of the class. When the SecurityManager needs to check a permission, it looks at the classes of all methods currently on the call stack. It then gets the protection domains of all classes and asks each protection domain if its permission collection allows the operation currently being checked. If all domains agree, the check passes. Otherwise, a SecurityException is thrown. By checking the entire call stack, the security mechanism ensures that one class can never ask another class to carry out a sensitive operation on its behalf.
### Security Policy files
The policy manager reads policy files that contain instructions for mapping code sources to permission. You can install policy files in standard locations. By default, there are two locations:
- The file java.policy in the Java platform's home directory
- The file .java.policy in the user's home directory
During testing, we don't like to constantly modify the standard policy files. Therefore, we prefer to explicitly name the policy file required for each application. Place the permission into a separate file--say, MyApp.policy. To apply the policy, you have two choices. You can set a system property inside your applications' main method:
```java
System.setProperty("java.security.policy", "MyApp.policy");
```
Alternatively, you can start the virtual machine as:
```bash
java -Djava.security.policy=MyApp.policy MyApp
```
In these examples, the MyApp.policy file is added to the other policies in effect. If you add a second equal sign, such as:
```bash
java -Djava.security.policy==MyApp.policy MyApp
```
then your application will use only the specified policy file, and the standard policy files will be ignored.\
As you saw previously, Java applications by default do not install a security manager. Therefore, you won't see the effect of policy files until you install one. You can, of course, add a line:
```java
System.setSecuritymanager(new SecurityManager());
```
into your main method. In the remainder of this section, we'll show you in detail how to describe permissions in the policy file, We'll describe the entire policy file format, except for code certificates which we cover later in this chapter.\
A policy file contains a sequence of grant entries. Each entry has the following form:
```
grant codesource
{
    permission1;
    permission2;
    ...
}
```
The code source contains a code base(which can be omitted if the entry applies to code from all sources) and the names of trusted principals and certificate signers (which can be omitted if signatures are not required for this entry). The permissions have the following structure:
```
permission className targetName, actionList;
```
The className is the fully qualified class name of the permission class. The targetName is a permission-specific value--for example, a file or directory name for the file permission , or a host and port for a socket permission. The actionList is also permission-specific. It is a list of actions, such as read or connect, separated by commas. Some permission classes don't need target names and action lists.
### Custom Permissions
skipped
### Implementation of a Permission Class
skipped

## 3 User Authentication
### The JAAS(Java Authentication and Authorization Service) Framework
skipped
### JAAS Login Modules
skipped

## 4 Digital Signatures
### Message Digests
A message digest has two essential properties:
- If one bit or several bits of the data are changed, the message digest also changes.
- A forger who is in possession of a given message cannot construct a fake message that has the same message digest as the original
Here is how you obtain an object that can compute SHA fingerprints:
```java
MessageDigert alg = MessageDigest.getInstance("SHA-1");
```
After you have obtained a MessageDigest object, feed it all the bytes in the message by repeatedly calling the update method. For example, the following code passes all bytes in a file to the alg object just created to do the fingerprinting:
```java
InputStream in = ...;
int ch;
while((ch = in.read()) != -1)
    alg.update((byte) ch)
```
Alternatively, if you have the bytes in an array, you can update the entire array at once:
```java
byte[] bytes = ...;
alg.update(bytes);
```
When you are done, call the digest method. This method pads the input as required by the fingerprinting algorithm, does the computation, and returns the digest as an array of bytes.

### Message Signing
In the last section, you saw how to compute a message digest--a fingerprint for the original message. If the message is altered, the fingerprint of the altered message will not match the fingerprint of the original. If the message and its fingerprint are delivered separately, the recipient can check whether the message has been tampered with. Message has been tampered with. However, if both the message and the fingerprint were intercepted, it is an easy matter to modify the message and then recompute the fingerprint. After all, the message digest algorithms are publicly known, and they don't require secret keys. In that case, the recipient of the forged message and the recomputed fingerprint would never know that the message has been altered. Digital signatures solve this problem.
To help you understand how digital signatures work, we'll explain a few concepts from the field called public key cryptography. Public key cryptography is based on the notion of a public key and private key. The idea is that you tell everyone in the world your public key. However, only you hold the private key. The keys are matched by mathematical relationships.\
It is believed to be practically impossible to compute one key from the other. It might seem difficult to believe that you can't compute the private key from the public key, but nobody has ever found an algorithm to do this for the encryption algorithms in common use today. Of course it is possible that someone could come up with algorithms for computing keys that are much more clever than brute force. For example, the RSA algorithm depends on the difficulty of factoring large numbers. For the last 20 yeas, many of the best mathematicians have tried to come up with good factoring algorithms, but so far with no success. For this reason, most cryptographers believe that keys with a "modulus" of 2000 bits or more are currently completely safe from any attack.\
Suppose Alice wants to send Bob a message, and Bob wants to know this message came from Alice and not an impostor. Alice writes the message and signs the message digest with her private key. Bob gets a copy of her public key. Bob then applies the public key to verify the signature. If the verification passes, Bob can be assured of two facts:
- The original message has not been altered.
- The message was signed by Alice, the holder of the private key that matched the public key that Bob used for verification.
You can see why the security of private keys is so important. If someone steals Alice's private key, or if a government can require her to turn it over, then she is in trouble. The thief or a government agent can now impersonate her by sending messages.

### Verifying a Signature
The keytool program manages keystores, databases of certificates and private/public key pairs. Each entry in the keystore has an alias. Here is how Alice creates a keystore, alice.cert, and generates a key pair with alias alice:
```bash
keytool -genkeypair -keystore alice.certs -alias alice
```
When creating or opening a keystore, you are prompted for a keystore password. For this example, just use secret. If you were to use the keytoo-generated keystore for any serious purpose, you would need to choose a good password and safeguard this file.\
Suppose Alice wants to give her public key to Bob. She needs to export a certificate file:
```bash
keytool -exportcert -keystore alice.certs -alias alice -file alice.cer
```
Now Alice can send the certificate to Bob. When Bob receives the certificate, he can print it:
```bash
keytool -printcert -file alice.cer
```
If Bob wants to check that he got the right certificate, he can call Alice and verify the certificate fingerprint over the phone. Once Bob trusts the certificate, he can import it into his keystore.
```bash
keytool -importcert -keystore bob.certs -alias alice -file alice.cer
```

> Caution: Never import into a keystore a certificate that you don't fully trust. Once a certificate is added to the keystore, any program that used the keystore assumes that the certificate can be used to verify signatures.

### The Authentication Problem
Now suppose you get a message from a stranger who claims to represent a famous software company, urging you to run a program attached to the message. The stranger even sends you a copy of his public key so you can verify that he authored the message. You check that the signature is valid. This proves that the message was signed with the matching private key and has not been corrupted.\
Be careful: You still have no idea who wrote the message. Anyone could have generated a pair of public and private keys, signed the message with the private key, and sent the signed message and the public key to you. The problem of determine the identity of the sender is called the authentication problem.\
The usual way to solve the authentication problem is simple. Suppose the stranger and you have a common acquaintance you both trust. Suppose the stranger meets your acquaintance in person and hands over a disk with the public key. Your acquaintance later meets you, assures you that he met the stranger and that the stranger indeed works for the famous software company, and then give you the disk. That way, your acquaintance vouches for the authenticity of the stranger.

### Certificate Signing
We've seen how Alice used a self-signed certificate to distribute a public key to Bob. However, Bob needed to ensure that the certificate was valid by verifying the fingerprint with Alice.\
Suppose Alice wants to send her colleague Cindy a signed message, but Cindy doesn't want to bother with verifying lots of signature fingerprints. Now suppose there is an entity that Cindy trusts to verify signatures. In this example, Cindy trusts the Information Resources Department at ACME Software.\
That department operates a certificate authority(CA). Everyone at ACME has the CA's public key in their keystore, installed by a system administrator who carefully checked the key fingerprint. The CA signs the keys of ACME employees. When they install each other's keys, the keystore will trust them implicitly because they are signed by a trusted key. For Alice to send messages to Cindy and to everyone else at ACME Software, she needs to bring her certificate to the Information Resource Department and have it signed. Alice then gives the signed certificate to Cindy and to anyone else in ACME Software. Remember, this file contains Alice's public key and an assertion by ACME Software that this key really belongs to Alice. Now Cindy imports the signed certificate into her keystore.
```java
keytool -importcert -keystore cindy.certs -alias alice -file alice_signedby_acmeroot.cer
```
The keystore verifies that the key was signed by a trusted root key that is already present in the keystore. Cindy is not asked to verify the certificate fingerprint. Once Cindy has added the root certificate and the certificates of the people who regularly send her documents, she never has to worry about the keystore again.

### Certificate Requests
skipped
### Code Signing
skipped

## 5 Encryption
### Symmetric Ciphers
The Java cryptographic extensions contain a class Cipher that is the superclass of all encryption algorithms. To get a cipher object, call the getInstance method:
```java
Cipher cipher = Cipher.getInstance(algorithmName);
Cipher cipher = Cipher.getInstance(algorithmName, providerName);
```
The algorithmName is a string such as "AES" or "DES/CBC/PKCS5Padding".\
The Data Encription Standard(DES) is a venerable bock cipher with a key length of 56 bits. Nowadays, the DES algorithm is considered obsolete because it can be cracked with brute force. A far better alternative is its successor, the Advanced Encryption Standard(AES).\
Consider the DES cipher. It has a block size of eight bytes. Suppose the last bock of the input data has fewer than eight bytes. Of course, we can fill the remaining bytes with -, to obtain one final block of eight bytes, and encrypt it. But when the blocks are decrypted, the result will have several trailing - bytes appended to it, and therefore will be slightly different from the original input file. To avoid this problem, we need a padding scheme. A commonly used padding scheme is the one described in the Public Key Cryptography Standard(PKCS) #5 by RSA Security, Inc.\
In this scheme, the last block is not padded with a pad value of zero, but with a pad value that equals the number of pad bytes. After decryption, the vary last byte of the plain text is a count of the padding characters to discard.

### Key Generation
To encrypt, you need to generate a key. Each cipher has a different format for keys, and you need to make sure that the key generation is random. Follow these steps:
1. Get a `KeyGenerator` for your algorithm.
2. Initialize the generator with a source for randomness. If the bock length of the cipher is variable, also specify the desired block length.
3. Call the `generateKey` method.
For example, here is how you generate an AES key:
```java
KeyGenerator keygen = KeyGenerator.getInstance("AES");
SecureRandom random = new SecureRandom();
keygen.init(random);
Key key = keygen.generateKey();
```
When generating keys, make sure you use truly random numbers. For example, the regular random number generator in the Random class, seeded by the current date and time, is not random enough. Suppose the computer clock is accurate to 1/10 of a second. Then there are at most 864,000 seeds per day. If an attacker knows the day a key was issued(which can often be deduced from a message date or certificate expiration date), it is an easy matter to generate all possible seeds for that day.\
The SecureRandom class generates random numbers that are far more secure than those produced by the Random class. You still need to provide a seed to start the number sequence a at a random spot. The best method for doing this is to obtain random input from a hardware device such as a white-noise generator. Another reasonable source for random input is to ask the user to type away aimlessly on the keyboard, with each keystroke contributing only one or two bits to the random seed. Once you gather such random bits in an array of bytes, pass it to the setSeed method:
```java
SecureRandom secrand = new SecureRandom();
byte[] b = new byte[20];
// fill with truly random bits
secrand.setSeed(b);
```
If you don't seed the random number generator, it will compute its own 20-byte seed by launching threads, putting them to sleep, and measuring the exact time when they are awakened.\
Note: This algorithm is not known to be safe. In the past, algorithms that relied on the timing of some components of the computer, such as hard disk access time, were shown not to be completely random.
### Cipher Streams
skipped
### Public Key Ciphers
The AES cipher that you have seen in the preceding section is a symmetric cipher. The same key is used for both encryption and decryption. The Achilles heel of symmetric ciphers is key distribution. If Alice sends Bob an encrypted method, Bob needs the same key that Alice used. If Alice changes the key, she needs to send Bob both the message and, through a secure channel, the new key. But perhaps she has no secure channel to Bob--which is why she encrypts her messages to him in the first place.\
Public key cryptography solves that problem. In a public key cipher, Bob has a key pair consisting of a public key and a matching private key. Bob can publish the public key anywhere, but he must closely guard the private key. Alice simply used the public key to encrypt her messages to Bob.\
Actually, it's not quite that simple. All known public key algorithms are much slower than symmetric key algorithms such as DES or AES. It would not be practical to use a public key algorithm to encrypt large amounts of information. However, that problem can easily be overcome by combining a public key cipher with a fast symmetric cipher, like this:
1. Alice generates a random symmetric encryption key. She used it to encrypt her plaintext.
2. Alice encrypts the symmetric key with Bob's public key.
3. Alice sends Bob both the encrypted symmetric key and the encrypted plaintext.
4. Bob used his private key to decrypt the symmetric key.
5. Bob used the decrypted symmetric key to decrypt the message.
Nobody but Bob can decrypt the symmetric key because only Bob has the private key for decryption. Thus, the expensive public key encryption is only applied to a small amount of key data.\
To use the RSA algorithm, you need a public/private key pair. Use a KeyPairGenerator like this;
```java
KeyPairGenerator pairgen = KeyPairGenerator.getInstance("RSA");
SecureRandom random = new SecureRandom();
pairgen.initialize(KEYSIZE, random)
KeyPair keyPaire = pairgen.generateKeyPair();
Key publicKEY = keyPair.getPublic();
Key privateKey = keyPair.getPrivate();
```
