package cn.staynoob.trap.java.basic.interfacespec;

public interface IWithMethodImplementation2 {
    default String foo() {
        return "foo2";
    }
}
