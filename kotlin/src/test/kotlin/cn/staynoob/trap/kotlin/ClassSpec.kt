package cn.staynoob.trap.kotlin

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class ClassSpec {

    @Test
    fun innerClass() {
        class Outer {
            inner class Inner {
                fun getOuterReference() = this@Outer
            }
        }

        val outer = Outer()
        assertThat(outer.Inner().getOuterReference()).isEqualTo(outer)
    }

    @Test
    fun dataClassShouldExCludeSecondaryConstructorParameter() {
        data class Foo(val foo: String, val bar: String) {
            var baz: String? = null

            constructor(foo: String, bar: String, baz: String) : this(foo, bar) {
                this.baz = baz
            }
        }

        val foo1 = Foo("foo", "bar")
        val foo2 = Foo("foo", "bar", "baz")
        assertThat(foo1 == foo2).isTrue()
        assertThat(foo1.hashCode() == foo2.hashCode()).isTrue()
    }

    @Test
    fun dataClassCopy() {
        data class Foo(val foo: String, val bar: String)

        val foo1 = Foo("foo", "bar")
        val foo2 = foo1.copy(bar = "baz")
        assertThat(foo2).isEqualTo(Foo("foo", "baz"))
    }

    private interface IFoo {
        fun foo(): String
        fun bar(): String
    }
    @Test
    fun delegation() {
        class Foo : IFoo {
            override fun foo(): String = "foo"
            override fun bar(): String = "foo"
        }
        // 委托foo属性来实现接口
        class Bar(val foo: Foo) : IFoo by foo {
            override fun bar(): String = "bar"
        }
        val bar = Bar(Foo())
        assertThat(bar.foo()).isEqualTo("foo")
        assertThat(bar.bar()).isEqualTo("bar")
    }

}
