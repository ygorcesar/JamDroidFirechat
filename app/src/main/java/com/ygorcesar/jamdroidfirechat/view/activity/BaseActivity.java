package com.ygorcesar.jamdroidfirechat.view.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;
import com.greysonparrelli.permiso.Permiso;
import com.ygorcesar.jamdroidfirechat.R;
import com.ygorcesar.jamdroidfirechat.utils.Constants;
import com.ygorcesar.jamdroidfirechat.utils.ConstantsFirebase;

public abstract class BaseActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener, BaseActivitySessionContract {
    private static final String TAG = "BaseActivity";
    protected String mProvider, mEncodedEmail;
    protected GoogleApiClient mGoogleApiClient;
    protected FirebaseAuth.AuthStateListener mAuthListener;
    protected FirebaseAuth mFirebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupGoogleApiClient();

        Permiso.getInstance().setActivity(this);

        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(BaseActivity.this);
        /* Get mEncodedEmail and mProvider from SharedPreferences, use null as default value */
        mEncodedEmail = sp.getString(Constants.KEY_ENCODED_EMAIL, null);
        mProvider = sp.getString(Constants.KEY_PROVIDER, null);

        if (!(this instanceof LoginActivity)) {
            mFirebaseAuth = FirebaseAuth.getInstance();
            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    /* The user has been logged out */
                    if (user == null) {
                        takeUserToLoginScreenOnUnAuth();
                        FirebaseMessaging.getInstance()
                                .unsubscribeFromTopic(ConstantsFirebase.FIREBASE_LOCATION_CHAT_GLOBAL);
                    }
                }
            };
            mFirebaseAuth.addAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    /**
     * Inicializa a api Google Client
     */
    private void setupGoogleApiClient() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_web_oauth_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    /**
     * Desconecta a conta google do app
     */
    public void logout() {
        mFirebaseAuth.signOut();
        if (mProvider != null) {
            if (mProvider.equals(ConstantsFirebase.GOOGLE_PROVIDER)) {
                /* Logout from Google Account */
                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(@NonNull Status status) {
                                // Logged Out.
                                Log.d(TAG, String.format("Logged Out: %s", status.isSuccess() ? "Success." : "Failed."));
                            }
                        }
                );
            } else if (mProvider.equals(ConstantsFirebase.FACEBOOK_PROVIDER)) {
                LoginManager.getInstance().logOut();
                Log.d(TAG, "Logged Out from Faccebook Account");
            }
        }
    }

    /**
     * Revoga o acesso da conta google a aplicação
     */
    @Override
    public void revokeAccess() {
        mFirebaseAuth.signOut();
        if (mProvider != null) {
            if (mProvider.equals(ConstantsFirebase.GOOGLE_PROVIDER)) {
                /* Revoke access from Google Account. */
                if (mGoogleApiClient.isConnected()) {
                    Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                            new ResultCallback<Status>() {
                                @Override
                                public void onResult(@NonNull Status status) {
                                    // Google Account Access Revoked.
                                    Log.d(TAG, String.format("Google Account revoked access: %s", status.isSuccess() ? "Success." : "Failed."));
                                }
                            }
                    );
                }
            } else if (mProvider.equals(ConstantsFirebase.FACEBOOK_PROVIDER)) {
                LoginManager.getInstance().logOut();
                Log.d(TAG, "Logged Out from Faccebook Account");
            }
        }
    }

    /**
     * Move o usuário para a tela de Login ao inicializar o app
     */
    private void takeUserToLoginScreenOnUnAuth() {
        Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Permiso.getInstance().onRequestPermissionResult(requestCode, permissions, grantResults);
    }
}
