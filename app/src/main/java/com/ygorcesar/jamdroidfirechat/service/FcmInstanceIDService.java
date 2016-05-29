package com.ygorcesar.jamdroidfirechat.service;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.ygorcesar.jamdroidfirechat.utils.ConstantsFirebase;
import com.ygorcesar.jamdroidfirechat.utils.Utils;

public class FcmInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "FcmInstanceIDService";

    @Override public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Token atualizado: " + refreshedToken);
        updateUserRegistrationToken(refreshedToken);
    }

    private void updateUserRegistrationToken(String token) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            FirebaseDatabase.getInstance().getReference(ConstantsFirebase.FIREBASE_LOCATION_USERS)
                    .child(Utils.encodeEmail(auth.getCurrentUser().getEmail()))
                    .child(ConstantsFirebase.FIREBASE_PROPERTY_USER_DEVICE_ID)
                    .setValue(token);
        }
    }

}
