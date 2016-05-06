package com.ygorcesar.jamdroidfirechat.utils;

import android.content.Context;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.ygorcesar.jamdroidfirechat.R;

public class Utils {
    private static AditionalNotificationData aditionalData;

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

    public static String generateNotificationJson(Context context, String msg, AditionalNotificationData aditionalData) {
        return context.getString(R.string.json_push_notification,
                aditionalData.getUserOneSignalId(), aditionalData.getUserName(), msg, new Gson().toJson(aditionalData));
    }

    public static AditionalNotificationData getAditionalData() {
        return aditionalData;
    }

    public static void setAditionalData(AditionalNotificationData aditionalData) {
        Utils.aditionalData = aditionalData;
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
