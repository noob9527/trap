@file:Suppress("UNUSED_VARIABLE")

package cn.staynoob.trap.kotlin.basic.operator

import cn.staynoob.trap.kotlin.basic.operator.Fixture.Operand
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("操作符")
class OperatorSpec {

    @Test
    @DisplayName("生成range")
    fun range() {
        val list: MutableList<Int> = ArrayList()

        list += 1..10
        assertThat(list).isEqualTo(mutableListOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10))
        list.clear()

        list += 1 until 10
        assertThat(list).isEqualTo(mutableListOf(1, 2, 3, 4, 5, 6, 7, 8, 9))
        list.clear()

        list += 10 downTo 1
        assertThat(list).isEqualTo(mutableListOf(10, 9, 8, 7, 6, 5, 4, 3, 2, 1))
        list.clear()

        list += 10 downTo 1 step 2
        assertThat(list).isEqualTo(mutableListOf(10, 8, 6, 4, 2))
    }

    @Test
    @DisplayName("in操作符")
    fun inOperator() {
        assertThat(10 in 1..10).isTrue()
        assertThat(11 !in 1..10).isTrue()
    }

    @Test
    @DisplayName("对于位运算，没有特殊字符来表示，而只可用中缀方式调用命名函数")
    internal fun test300() {
        assertThat(2 shl 1).isEqualTo(4)
        assertThat(2 shr 1).isEqualTo(1)
        assertThat((-1).inv()).isEqualTo(0)
    }

    /**
     * @see <a href="https://kotlinlang.org/docs/reference/operator-overloading.html">Operator Overloading</a>
     */
    @Nested
    @DisplayName("操作符重载")
    inner class OperatorOverloadingSpec {
        @Test
        @DisplayName("basic usage")
        internal fun test100() {
            data class Operand(val value: Int) {
                operator fun plus(other: Operand) = Operand(value + other.value)
            }

            operator fun Operand.minus(other: Operand) = Operand(value - other.value)
            assertThat(Operand(1) + Operand(1))
                    .isEqualTo(Operand(2))
            assertThat(Operand(1) - Operand(1))
                    .isEqualTo(Operand(0))
        }

        @Test
        @DisplayName("复合赋值运算符可以不修改变量引用")
        internal fun test200() {
            // += 调用 plus 方法
            data class Operand1(val value: Int) {
                operator fun plus(other: Operand1) = Operand1(value + other.value)
            }

            var operand1 = Operand1(0)
            operand1 += Operand1(1)
            assertThat(operand1).isEqualTo(Operand1(1))
            // += 调用 plusAssign 方法
            data class Operand2(var value: Int) {
                operator fun plusAssign(other: Operand2) {
                    value += other.value
                }
            }

            val operand2 = Operand2(0)
            operand2 += Operand2(1) // 没有修改引用
            assertThat(operand2).isEqualTo(Operand2(1))
            // 不要同时声明plus与plusAssign方法
            class Operand3(var value: Int) {
                operator fun plus(other: Operand3) = Operand3(value + other.value)
                operator fun plusAssign(other: Operand3) {
                    value += other.value
                }
            }

            var op1 = Operand3(1)
            val op2 = Operand3(1)
            // correct
            op2 += Operand3(1)
            // incorrect
//            op1 += Operand3(1)

        }

        @Test
        @DisplayName("Operator关键字会被继承")
        internal fun test300() {
            open class Parent(val value: Int) {
                open operator fun inc(): Parent = Parent(value + 1)
            }
            class Child(value: Int) : Parent(value) {
                // 该方法会作为 operator方法
                override fun inc(): Child {
                    return Child(value + 2)
                }
            }
            var child = Child(0)
            assertThat((++child).value).isEqualTo(2)
        }

        /**
         * 由于java没有operator关键字，因此只要方法签名匹配约定就可以直接调用
         */
        @Test
        @DisplayName("调用java代码中的重载运算符")
        internal fun test1000() {
            operator fun Operand.minus(other: Operand) = subtract(other)
            assertThat(Operand(1) + Operand(1))
                    .isEqualTo(2)
            assertThat(Operand(1) - Operand(1))
                    .isEqualTo(0)
        }
    }
}