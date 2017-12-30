/**
 * Avoid finalizers
 * Finalizers are unpredictable, often dangerous, and generally unnecessary.
 * - There is no guarantee they'll be executed promptly. It can take arbitrarily long
 * between the time that an object becomes unreachable and the time that its
 * finalizer is executed.
 * - Providing a finalizer for a class can, under rare conditions, arbitrarily
 * delay reclamation of its instances.
 * - Not only does the language specification provide no guarantee that finalizers
 * will get executed promptly; it provides no guarantee that they'll get executed
 * at all.
 */
package cn.staynoob.trap.java.effectivejava.chapter02.item07;