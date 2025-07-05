package com.client.xvideos.redgifs.common.users

import com.client.xvideos.redgifs.network.types.UserInfo
import java.util.concurrent.ConcurrentHashMap

object UsersRed {

    private val usersMap = ConcurrentHashMap<String, UserInfo>()

    /** Получить "снимок" всех пользователей. Быстрое чтение. */
    val listAllUsers: List<UserInfo>
        get() = usersMap.values.toList()

    /** Добавить пользователя, исключая дубликаты по username. */
    fun addUser(user: UserInfo) {
        usersMap[user.username] = user
    }

    /** Найти пользователя по username. */
    fun findUser(username: String): UserInfo? {
        return usersMap[username]
    }

    /** Удалить пользователя. */
    fun removeUser(username: String) {
        usersMap.remove(username)
    }

    /** Очистить всех пользователей. */
    fun clear() {
        usersMap.clear()
    }
}
