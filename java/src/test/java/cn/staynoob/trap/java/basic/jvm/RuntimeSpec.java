package cn.staynoob.trap.java.basic.jvm;

import com.google.common.base.Charsets;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.assertj.core.api.Assertions.assertThat;

public class RuntimeSpec {
    @Test
    @DisplayName("get the jvm memory information")
    void test100() {
        Runtime runtime = Runtime.getRuntime();

        long mb = 1024 * 1024;

        long freeMemory = runtime.freeMemory() / mb;
        long maxMemory = runtime.maxMemory() / mb;
        long totalMemory = runtime.totalMemory() / mb;

        System.out.println(freeMemory);
        System.out.println(maxMemory);
        System.out.println(totalMemory);
    }

    @Test
    @DisplayName("run gc manually")
    void test200() {
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
    }

    @Test
    @DisplayName("execute external process")
    void test300() throws IOException {
        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec("echo hello world");
        try (InputStream inputStream = process.getInputStream()) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, Charsets.UTF_8));
            String line = bufferedReader.readLine();
            assertThat(line).isEqualTo("hello world");
        }
    }
}
