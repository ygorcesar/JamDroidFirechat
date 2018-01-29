package com.ygorcesar.jamdroidfirechat.data.entity

import com.google.firebase.database.ServerValue
import com.ygorcesar.jamdroidfirechat.utils.ConstantsFirebase

data class Message(var email: String = "", var friendEmail: String = "", var message: String = "", var imgUrl: String = "", var type: Int = ConstantsFirebase.MessageType.TEXT,
                   var time: HashMap<String, Any> = hashMapOf(ConstantsFirebase.FIREBASE_PROPERTY_MESSAGE_TIME_SENDED to ServerValue.TIMESTAMP),
                   var mapLocation: MapLocation? = null)