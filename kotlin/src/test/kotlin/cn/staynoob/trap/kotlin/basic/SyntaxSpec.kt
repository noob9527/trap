@file:Suppress("ConstantConditionIf", "CanBeVal", "UseWithIndex", "UNREACHABLE_CODE")

package cn.staynoob.trap.kotlin.basic

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Suppress("UNUSED_VARIABLE")
@DisplayName("语法")
class SyntaxSpec {

    @Test
    @DisplayName("在所有使用代码块并期望得到结果的地方，代码块最后一个表达式就是结果（该规则对函数不成立）")
    fun test50() {
        // 类似的有 when,try,catch,lambda代码块
        val foo = if (true) {
            "foo"
        } else {
            "bar"
        }
        assertThat(foo).isEqualTo("foo")
    }

    @Test
    @DisplayName("if是表达式，不是语句")
    fun test100() {
        assertThat(if (true) 1 else 2).isEqualTo(1)
    }

    @Test
    @DisplayName("try是表达式，不是语句")
    fun test200() {
        val number = try {
            Integer.parseInt("1")
        } catch (e: NumberFormatException) {
            null
        }
        assertThat(number).isEqualTo(1)
    }

    @Test
    @DisplayName("赋值是语句，而不是表达式")
    fun test300() {
        var a = 0
        var b = 0
//        error
//        a = b = 1
    }

    /**
     * @see cn.staynoob.trap.kotlin.lambda.LambdaSpec.FlowControlInLambda#test200
     */
    @Test
    @DisplayName("流程控制与标签")
    internal fun test400() {
        var count1 = 0
        var count2 = 0
        loop@ for (i in 1..5) {
            count1++
            continue@loop
            fail("won't invoke")
        }
        assertThat(count1).isEqualTo(5)
        loop@ for (i in 1..5) {
            count2++
            break@loop
            fail("won't invoke")
        }
        assertThat(count2).isEqualTo(1)
        loop@ for (i in 1..5) {
            return@loop
            fail("won't invoke")
        }
        fail("won't invoke")
    }

    @Nested
    @DisplayName("解构声明")
    inner class DestructingDeclaration {

        @Test
        @DisplayName("目前kotlin只支持解构声明，不支持解构赋值")
        fun test100() {
            // correct
            var (a, b) = 1 to 2
            // incorrect
//        (a, b) = 1 to 2
            assertThat(a).isEqualTo(1)
            assertThat(b).isEqualTo(2)
        }

        @Test
        @DisplayName("kotlin使用约定方法来实现解构")
        fun test200() {
            class Destructable(val foo: String = "foo", val bar: String = "bar") {
                operator fun component1() = foo
                operator fun component2() = bar
            }
            val (foo, bar) = Destructable()
            assertThat(foo).isEqualTo("foo")
            assertThat(bar).isEqualTo("bar")
        }

        @Test
        @DisplayName("kotlin自动为数据类的主构造函数中的属性生成ComponentN约定方法")
        internal fun test300() {
            data class Destructable(val foo: String = "foo", val bar: String = "bar")
            val (foo, bar) = Destructable()
            assertThat(foo).isEqualTo("foo")
            assertThat(bar).isEqualTo("bar")
        }

        @Test
        @DisplayName("使用下划线占位不需要的变量")
        internal fun test400() {
            class Destructable {
                operator fun component1() = 1
                operator fun component2() = 2
                operator fun component3() = 3
            }
            val (_,_,a3) = Destructable()
            assertThat(a3).isEqualTo(3)
        }
    }

    @Nested
    @DisplayName("when表达式")
    inner class WhenSpec {
        @Test
        @DisplayName("不需要break")
        fun test100() {
            val x = 1
            when (x) {
                1 -> return
                2 -> throw Exception("shouldn't execute me")
            }
        }

        @Test
        @DisplayName("使用逗号合并分支")
        fun test200() {
            fun fn(x: Int) = when (x) {
                1, 2 -> "foo"
                else -> "bar"
            }
            fn(1)
            assertThat(fn(1)).isEqualTo("foo")
            assertThat(fn(2)).isEqualTo("foo")
            assertThat(fn(3)).isEqualTo("bar")
        }

        @Test
        @DisplayName("分支条件可以是任意对象")
        fun test300() {
            data class Foo(val foo: String)

            fun fn(foo: Foo) = when (foo) {
                Foo("foo") -> "foo"
                else -> "bar"
            }
            assertThat(fn(Foo("foo"))).isEqualTo("foo")
            assertThat(fn(Foo("asd"))).isEqualTo("bar")
        }

        @Test
        @DisplayName("when表达式不仅限于检查值相等")
        fun test400() {
            fun fn(x: Any?) = when (x) {
                in 1..10 -> "number"
                is String -> "string"
                else -> "any"
            }
            assertThat(fn(10)).isEqualTo("number")
            assertThat(fn("")).isEqualTo("string")
            assertThat(fn(null)).isEqualTo("any")
        }

        @Test
        @DisplayName("不带参数的when, 分支条件可以是任意的布尔表达式")
        fun test500() {
            fun fn(bool: Boolean) = when {
                bool -> true
                else -> false
            }
            assertThat(fn(true)).isTrue()
            assertThat(fn(false)).isFalse()
        }
    }
}
