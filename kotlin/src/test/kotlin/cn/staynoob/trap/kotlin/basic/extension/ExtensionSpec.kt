@file:Suppress("unused", "ClassName")

package cn.staynoob.trap.kotlin.basic.extension

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import cn.staynoob.trap.kotlin.basic.extension.anotherpackage.extendProperty as aliasProperty
import cn.staynoob.trap.kotlin.basic.extension.anotherpackage.extendFunction as aliasFunction

@DisplayName("扩展方法和扩展属性")
class ExtensionSpec {

    @Test
    @DisplayName("为类的实例扩展方法")
    fun test100() {
        class Foo
        fun Foo.sayHello() = "foo"
        Assertions.assertThat(Foo().sayHello()).isEqualTo("foo")
    }

    class Test150_1 {
        companion object
    }
    class Test150_2 {
        companion object WithName
    }
    @Test
    @DisplayName("为类扩展方法（通过为类的伴生对象扩展方法实现）")
    fun test150() {
        fun Test150_1.Companion.sayHello() = "foo"
        fun Test150_2.WithName.sayHello() = "foo"
        assertThat(Test150_1.sayHello()).isEqualTo("foo")
        assertThat(Test150_2.sayHello()).isEqualTo("foo")
    }

    @Test
    @DisplayName("扩展方法不能访问私有或受保护的成员")
    fun test200() {
        @Suppress("RedundantVisibilityModifier")
        open class Foo {
            private val foo = "foo"
            protected val bar = "bar"
            internal val baz = "baz"
            public val qux = "qux"
        }

        fun Foo.sayHello() {
            // correct
            this.baz
            this.qux
            // incorrect
//            this.bar
//            this.foo
        }
    }

    @Test
    @DisplayName("扩展方法与成员方法重名时，优先调用成员方法")
    fun test300() {
        class Foo {
            fun sayHello() = "foo"
        }

        @Suppress("EXTENSION_SHADOWED_BY_MEMBER")
        fun Foo.sayHello() = "bar"
        Assertions.assertThat(Foo().sayHello()).isEqualTo("foo")
    }

    @Test
    @DisplayName("调用扩展函数由该变量的静态类型决定，因此无法重写扩展函数")
    fun test400() {
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
        Assertions.assertThat(child1.sayHello()).isEqualTo("child")
        Assertions.assertThat(child2.sayHello()).isEqualTo("child")
        // 调用哪个扩展函数取决于编译时类型，而不是运行时类型
        Assertions.assertThat(child1.extensionSayHello()).isEqualTo("child")
        Assertions.assertThat(child2.extensionSayHello()).isEqualTo("parent")
    }

    @Test
    @DisplayName("同一个包中的扩展函数可以直接使用，不同包则需要先导入，可以使用as为导入的扩展函数指定别名")
    fun test2000() {
        fun Fixture.samePackage() = null
        val demo = Fixture()
        // correct
        demo.samePackage()
        demo.aliasFunction()
        demo.aliasProperty
        // incorrect
//        demo.extendFunction()
//        demo.extendProperty
    }
}