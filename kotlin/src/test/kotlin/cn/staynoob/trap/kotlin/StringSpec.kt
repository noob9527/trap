package cn.staynoob.trap.kotlin

import org.junit.Test
import org.assertj.core.api.Assertions.*

class StringSpec {
    @Test
    fun splitByDot() {
        assertThat("1.2-3".split(".".toRegex())).isEqualTo(listOf("", "", "", "", "", ""))
        assertThat("1.2-3".split(".").size).isEqualTo(2)
        assertThat("1.2-3".split(".", "-")).isEqualTo(listOf("1", "2", "3"))
    }

    @Test
    fun parsePathTest() {
        class PathString(val directory: String, val fileName: String, val ext: String)

        fun parsePath1(path: String): PathString {
            val dir = path.substringBeforeLast("/")
            val fullName = path.substringAfterLast("/")
            val fileName = fullName.substringBeforeLast(".")
            val ext = fullName.substringAfterLast(".")
            return PathString(dir, fileName, ext)
        }
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
    fun dollarSymbolInMultiLineString(){
        val dollar1 = """${'$'}"""
        val dollar2 = "\$"
        assertThat(dollar1).isEqualTo(dollar2)
    }
}