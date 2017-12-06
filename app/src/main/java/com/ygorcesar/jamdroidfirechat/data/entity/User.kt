package com.ygorcesar.jamdroidfirechat.data.entity

data class User(var fcmUserDeviceId: String = "", var name: String = "", var email: String = "", var photoUrl: String = "",
                var timestampJoined: HashMap<String, Any> = hashMapOf())