package com.ygorcesar.jamdroidfirechat.data.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey


@Entity(tableName = "chat_reference")
data class ChatReference(@PrimaryKey var userEmail: String, var chatKey: String)