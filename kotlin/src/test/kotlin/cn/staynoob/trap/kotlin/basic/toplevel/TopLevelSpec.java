package cn.staynoob.trap.kotlin.basic.toplevel;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("顶层函数与顶层属性")
public class TopLevelSpec {
    @Test
    @DisplayName("顶层函数将被编译到kotlin文件名对应的类下")
    void test100() {
        // TopLevel.kt编译成了TopLevelKt类
        assertThat(TopLevelKt.topLevelFunction()).isNull();
    }

    @Test
    @DisplayName("使用@JvmName自定义编译后的类名")
    void test200() {
        // TopLevelWithJVMName.kt编译成了TopLevel类
        assertThat(TopLevel.topLevelWithJVMNameFunction()).isNull();
    }

    @Test
    @DisplayName("顶层属性")
    void test300() {
        assertThat(TopLevelKt.getTopLevelProperty()).isNull();
        TopLevelKt.setTopLevelProperty(0);
        assertThat(TopLevelKt.getTopLevelProperty()).isEqualTo(0);
    }

    @Test
    @DisplayName("顶层只读属性")
    void test400() {
        // correct
        TopLevelKt.getTopLevelPropertyWithoutSetter();
        // incorrect
//        TopLevelKt.setTopLevelPropertyWithoutSetter();
    }

    /**
     * 使用const关键字标记的常量会被编译成 static final
     * @throws NoSuchFieldException
     */
    @Test
    @DisplayName("静态常量")
    void test500() throws NoSuchFieldException {
        assertThat(TopLevelKt.TOP_LEVEL_CONSTANT).isEqualTo(0);
        int modifiers = TopLevelKt.class.getField("TOP_LEVEL_CONSTANT").getModifiers();
        assertThat(Modifier.isStatic(modifiers)).isTrue();
        assertThat(Modifier.isFinal(modifiers)).isTrue();
    }
}
