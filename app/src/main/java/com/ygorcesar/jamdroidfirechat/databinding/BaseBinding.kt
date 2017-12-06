package com.ygorcesar.jamdroidfirechat.databinding

import android.databinding.BaseObservable
import com.ygorcesar.jamdroidfirechat.data.entity.User

abstract class BaseBinding(user: User) : BaseObservable() {
    var user: User = user
        set(value) {
            notifyChange()
        }

    fun getName() = user.name

    fun getEmail() = user.email

    fun getPhotoUrl() = user.photoUrl
}