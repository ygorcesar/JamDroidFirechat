package com.ygorcesar.jamdroidfirechat;

import android.content.Context;

import com.firebase.client.Firebase;

public class DroidFireChatApplication extends android.app.Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        DroidFireChatApplication.context = getApplicationContext();

        /**
         * Inicializando Firebase junto com a aplicação
         */
        Firebase.setAndroidContext(this);
        Firebase.getDefaultConfig().setPersistenceEnabled(true);
    }

    public static Context getAppContext() {
        return DroidFireChatApplication.context;
    }
}
