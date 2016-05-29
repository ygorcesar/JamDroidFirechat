package com.ygorcesar.jamdroidfirechat;

import android.util.Log;

import com.facebook.FacebookSdk;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

public class DroidFireChatApplication extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (!FirebaseApp.getApps(this).isEmpty()) {
            Log.d("FIREBASE", "Enabling persinstence!");
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }

        /* Initialize Facebook SDK*/
        FacebookSdk.sdkInitialize(getApplicationContext());
    }
}
