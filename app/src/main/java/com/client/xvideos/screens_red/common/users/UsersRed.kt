package com.client.xvideos.screens_red.common.users

import com.client.xvideos.feature.redgifs.types.UserInfo

object UsersRed {

    val listAllUsers = mutableListOf<UserInfo>()

    fun findUser(username: String): UserInfo? {
        return listAllUsers.find { it.username == username }
    }
}