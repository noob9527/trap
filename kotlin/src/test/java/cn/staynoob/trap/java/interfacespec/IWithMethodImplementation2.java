package cn.staynoob.trap.java.interfacespec;

public interface IWithMethodImplementation2 {
    default String foo() {
        return "foo2";
    }
}
