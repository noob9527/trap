package cn.staynoob.trap.kotlin.basic.collection;

import java.util.Collection;

public class Fixture {
    public static <T> void addElement(Collection<T> collection, T element) {
        collection.add(element);
    }
}
