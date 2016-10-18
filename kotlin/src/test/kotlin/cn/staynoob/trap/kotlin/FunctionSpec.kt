package cn.staynoob.trap.kotlin

import org.junit.Test
import org.assertj.core.api.Assertions.*

class FunctionSpec {
    // extension function
    @Test
    fun extensionFunction() {
        fun String.lastChar(): Char = get(length - 1)
        assertThat("kotlin".lastChar()).isEqualTo('n')
    }

    @Test
    fun preferMemberFunction() {
        class Foo {
            fun sayHello() = "foo"
        }

        fun Foo.sayHello() = "bar"
        assertThat(Foo().sayHello()).isEqualTo("foo")
    }

    @Test
    fun cannotOverwriteExtensionFunction() {
        open class Parent {
            open fun sayHello() = "parent"
        }

        class Child : Parent() {
            override fun sayHello() = "child"
        }

        fun Parent.extensionSayHello() = "parent"
        fun Child.extensionSayHello() = "child"

        val child1 = Child()
        val child2: Parent = Child()

        // 调用哪个成员函数取决于运行时类型
        assertThat(child1.sayHello()).isEqualTo("child")
        assertThat(child2.sayHello()).isEqualTo("child")
        // 调用哪个扩展函数取决于编译时类型，而不是运行时类型
        assertThat(child1.extensionSayHello()).isEqualTo("child")
        assertThat(child2.extensionSayHello()).isEqualTo("parent")
    }

    // varargs
    @Test
    fun varargsShouldNotAutoExpandArray() {
        fun <T> genericListOf(vararg element: T): List<T> {
            return listOf(*element)
        }
        val arr = arrayOf(1, 2, 3)
        assertThat(genericListOf(arr).size).isEqualTo(1)
        assertThat(genericListOf(1, arr).size).isEqualTo(2)
        assertThat(genericListOf(*arr).size).isEqualTo(3)
        assertThat(genericListOf(1, *arr).size).isEqualTo(4)
    }

    // infix
    @Test
    fun infixFunction() {
        infix fun Int.add(other: Int):Int = this + other
        assertThat(1.add(2))
                .isEqualTo(1 add 2)
                .isEqualTo(3)
    }
}