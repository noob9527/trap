@file:Suppress("USELESS_ELVIS", "CAST_NEVER_SUCCEEDS", "UNREACHABLE_CODE", "SENSELESS_COMPARISON", "ReplaceSingleLineLet", "unused", "UNUSED_VARIABLE", "UNUSED_EXPRESSION")

package cn.staynoob.trap.kotlin.basic.nullsafety

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.lang.NullPointerException

@DisplayName("空安全")
class NullSafetySpec {

    @Test
    @DisplayName("Safe Calls")
    fun test100() {
        fun safeCall(str: String?): String? = str?.toUpperCase()?.toLowerCase()
        assertThat(safeCall("Foo")).isEqualTo("foo")
        assertThat(safeCall(null)).isNull()
    }

    @Test
    @DisplayName("elvis operator")
    fun test200() {
        assertThat("foo" ?: "bar").isEqualTo("foo")
        assertThat(null ?: "bar").isEqualTo("bar")
    }

    @Test
    @DisplayName("safe casts")
    fun test300() {
        assertThat(1 as? String).isNull()
    }

    @Test
    @DisplayName("!! operator")
    fun test400() {
        assertThatThrownBy { null!! }
                .hasSameClassAs(KotlinNullPointerException())
        val nullable: String? = null
        assertThatThrownBy { nullable!! }
                .hasSameClassAs(KotlinNullPointerException())
    }

    @Test
    @DisplayName("使用let函数用可空变量调用接受非空参数的函数")
    fun test500() {
        // 类似java中Optional的ifPresent方法
        fun notNull(input: String): String = input

        val nullAble: String? = "foo"

        // correct
        assertThat(null.let { it == null }).isTrue()
        assertThat(nullAble?.let { notNull(it) }).isEqualTo("foo")
        assertThat(null?.let { notNull(it) }).isNull()
        // incorrect
//        notNull(nullAble)
    }

    @Test
    @DisplayName("属性延迟初始化")
    fun test600() {
        class Foo {
            lateinit var foo: String
        }

        val foo = Foo()
        assertThatThrownBy { foo.foo }
                .hasSameClassAs(UninitializedPropertyAccessException())
    }

    @Test
    @DisplayName("可空扩展")
    fun test700() {
        class Foo

        fun Foo?.isNull() = this == null
        assertThat(null.isNull()).isTrue()
        assertThat(Foo().isNull()).isFalse()
        // 成功调用了一个变量的实例扩展方法, 并不代表该变量不是null
        val foo: Foo? = null
        foo.isNull()
        assertThat(foo == null).isTrue()
    }

    @Test
    @DisplayName("类型参数默认可空")
    fun test800() {
        fun <T> identity(input: T) = input //这里的类型参数有默认的上界 Any?
        assertThat(identity(null as String?)).isNull()
        // 为泛型指明非空上界
        fun <T : Any> identityNotNull(input: T) = input
        // incorrect
//        identityNotNull(null)
    }

    @Test
    @DisplayName("访问java类型")
    fun test900() {
        val demo = Fixture()
        // java原始数据类型是非空的
        val primitive: Int = demo.primitive
        // 使用了类型注解的java属性会得到正确的kotlin类型
        val notNull: String = demo.withNotNullAnnotation
        val nullAble: String? = demo.withNullAbleAnnotation

        // 错误的使用java属性会在运行时报错
        assertThatThrownBy {
            val platform1: String = demo.platformType
        }.hasSameClassAs(IllegalStateException())
        assertThatThrownBy {
            demo.platformType.toUpperCase()
        }.hasSameClassAs(IllegalStateException())
        val platform2: String? = demo.platformType
    }

    @Test
    @DisplayName("重写java方法时可以使用可空或非空类型")
    fun test1000() {
        class SubClass1 : Fixture() {
            override fun fn(str: String?) = Unit
        }

        class SubClass2 : Fixture() {
            // 如果使用非空类型，kotlin会自动生成非空断言
            // 在java中使用null调用这个重写方法，会报错
            override fun fn(str: String) = Unit
        }
    }

    @Test
    @DisplayName("将可空集合转换为非空集合")
    fun test1100() {
        val list: List<Int> = listOf(1, 2, 3, null).filterNotNull()
        assertThat(list).containsExactly(1, 2, 3)
    }

    @Test
    @DisplayName("java代码可以在非空集合中添加空元素，访问该元素将抛出NPE")
    fun test1200() {
        val set = setOf(1, 2, 3)
        Fixture.addElement(set, null)
        assertThatThrownBy { set.forEach { it } }
                .hasSameClassAs(NullPointerException())
    }
}