package cn.staynoob.trap.kotlin.nd4j

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.nd4j.linalg.factory.Nd4j

class NdSpec {
    @Test
    @DisplayName("test shape")
    fun test100() {
        val d1 = Nd4j.zeros(10)
        val d2 = Nd4j.zeros(10, 10)
        val d3 = Nd4j.zeros(10, 10, 10)
        assertThat(d1.shape()).containsExactly(1, 10)
        assertThat(d2.shape()).containsExactly(10, 10)
        assertThat(d3.shape()).containsExactly(10, 10, 10)
    }

    @Test
    @DisplayName("norm2")
    fun test200() {
        val arr = Nd4j.create(doubleArrayOf(3.0, 4.0))
        val res = arr.norm2Number()
        assertThat(res).isEqualTo(5.0)
    }

    @Test
    @DisplayName("stack")
    fun test300() {
        val d1 = Nd4j.zeros(2)
        val d2 = Nd4j.ones(2)
        val h = Nd4j.hstack(d1, d2)
        val v = Nd4j.vstack(d1, d2)

//        println(h)
//        println(v)
        assertThat(h.toDoubleVector()).containsExactly(0.0, 0.0, 1.0, 1.0)
        assertThat(v.getRow(1).toDoubleVector()).containsExactly(1.0, 1.0)
    }
}
