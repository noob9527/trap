package cn.staynoob.trap.kotlin.basic

import cn.staynoob.trap.kotlin.basic.utils.any
import cn.staynoob.trap.kotlin.basic.utils.eq
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito.spy
import org.mockito.Mockito.verify
import kotlin.reflect.KProperty

@DisplayName("属性")
class PropertySpec {
    @Test
    @DisplayName("自定义存取器")
    fun test100() {
        class Person(var firstName: String, var lastName: String) {
            var fullName: String
                get() {
                    return "$firstName-$lastName"
                }
                set(value: String) {
                    val list = value.split("-")
                    firstName = list[0]
                    lastName = list[1]
                }
        }

        val person = Person("yan", "xu")
        assertThat(person.fullName).isEqualTo("yan-xu")
        person.fullName = "foo-bar"
        assertThat(person.firstName).isEqualTo("foo")
        assertThat(person.lastName).isEqualTo("bar")
    }

    @Test
    @DisplayName("使用field添加带有支持字段的存取器")
    fun test200() {
        class Person {
            var name: String = ""
                set(value) {
                    field = value.toLowerCase()
                }
        }

        val person = Person()
        person.name = "FOO"
        assertThat(person.name).isEqualTo("foo")
    }

    @Test
    @DisplayName("修改setter可见性")
    fun test300() {
        class Person {
            var foo: String = ""
                private set
        }
        // incorrect
//        Person().foo = ""
    }

    @Nested
    @DisplayName("委托属性")
    inner class DelegatedProperties {
        @Test
        @DisplayName("basic usage")
        internal fun test100() {
            class Delegate {
                operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
                    return "foo"
                }

                operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) = Unit
            }

            val spy = spy(Delegate::class.java)

            class Foo {
                var foo: String by spy
            }

            val foo = Foo()
            assertThat(foo.foo).isEqualTo("foo")
            foo.foo = "foo"
            verify(spy)
                    .getValue(eq(foo), any())
            verify(spy)
                    .setValue(eq(foo), any(), eq("foo"))
        }

        @Test
        @DisplayName("使用委托属性实现属性懒加载")
        internal fun test200() {
            fun loadName() = "foo"
            class Foo {
                val name by lazy { loadName() }
            }
            assertThat(Foo().name).isEqualTo("foo")
        }
    }

}