package com.ricky.plugin.gson

import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.ricky.plugin.model.User
import com.ricky.plugin_gson_sdk.GsonPluginUtil
import org.json.JSONObject

class GsonUnitTest {
    private lateinit var mockJson: String
    private val mGson = GsonBuilder().create()

    init {
        GsonPluginUtil.setListener { exception, invokeStack ->
            Log.e("gson:", exception)
        }

        val mockUser = User(
            name = "ricky",
            age = 12,
            sex = 0,
            birthday = "19920217",
            habits = listOf(
                User.Habit().apply { name = "game" },
                User.Habit().apply { name = "movie" }),
            place = "xiapu",
            marry = true,
            more = User.More().apply { name = "more" },
            mark = JsonObject()
        )

        mockJson = mGson.toJson(mockUser)
    }

    //模拟后台：Int 返回 String
    fun mockIntReturnString() {
        val json = kotlin.run {
            val j = JSONObject(mockJson)
            j.put("sex", "null")
            j.toString()
        }
        val user = mGson.fromJson(json, User::class.java)
        Log.i("gson:", user.toString())
    }

    //模拟后台：Boolean 返回 Int
    fun mockBooleanReturnInt() {
        val json = kotlin.run {
            val j = JSONObject(mockJson)
            j.put("marry", 0)
            j.toString()
        }
        val user = mGson.fromJson(json, User::class.java)
        Log.i("gson:", user.toString())
    }

    //模拟后台：Array 返回 String
    fun mockArrayReturnString() {
        val json = kotlin.run {
            val j = JSONObject(mockJson)
            j.put("habits", "")
            j.toString()
        }
        val user = mGson.fromJson(json, User::class.java)
        Log.i("gson:", user.toString())
    }


    //模拟后台：Object 返回 String
    fun mockObjectReturnString() {
        val json = kotlin.run {
            val j = JSONObject(mockJson)
            j.put("more", "")
            j.toString()
        }
        val user = mGson.fromJson(json, User::class.java)
        Log.i("gson:", user.toString())
    }

    //模拟后台：String 返回 其它
    fun mockStringReturnArray() {
        val json = kotlin.run {
            val j = JSONObject(mockJson)
            j.put("name", mockJson)
            j.toString()
        }
        val user = mGson.fromJson(json, User::class.java)
        Log.i("gson:", user.toString())
    }

}