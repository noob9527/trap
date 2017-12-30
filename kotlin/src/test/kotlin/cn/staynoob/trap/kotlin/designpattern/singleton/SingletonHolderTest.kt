package cn.staynoob.trap.kotlin.designpattern.singleton

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SingletonHolderTest {

    class Sample private constructor(val foo: String) {
        companion object : SingletonHolder<Sample, String>(::Sample)
    }

    @Test
    fun test100() {
        val res1 = Sample.getInstance("foo")
        val res2 = Sample.getInstance("bar")
        assertThat(res1 === res2)
    }
}
