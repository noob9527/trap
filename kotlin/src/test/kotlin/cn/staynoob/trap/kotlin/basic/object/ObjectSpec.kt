package cn.staynoob.trap.kotlin.basic.`object`

import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName

@Suppress("EqualsOrHashCode")
@DisplayName("对象")
class ObjectSpec {

    @Test
    @DisplayName("对象判等，使用==调用equals方法，使用===比较引用")
    fun test100() {
        class Foo {
            override fun equals(other: Any?) = true
        }
        val foo1 = Foo()
        val foo2 = Foo()
        assertThat(foo1 == foo2).isTrue()
        assertThat(foo1 === foo2).isFalse()
    }

    @Test
    @DisplayName("使用对象表达式替代匿名内部类")
    fun test200() {
        // kotlin 中对象表达式能够访问并修改局部变量（java匿名内部类不行）
        var count = 0
        object {
            fun add() = count ++
        }.add()
        assertThat(count).isEqualTo(1)
    }

    interface ITest300 {
        fun foo():String
    }

    class Test300Impl {
        companion object: ITest300 {
            override fun foo() = "foo"
        }
    }
    @Test
    @DisplayName("使用伴生对象实现接口，注意使用类名作为该接口的实例")
    fun test300(){
        val foo: ITest300 = Test300Impl
        assertThat(foo.foo()).isEqualTo("foo")
    }
}