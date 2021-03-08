package cn.staynoob.trap.java.basic.concurrent

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.util.concurrent.ConcurrentHashMap

class ConcurrentHashMapTest {
    /**
     * for now, I this this is a JDK bug
     * the following code runs forever.
     */
    @Test
    @Disabled
    fun test100() {
        val map: MutableMap<Int, Int> = ConcurrentHashMap()
        val thread = Thread {
            map.computeIfAbsent(1) {
                map.computeIfAbsent(1) {
                    2
                }
                1
            }
        }
        thread.start()
        thread.join(100)
        thread.interrupt()
        thread.join()
    }
}
