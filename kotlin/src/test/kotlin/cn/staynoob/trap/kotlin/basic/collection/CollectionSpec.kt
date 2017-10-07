package cn.staynoob.trap.kotlin.basic.collection

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.lang.UnsupportedOperationException

@DisplayName("集合")
class CollectionSpec {
    @Test
    @DisplayName("可能有多个变量持有同一集合的引用，不可变集合并不总是不可变")
    internal fun test100() {
        val mutableCollection: MutableCollection<Int> = mutableListOf(1, 2, 3)
        val collection: Collection<Int> = mutableCollection
        assertThat(collection.size).isEqualTo(3)
        mutableCollection.add(4)
        assertThat(collection.size).isEqualTo(4)
    }

    @Test
    @DisplayName("java代码可以修改不可变集合")
    internal fun test200() {
        val set = setOf(1, 2, 3)
        Fixture.addElement(set, 4)
        assertThat(set).containsExactly(1, 2, 3, 4)
    }

    @Test
    @DisplayName("java代码只能修改可变的list")
    internal fun test300() {
        val mutable = mutableListOf(1, 2, 3)
        val immutable = listOf(1, 2, 3)
        Fixture.addElement(mutable, 4)
        assertThat(mutable).containsExactly(1, 2, 3, 4)
        assertThatThrownBy { Fixture.addElement(immutable, 4) }
                .hasSameClassAs(UnsupportedOperationException())
    }

    @Nested
    @DisplayName("数组")
    inner class ArraySpec {
        @Test
        @DisplayName("basic usage")
        internal fun test100() {
            val letters = Array(26) { i -> ('a' + i).toString() }
            assertThat(letters.size).isEqualTo(26)
            assertThat(letters).startsWith("a", "b", "c")
            assertThat(letters).endsWith("x", "y", "z")
        }

        @Test
        @DisplayName("使用展开运算符展开数组")
        internal fun test200() {
            val arr = arrayOf(1, 2, 3)
            val list = listOf(*arr)
            val set = setOf(*list.toTypedArray())
            assertThat(list).containsExactly(1, 2, 3)
            assertThat(set).contains(1, 2, 3)
        }

        @Test
        @DisplayName("创建基本类型数组")
        internal fun test300() {
            val arr = IntArray(3) { it }
            assertThat(arr).containsExactly(0,1,2)
            assertThat(arr.all { it::class.java.isPrimitive })
                    .isTrue()
        }
    }
}