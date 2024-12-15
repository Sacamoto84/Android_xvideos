package com.client.xvideos.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

@Entity
data class Crime(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    var date: Date = Date(),
    var title: String = "",
    var isSolved: Boolean = false,
)