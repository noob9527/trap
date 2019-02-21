package cn.staynoob.trap.java.basic.concurrent;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ConcurrentCollectionSpec {
    @Test
    @DisplayName("concurrently modify normal collection during iterating should throw ConcurrentModificationException")
    void test100() {
        Set<Integer> set = new HashSet<>();
        Iterator<Integer> iterator = set.iterator();
        set.add(1);
        assertThatThrownBy(iterator::next)
                .isInstanceOf(ConcurrentModificationException.class);
    }

    @Test
    @DisplayName("after a concurrent collection construct an iterator, the subsequent modification may not be reflected by that iterator")
    void test200() {
        Set<Integer> set = new ConcurrentSkipListSet<>();
        Iterator<Integer> iterator = set.iterator();
        set.add(1);
        assertThatThrownBy(iterator::next)
                .isInstanceOf(NoSuchElementException.class);
    }
}
