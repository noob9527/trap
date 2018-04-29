package cn.staynoob.trap.java.basic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

public class IoSpec {
    @Test
    @DisplayName("get user current working directory")
    void test100() {
        String res = System.getProperty("user.dir");
        assertThat(res).endsWith("trap/java");
    }

    @Test
    @DisplayName("classes from java.io will interpret relative path as starting from cwd")
    void test200() {
        // all the classes in java.io interpret
        // relative path names as starting
        // from the user's working directory.
        String path1 = new File("").getAbsolutePath();
        String path2 = System.getProperty("user.dir");
        assertThat(path1).isEqualTo(path2);
    }
}
