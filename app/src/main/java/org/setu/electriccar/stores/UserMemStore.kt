package org.setu.electriccar.stores

import org.setu.electriccar.models.User
import java.util.concurrent.atomic.AtomicLong

class UserMemStore : UserStore {

    private val users = ArrayList<User>()
    private val lastId = AtomicLong()

    override fun findAll(): List<User> {
        return users
    }

    override fun findOne(id: Long): User? {
        return users.find { user -> user.userId == id }
    }

    override fun findByEmail(email: String): User? {
        return users.find { user -> user.email.equals(email, ignoreCase = true) }
    }

    override fun create(user: User) {
        user.userId = lastId.incrementAndGet()
        users.add(user)
    }

    override fun update(user: User): Boolean {
        val found = findOne(user.userId)

        if (found != null) {
            found.name = user.name
            found.email = user.email
            found.password = user.password
            return true
        }

        return false
    }

    override fun delete(id: Long): Boolean {
        val found = findOne(id)

        if (found != null) {
            users.remove(found)
            return true
        }

        return false
    }
}