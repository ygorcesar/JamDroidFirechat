package com.ygorcesar.jamdroidfirechat.view.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.ygorcesar.jamdroidfirechat.R;
import com.ygorcesar.jamdroidfirechat.model.User;
import com.ygorcesar.jamdroidfirechat.utils.Constants;
import com.ygorcesar.jamdroidfirechat.utils.ConstantsFirebase;
import com.ygorcesar.jamdroidfirechat.utils.Utils;

import java.util.Arrays;
import java.util.HashMap;

public class LoginActivity extends BaseActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private String provider;
    private ProgressDialog mAuthProgressDialog;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private SharedPreferences.Editor mSharedPrefEditor;
    private static final int RC_SIGN_IN = 1;

    private static final int RC_FACEBOOK_LOGIN = 64206;

    private CallbackManager mFacebookCallbackManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        mSharedPrefEditor = sharedPref.edit();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Log.d(TAG, "onCreate: " + extras.toString());
        }
        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    setAuthenticatedWithOAuth(user);
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        };

        initializeScreen();
    }

    @Override protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override protected void onStop() {
        super.onStop();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    private void initializeScreen() {
        mAuthProgressDialog = new ProgressDialog(this);
        mAuthProgressDialog.setCancelable(false);

        setupGoogleSignIn();
        setupFacebookSignIn();
    }

    private void setupGoogleSignIn() {
        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_with_google);
        if (signInButton != null) {
            signInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onSignInGooglePressed(v);
                }
            });
        }
    }

    private void setupFacebookSignIn() {
        mFacebookCallbackManager = CallbackManager.Factory.create();

        LoginButton mFacebookLoginButton = (LoginButton) findViewById(R.id.login_button);
        if (mFacebookLoginButton != null) {
            mFacebookLoginButton.setReadPermissions(Arrays.asList("public_profile", "email"));
            mFacebookLoginButton.registerCallback(mFacebookCallbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(final LoginResult loginResult) {
                    mAuthProgressDialog.setMessage(getString(R.string.progress_dialog_authenticating_with, "Facebook"));
                    mAuthProgressDialog.show();
                    firebaseAuthWithFacebook(loginResult.getAccessToken());
                }

                @Override
                public void onCancel() {
                    Log.d(TAG, "onCancel: facebook cancel");
                }

                @Override
                public void onError(FacebookException e) {
                    Log.e(TAG, "onError: " + e.getMessage(), e.getCause());
                }
            });
        }
    }

    /**
     * Sign in with Google plus when user clicks "Sign in with Google" textView (button)
     */
    public void onSignInGooglePressed(View view) {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
        mAuthProgressDialog.setMessage(getString(R.string.progress_dialog_authenticating_with, "Google"));
        mAuthProgressDialog.show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode() + "" +
                "ConnectionResult.getErrorMessage() = " + connectionResult.getErrorMessage());
        Log.d(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = " + connectionResult);
        mAuthProgressDialog.dismiss();
        showErrorToast(connectionResult.toString());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RC_SIGN_IN:
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                if (result.isSuccess()) {
                    GoogleSignInAccount account = result.getSignInAccount();
                    firebaseAuthWithGoogle(account);
                } else {
                    Log.e(TAG, "onActivityResult: " + result.getStatus());
                }
                break;
            case RC_FACEBOOK_LOGIN:
                mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    /**
     * Show error toast to users
     */
    private void showErrorToast(String message) {
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d(TAG, "firebaseAuthWithGoogle: " + account.getId());
        provider = ConstantsFirebase.GOOGLE_PROVIDER;
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        signInWithCredential(credential);
    }

    private void firebaseAuthWithFacebook(AccessToken token) {
        Log.d(TAG, "firebaseAuthWithFacebook: " + token);
        provider = ConstantsFirebase.FACEBOOK_PROVIDER;
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        signInWithCredential(credential);
    }

    private void signInWithCredential(AuthCredential credential) {
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            mAuthProgressDialog.dismiss();
                            Log.w(TAG, "signInWithCredential: ", task.getException());
                            Toast.makeText(LoginActivity.this, "Falha na autenticação", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(this, new OnFailureListener() {
            @Override public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
                Log.e(TAG, "onFailure: FALHA", e);
            }
        });
    }

    private void setAuthenticatedWithOAuth(FirebaseUser user) {
        final String unprocessedEmail, displayName, photoUrl;

        unprocessedEmail = user.getEmail();
        displayName = user.getDisplayName();
        if (user.getPhotoUrl() != null) {
            photoUrl = user.getPhotoUrl().toString();
        } else {
            photoUrl = "";
        }

        mEncodedEmail = Utils.encodeEmail(unprocessedEmail);

        final DatabaseReference userLocation = FirebaseDatabase.getInstance()
                .getReference(ConstantsFirebase.FIREBASE_LOCATION_USERS)
                .child(mEncodedEmail);
        userLocation.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    final HashMap<String, Object> timestampJoined = new HashMap<>();
                    timestampJoined.put(ConstantsFirebase.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);

                    User user = new User(FirebaseInstanceId.getInstance().getToken(),
                            Utils.capitalizeString(displayName), mEncodedEmail, photoUrl, timestampJoined);
                    userLocation.setValue(user);
                } else {
                    userLocation.child(ConstantsFirebase.FIREBASE_PROPERTY_USER_DEVICE_ID)
                            .setValue(FirebaseInstanceId.getInstance().getToken());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, getString(R.string.log_error_occurred) + databaseError.getMessage());
            }
        });

        mSharedPrefEditor.putString(Constants.KEY_USER_EMAIL, unprocessedEmail).apply();
        mSharedPrefEditor.putString(Constants.KEY_USER_DISPLAY_NAME, displayName).apply();
        mSharedPrefEditor.putString(Constants.KEY_USER_PROVIDER_PHOTO_URL, photoUrl).apply();

        /* Save provider name and encodedEmail for later use and start MainActivity */
        mSharedPrefEditor.putString(Constants.KEY_PROVIDER, provider).apply();
        mSharedPrefEditor.putString(Constants.KEY_ENCODED_EMAIL, mEncodedEmail).apply();
        mSharedPrefEditor.putBoolean(Constants.KEY_PREF_NOTIFICATION, true).apply();

        FirebaseMessaging.getInstance().subscribeToTopic(ConstantsFirebase.FIREBASE_LOCATION_CHAT_GLOBAL);
    }
}
