package org.setu.electriccar.stores

import org.setu.electriccar.models.User

interface UserStore {
    fun findAll(): List<User>
    fun findOne(id: Long): User?
    fun findByEmail(email: String): User?
    fun create(user: User)
    fun update(user: User): Boolean
    fun delete(id: Long): Boolean
}