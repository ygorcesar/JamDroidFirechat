package com.ygorcesar.jamdroidfirechat.ui.login

import android.arch.lifecycle.ViewModel
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.firebase.auth.*
import com.google.firebase.database.ServerValue
import com.google.firebase.iid.FirebaseInstanceId
import com.ygorcesar.jamdroidfirechat.data.entity.User
import com.ygorcesar.jamdroidfirechat.data.repository.remote.UserDao
import com.ygorcesar.jamdroidfirechat.utils.ConstantsFirebase
import org.jetbrains.anko.AnkoLogger

class LoginViewModel : ViewModel(), AnkoLogger {
    private val userDao: UserDao by lazy { UserDao() }

    fun authWithGoogle(result: GoogleSignInResult, authOnFirebase: (credentials: AuthCredential) -> Unit, onUnSuccess: () -> Unit) {
        if (result.isSuccess) {
            result.signInAccount?.let { account ->
                GoogleAuthProvider.getCredential(account.idToken, null)?.let { authOnFirebase(it) }
            }
        } else {
            onUnSuccess()
        }
    }

    fun authWithFacebook(result: LoginResult?, authOnFirebase: (credentials: AuthCredential) -> Unit) {
        result?.let {
            FacebookAuthProvider.getCredential(it.accessToken.token)?.let { authOnFirebase(it) }
        }
    }

    fun userAuthenticated(firebaseUser: FirebaseUser) = userDao.fetchUser(firebaseUser.email)

    fun createUser(firebaseUser: FirebaseUser) {
        firebaseUser.email?.let { email ->
            val deviceId = FirebaseInstanceId.getInstance().token ?: ""
            val user = firebaseUser.run {
                val joined = hashMapOf<String, Any>(ConstantsFirebase.FIREBASE_PROPERTY_TIMESTAMP to ServerValue.TIMESTAMP)
                User(deviceId, displayName ?: "", email, photoUrl.toString(), joined)
            }
            userDao.createOrUpdateUser(user).subscribe()
        }
    }

    fun updateUserDeviceId(user: User) {
        if (user.email.isNotBlank() && user.name.isNotEmpty()) {
            val deviceId = FirebaseInstanceId.getInstance().token
            userDao.updateUserDeviceId(deviceId, user).subscribe()
        } else {
            FirebaseAuth.getInstance().currentUser?.let { createUser(it) }
        }
    }
}