package com.ygorcesar.jamdroidfirechat.ui.messages

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.databinding.Bindable
import android.databinding.Observable
import android.databinding.PropertyChangeRegistry
import android.net.Uri
import android.util.Log
import android.view.View
import com.ygorcesar.jamdroidfirechat.BR
import com.ygorcesar.jamdroidfirechat.R
import com.ygorcesar.jamdroidfirechat.data.entity.ChatReference
import com.ygorcesar.jamdroidfirechat.data.entity.MapLocation
import com.ygorcesar.jamdroidfirechat.data.entity.Message
import com.ygorcesar.jamdroidfirechat.data.entity.User
import com.ygorcesar.jamdroidfirechat.data.repository.UserRepository
import com.ygorcesar.jamdroidfirechat.data.repository.local.AppDatabase
import com.ygorcesar.jamdroidfirechat.data.repository.remote.MessageDao
import com.ygorcesar.jamdroidfirechat.extensions.getBitmapBytes
import com.ygorcesar.jamdroidfirechat.utils.ConstantsFirebase
import durdinapps.rxfirebase2.RxFirebaseChildEvent

class MessagesViewModel(context: Application, val chatReference: ChatReference, val userEmail: String) : AndroidViewModel(context), Observable {
    private val repository: UserRepository by lazy { UserRepository(AppDatabase.getInstance(this.getApplication<Application>().applicationContext)) }
    private val messageDao: MessageDao by lazy { MessageDao() }
    private val propertyChanged: PropertyChangeRegistry by lazy { PropertyChangeRegistry() }
    lateinit var messagesContract: MessagesContract
    var messageText: String = ""
        @Bindable get
        set(value) {
            field = value
            propertyChanged.apply {
                notifyChange(this@MessagesViewModel, BR.messageText)
                notifyChange(this@MessagesViewModel, BR.messageNotEmpty)
            }
        }

    var messages: RxFirebaseChildEvent<Message>? = null
        @Bindable get
        set(value) {
            field = value
            propertyChanged.notifyChange(this@MessagesViewModel, BR.messages)
        }

    fun invalidateLastMessage() {
        messages = null
    }

    fun fetchMessages(chatKey: String) = messageDao.fetchMessages(chatKey)

    fun fetchUserOfMessage(email: String, onFetch: (user: User) -> Unit, onError: (err: Throwable) -> Unit) {
        repository.fetchUser(email, onFetch, onError)
    }

    fun onViewClick(view: View) {
        if (view.id == R.id.iv_attachment) {
            messagesContract.toggleViewAttachment()
        } else {
            messagesContract.toggleViewAttachment(true)
            when (view.id) {
                R.id.fab -> sendMessageText()
                R.id.iv_camera -> messagesContract.showImagePicker()
                R.id.iv_attach_camera -> messagesContract.initializeCameraIntent()
                R.id.iv_attach_image -> messagesContract.initializeGalleryIntent()
                R.id.iv_attach_location -> messagesContract.initializeMapIntent()
                else -> print("Do nothing")
            }
        }
    }

    @Bindable
    fun isMessageNotEmpty() = messageText.isNotEmpty()

    private fun sendMessage(message: Message, bytes: ByteArray? = null) {
        message.apply {
            email = userEmail
            friendEmail = chatReference.userEmail
        }
        messagesContract.toggleViewAttachment(true)
        messageText = ""

        when (message.type) {
            ConstantsFirebase.MessageType.IMAGE -> {
                val messageKey = messageDao.sendImageMessage(message, chatReference.chatKey)
                bytes?.let {
                    messageDao.uploadImage(chatReference, messageKey, bytes).subscribe({
                        messageDao.updateMessageImage(chatReference.chatKey, messageKey, it.downloadUrl.toString()).subscribe()
                    }, {
                        Log.e("MessagesViewModel", "Error on upload image", it)
                    }, {
                        Log.i("MessagesViewModel", "Upload successful!!!")
                    })
                }
            }
            else -> messageDao.sendMessage(message, chatReference.chatKey).subscribe()
        }
        propertyChanged.notifyChange(this@MessagesViewModel, BR.messageText)
    }

    private fun sendMessageText() = sendMessage(Message(message = messageText))

    fun sendMessageLocation(location: MapLocation) = sendMessage(Message(type = ConstantsFirebase.MessageType.LOCATION, mapLocation = location))

    fun sendMessageImage(uri: Uri?) {
        uri?.getBitmapBytes(getApplication<Application>().applicationContext)?.let { bytes ->
            sendMessage(Message(type = ConstantsFirebase.MessageType.IMAGE), bytes)
        }
    }

    fun deleteMessage(messageKey: String) = messageDao.deleteMessage(chatReference.chatKey, messageKey)

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) = propertyChanged.add(callback)

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) = propertyChanged.remove(callback)
}