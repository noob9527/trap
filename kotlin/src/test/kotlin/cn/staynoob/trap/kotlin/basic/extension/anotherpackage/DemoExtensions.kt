package cn.staynoob.trap.kotlin.basic.extension.anotherpackage

import cn.staynoob.trap.kotlin.basic.extension.Fixture

fun Fixture.extendFunction() = this.ownFunction()

var Fixture.extendProperty: String
    get() = this.ownProperty
    set(value) {
        this.ownProperty = value
    }
