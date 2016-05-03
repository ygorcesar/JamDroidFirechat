package com.ygorcesar.jamdroidfirechat.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.Scope;
import com.ygorcesar.jamdroidfirechat.R;
import com.ygorcesar.jamdroidfirechat.model.User;
import com.ygorcesar.jamdroidfirechat.utils.Constants;
import com.ygorcesar.jamdroidfirechat.utils.ConstantsFirebase;
import com.ygorcesar.jamdroidfirechat.utils.Utils;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

public class LoginActivity extends BaseActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private ProgressDialog mAuthProgressDialog;
    private Firebase mFirebaseRef;
    private Firebase.AuthStateListener mAuthStateListener;

    private SharedPreferences mSharedPref;
    private SharedPreferences.Editor mSharedPrefEditor;

    /**
     * Variables related to Google Login
     */
    /* A flag indicating that a PendingIntent is in progress and prevents us from starting further intents. */
    private boolean mGoogleIntentInProgress;
    /* Request code used to invoke sign in user interactions for Google+ */
    private static final int RC_SIGN_IN = 1;
    private static final int RC_GOOGLE_LOGIN = 2;

    private static final int RC_FACEBOOK_LOGIN = 64206;
    /* A Google account object that is populated if the user signs in with Google */
    GoogleSignInAccount mGoogleAccount;

    private CallbackManager mFacebookCallbackManager;
    private LoginButton mFacebookLoginButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mSharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        mSharedPrefEditor = mSharedPref.edit();

        mFirebaseRef = new Firebase(ConstantsFirebase.FIREBASE_URL);

        initializeScreen();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAuthStateListener = new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                mAuthProgressDialog.dismiss();

                if (authData != null) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        };

        mFirebaseRef.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseRef.removeAuthStateListener(mAuthStateListener);
    }

    private void initializeScreen() {
        mAuthProgressDialog = new ProgressDialog(this);
        mAuthProgressDialog.setTitle(getString(R.string.progress_dialog_loading));
        mAuthProgressDialog.setMessage(getString(R.string.progress_dialog_authenticating_with_google));
        mAuthProgressDialog.setCancelable(false);

        setupGoogleSignIn();
        setupFacebookSignIn();
    }

    private void setupGoogleSignIn() {
        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_with_google);
//        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSignInGooglePressed(v);
            }
        });
    }

    private void setupFacebookSignIn() {
        mFacebookCallbackManager = CallbackManager.Factory.create();

        mFacebookLoginButton = (LoginButton) findViewById(R.id.login_button);
        mFacebookLoginButton.setReadPermissions(Arrays.asList("public_profile", "email"));

        mFacebookLoginButton.registerCallback(mFacebookCallbackManager, getFacebookOAuthTokenAndLogin());
    }

    /**
     * Sign in with Google plus when user clicks "Sign in with Google" textView (button)
     */
    public void onSignInGooglePressed(View view) {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
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
                handleSignInResult(result);
                break;
            case RC_GOOGLE_LOGIN:
                if (mGoogleAccount.getEmail() != null)
                    getGoogleOAuthTokenAndLogin();
                break;
            case RC_FACEBOOK_LOGIN:
                mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult(): " + result.isSuccess());
        if (result.isSuccess()) {
            /* Signed in successfully with Google Account, get the Oauth Token */
            mGoogleAccount = result.getSignInAccount();
            getGoogleOAuthTokenAndLogin();
        } else {
            showErrorToast(result.getStatus().toString());
            mAuthProgressDialog.dismiss();
            Log.e(TAG, "Status Code: " + result.getStatus().getStatusCode());
            Log.e(TAG, "Status Message: " + result.getStatus().getStatus());
        }
    }

    /**
     * Show error toast to users
     */
    private void showErrorToast(String message) {
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
    }

    private void loginWithOathToken(String provider, String token) {
        mFirebaseRef.authWithOAuthToken(provider, token, new FireBaseAuthResultHandler(provider));
    }

    private void setAuthenticatedWithOAuth(AuthData authData) {
        final String unprocessedEmail, displayName, photoUrl;

        unprocessedEmail = authData.getProviderData().get(ConstantsFirebase.PROVIDER_DATA_EMAIL).toString();
        displayName = authData.getProviderData().get(ConstantsFirebase.PROVIDER_DATA_DISPLAY_NAME).toString();
        photoUrl = authData.getProviderData().get(ConstantsFirebase.PROVIDER_DATA_PROFILE_IMAGE_URL).toString();
        mEncodedEmail = Utils.encodeEmail(unprocessedEmail);

        final Firebase userLocation = new Firebase(ConstantsFirebase.FIREBASE_URL_USERS).child(mEncodedEmail);
        userLocation.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    HashMap<String, Object> timestampJoined = new HashMap<>();
                    timestampJoined.put(ConstantsFirebase.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);

                    User user = new User(displayName, mEncodedEmail, photoUrl, timestampJoined);
                    userLocation.setValue(user);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.d(TAG, getString(R.string.log_error_occurred) + firebaseError.getMessage());
            }
        });

        mSharedPrefEditor.putString(Constants.KEY_USER_EMAIL, unprocessedEmail).apply();
        mSharedPrefEditor.putString(Constants.KEY_USER_DISPLAY_NAME, displayName).apply();
        mSharedPrefEditor.putString(Constants.KEY_USER_PROVIDER_PHOTO_URL, photoUrl).apply();

        /* Save provider name and encodedEmail for later use and start MainActivity */
        mSharedPrefEditor.putString(Constants.KEY_PROVIDER, authData.getProvider()).apply();
        mSharedPrefEditor.putString(Constants.KEY_ENCODED_EMAIL, mEncodedEmail).apply();
    }

    /**
     * Get GoogleOAuthToken
     */
    private void getGoogleOAuthTokenAndLogin() {
        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            String mErrorMessage = null;

            @Override
            protected String doInBackground(Void... params) {
                String token = null;

                try {
                    String scope = String.format(getString(R.string.oauth2_format), new Scope(Scopes.PROFILE)) + " email";

                    token = GoogleAuthUtil.getToken(LoginActivity.this, mGoogleAccount.getEmail(), scope);
                } catch (IOException transientEx) {
                    /* Network or Server Error */
                    Log.e(TAG, getString(R.string.google_error_auth_with_google) + transientEx);
                    mErrorMessage = getString(R.string.google_error_network_error) + transientEx.getMessage();
                } catch (UserRecoverableAuthException e) {
                    Log.w(TAG, getString(R.string.google_error_recoverable_oauth_error) + e.toString());

                    /* We probably need to ask for permissions, so start the intent if there is none pending */
                    if (!mGoogleIntentInProgress) {
                        mGoogleIntentInProgress = true;
                        startActivityForResult(e.getIntent(), RC_GOOGLE_LOGIN);
                    }
                } catch (GoogleAuthException authEx) {
                    Log.e(TAG, " " + authEx.getMessage(), authEx);
                }
                return token;
            }

            @Override
            protected void onPostExecute(String token) {
                mAuthProgressDialog.dismiss();
                if (token != null) {
                    /* Successfully got OAuth token, now login with Google */
                    Log.i(TAG, String.format("Token: %s", token));
                    loginWithOathToken(ConstantsFirebase.GOOGLE_PROVIDER, token);
                } else if (mErrorMessage != null) {
                    showErrorToast(mErrorMessage);
                }
            }
        };
        task.execute();
    }

    private FacebookCallback<LoginResult> getFacebookOAuthTokenAndLogin() {
        return new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                // App code
                loginWithOathToken(ConstantsFirebase.FACEBOOK_PROVIDER, loginResult.getAccessToken().getToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "onCancel: facebook cancel");
            }

            @Override
            public void onError(FacebookException e) {
                Log.e(TAG, "onError: " + e.getMessage(), e.getCause());
            }
        };
    }

    private class FireBaseAuthResultHandler implements Firebase.AuthResultHandler {

        private final String provider;

        private FireBaseAuthResultHandler(String provider) {
            this.provider = provider;
        }

        /**
         * On successful authentication call setAuthenticatedUser if it was not already
         * called in
         */
        @Override
        public void onAuthenticated(AuthData authData) {
            mAuthProgressDialog.dismiss();
            Log.i(TAG, provider + " " + getString(R.string.log_message_auth_successful));

            if (authData != null) {
                /**
                 * Verify if user logged with Google Account provider
                 */
                if (authData.getProvider().equals(ConstantsFirebase.GOOGLE_PROVIDER)) {
                    setAuthenticatedWithOAuth(authData);
                } else if (authData.getProvider().equals(ConstantsFirebase.FACEBOOK_PROVIDER)) {
                    setAuthenticatedWithOAuth(authData);
                } else {
                    Log.e(TAG, getString(R.string.log_error_invalid_provider) + authData.getProvider());
                }

                /* Go to MainActivity */
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        }

        @Override
        public void onAuthenticationError(FirebaseError firebaseError) {
            mAuthProgressDialog.dismiss();

            switch (firebaseError.getCode()) {
                case FirebaseError.USER_DOES_NOT_EXIST:
                    break;
                case FirebaseError.NETWORK_ERROR:
                    showErrorToast(getString(R.string.error_message_failed_sign_in_no_network));
                    break;
                default:
                    showErrorToast(firebaseError.toString());
            }
        }
    }
}
