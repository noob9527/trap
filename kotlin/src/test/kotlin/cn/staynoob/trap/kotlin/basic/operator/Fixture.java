package cn.staynoob.trap.kotlin.basic.operator;

public class Fixture {
    static class Operand {
        int value;

        public Operand(int value) {
            this.value = value;
        }

        public int plus(Operand other) {
            return this.value + other.value;
        }

        public int subtract(Operand other) {
            return this.value - other.value;
        }
    }
}
