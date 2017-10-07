package cn.staynoob.trap.kotlin.basic.extension;

import cn.staynoob.trap.kotlin.basic.extension.anotherpackage.DemoExtensionsKt;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("扩展方法与扩展属性")
public class ExtensionJavaSpec {
    @Test
    @DisplayName("在java中调用扩展函数/属性")
    void test100() {
        Fixture fixture = new Fixture();
        Assertions.assertThat(DemoExtensionsKt.extendFunction(fixture)).isEqualTo("ownFunction");
        Assertions.assertThat(DemoExtensionsKt.getExtendProperty(fixture)).isEqualTo("ownProperty");
    }
}
