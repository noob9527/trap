package cn.staynoob.trap.java;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class StringSpec {
    @Test
    public void splitByDot() throws Exception {
        assertThat("1.2-3".split("-").length).isEqualTo(2);
        // 由于split方法接受正则表达式作为参数，而.在正则表达式中匹配任意字符
        assertThat("1.2-3".split(".").length).isEqualTo(0);
    }
}
