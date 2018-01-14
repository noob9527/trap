package cn.staynoob.trap.kotlin.designpattern.singleton

@Suppress("UNUSED_PARAMETER")
class Sample2 private constructor(
        foo: String
) {
    companion object {
        @Volatile
        var INSTANCE: Sample2? = null
            private set

        fun createInstance(foo: String): Sample2 {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Sample2(foo).also { INSTANCE = it }
            }
        }
    }
}