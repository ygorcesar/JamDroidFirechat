package com.ygorcesar.jamdroidfirechat.ui.messages

import android.app.Application
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.ygorcesar.jamdroidfirechat.data.entity.ChatReference

class MessagesViewModelFactory(private val chatReference: ChatReference, val context: Application, val userEmail: String)
    : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MessagesViewModel::class.java)) {
            return MessagesViewModel(context, chatReference, userEmail) as T
        }
        throw IllegalArgumentException("Unknow ViewModel Class")
    }
}