@file:Suppress("UNREACHABLE_CODE", "UNUSED_VARIABLE")

package cn.staynoob.trap.kotlin.basic.lambda

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.times
import java.util.function.Predicate

@DisplayName("lambda")
class LambdaSpec {
    @Test
    @DisplayName("匿名函数")
    fun test100() {
        val sum = { x: Int, y: Int -> x + y }
        val square1: (Int) -> Int = { x -> x * x }
        val square2: (Int) -> Int = { it * it }
        assertThat(sum(1, 2)).isEqualTo(3)
        assertThat(square1(2)).isEqualTo(4)
        assertThat(square2(2)).isEqualTo(4)
    }

    @Test
    @DisplayName("访问并修改外部作用域")
    fun test200() {
        var count = 0
        run { count++ }
        assertThat(count).isEqualTo(1)
    }

    @Test
    @DisplayName("lambda中的this引用指向包围它的类")
    fun test300() {
        assertThat(run { this }).isEqualTo(this)
    }

    @Nested
    @DisplayName("带接收者的lambda表达式")
    inner class LambdaWithReceiver {

        /**
         * @see with
         * @see apply
         */
        @Test
        @DisplayName("绑定lambda表达式中的this引用")
        fun test100() {
            class Foo(val foo: String = "foo") {
                fun bar() = "bar"
            }

            fun Foo.withReceiver1(block: Foo.() -> String): String = this.block()
            fun withReceiver2(foo: Foo, block: Foo.() -> String): String = foo.block()
            assertThat(Foo().withReceiver1 { this.foo + bar() })
                    .isEqualTo("foobar")
            assertThat(withReceiver2(Foo()) { this.foo + bar() })
                    .isEqualTo("foobar")
        }

        @Test
        @DisplayName("使用标签访问接收者引用")
        internal fun test200() {
            class Foo(val id: Int)

            fun Foo.withReceiver(block: Foo.() -> Unit) = block()
            Foo(1).withReceiver foo1@ {
                Foo(2).withReceiver foo2@ {
                    Foo(3).withReceiver foo3@ {
                        assertThat(this@foo1.id).isEqualTo(1)
                        assertThat(this@foo2.id).isEqualTo(2)
                        assertThat(this@foo3.id).isEqualTo(3)
                    }
                }
            }
        }

        @Test
        @DisplayName("使用函数名访问接收者引用")
        internal fun test300() {
            class Foo(val id: Int)
            fun Foo.withReceiver1(block: Foo.() -> Unit) = block()
            fun Foo.withReceiver2(block: Foo.() -> Unit) = block()
            fun Foo.withReceiver3(block: Foo.() -> Unit) = block()
            Foo(1).withReceiver1 {
                Foo(2).withReceiver2 {
                    Foo(3).withReceiver3{
                        assertThat(this@withReceiver1.id).isEqualTo(1)
                        assertThat(this@withReceiver2.id).isEqualTo(2)
                        assertThat(this@withReceiver3.id).isEqualTo(3)
                    }
                }
            }
        }

        fun method() = "Outer"
        @Test
        @DisplayName("访问同名的局部引用和外部类引用")
        fun test400() {
            fun method() = "Local"
            class Foo {
                fun method() = "Instance"
            }

            fun Foo.withReceiver(block: Foo.() -> String): String = block()
            assertThat(Foo().withReceiver {
                this.method() + method() + this@LambdaWithReceiver.method()
            }
            ).isEqualTo("InstanceLocalOuter")
        }
    }

    @Nested
    @DisplayName("成员引用")
    inner class MemberReference {
        @Test
        @DisplayName("引用方法")
        fun test100() {
            fun foo() = "foo"
            val bar: () -> String = ::foo
            assertThat(bar()).isEqualTo("foo")
        }

        @Test
        @DisplayName("引用构造方法")
        fun test200() {
            data class Person(val name: String)

            val ctor = ::Person
            val person = ctor("foo")
            assertThat(person).isEqualTo(Person("foo"))
        }

        @Test
        @DisplayName("引用实例方法")
        fun test300() {
            class Person(val name: String) {
                fun sayName() = name
            }

            val person = Person("foo")
            val classSayName = Person::sayName
            val instanceSayName = person::sayName
            assertThat(classSayName(person)).isEqualTo("foo")
            assertThat(instanceSayName()).isEqualTo("foo")
        }

        @Test
        @DisplayName("引用扩展函数")
        fun test400() {
            class Person(val name: String)

            fun Person.sayName() = name
            val person = Person("foo")
            val sayName = person::sayName
            assertThat(sayName()).isEqualTo("foo")
        }
    }

    @Nested
    @DisplayName("sequence（类似于stream）")
    inner class Sequence {
        @Test
        @DisplayName("只有末端操作才会触发计算")
        fun test100() {
            class Mock {
                fun spy() = Unit
            }

            val mock = Mockito.mock(Mock::class.java)
            listOf(1, 2, 3, 4)
                    .map { mock.spy(); it }
            Mockito.verify(mock, times(4)).spy()
            Mockito.reset(mock)

            val seq = listOf(1, 2, 3, 4)
                    .asSequence()
                    .map { mock.spy(); it }
            Mockito.verify(mock, times(0)).spy()
            seq.toList()
            Mockito.verify(mock, times(4)).spy()
        }

        @Test
        @DisplayName("惰性求值的计算顺序优化")
        fun test200() {
            class Mock {
                fun spy() = Unit
            }

            val mock = Mockito.mock(Mock::class.java)

            // list先对所有元素进行map操作，之后才开始find
            listOf(1, 2, 3, 4, 5)
                    .map { mock.spy(); it >= 1 }
                    .find { it }
            Mockito.verify(mock, times(5)).spy()
            Mockito.reset(mock)

            // sequence对每个元素先map,再find，得到结果后就不再进行后续的map
            listOf(1, 2, 3, 4, 5)
                    .asSequence()
                    .map { mock.spy(); it >= 1 }
                    .find { it }
            Mockito.verify(mock, times(1)).spy()
        }

        @Test
        @DisplayName("生成序列")
        fun test300() {
            val sum = generateSequence(0) { it + 1 }
                    .takeWhile { it <= 100 }
                    .sum()
            assertThat(sum).isEqualTo(5050)
        }
    }

    @Nested
    @DisplayName("lambda vs Single Abstract Method")
    inner class SingleAbstractMethod {

        @Test
        @DisplayName("区分kotlin lambda和java函数式接口")
        fun test100() {
            // correct
            val p1: Predicate<Int> = Predicate { it > 0 }
            // incorrect
//            val p2: Predicate<Int> = { it > 0}

            // correct
            val p3: (Int) -> Boolean = { it > 0 }
            // incorrect
//            val p4: (Int) -> Boolean = Predicate { it > 0 }
        }

        @Test
        @DisplayName("允许在调用接收SAM实例作为参数的java代码时使用lambda")
        fun test200() {
            fun fun1(e: Int, predicate: Predicate<Int>) = predicate.test(e)

            // correct
            Fixture.fun1(1, Predicate { it > 0 })
            Fixture.fun1(1, { it > 0 })

            fun1(1, Predicate { it > 0 })
            // incorrect
//            fun1(1, { it > 0 }) //　不可以用lambda调用接收SAM实例的kotlin方法
        }
    }

    @Nested
    @DisplayName("lambda流程控制")
    inner class FlowControlInLambda {
        @Test
        @DisplayName("内联的lambda可以使用return关键字实现非局部返回")
        internal fun test100() {
            (1..5).forEach {}
            (1..5).forEach { return }
            fail("won't invoke")
        }

        @Test
        @DisplayName("使用带标签的return关键字实现局部返回，类似于continue关键字")
        internal fun test200() {
            var count = 0
            (1..5).forEach foo@ {
                count++
                return@foo
                fail("won't invoke")
            }
            assertThat(count).isEqualTo(5)
            (1..5).forEach {
                return
            }
            fail("won't invoke")
        }

        @Test
        @DisplayName("接受lambda参数的函数名可以作为标签")
        internal fun test300() {
            var count = 0
            (1..5).forEach {
                count++
                return@forEach
                fail("won't invoke")
            }
            assertThat(count).isEqualTo(5)
        }

        @Test
        @DisplayName("使用匿名函数实现局部返回")
        internal fun test400() {
            var count = 0
            (1..5).forEach(fun(_) {
                count++
                return
                fail("won't invoke")
            })
            assertThat(count).isEqualTo(5)
        }

        @Test
        @DisplayName("不允许在非内联的lambda表达式中使用非局部返回")
        internal fun test500() {
            // correct
            val fn1 = { 42 }
            val fn2 = foo@ { return@foo }
            // incorrect
//            val fn2 = { return }
        }

    }

}
