package cn.staynoob.trap.java.basic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("注解")
public class AnnotationSpec {

    private @interface Test100Annotation1 {}
    @Retention(RetentionPolicy.RUNTIME)
    private @interface Test100Annotation2 {}

    @Test100Annotation1
    @Test100Annotation2
    private static class Test100Class {}

    @Test
    @DisplayName("java注解默认的RetentionPolicy是class，因此运行时无法通过反射获取")
    void test100() {
        Annotation[] arr = Test100Class.class.getAnnotations();
        assertThat(arr.length)
                .isEqualTo(1);
        assertThat(arr)
                .allMatch(e -> e instanceof Test100Annotation2);
    }
}
