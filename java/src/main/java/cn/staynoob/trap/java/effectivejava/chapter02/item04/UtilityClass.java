// Noninstantiable utility class
package cn.staynoob.trap.java.effectivejava.chapter02.item04;

public class UtilityClass {
	// Suppress default constructor for noninstantiability
	private UtilityClass() {
		throw new AssertionError();
	}
}
