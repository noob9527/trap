@file:Suppress("USELESS_IS_CHECK", "UNCHECKED_CAST", "unused", "UNUSED_VARIABLE", "UNUSED_PARAMETER")

package cn.staynoob.trap.kotlin.basic

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/**
 * @see cn.staynoob.trap.java.basic.GenericSpec
 */
@DisplayName("泛型")
class GenericSpec {

    @Test
    @DisplayName("kotlin不支持“原生态类型”，所有泛型类必须指定类型参数")
    internal fun test100() {
        val list1: List<Any> = listOf(1, "1")
        // incorrect
//        val list2: List = listOf(1, "1")
    }

    interface Parent1 {
        fun getName1() = "parent1"
    }

    interface Parent2 {
        fun getName2() = "parent2"
    }

    @Nested
    @DisplayName("泛型约束")
    inner class GenericConstraintSpec {
        @Test
        @DisplayName("指定上界")
        internal fun test100() {
            open class Parent(val greeting: String = "hello")
            class Foo : Parent()
            class Bar : Parent()

            fun <T : Parent> greeting(obj: T) = obj.greeting
            assertThat(greeting(Parent())).isEqualTo("hello")
            assertThat(greeting(Foo())).isEqualTo("hello")
            assertThat(greeting(Bar())).isEqualTo("hello")
        }

        @Test
        @DisplayName("使用where关键字指定多个上界")
        internal fun test200() {
            fun <T> greeting(obj: T): String
                    where T : Parent1,
                          T : Parent2 {
                return obj.getName1() + obj.getName2()
            }

            class Foo : Parent1, Parent2
            assertThat(greeting(Foo())).isEqualTo("parent1parent2")
        }
    }

    @Nested
    @DisplayName("类型擦除")
    inner class TypeErasureSpec {
        @Test
        @DisplayName("运行时类型会被擦除")
        internal fun test100() {
            val any: Any? = listOf(0)
            // correct
            assertThat(any is List<*>).isTrue()
            // incorrect
//            any is List
//            any is List<String>
        }

        @Test
        @DisplayName("如果是已知类型，则可以检查运行时类型")
        internal fun test200() {
            fun isIntList(c: Collection<Int>) = c is List<Int>
            assertThat(isIntList(listOf())).isTrue()
            assertThat(isIntList(setOf())).isFalse()
        }

        @Test
        @DisplayName("即便有错误的类型参数，显式转换也不会立即失败")
        internal fun test300() {
            val intList: List<Int> = listOf("a", "b", "c") as List<Int>
            assertThat(intList.size).isEqualTo(3)
            assertThatThrownBy { intList[0] + 1 }
                    .hasSameClassAs(ClassCastException())
        }

        private inline fun <reified T> isA(value: Any) = value is T
        @Test
        @DisplayName("内联函数可以实化类型参数")
        internal fun test400() {
            // incorrect
//            fun <T> isA(value: Any) = value is T
            assertThat(isA<String>("")).isTrue()
            assertThat(isA<Int>("")).isFalse()
        }
    }

    /**
     * kotlin允许声明处型变与使用点型变（类型投影）
     * @see cn.staynoob.trap.java.basic.GenericSpec.test200
     */
    @Nested
    @DisplayName("型变")
    inner class VarianceSpec {
        /**
         * 允许生产和消费对应的类型参数
         * Invariant<Int> 不是 Invariant<Number> 的子类
         * Invariant<Number> 也不是 Invariant<Int> 的子类
         */
        @Test
        @DisplayName("不变类型参数 invariant")
        internal fun test100() {
            class Invariant<T> {
                fun produce(): T? = null
                fun consume(value: T) = Unit
            }
            // incorrect
//            val v1: Invariant<Int> = Invariant<Number>()
//            val v2: Invariant<Number> = Invariant<Int>()
        }

        /**
         * 协变类型参数只允许生产，不允许消费
         * Producer<Number> 是 Producer<Int> 的父类型
         */
        @Test
        @DisplayName("协变类型参数 covariant")
        internal fun test200() {
            class Producer<out T> {
                // correct
                fun produce(): T? = null

                private fun internalConsume(value: T) = Unit
                // incorrect
//                fun consume(value: T) = Unit
            }
            // correct
            val v1: Producer<Number> = Producer<Int>()
            // incorrect
//            val v2: Producer<Int> = Producer<Number>()
        }

        /**
         * 逆变类型参数只允许消费，不允许生产
         * Consumer<Int> 是 Consumer<Number> 的父类型
         */
        @Test
        @DisplayName("逆变类型参数 contravariant")
        internal fun test300() {
            class Consumer<in T> {
                // correct
                fun consume(value: T) = Unit

                private fun internalProduce(): T? = null
                // incorrect
//                fun produce(): T? = null
            }
            // correct
            val v2: Consumer<Int> = Consumer<Number>()
            // incorrect
//            val v1: Consumer<Number> = Consumer<Int>()
        }

        @Test
        @DisplayName("允许在构造方法中消费out参数（不能使用var关键字，因为setter消费类型参数）")
        internal fun test400() {
            // correct
            class Producer1<out T>(value: T)

            class Producer2<out T>(val value: T)
            // incorrect
//            class Producer3<out T>(var value: T)
        }

        @DisplayName("类型投影")
        @Nested
        inner class TypeProjectionSpec {
            /**
             * <out T>等价于<? extends T>
             * <in T>等价于<? super T>
             * @see cn.staynoob.trap.java.basic.GenericSpec#copyData
             */
            @Test
            @DisplayName("使用点变型(Use-site variance)")
            internal fun test100() {
                fun <T> copyData(
                        src: MutableCollection<out T>,
                        dest: MutableCollection<in T>
                ) {
                    dest.addAll(src)
                }

                val numList = ArrayList<Number>()
                val intList = mutableListOf(1, 2, 3)
                copyData(intList, numList)
            }

            @Nested
            @DisplayName("星号投影")
            inner class StarProjectionSpec {

                @Test
                @DisplayName("星号投影相当于java中的<?>")
                internal fun test50() {
                    val list1: MutableList<Any?> = mutableListOf(1, "1")
                    val list2: MutableList<*> = list1
                    // correct
                    list1.add(null)
                    // error
//                    list2.add(null)
                }

                /**
                 * Producer<*> 等价于 Producer<out Number>
                 */
                @Test
                @DisplayName("协变类型参数星号投影后可以安全访问上界")
                internal fun test100() {
                    class Producer<out T : Number>(val value: T)

                    fun fn(producer: Producer<*>) = producer.value.toInt()
                }

                /**
                 * Consumer<*> 等价于 Consumer<in Nothing>
                 */
                @Test
                @DisplayName("逆变类型参数星号投影后不允许消费")
                internal fun test200() {
                    class Consumer<in T : Number> {
                        fun consume(value: T) = Unit
                    }

                    fun fn1(consumer: Consumer<Int>) = consumer.consume(1)
//                    fun fn2(consumer: Consumer<*>) = consumer.consume(1)
                }

                /**
                 * Invariant<*> 在生产时相当于Invariant<out Number>
                 * Invariant<*> 在消费时相当于Invariant<in Nothing>
                 */
                @Test
                @DisplayName("不变类型参数星号投影")
                internal fun test300() {
                    class Invariant<T : Number>(var value: T)

                    fun fn(invariant: Invariant<*>) {
                        // incorrect
//                        invariant.value = 1
                        // correct
                        invariant.value.toInt()
                    }
                }
            }
        }


    }

}