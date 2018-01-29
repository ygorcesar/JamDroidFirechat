package com.ygorcesar.jamdroidfirechat.ui

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.messaging.FirebaseMessaging
import com.ygorcesar.jamdroidfirechat.R
import com.ygorcesar.jamdroidfirechat.extensions.unsubscribeFromGlobal
import com.ygorcesar.jamdroidfirechat.ui.login.LoginActivity
import com.ygorcesar.jamdroidfirechat.utils.Constants
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.debug

abstract class BaseActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener, AnkoLogger {
    var loginProvider: String? = null
    var userEmail: String? = null
    val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    val firebaseAuthListener: FirebaseAuth.AuthStateListener by lazy {
        FirebaseAuth.AuthStateListener { if (it.currentUser == null) takeUserToLoginScreenOnUnAuth() }
    }
    val googleApiClient: GoogleApiClient by lazy {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_web_oauth_client_id))
                .requestEmail()
                .build()

        GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedInstanceState?.apply {
            userEmail = Constants.KEY_USER_EMAIL
            loginProvider = Constants.KEY_PROVIDER
        }

        if (!googleApiClient.isConnecting) googleApiClient.connect()

        /* Get mEncodedEmail and mProvider from SharedPreferences, use null as default value */
        PreferenceManager.getDefaultSharedPreferences(applicationContext).apply {
            userEmail = getString(Constants.KEY_USER_EMAIL, null)
            loginProvider = getString(Constants.KEY_PROVIDER, null)
        }

        if (this !is LoginActivity) firebaseAuth.addAuthStateListener(firebaseAuthListener)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.apply {
            putString(Constants.KEY_USER_EMAIL, userEmail)
            putString(Constants.KEY_PROVIDER, loginProvider)
        }
    }


    /**
     * Desconecta a conta google do app
     */
    fun logout() {
        firebaseAuth.signOut()
        loginProvider?.let {
            when (it) {
                GoogleAuthProvider.PROVIDER_ID -> Auth.GoogleSignInApi.signOut(googleApiClient)
                FacebookAuthProvider.PROVIDER_ID -> LoginManager.getInstance().logOut()
                else -> debug { "UnknowProvide" }
            }
        }
    }

    fun revokeAccess() {
        firebaseAuth.signOut()
        loginProvider?.let {
            when (it) {
                GoogleAuthProvider.PROVIDER_ID -> Auth.GoogleSignInApi.revokeAccess(googleApiClient)
                FacebookAuthProvider.PROVIDER_ID -> LoginManager.getInstance().logOut()
                else -> debug { "UnknowProvider" }
            }
        }
    }

    /**
     * Move o usuário para a tela de Login ao inicializar o app
     */
    private fun takeUserToLoginScreenOnUnAuth() {
        FirebaseMessaging.getInstance().unsubscribeFromGlobal()
        val intent = Intent(this, LoginActivity::class.java).apply {
            this.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {}
}