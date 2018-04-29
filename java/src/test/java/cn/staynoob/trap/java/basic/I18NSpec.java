package cn.staynoob.trap.java.basic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ResourceBundle;

import static org.assertj.core.api.Assertions.assertThat;

public class I18NSpec {
    @Test
    @DisplayName("read bundle")
    void test100() {
        ResourceBundle bundle = ResourceBundle.getBundle("I18NSample");
        assertThat(bundle.getString("foo")).isEqualTo("foo");
        assertThat(bundle.getString("bar")).isEqualTo("bar");
        assertThat(bundle.getString("baz")).isEqualTo("baz");
    }

    @Test
    @DisplayName("files for storing properties are always ASCII files")
    void test200() {
        ResourceBundle bundle = ResourceBundle.getBundle("I18NSample");
        assertThat(bundle.getString("hello")).isNotEqualTo("你好");
    }
}
