package cn.staynoob.trap.kotlin.basic.lambda;

import java.util.function.Predicate;

public class Fixture {
    public static boolean fun1(Integer e, Predicate<Integer> predicate) {
        return predicate.test(e);
    }
}
