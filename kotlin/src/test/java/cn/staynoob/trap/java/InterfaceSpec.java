package cn.staynoob.trap.java;

import cn.staynoob.trap.java.interfacespec.IWithMethodImplementation1;
import cn.staynoob.trap.java.interfacespec.IWithMethodImplementation2;
import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

public class InterfaceSpec {

    class Foo implements IWithMethodImplementation1, IWithMethodImplementation2{
        @Override
        public String foo() {
            return IWithMethodImplementation1.super.foo();
        }
    }

    @Test
    public void WithMultiMethodImplementation() throws Exception {
        Foo foo = new Foo();
        assertThat(foo.foo()).isEqualTo("foo1");
    }
}
