package cn.staynoob.trap.java.basic;

import cn.staynoob.trap.java.basic.interfacespec.IWithMethodImplementation1;
import cn.staynoob.trap.java.basic.interfacespec.IWithMethodImplementation2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("接口")
public class InterfaceSpec {

    class Foo implements IWithMethodImplementation1, IWithMethodImplementation2{
        @Override
        public String foo() {
            return IWithMethodImplementation1.super.foo();
        }
    }

    /**
     * @see cn.staynoob.trap.kotlin.basic.InterfaceSpec#test200()
     * @throws Exception
     */
    @Test
    @DisplayName("如果一个类实现了多个带有相同方法实现的接口，则必须重写该方法")
    public void test100() throws Exception {
        Foo foo = new Foo();
        assertThat(foo.foo()).isEqualTo("foo1");
    }
}
