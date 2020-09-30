@file:Suppress("MemberVisibilityCanPrivate")

package cn.staynoob.trap.kotlin.basic.annotation

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.javaField

@DisplayName("注解")
class AnnotationSpec {

    annotation class Test100Annotation

    @Test
    @DisplayName("kotlin注解默认拥有Runtime保留期")
    internal fun test100() {
        @Test100Annotation
        class Foo
        assertThat(Foo::class.annotations[0])
                .isInstanceOf(Test100Annotation::class.java)
    }

    @Target(AnnotationTarget.PROPERTY)
    annotation class Test200PropertyAnnotation

    @Target(AnnotationTarget.FIELD)
    annotation class Test200FieldAnnotation

    @Test
    @DisplayName("属性注解目标")
    internal fun test200() {
        class Foo {
            @Test200PropertyAnnotation
            val foo: String = "foo"

            @Test200FieldAnnotation
            val bar: String = "bar"

            @Fixture.JavaFieldAnnotation
            val baz: String = "baz"
        }
        assertThat(Foo::foo.annotations.size).isEqualTo(1)
        assertThat(Foo::bar.annotations.size).isEqualTo(0)
        assertThat(Foo::baz.annotations.size).isEqualTo(0)
        assertThat(Foo::foo.javaField?.annotations?.size).isEqualTo(0)
        assertThat(Foo::bar.javaField?.annotations?.size).isEqualTo(1)
        assertThat(Foo::baz.javaField?.annotations?.size).isEqualTo(1)
    }

    @Target(AnnotationTarget.PROPERTY, AnnotationTarget.FIELD)
    annotation class Test300Annotation

    @Test
    @DisplayName("显式指定注解目标")
    internal fun test300() {
        class Foo {
            @property:Test300Annotation
            val foo: String = "foo"

            @field:Test300Annotation
            val bar: String = "bar"
        }
        assertThat(Foo::foo.annotations.size).isEqualTo(1)
        assertThat(Foo::bar.annotations.size).isEqualTo(0)
        assertThat(Foo::foo.javaField?.annotations?.size).isEqualTo(0)
        assertThat(Foo::bar.javaField?.annotations?.size).isEqualTo(1)
    }

    annotation class Test400Annotation(val name: String)

    @Test400Annotation("foo")
    fun test400Foo() = Unit

    @Test400Annotation("bar")
    fun test400Bar() = Unit

    @Test
    @DisplayName("使用findAnnotation方法获取指定元素上的指定注解")
    internal fun test400() {
        assertThat(this::test400Foo.findAnnotation<Test400Annotation>()?.name)
                .isEqualTo("foo")
        assertThat(this::test400Bar.findAnnotation<Test400Annotation>()?.name)
                .isEqualTo("bar")
    }

    //    @Inherited
    @Target(AnnotationTarget.PROPERTY, AnnotationTarget.CLASS)
    annotation class Test500Annotation()

    @Test500Annotation
    abstract class Test500Parent {
        @Test500Annotation
        val prop1: String = ""

        @Test500Annotation
        abstract val prop2: String

        @Test500Annotation
        open val prop3: String = ""
    }

    interface Test500Interface {
        @Test500Annotation
        val prop4: String
    }

    class Test500Child : Test500Parent(), Test500Interface {
        override val prop2: String = ""
        override val prop3: String = ""
        override val prop4: String = ""
    }

    @Nested
    inner class AnnotationInheritance {
        @Test
        @DisplayName("kotlin @Inherited 注解没有效果")
        fun test100() {
            assertThat(Test500Parent::class.findAnnotation<Test500Annotation>()).isNotNull()

            assertThat(Test500Child::class.findAnnotation<Test500Annotation>()).isNull()
        }

        @Test
        @DisplayName("未被重写的属性可以找到注解")
        fun test200() {
            assertThat(Test500Child::prop1.findAnnotation<Test500Annotation>()).isNotNull()
        }

        @Test
        @DisplayName("重写的属性无法找到注解")
        fun test300() {
            assertThat(Test500Child::prop2.findAnnotation<Test500Annotation>()).isNull()
            assertThat(Test500Child::prop3.findAnnotation<Test500Annotation>()).isNull()
            assertThat(Test500Child::prop4.findAnnotation<Test500Annotation>()).isNull()
        }
    }

}
