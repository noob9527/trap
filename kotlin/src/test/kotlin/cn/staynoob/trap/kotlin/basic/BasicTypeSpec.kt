@file:Suppress("UNUSED_VARIABLE", "RedundantExplicitType")

package cn.staynoob.trap.kotlin.basic

import org.junit.jupiter.api.DisplayName
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("基础类型")
class BasicTypeSpec {

    @Test
    @DisplayName("kotlin基础数据类型间不会发生隐式类型转换")
    internal fun test100() {
        // correct
        val aInt: Int = 1
        val aLong: Long = aInt.toLong()

        // incorrect
//        val aInt: Int = 1
//        val aLong: Long = aInt
    }

    @Test
    @DisplayName("数字字面量支持使用下划线使常量更易读")
    internal fun test200() {
        val million = 1_000_000
    }

    @Test
    @DisplayName("字符串转基础数据类型")
    internal fun test300() {
        assertThat("1".toInt()).isEqualTo(1)
        assertThat("whatever".toIntOrNull()).isNull()
        // 除 "true" 以外，全部转换为 false
        assertThat("true".toBoolean()).isTrue()
        assertThat("true ".toBoolean()).isFalse()
        assertThat("whatever".toBoolean()).isFalse()
    }

    @Test
    @DisplayName("返回Nothing类型的函数永远不会正常终止")
    internal fun test400() {
        // correct
        fun fail1(): Nothing = throw Exception()

        fun fail2(): Nothing {
            while (true) {
            }
        }
        // incorrect
//        fun fail3(): Nothing {
//            return
//        }

        val nullAble: String? = "foo"
        nullAble ?: fail2() // 等价于类型断言
        val notNull: String = nullAble
    }

    @Nested
    @DisplayName("数字")
    inner class NumberSpec {

        @Test
        @DisplayName("默认的数字类型")
        fun test100() {
            val a = 2147483647
            val b = 2147483648
            val c = 1.0
            assertThat(a.javaClass.kotlin).isEqualTo(Int::class)
            assertThat(b.javaClass.kotlin).isEqualTo(Long::class)
            assertThat(c.javaClass.kotlin).isEqualTo(Double::class)
        }
    }

    @Nested
    @DisplayName("字符串")
    inner class StringSpec {

        @Test
        @DisplayName("字符串模板可以嵌套")
        fun test100() {
            val foo = "foo"
            val template = "$foo${"$foo"}"
            assertThat(template).isEqualTo("foofoo")
        }

        /**
         * @see cn.staynoob.trap.java.basic.StringSpec.test100
         */
        @Test
        @DisplayName("kotlin重新实现了split方法，屏蔽了可能由正则表达式造成的陷阱")
        fun test200() {
            assertThat("1.2-3".split(".".toRegex())).isEqualTo(listOf("", "", "", "", "", ""))
            assertThat("1.2-3".split(".").size).isEqualTo(2)
            assertThat("1.2-3".split(".", "-")).isEqualTo(listOf("1", "2", "3"))
        }

        @Test
        @DisplayName("解析文件路径的例子")
        fun parsePathTest() {
            class PathString(val directory: String, val fileName: String, val ext: String)

            // 字符串方法实现
            fun parsePath1(path: String): PathString {
                val dir = path.substringBeforeLast("/")
                val fullName = path.substringAfterLast("/")
                val fileName = fullName.substringBeforeLast(".")
                val ext = fullName.substringAfterLast(".")
                return PathString(dir, fileName, ext)
            }

            // 正则表达式实现
            fun parsePath2(path: String): PathString {
                val regex = """(.+)/(.+)\.(.+)""".toRegex()
                val result = regex.matchEntire(path)
                val (dir, fileName, ext) = result!!.destructured
                return PathString(dir, fileName, ext)
            }

            val path = "/home/HelloWorld.kt"
            val result1 = parsePath1(path)
            val result2 = parsePath2(path)
            assertThat(result1.directory).isEqualTo("/home")
            assertThat(result1.fileName).isEqualTo("HelloWorld")
            assertThat(result1.ext).isEqualTo("kt")

            assertThat(result2.directory).isEqualTo("/home")
            assertThat(result2.fileName).isEqualTo("HelloWorld")
            assertThat(result2.ext).isEqualTo("kt")
        }

        @Test
        @DisplayName("由于在三重引号字符串中不会转义字符，只能使用嵌入表达式来表示美元符号字面量")
        fun dollarSymbolInMultiLineString() {
            val dollar1 = """${'$'}"""
            val dollar2 = "\$"
            assertThat(dollar1).isEqualTo(dollar2)
        }

    }
}