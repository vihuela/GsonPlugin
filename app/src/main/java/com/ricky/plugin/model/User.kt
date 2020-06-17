package com.ricky.plugin.model

import com.google.gson.JsonElement

data class User(
    val name: String = "",
    val age: Int = 12,
    val sex: Int = 1,
    val birthday: String = "",
    val habits: List<Habit> = listOf(),
    val place: String = "",
    val marry: Boolean = true,
    val more: More = More(),
    val mark:JsonElement
) {


    class Habit {
        var name = ""

        override fun toString(): String {
            return "Habit(name='$name')"
        }

    }

    class More {
        var name = ""
        override fun toString(): String {
            return "More(name='$name')"
        }

    }

    override fun toString(): String {
        return "User(name='$name', age=$age, sex=$sex, birthday='$birthday', habits=$habits, place='$place', marry=$marry, more=$more)"
    }


}