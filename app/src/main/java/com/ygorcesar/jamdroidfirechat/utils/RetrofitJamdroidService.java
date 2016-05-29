package com.ygorcesar.jamdroidfirechat.utils;

import com.ygorcesar.jamdroidfirechat.BuildConfig;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface RetrofitJamdroidService {

    @Headers({"Content-Type: application/json", "Authorization: "+ BuildConfig.FCM_KEY})
    @POST("send")
    Call<Object> sendPushNotification(@Body PushNotificationObject data);
}
