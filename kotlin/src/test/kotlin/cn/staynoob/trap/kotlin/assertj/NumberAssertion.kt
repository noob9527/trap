package cn.staynoob.trap.kotlin.assertj

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.data.Offset
import org.assertj.core.data.Percentage
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class NumberAssertion {
    @Nested
    inner class IsCloseTo {
        @Test
        @DisplayName("Offset test")
        fun test100() {
            assertThat(99).isCloseTo(100, Offset.offset(1))
            assertThat(100).isCloseTo(100, Offset.offset(1))
            assertThat(101).isCloseTo(100, Offset.offset(1))

            assertThat(98).isNotCloseTo(100, Offset.offset(1))
            assertThat(102).isNotCloseTo(100, Offset.offset(1))
        }

        @Test
        @DisplayName("Percentage test")
        fun test200() {
            assertThat(99).isCloseTo(100, Percentage.withPercentage(1.0))
            assertThat(100).isCloseTo(100, Percentage.withPercentage(1.0))
            assertThat(101).isCloseTo(100, Percentage.withPercentage(1.0))

            assertThat(98).isNotCloseTo(100, Percentage.withPercentage(1.0))
            assertThat(102).isNotCloseTo(100, Percentage.withPercentage(1.0))
        }
    }
}