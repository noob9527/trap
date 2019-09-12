package cn.staynoob.trap.kotlin.basic.collection

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.*

@DisplayName("Map")
class MapSpec {
    @Test
    @DisplayName("Tree map size is effected by Comparator")
    internal fun test100() {
        class Key(
                val index1: Int,
                val index2: Int
        ) : Comparable<Key> {
            override fun compareTo(other: Key): Int {
                return index2.compareTo(other.index2)
            }
        }

        val map1 = TreeMap<Key, String>(Comparator.comparing(Key::index1))
        val map2 = TreeMap<Key, String>(Comparator.comparing(Key::index2))
        map1[Key(0, 1)] = "foo"
        map1[Key(0, 2)] = "bar"
        map2[Key(0, 1)] = "foo"
        map2[Key(0, 2)] = "bar"
        assertThat(map1.size).isEqualTo(1)
        assertThat(map2.size).isEqualTo(2)
    }
}
