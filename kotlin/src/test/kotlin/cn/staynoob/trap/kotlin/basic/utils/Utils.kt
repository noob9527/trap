package cn.staynoob.trap.kotlin.basic.utils

import org.mockito.Mockito

/**
 * fix mockito kotlin issue
 * @see <a href="https://stackoverflow.com/questions/30305217/is-it-possible-to-use-mockito-in-kotlin">Is it possible to use Mockito in Kotlin?</a>
 */
fun <T> any(): T = Mockito.any<T>()
fun <T> eq(value: T): T = Mockito.eq(value) ?: value