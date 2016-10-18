package cn.staynoob.trap.java;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class CollectionSpec {

    private static class Foo {
        private int id;
        private int hashCode;
        private int hashCodeCallCount = 0;
        private int equalsCallCount = 0;

        private Foo(int id, int hashCode) {
            this.id = id;
            this.hashCode = hashCode;
        }

        @Override
        public int hashCode() {
            hashCodeCallCount++;
            return hashCode;
        }

        @Override
        public boolean equals(Object obj) {
            equalsCallCount++;
            if (!(obj instanceof Foo)) return false;
            Foo other = (Foo) obj;
            return id == other.id;
        }
    }

    @Test
    public void hashSetContainsShouldCheckHashCodeAndEquals() throws Exception {
        Set<Foo> set = new HashSet<>();
        Foo foo = new Foo(1, 1);
        set.add(foo);
        assertThat(set.contains(new Foo(1, 0))).isFalse();
        assertThat(foo.hashCodeCallCount).isEqualTo(1);
        assertThat(foo.equalsCallCount).isEqualTo(0);
        assertThat(set.contains(new Foo(0, 1))).isFalse();
        assertThat(set.contains(new Foo(1, 1))).isTrue();
    }
}

