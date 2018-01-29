package com.ygorcesar.jamdroidfirechat.extensions

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.ygorcesar.jamdroidfirechat.data.entity.ChatReference
import com.ygorcesar.jamdroidfirechat.utils.ConstantsFirebase

fun FirebaseMessaging.subscribeToGlobal() = this.subscribeToTopic(ConstantsFirebase.Topics.CHAT_GLOBAL)

fun FirebaseMessaging.unsubscribeFromGlobal() = this.unsubscribeFromTopic(ConstantsFirebase.Topics.CHAT_GLOBAL)

fun FirebaseDatabase.usersReference(): DatabaseReference = this.getReference(ConstantsFirebase.Location.USERS)

fun FirebaseDatabase.userReference(userEmail: String?): DatabaseReference = this.getReference(ConstantsFirebase.Location.USERS).child(userEmail?.encodeEmail() ?: "invalid_email")

fun FirebaseDatabase.userDeviceIdReference(userEmail: String?): DatabaseReference = this.userReference(userEmail).child(ConstantsFirebase.FIREBASE_PROPERTY_USER_DEVICE_ID)

fun FirebaseDatabase.userFriendReference(userEmail: String, friendEmail: String): DatabaseReference = this.getReference(ConstantsFirebase.Location.USER_FRIENDS)
        .child(userEmail.encodeEmail())
        .child(friendEmail.encodeEmail())

fun FirebaseStorage.imageFromChatReference(chatReference: ChatReference, messageKey: String): StorageReference =
        this.getReference(ConstantsFirebase.Location.STORAGE_CHAT_IMAGES_LOCATION)
                .child(chatReference.chatKey)
                .child("IMG_$messageKey.jpg")


fun FirebaseDatabase.baseChatReference() = this.getReference(ConstantsFirebase.Location.CHAT)

fun FirebaseDatabase.chatRefrence(chatKey: String) = this.baseChatReference().child(chatKey)

fun FirebaseDatabase.messageRefrence(chatKey: String, messageKey: String) = this.chatRefrence(chatKey).child(messageKey)

fun FirebaseDatabase.messageImgReference(chatKey: String, messageKey: String) =
        this.messageRefrence(chatKey, messageKey)
                .child(ConstantsFirebase.FIREBASE_PROPERTY_MESSAGE_IMG)

fun String.encodeEmail() = this.replace(".", ",")

fun String.decodeEmail() = this.replace(",", "")