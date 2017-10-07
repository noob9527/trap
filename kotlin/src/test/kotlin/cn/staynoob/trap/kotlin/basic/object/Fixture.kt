package cn.staynoob.trap.kotlin.basic.`object`

object ObjectExpression {
    val foo = "foo"
    fun fn() = "fn"
}

class ClassWithCompanion {
    companion object {
        val foo = "foo"
        fun fn() = "fn"
    }
}

class ClassWithNamingCompanion {
    companion object WithName{
        val foo = "foo"
        fun fn() = "fn"
    }
}

class UserFactory private constructor(val account: String) {
    companion object {
        fun createUserByName(name: String) = UserFactory(name)
        fun createUserByPhone(phone: String) = UserFactory(phone)
        fun createUserByEmail(email: String) = UserFactory(email)
    }
}