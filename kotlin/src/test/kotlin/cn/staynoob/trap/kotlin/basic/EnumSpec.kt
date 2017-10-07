@file:Suppress("UNUSED_VARIABLE")

package cn.staynoob.trap.kotlin.basic

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test


@DisplayName("枚举")
class EnumSpec {

    //和java一样，枚举类可以有属性，方法
    enum class Color(val r: Int, val g: Int, val b: Int) {
        RED(255, 0, 0),
        GREEN(0, 255, 0),
        BLACK(0, 0, 255); //使用分号分隔常量列表和方法

        fun rgb() = (r * 256 + g) * 256 + b
    }

    @Test
    @DisplayName("enum作为软关键字，只有出现在class前才有意义")
    fun test100() {
        val enum = 1
    }
}