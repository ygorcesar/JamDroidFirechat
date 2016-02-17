package com.ygorcesar.jamdroidfirechat;

import com.firebase.client.Firebase;

public class DroidFireChatApplication extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        /* Initialize FireBase */
        Firebase.setAndroidContext(this);
    }
}
