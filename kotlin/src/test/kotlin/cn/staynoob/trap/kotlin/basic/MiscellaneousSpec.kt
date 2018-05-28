@file:Suppress("UNUSED_VALUE")

package cn.staynoob.trap.kotlin.basic

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Suppress("UNUSED_VARIABLE")
@DisplayName("杂项")
class MiscellaneousSpec {

    @Test
    @DisplayName("显式类型转换")
    fun test100() {
        val any: Any = ""
        val str: String = any as String
    }


    @Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")
    @DisplayName("类型检测")
    @Nested
    inner class TypeCheck {
        @Test
        @DisplayName("使用 is 操作符检测类型，同时智能转换")
        fun test100() {
            val foo: Any = "foo"
            Assertions.assertThat(foo is String).isTrue()
            if (foo !is String) return
            Assertions.assertThat(foo.length).isNotNull()
        }

        @Test
        @DisplayName("is 操作符会区分可空和不可空类型")
        fun test150() {
            val foo: String? = null
            val bar: String? = "bar"

            assertThat(foo is String).isFalse()
            assertThat(bar is String).isTrue()
        }

        @Test
        @DisplayName("必须满足是val属性,没有自定义访问器，且不是open的属性才可以智能转换")
        fun test200() {
            open class Obj(var foo: Any = "foo") {
                val bar: Any
                    get():Any = "bar"
                open val baz: Any = "baz"
                val qux: Any = "qux"
            }

            val obj = Obj()
            if (obj.foo !is String) return
            if (obj.bar !is String) return
            if (obj.baz !is String) return
            if (obj.qux !is String) return
            var str: String
            // correct
            str = obj.qux
            // incorrect
//            str = obj.foo
//            str = obj.bar
//            str = obj.baz
        }
    }
}