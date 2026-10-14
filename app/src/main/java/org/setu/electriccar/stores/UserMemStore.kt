package org.setu.electriccar.stores

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.setu.electriccar.models.User
import java.io.File

class UserMemStore(context: Context) {

    private val file = File(context.filesDir, "users.json")
    private var users = ArrayList<User>()

    init {
        if (file.exists()) {
            val type = object : TypeToken<ArrayList<User>>() {}.type
            users = Gson().fromJson(file.readText(), type)
        }
    }

    fun save() {
        file.writeText(Gson().toJson(users))
    }

    fun findAll(): List<User> {
        return users
    }

    fun findByEmail(email: String): User? {
        return users.find { it.email.equals(email, ignoreCase = true) }
    }

    fun create(user: User) {
        user.userId = (users.maxOfOrNull { it.userId } ?: 0L) + 1
        users.add(user)
        save()
    }
}