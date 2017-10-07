package cn.staynoob.trap.java.basic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("字符串")
public class StringSpec {

    /**
     * @see cn.staynoob.trap.kotlin.basic.BasicTypeSpec.StringSpec#test200()
     * @throws Exception
     */
    @Test
    @DisplayName("java split方法接受正则表达式作为参数，可能引发问题")
    public void test100() throws Exception {
        assertThat("1.2-3".split("-").length).isEqualTo(2);
        // 由于split方法接受正则表达式作为参数，而.在正则表达式中匹配任意字符
        assertThat("1.2-3".split(".").length).isEqualTo(0);
    }
}
