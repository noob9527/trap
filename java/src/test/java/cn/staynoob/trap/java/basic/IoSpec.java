package cn.staynoob.trap.java.basic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class IoSpec {
    @Test
    @DisplayName("get user current working directory")
    void test100() {
        String res = System.getProperty("user.dir");
        assertThat(res).endsWith("trap/java");
    }
}
