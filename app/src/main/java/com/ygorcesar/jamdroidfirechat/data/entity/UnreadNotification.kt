package com.ygorcesar.jamdroidfirechat.data.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "unread_notifications")
data class UnreadNotification(@PrimaryKey(autoGenerate = true) var id: Long? = null, var title: String, var body: String, var email: String?, var chatRef: String?)