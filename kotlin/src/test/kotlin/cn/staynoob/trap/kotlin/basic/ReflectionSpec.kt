package cn.staynoob.trap.kotlin.basic

import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.reflect.KClass

@DisplayName("反射")
class ReflectionSpec {
    @Test
    @DisplayName("获取kotlin类引用")
    internal fun test100() {
        val clazz1: Any = Int::class
        val clazz2: Any = (1).javaClass.kotlin
        assertThat(clazz1 is KClass<*>).isTrue()
        assertThat(clazz2 is KClass<*>).isTrue()
    }

    @Test
    @DisplayName("获取java类引用")
    internal fun test200() {
        val clazz1: Any = Int::class.java
        val clazz2: Any = (1).javaClass
        assertThat(clazz1 is Class<*>).isTrue()
        assertThat(clazz2 is Class<*>).isTrue()
    }

    @Nested
    @DisplayName("函数和属性是一等公民")
    inner class FirstClassSpec {
        @Test
        @DisplayName("引用函数")
        internal fun test100() {
            fun isOdd(x: Int) = x % 2 != 0
            class Foo(val foo: String = "foo") {
                fun isFoo(input: String) = input == foo
            }
            assertThat(::isOdd.invoke(1)).isTrue()
            assertThat((::isOdd)(2)).isFalse() //KFunction可以直接调用
            assertThat(Foo()::isFoo.invoke("foo")).isTrue()
            assertThat(Foo("bar")::isFoo.invoke("foo")).isFalse()
        }

        @Test
        @DisplayName("引用构造函数")
        internal fun test200() {
            class Foo
            assertThat(::Foo.invoke())
                    .isInstanceOf(Foo::class.java)
        }

        @Test
        @DisplayName("引用属性")
        internal fun test300() {
            assertThat(listOf("a", "ab", "abc").map(String::length))
                    .containsExactly(1, 2, 3)
        }

        @Test
        @DisplayName("绑定this的属性引用")
        internal fun test400() {
            val prop = "abc"::length
            assertThat(prop.get()).isEqualTo(3)
        }

        fun test500Fn(foo: String = "foo") = foo

        @Test
        @DisplayName("使用callBy应用默认参数")
        internal fun test500() {
            assertThat(this::test500Fn.callBy(mapOf()))
                    .isEqualTo("foo")
            assertThat(this::test500Fn.callBy(mapOf((this::test500Fn.parameters[0] to "bar"))))
                    .isEqualTo("bar")
        }

        @Test
        @DisplayName("截至kotlin1.15，反射local表达式并没有完全支持")
        internal fun test600() {
            fun fn() = Unit
            assertThatThrownBy { ::fn.annotations }
        }
    }
}