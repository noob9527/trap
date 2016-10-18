package cn.staynoob.trap.kotlin

import org.assertj.core.api.Assertions.*
import org.junit.Test

class PropertySpec {
    @Test
    fun customizeGetterSetter(){
        class Person(var firstName: String, var lastName: String){
            var fullName:String
                get(){
                    return "$firstName-$lastName"
                }
                set(value:String){
                    val list = value.split("-")
                    firstName = list[0]
                    lastName = list[1]
                }
        }
        val person = Person("yan", "xu")
        assertThat(person.fullName).isEqualTo("yan-xu")
        person.fullName ="foo-bar"
        assertThat(person.firstName).isEqualTo("foo")
        assertThat(person.lastName).isEqualTo("bar")
    }

    private var StringBuilder.lastChar: Char
        get() = get(length - 1)
        set(value: Char) {
            setCharAt(length - 1, value)
        }

    @Test
    fun extensionProperty(){
        val sb = StringBuilder("kotlin?")
        assertThat(sb.lastChar).isEqualTo('?')
        sb.lastChar = '!'
        assertThat(sb.toString()).isEqualTo("kotlin!")
    }
}