package cn.staynoob.trap.kotlin

import org.junit.Test
import org.assertj.core.api.Assertions.assertThat

class ObjectSpec {

    @Test
    fun equality() {
        class Foo {
            override fun equals(other: Any?) = true
        }
        val foo1 = Foo()
        val foo2 = Foo()
        assertThat(foo1 == foo2).isTrue()
        assertThat(foo1 === foo2).isFalse()
    }

    @Test
    fun objectExpressionAsAnonymousInnerClass() {
        // kotlin 中对象表达式能够访问并修改局部变量（java匿名内部类不行）
        var count = 0
        object {
            fun add() = count ++
        }.add()
        assertThat(count).isEqualTo(1)
    }
}