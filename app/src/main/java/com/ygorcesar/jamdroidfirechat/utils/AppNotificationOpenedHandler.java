package com.ygorcesar.jamdroidfirechat.utils;

import android.util.Log;

import com.google.gson.Gson;
import com.onesignal.OneSignal;

import org.json.JSONObject;

public class AppNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
    private static AppNotificationOpenedHandler instance;
    private static AppNotificationContract sNotificationContract;
    private static final String TAG = "NotificationOpened";

    public static AppNotificationOpenedHandler getInstance() {
        if (instance == null) {
            instance = new AppNotificationOpenedHandler();
        }
        return instance;
    }

    @Override
    public void notificationOpened(String message, JSONObject additionalData, boolean isActive) {
        AditionalNotificationData aditionalData = new Gson().fromJson(additionalData.toString(), AditionalNotificationData.class);

        if (aditionalData.getChatKey() != null && aditionalData.getUserName() != null) {
            if (!isActive) {
                Log.d(TAG, "notificationOpened: App em Background");
                Utils.setAditionalData(aditionalData);
            } else {
                Log.d(TAG, "notificationOpened: App em Active");
                if (message != null) {
                    if (sNotificationContract != null) {
                        sNotificationContract.showNotificationInApp(aditionalData.getUserName(), message);
                    }
                }
            }
        }
    }

    public void setNotificationContract(AppNotificationContract notificationContract) {
        sNotificationContract = notificationContract;
    }

    public void removeListener() {
        sNotificationContract = null;
    }
}
