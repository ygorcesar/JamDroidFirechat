package com.ygorcesar.jamdroidfirechat.ui.login

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.common.ConnectionResult
import com.google.firebase.auth.*
import com.google.firebase.messaging.FirebaseMessaging
import com.ygorcesar.jamdroidfirechat.databinding.LoginActivityBinding
import com.ygorcesar.jamdroidfirechat.extensions.provideViewModel
import com.ygorcesar.jamdroidfirechat.extensions.subscribeToGlobal
import com.ygorcesar.jamdroidfirechat.ui.BaseActivity
import com.ygorcesar.jamdroidfirechat.ui.users.UsersActivity
import com.ygorcesar.jamdroidfirechat.utils.Constants
import durdinapps.rxfirebase2.RxFirebaseAuth
import kotlinx.android.synthetic.main.login_activity.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick

class LoginActivity : BaseActivity() {
    private val binding: LoginActivityBinding by lazy { LoginActivityBinding.inflate(layoutInflater) }
    private val viewModel: LoginViewModel by lazy { provideViewModel<LoginViewModel>() }
    private val fbCallbackManager: CallbackManager by lazy { CallbackManager.Factory.create() }
    private val authStateListener: FirebaseAuth.AuthStateListener by lazy {
        FirebaseAuth.AuthStateListener { it.currentUser?.let { userAuthenticated(it) } }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.viewmodel = viewModel
        setContentView(binding.root)

        sign_in_google.onClick { signInWithGoogle() }
        setupSignInWithFacebook()
        firebaseAuth.addAuthStateListener(authStateListener)
    }

    override fun onStop() {
        super.onStop()
        firebaseAuth.removeAuthStateListener(authStateListener)
    }

    private fun goToMainActivity() {
        PreferenceManager.getDefaultSharedPreferences(applicationContext).apply {
            edit().putString(Constants.KEY_USER_EMAIL, userEmail).apply()
            edit().putString(Constants.KEY_PROVIDER, loginProvider).apply()
        }
        startActivity(intentFor<UsersActivity>().newTask().clearTask())
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RC_GOOGLE_SIGN_IN -> {
                val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
                viewModel.      authWithGoogle(result, { authenticateWithCredentials(it, GoogleAuthProvider.PROVIDER_ID) })
            }
            RC_FACEBOOK_SIGN_IN -> fbCallbackManager.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient)
        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN)
        viewModel.showProgress(GoogleAuthProvider.PROVIDER_ID)
    }

    private fun setupSignInWithFacebook() {
        sign_in_facebook?.apply {
            setReadPermissions(Constants.FACEBOOK_PERMISSION_PUBLIC, Constants.FACEBOOK_PERMISSION_EMAIL)
            registerCallback(fbCallbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult?) {
                    viewModel.authWithFacebook(result, { authenticateWithCredentials(it, FacebookAuthProvider.PROVIDER_ID) })
                }

                override fun onCancel() {
                    viewModel.hideProgress()
                }

                override fun onError(error: FacebookException?) {
                    viewModel.hideProgress()
                }
            })
        }
    }

    private fun authenticateWithCredentials(credential: AuthCredential, provider: String) {
        loginProvider = provider
        RxFirebaseAuth.signInWithCredential(firebaseAuth, credential)
                .map { authResult -> authResult.user != null }
                .subscribe { logged ->
                    info { "User logged: $logged" }
                    FirebaseMessaging.getInstance().subscribeToGlobal()
                }
    }

    private fun userAuthenticated(firebaseUser: FirebaseUser) {
        userEmail = firebaseUser.email
        viewModel.userAuthenticated(firebaseUser)
                .subscribe({ viewModel.updateUserDeviceId(it) },
                        { err ->
                            if (err is NullPointerException) {
                                viewModel.createUser(firebaseUser)
                            } else {
                                error("Error on fetch user!", err)
                            }
                        })
        viewModel.hideProgress()
        goToMainActivity()
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        super.onConnectionFailed(connectionResult)
        viewModel.hideProgress()
        longToast(connectionResult.toString())
    }

    companion object {
        val RC_GOOGLE_SIGN_IN = 1
        val RC_FACEBOOK_SIGN_IN = 64206
    }
}