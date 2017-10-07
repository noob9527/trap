package cn.staynoob.trap.kotlin.basic

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("接口")
class InterfaceSpec {

    interface IWithMethodImplementation1 {
        fun foo() = "foo1"
    }
    interface IWithMethodImplementation2 {
        fun foo() = "foo2"
    }

    @Test
    @DisplayName("接口可以带有方法实现（不需要default关键字）")
    fun test100() {
        class Foo : IWithMethodImplementation1 {}
        assertThat(Foo().foo()).isEqualTo("foo1")
    }

    /**
     * @see cn.staynoob.trap.java.basic.InterfaceSpec.test100
     */
    @Test
    @DisplayName("如果一个类实现了多个带有相同方法实现的接口，则必须重写该方法")
    fun test200() {
        class Foo :
                IWithMethodImplementation1,
                IWithMethodImplementation2 {
            override fun foo() = super<IWithMethodImplementation1>.foo()
        }
        assertThat(Foo().foo()).isEqualTo("foo1")
    }

    interface Test300 {
        val name: String
    }
    @Test
    @DisplayName("接口可以声明属性")
    fun test300() {
        // correct
        class Impl1(override val name:String): Test300
        class Impl2: Test300 {
            override val name: String
                get() = "foo"
        }
        abstract class Impl3: Test300
        // incorrect
//        class Impl4: Test300
    }

    interface Test400 {
        var firstName: String
        var lastName: String
        var fullName:String
            get() = "$firstName-$lastName"
            set(value: String) {
                val arr = value.split("-")
                firstName = arr[0]
                lastName = arr[1]
            }
    }
    @Test
    @DisplayName("接口属性可以有getter,setter，但不能有相应的支持字段")
    fun test400(){
        class Impl(
                override var firstName: String,
                override var lastName: String) : Test400
        val impl = Impl("foo", "bar")
        assertThat(impl.fullName).isEqualTo("foo-bar")
        impl.fullName = "baz-qux"
        assertThat(impl.firstName).isEqualTo("baz")
        assertThat(impl.lastName).isEqualTo("qux")
    }
}