@file:Suppress("RedundantVisibilityModifier", "unused", "UNUSED_PARAMETER")

package cn.staynoob.trap.kotlin.basic

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("类")
class ClassSpec {

    @Test
    @DisplayName("override的成员默认是open的，如果要禁止重写，需要显式使用final关键字")
    fun test100() {
        open class Parent {
            open fun foo() = "parent"
            open fun bar() = "parent"
        }

        open class Child : Parent() {
            override fun foo() = "child" //该方法默认是open的
            final override fun bar() = "child" //禁止继承
        }

        class GrandChild : Child() {
            // correct
            override fun foo() = "grandChild"
            // incorrect
//            override fun bar() = "grandChild"
        }
    }

    /**
     * @see cn.staynoob.trap.java.basic.ClassSpec#test100
     */
    @Test
    @DisplayName("kotlin中外部类无法看到内部类中的私有/受保护的成员")
    fun test200() {
        class Outer {
            open inner class Inner {
                private val foo: String = "foo"
                protected val bar = "bar"
                internal val baz = "baz"
                public val qux = "qux"
            }

            fun fn() {
                val inner = Inner()
                // correct
                inner.qux
                inner.baz
                // incorrect
//                inner.foo
//                inner.bar
            }
        }
    }

    @Test
    @DisplayName("kotlin嵌套类默认是static的，使用inner关键字标记内部类，使用this@Outer获取外部类引用")
    fun test300() {
        class Outer {
            inner class Inner {
                fun getOuterReference() = this@Outer
            }
        }

        val outer = Outer()
        assertThat(outer.Inner().getOuterReference()).isEqualTo(outer)
    }

    sealed class Test400 {
        // correct
        class Test400Child1 : Test400()

        class Test400Child2 : Test400()
    }

    // incorrect
//    class Test400Child3 : Test400()

    @Test
    @DisplayName("密封类要求所有子类必须嵌套定义在父类中，这有利于使用when表达式")
    fun test400() {
        open class UnSealed {
            inner class UnSealedChild1 : UnSealed()
            inner class UnSealedChild2 : UnSealed()
        }

        // correct
        fun fn1(x: Test400): String = when (x) {
            is Test400.Test400Child1 -> "child1"
            is Test400.Test400Child2 -> "child2"
        }
        // incorrect(the else branch is necessary)
//        fun fn2(x: UnSealed): String = when (x) {
//            is UnSealed.UnSealedChild1 -> "child1"
//            is UnSealed.UnSealedChild2 -> "child2"
//        }
    }

    @Nested
    @DisplayName("构造方法与初始化语句")
    inner class ConstructorSpec {
        @Test
        @DisplayName("一个类可以有多个初始化语句块")
        fun test100() {
            class Foo {
                var count = 0

                init {
                    count++
                }

                init {
                    count++
                }
            }
            assertThat(Foo().count).isEqualTo(2)
        }

        @Test
        @DisplayName("使用constructor关键字声明构造方法")
        fun test200() {
            @Suppress("JoinDeclarationAndAssignment")
            class Foo constructor(foo: String, _bar: String) {
                val foo: String
                val bar = _bar

                init {
                    this.foo = foo
                }
            }
            val foo = Foo("foo", "bar")
            assertThat(foo.foo).isEqualTo("foo")
            assertThat(foo.bar).isEqualTo("bar")
        }

        @Test
        @DisplayName("可以声明多个构造方法，每个构造方法要么初始化基类，要么委托给这么做了的其它构造方法")
        fun test300(){
            open class Parent
            class Child: Parent {
                constructor():super()
                constructor(foo:String):this()
                constructor(bar:Int):this("foo")
            }
        }

    }

    @Nested
    @DisplayName("数据类")
    inner class DataClassSpec {
        @Test
        @DisplayName("没有在主构造方法中声明的属性不会加入到equals和hashCode计算中")
        fun test100() {
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
        @DisplayName("优先考虑使用copy方法，避免mutate")
        fun test200() {
            data class Foo(val foo: String, val bar: String)

            val foo1 = Foo("foo", "bar")
            val foo2 = foo1.copy(bar = "baz")
            assertThat(foo2).isEqualTo(Foo("foo", "baz"))
        }

    }

    private interface IFoo {
        fun foo(): String
        fun bar(): String
    }

    @Test
    @DisplayName("委托其它类来实现接口（组合优于继承）")
    fun test500() {
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
