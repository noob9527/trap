/**
 * Consider static factory methods instead of constructors
 * advantages:
 * 1. they have names.
 * ex: the constructor BigInteger(...) which returns a BigInteger that
 * is probably prime, would have been better expressed as a static factory method
 * named BigInteger.probablePrime(...).
 * 2. they are not required to create a new
 * object each time they're invoked.
 * ex: Boolean.valueOf(boolean), which never creates new object
 * 3. they can return an object of any subtype of their return type.
 * ex: java.util.Collections, java.util.EnumSet
 * disadvantages:
 * 1. The main disadvantage of providing only static factory methods is that classes without
 * public or protected constructors cannot be subclassed.
 * 2. A second disadvantage of static factory methods is that
 * they are not readily distinguishable from other static methods.
 */
package cn.staynoob.trap.java.effectivejava.chapter02.item01;