package com.ygorcesar.jamdroidfirechat;

import com.facebook.FacebookSdk;
import com.firebase.client.Firebase;
import com.onesignal.OneSignal;
import com.ygorcesar.jamdroidfirechat.utils.AppNotificationOpenedHandler;

public class DroidFireChatApplication extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();

         /* Inicializando Firebase junto com a aplicação*/
        Firebase.setAndroidContext(this);
        Firebase.getDefaultConfig().setPersistenceEnabled(true);

        /* Initialize Facebook SDK*/
        FacebookSdk.sdkInitialize(getApplicationContext());

        /* Initialize OneSignal API */
        OneSignal.startInit(this)
                .setNotificationOpenedHandler(new AppNotificationOpenedHandler())
                .init();
    }
}
