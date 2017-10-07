package cn.staynoob.trap.java.basic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("类")
public class ClassSpec {

    private class Inner {
        private String foo = "foo";
        protected String bar = "bar";
        String baz = "baz";
        public String qux = "qux";
    }

    /**
     * @see cn.staynoob.trap.kotlin.basic.ClassSpec#test200()
     */
    @Test
    @DisplayName("java中外部类可以访问内部类的所有成员")
    void test100() {
        Inner inner = new Inner();
        assertThat(inner.foo).isNotEmpty();
        assertThat(inner.bar).isNotEmpty();
        assertThat(inner.baz).isNotEmpty();
        assertThat(inner.qux).isNotEmpty();
    }
}
