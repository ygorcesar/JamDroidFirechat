package com.ygorcesar.jamdroidfirechat.utils;


import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Singleton {
    private static final String TAG = "Singleton";
    private static Singleton ourInstance = new Singleton();
    private RetrofitJamdroidService mRetrofitJamdroidService;
    private int mNumMessages;
    private List<String> mNotificationMessages;

    private Singleton() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://fcm.googleapis.com/fcm/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mRetrofitJamdroidService = retrofit.create(RetrofitJamdroidService.class);

        mNumMessages = 0;
        mNotificationMessages = new ArrayList<>();
    }

    public static Singleton getInstance() {
        return ourInstance;
    }

    public void sendMsgPushNotification(PushNotificationObject data) {
        mRetrofitJamdroidService.sendPushNotification(data).enqueue(new Callback<Object>() {
            @Override public void onResponse(Call<Object> call, Response<Object> response) {
                Log.d(TAG, "onResponse: " + response.toString());
            }

            @Override public void onFailure(Call<Object> call, Throwable t) {
                Log.e(TAG, "onFailure: " + call, t);
            }
        });
    }

    public int getNumMessages() {
        mNumMessages++;
        return mNumMessages;
    }

    public List<String> getNotificationMessages() {
        return mNotificationMessages;
    }

    public void clearMessagesAndNumber(){
        mNumMessages = 0;
        mNotificationMessages.clear();
    }

    public void addMessage(String message){
        mNotificationMessages.add(message);
    }
}
