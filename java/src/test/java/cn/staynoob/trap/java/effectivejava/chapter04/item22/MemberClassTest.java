package cn.staynoob.trap.java.effectivejava.chapter04.item22;


import cn.staynoob.trap.java.effectivejava.chapter04.item22.fixture.Example;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@SuppressWarnings("unused")
public class MemberClassTest {

    @Test
    @DisplayName("create a nonstatic member class instance")
    public void test100() throws Exception {
        Example example = new Example();
        // incorrect
//        new Example.Foo();
        // correct
        Example.Foo foo = example.new Foo();
    }
}
