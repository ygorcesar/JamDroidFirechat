package com.ygorcesar.jamdroidfirechat.utils

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging

fun FirebaseMessaging.subscribeToGlobal() = this.subscribeToTopic(ConstantsFirebase.FIREBASE_TOPIC_CHAT_GLOBAL)

fun FirebaseMessaging.unsubscribeFromGlobal() = this.unsubscribeFromTopic(ConstantsFirebase.FIREBASE_TOPIC_CHAT_GLOBAL)

fun FirebaseDatabase.usersReference(): DatabaseReference = this.getReference(ConstantsFirebase.FIREBASE_LOCATION_USERS)

fun FirebaseDatabase.userReference(userEmail: String?): DatabaseReference = this.getReference(ConstantsFirebase.FIREBASE_LOCATION_USERS).child(userEmail?.encodeEmail() ?: "invalid_email")

fun FirebaseDatabase.userDeviceIdReference(userEmail: String?): DatabaseReference = this.userReference(userEmail).child(ConstantsFirebase.FIREBASE_PROPERTY_USER_DEVICE_ID)

fun FirebaseDatabase.userFriendReference(userEmail: String, friendEmail: String): DatabaseReference = this.getReference(ConstantsFirebase.FIREBASE_LOCATION_USER_FRIENDS)
        .child(userEmail.encodeEmail())
        .child(friendEmail.encodeEmail())


fun FirebaseDatabase.chatReference() = this.getReference(ConstantsFirebase.FIREBASE_LOCATION_CHAT)

fun String.encodeEmail() = this.replace(".", ",")

fun String.decodeEmail() = this.replace(",", ".")