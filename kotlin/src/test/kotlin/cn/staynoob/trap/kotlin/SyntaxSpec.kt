package cn.staynoob.trap.kotlin

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class SyntaxSpec {
    @Test
    fun ifAsExpression() {
        assertThat(if (true) 1 else 2).isEqualTo(1)
    }

    @Test
    fun tryAsExpression(){
        val number = try {
            Integer.parseInt("1")
        } catch (e: NumberFormatException){
            null
        }
        assertThat(number).isEqualTo(1)
    }

    @Test
    fun range() {
        val list: MutableList<Int> = ArrayList()

        list += 1..10
        assertThat(list).isEqualTo(mutableListOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10))
        list.clear()

        list += 1 until 10
        assertThat(list).isEqualTo(mutableListOf(1, 2, 3, 4, 5, 6, 7, 8, 9))
        list.clear()

        list += 10 downTo 1
        assertThat(list).isEqualTo(mutableListOf(10, 9, 8, 7, 6, 5, 4, 3, 2, 1))
        list.clear()

        list += 10 downTo 1 step 2
        assertThat(list).isEqualTo(mutableListOf(10, 8, 6, 4, 2))
    }

    @Test
    fun inOperator(){
        assertThat(10 in 1..10).isTrue()
        assertThat(11 !in 1..10).isTrue()
    }

    @Test
    fun destructuringDeclaration(){
        val (a, b) = 1 to 2
        assertThat(a).isEqualTo(1)
        assertThat(b).isEqualTo(2)
    }
}
