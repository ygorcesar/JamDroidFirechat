package com.ygorcesar.jamdroidfirechat.data.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "users")
data class User(@Ignore var fcmUserDeviceId: String = "", var name: String = "", @PrimaryKey var email: String = "", var photoUrl: String = "",
                @Ignore var timestampJoined: HashMap<String, Any> = hashMapOf())