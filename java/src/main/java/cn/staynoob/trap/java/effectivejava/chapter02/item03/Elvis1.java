package cn.staynoob.trap.java.effectivejava.chapter02.item03;

import java.io.Serializable;

public class Elvis1 implements Serializable {
    public static final Elvis1 INSTANCE = new Elvis1();

    private Elvis1() {
    }

    public void leaveTheBuilding() {
        System.out.println("Whoa baby, I'm outta here!");
    }

    private Object readResolve() {
        // Return the one true Elvis1 and let the garbage collector
        // take care of the Elvis1 impersonator.
        return INSTANCE;
    }
}
