package cn.staynoob.trap.java.basic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Object")
public class ObjectSpec {

    static class CloneSample100 {
        @Override
        public Object clone() throws CloneNotSupportedException {
            return super.clone();
        }
    }

    static class CloneSample200 implements Cloneable {
        public String foo = "foo";

        @Override
        public Object clone() throws CloneNotSupportedException {
            return super.clone();
        }
    }

    static class BrokenInheritanceChain {
        @Override
        @SuppressWarnings("MethodDoesntCallSuperMethod")
        public Object clone() throws CloneNotSupportedException {
            return new BrokenInheritanceChain();
        }
    }

    static class CloneSample300
            extends BrokenInheritanceChain
            implements Cloneable {
        @Override
        public Object clone() throws CloneNotSupportedException {
            return super.clone();
        }
    }

    @Nested
    @DisplayName("Object.clone")
    class Clone {
        @Test
        @DisplayName("如果类重写clone方法但是未实现cloneable接口, 调用clone方法将抛出异常")
        void test100() {
            CloneSample100 sample = new CloneSample100();
            assertThatThrownBy(sample::clone)
                    .isInstanceOf(CloneNotSupportedException.class);
        }

        @Test
        @DisplayName("如果实现了cloneable接口, Object.clone会返回子类的实例")
        void test200() throws CloneNotSupportedException {
            Object sample = new CloneSample200().clone();
            assertThat(sample.getClass())
                    .isEqualTo(CloneSample200.class);
            assertThat(((CloneSample200) sample).foo)
                    .isEqualTo("foo");
        }

        @Test
        @DisplayName("如果一个类是可继承的，那么它的clone方法应该返回由Object.clone创建的对象，否则子类clone方法不会返回同类型的对象")
        void test300() throws CloneNotSupportedException {
            Object sample = new CloneSample300().clone();
            assertThat(sample.getClass())
                    .isNotEqualTo(CloneSample200.class);
        }
    }

    @Nested
    @DisplayName("Comparable.compareTo")
    class CompareTo {
        @Test
        @DisplayName("一些类的compareTo结果可能与equals结果不一致")
        void test100() {
            BigDecimal b1 = new BigDecimal("1.0");
            BigDecimal b2 = new BigDecimal("1.00");
            assertThat(b1.equals(b2)).isFalse();
            assertThat(b1.compareTo(b2)).isEqualTo(0);
        }

        @Test
        @DisplayName("一些基础类采用equals判等，一些类采用compareTo判等")
        void test200() {
            BigDecimal b1 = new BigDecimal("1.0");
            BigDecimal b2 = new BigDecimal("1.00");
            Set set1 = new HashSet<>(Arrays.asList(b1, b2));
            Set set2 = new TreeSet(Arrays.asList(b1, b2));
            assertThat(set1).hasSize(2);
            assertThat(set2).hasSize(1);
        }
    }
}
