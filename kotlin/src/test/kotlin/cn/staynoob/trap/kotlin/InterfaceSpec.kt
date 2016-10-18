package cn.staynoob.trap.kotlin

import org.junit.Test
import org.assertj.core.api.Assertions.*

class InterfaceSpec {

    interface IWithMethodImplementation1 {
        fun foo() = "foo1"
    }
    interface IWithMethodImplementation2 {
        fun foo() = "foo2"
    }

    @Test
    fun withMethodImplementation() {
        class Foo : IWithMethodImplementation1 {}
        assertThat(Foo().foo()).isEqualTo("foo1")
    }

    @Test
    fun withMultiMethodImplementation() {
        class Foo :
                IWithMethodImplementation1,
                IWithMethodImplementation2 {
            override fun foo() = super<IWithMethodImplementation1>.foo()
        }
        assertThat(Foo().foo()).isEqualTo("foo1")
    }
}