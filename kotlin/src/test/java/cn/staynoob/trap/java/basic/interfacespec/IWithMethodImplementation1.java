package cn.staynoob.trap.java.basic.interfacespec;

public interface IWithMethodImplementation1 {
    default String foo() {
        return "foo1";
    }
}
