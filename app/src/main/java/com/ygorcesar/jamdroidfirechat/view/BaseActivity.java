package com.ygorcesar.jamdroidfirechat.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.ygorcesar.jamdroidfirechat.utils.Constants;
import com.ygorcesar.jamdroidfirechat.utils.ConstantsFirebase;

public abstract class BaseActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener {
    private final String TAG = BaseActivity.class.getSimpleName();
    protected String mProvider, mEncodedEmail;
    protected GoogleApiClient mGoogleApiClient;
    protected Firebase.AuthStateListener mAuthListener;
    protected Firebase mFireBaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupGoogleApiClient();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(BaseActivity.this);
        /* Get mEncodedEmail and mProvider from SharedPreferences, use null as default value */
        mEncodedEmail = sp.getString(Constants.KEY_ENCODED_EMAIL, null);
        mProvider = sp.getString(Constants.KEY_PROVIDER, null);

        if (!(this instanceof LoginActivity)) {
            mFireBaseRef = new Firebase(ConstantsFirebase.FIREBASE_URL);
            mAuthListener = new Firebase.AuthStateListener() {
                @Override
                public void onAuthStateChanged(AuthData authData) {
                     /* The user has been logged out */
                    if (authData == null) {
                        takeUserToLoginScreenOnUnAuth();
                    }
                }
            };
            mFireBaseRef.addAuthStateListener(mAuthListener);
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
    protected void logout() {
        if (mProvider != null) {
            mFireBaseRef.unauth();

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
            }
        }
    }

    /**
     * Revoga o acesso da conta google a aplicação
     */
    protected void revokeAccess() {
        if (mProvider != null) {
            mFireBaseRef.unauth();

            if (mProvider.equals(ConstantsFirebase.GOOGLE_PROVIDER)) {
                /* Revoke access from Google Account. */
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
}
