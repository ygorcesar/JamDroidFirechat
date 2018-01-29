package com.ygorcesar.jamdroidfirechat.data.repository.remote

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.ygorcesar.jamdroidfirechat.data.entity.ChatReference
import com.ygorcesar.jamdroidfirechat.data.entity.Message
import com.ygorcesar.jamdroidfirechat.extensions.chatRefrence
import com.ygorcesar.jamdroidfirechat.extensions.imageFromChatReference
import com.ygorcesar.jamdroidfirechat.extensions.messageImgReference
import com.ygorcesar.jamdroidfirechat.extensions.messageRefrence
import durdinapps.rxfirebase2.RxFirebaseChildEvent
import durdinapps.rxfirebase2.RxFirebaseDatabase
import durdinapps.rxfirebase2.RxFirebaseStorage
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable

class MessageDao {

    fun fetchMessages(chatKey: String): Observable<RxFirebaseChildEvent<Message>> {
        val messagesReference = FirebaseDatabase.getInstance().chatRefrence(chatKey)
        return RxFirebaseDatabase.observeChildEvent(messagesReference, Message::class.java).toObservable()
    }

    fun sendMessage(message: Message, chatKey: String): Completable {
        val messageReference = FirebaseDatabase.getInstance().chatRefrence(chatKey).push()
        return RxFirebaseDatabase.setValue(messageReference, message)
    }

    fun deleteMessage(chatKey: String, messageKey: String) {
        val messageReference = FirebaseDatabase.getInstance().messageRefrence(chatKey, messageKey)
        messageReference.removeValue()
    }

    fun sendImageMessage(message: Message, chatKey: String): String {
        val messageReference = FirebaseDatabase.getInstance().chatRefrence(chatKey).push()
        val messageKey = messageReference.key
        RxFirebaseDatabase.setValue(messageReference, message).subscribe()
        return messageKey
    }

    fun uploadImage(chatReference: ChatReference, messageKey: String, bytes: ByteArray): Maybe<UploadTask.TaskSnapshot> {
        val storageRef = FirebaseStorage.getInstance().imageFromChatReference(chatReference, messageKey)
        return RxFirebaseStorage.putBytes(storageRef, bytes)
    }

    fun updateMessageImage(chatKey: String, messageKey: String, imgUrl: String): Completable {
        val messageImgReference = FirebaseDatabase.getInstance().messageImgReference(chatKey, messageKey)
        return RxFirebaseDatabase.setValue(messageImgReference, imgUrl)
    }
}