package com.ygorcesar.jamdroidfirechat.databinding

import android.databinding.BaseObservable
import com.ygorcesar.jamdroidfirechat.data.entity.User

abstract class BaseBinding(user: User) : BaseObservable() {
    var user: User = user
        set(value) {
            field = value
            notifyChange()
        }

    fun getUserName() = user.name

    fun getUserEmail() = user.email

    fun getUserPhotoUrl() = user.photoUrl
}