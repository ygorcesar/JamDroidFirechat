package com.ygorcesar.jamdroidfirechat.ui.login

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.databinding.Bindable
import android.databinding.Observable
import android.databinding.PropertyChangeRegistry
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.firebase.auth.*
import com.google.firebase.database.ServerValue
import com.google.firebase.iid.FirebaseInstanceId
import com.ygorcesar.jamdroidfirechat.BR
import com.ygorcesar.jamdroidfirechat.R
import com.ygorcesar.jamdroidfirechat.data.entity.User
import com.ygorcesar.jamdroidfirechat.data.repository.UserRepository
import com.ygorcesar.jamdroidfirechat.data.repository.local.AppDatabase
import com.ygorcesar.jamdroidfirechat.utils.ConstantsFirebase
import org.jetbrains.anko.AnkoLogger

class LoginViewModel(context: Application) : AndroidViewModel(context), AnkoLogger, Observable {
    private val propertyChanged: PropertyChangeRegistry by lazy { PropertyChangeRegistry() }
    private val repository: UserRepository by lazy { UserRepository(AppDatabase.getInstance(this.getApplication<Application>().applicationContext)) }

    var authProvideString: Int = R.string.authenticating
        @Bindable get
        set(value) {
            field = value
            propertyChanged.notifyChange(this, BR.authProvideString)
        }


    var inProgress: Boolean = false
        @Bindable get
        set(value) {
            field = value
            propertyChanged.notifyChange(this, BR.inProgress)
        }

    fun authWithGoogle(result: GoogleSignInResult, authOnFirebase: (credentials: AuthCredential) -> Unit) {
        if (result.isSuccess) {
            result.signInAccount?.let { account ->
                GoogleAuthProvider.getCredential(account.idToken, null)?.let { authOnFirebase(it) }
            }
        } else {
            hideProgress()
        }
    }

    fun authWithFacebook(result: LoginResult?, authOnFirebase: (credentials: AuthCredential) -> Unit) {
        showProgress(FacebookAuthProvider.PROVIDER_ID)
        result?.let {
            FacebookAuthProvider.getCredential(it.accessToken.token)?.let { authOnFirebase(it) }
        }
    }

    fun userAuthenticated(firebaseUser: FirebaseUser) = repository.mUserRemoteDao.fetchUser(firebaseUser.email)

    fun createUser(firebaseUser: FirebaseUser) {
        firebaseUser.email?.let { email ->
            val deviceId = FirebaseInstanceId.getInstance().token ?: ""
            val user = firebaseUser.run {
                val joined = hashMapOf<String, Any>(ConstantsFirebase.FIREBASE_PROPERTY_TIMESTAMP to ServerValue.TIMESTAMP)
                User(deviceId, displayName ?: "", email, photoUrl.toString(), joined)
            }
            repository.insertUsers(user)
            repository.mUserRemoteDao.createOrUpdateUser(user).subscribe()
        }
    }

    fun updateUserDeviceId(user: User) {
        if (user.email.isNotBlank() && user.name.isNotEmpty()) {
            val deviceId = FirebaseInstanceId.getInstance().token
            repository.mUserRemoteDao.updateUserDeviceId(deviceId, user).subscribe()
        } else {
            FirebaseAuth.getInstance().currentUser?.let { createUser(it) }
        }
    }

    fun showProgress(provider: String) {
        inProgress = true
        authProvideString = when (provider) {
            GoogleAuthProvider.PROVIDER_ID -> R.string.authenticating_with_google
            FacebookAuthProvider.PROVIDER_ID -> R.string.authenticating_with_facebook
            else -> R.string.authenticating
        }
    }

    fun hideProgress() {
        inProgress = false
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) = propertyChanged.add(callback)

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) = propertyChanged.remove(callback)
}