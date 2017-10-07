package cn.staynoob.trap.kotlin.basic.object;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

@DisplayName("对象")
public class ObjectJavaSpec {

    @Test
    @DisplayName("在java中访问对象表达式")
    void test100() {
        assertThat(ObjectExpression.INSTANCE.fn()).isEqualTo("fn");
        assertThat(ObjectExpression.INSTANCE.getFoo()).isEqualTo("foo");
    }

    @Test
    @DisplayName("在java中访问伴生对象")
    void test200() {
        assertThat(ClassWithCompanion.Companion.fn()).isEqualTo("fn");
        assertThat(ClassWithCompanion.Companion.getFoo()).isEqualTo("foo");
    }

    @Test
    @DisplayName("在java中访问具名伴生对象")
    void test300() {
        assertThat(ClassWithNamingCompanion.WithName.fn()).isEqualTo("fn");
        assertThat(ClassWithNamingCompanion.WithName.getFoo()).isEqualTo("foo");
    }
}
