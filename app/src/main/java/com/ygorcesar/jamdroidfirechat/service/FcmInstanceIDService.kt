package com.ygorcesar.jamdroidfirechat.service

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import com.ygorcesar.jamdroidfirechat.extensions.userDeviceIdReference

class FcmInstanceIDService : FirebaseInstanceIdService() {

    override fun onTokenRefresh() {
        super.onTokenRefresh()
        val refreshedToken = FirebaseInstanceId.getInstance().token
        updateUserRegistrationToken(refreshedToken)
    }

    private fun updateUserRegistrationToken(token: String?) {
        FirebaseAuth.getInstance().currentUser?.let { auth ->
            FirebaseDatabase.getInstance().userDeviceIdReference(auth.email).setValue(token)
        }
    }
}
