package com.ygorcesar.jamdroidfirechat.utils;

import android.content.Context;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class Utils {
    private static PushNotificationObject.AdditionalData additionalData;

    /**
     * Encoda o email pois os paths do Firebase não podem conter: '.', '#', '$', '[', ou ']'
     *
     * @param userEmail
     * @return
     */
    public static String encodeEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }

    /**
     * Decoda o email
     *
     * @param userEmail
     * @return
     */
    public static String decodeEmail(String userEmail) {
        return userEmail.replace(",", ".");
    }


    /**
     * Utilizando Librarie Picasso para carregar imagens em imageview
     *
     * @param context
     * @param imageView
     * @param url
     * @param idPlaceHolder
     */
    public static void loadImageWithPicasso(Context context, ImageView imageView, String url, int idPlaceHolder) {
        Picasso.with(context).load(url)
                .placeholder(idPlaceHolder)
                .into(imageView);
    }

    public static PushNotificationObject.AdditionalData getAdditionalData() {
        return additionalData;
    }

    public static void setAdditionalData(PushNotificationObject.AdditionalData additionalData) {
        Utils.additionalData = additionalData;
    }

    public static void animateScaleXY(View view, int delay, long duration) {
        view.setScaleX(0f);
        view.setScaleY(0f);
        view.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setInterpolator(new FastOutSlowInInterpolator())
                .setStartDelay(delay)
                .setDuration(duration)
                .start();
    }
}
