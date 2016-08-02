package com.ygorcesar.jamdroidfirechat.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.View;
import android.view.ViewAnimationUtils;

import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;

public class Utils {
    private static PushNotificationObject.AdditionalData additionalData;

    /**
     * Cria key exclusiva para chat
     *
     * @param userEmail
     * @return
     */
    public static String createChat(String loggedUserEmail, String userEmail) {
        String chatKey = FirebaseDatabase.getInstance()
                .getReference(ConstantsFirebase.FIREBASE_LOCATION_CHAT)
                .push()
                .getKey();

        Utils.makeFriends(loggedUserEmail, userEmail, chatKey);
        Utils.makeFriends(userEmail, loggedUserEmail, chatKey);
        return chatKey;
    }

    /**
     * Cria relação entre usuário atual e selecionado para chat, setando uma key exclusiva
     * de chat para ambos
     *
     * @param userEmail
     * @param userFriend
     * @param key
     */
    private static void makeFriends(String userEmail, String userFriend, String key) {
        FirebaseDatabase.getInstance()
                .getReference(ConstantsFirebase.FIREBASE_LOCATION_USER_FRIENDS)
                .child(userEmail).child(userFriend).setValue(key);
    }

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

    public static PushNotificationObject.AdditionalData getAdditionalData() {
        return additionalData;
    }

    public static void setAdditionalData(PushNotificationObject.AdditionalData additionalData) {
        Utils.additionalData = additionalData;
    }

    public static String capitalizeString(String string) {
        if (string == null || string.isEmpty()) {
            return string;
        } else {
            return string.substring(0, 1).toUpperCase() + string.substring(1);
        }
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

    public static void animateFadeIn(final View view, int delay, long duration) {
        view.setAlpha(0f);
        view.animate()
                .alpha(1f)
                .setInterpolator(new FastOutSlowInInterpolator())
                .setStartDelay(delay)
                .setDuration(duration)
                .setListener(new Animator.AnimatorListener() {
                    @Override public void onAnimationStart(Animator animator) {
                        view.setVisibility(View.VISIBLE);
                    }

                    @Override public void onAnimationEnd(Animator animator) {

                    }

                    @Override public void onAnimationCancel(Animator animator) {

                    }

                    @Override public void onAnimationRepeat(Animator animator) {

                    }
                })
                .start();

    }

    public static void animateFadeOut(final View view, int delay, long duration) {
        view.setAlpha(1f);
        view.animate()
                .alpha(0f)
                .setInterpolator(new FastOutSlowInInterpolator())
                .setStartDelay(delay)
                .setDuration(duration)
                .setListener(new Animator.AnimatorListener() {
                    @Override public void onAnimationStart(Animator animator) {

                    }

                    @Override public void onAnimationEnd(Animator animator) {
                        view.setVisibility(View.GONE);
                    }

                    @Override public void onAnimationCancel(Animator animator) {

                    }

                    @Override public void onAnimationRepeat(Animator animator) {

                    }
                })
                .start();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void enterCircularReveal(View v) {
        int cx = v.getMeasuredWidth() / 2;
        int cy = v.getMeasuredHeight() / 2;

        int finalRadius = Math.max(v.getWidth(), v.getHeight()) / 2;
        Animator anim = ViewAnimationUtils.createCircularReveal(v, cx, cy, 0, finalRadius);
        v.setVisibility(View.VISIBLE);
        anim.start();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void exitCircularReveal(final View v) {
        int cx = v.getMeasuredWidth() / 2;
        int cy = v.getMeasuredHeight() / 2;

        int initialRadius = v.getWidth() / 2;
        Animator anim = ViewAnimationUtils.createCircularReveal(v, cx, cy, initialRadius, 0);

        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                v.setVisibility(View.INVISIBLE);
            }
        });
        anim.start();
    }

    public static Bitmap getBitmapFromUri(Context context, String uri) throws IOException {
        return MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(uri));
    }

    public static Bitmap getScaledBitmap(Bitmap bitmap, int maxSize) {
        float ratio = Math.min(
                (float) maxSize / bitmap.getWidth(),
                (float) maxSize / bitmap.getHeight());
        int width = Math.round(ratio * bitmap.getWidth());
        int height = Math.round(ratio * bitmap.getHeight());
        return Bitmap.createScaledBitmap(bitmap, width, height, false);
    }

    public static String[] getSharedType(String content, Bundle args) {
        String[] s = new String[4];
        switch (content) {
            case Intent.EXTRA_TEXT:
                s[Constants.ARGS_POS_SHARED] = args.getString(Intent.EXTRA_TEXT);
                s[Constants.ARGS_POS_TYPE] = Intent.EXTRA_TEXT;
                break;
            case Intent.EXTRA_STREAM:
                s[Constants.ARGS_POS_SHARED] = args.getString(Intent.EXTRA_STREAM);
                s[Constants.ARGS_POS_TYPE] = Intent.EXTRA_STREAM;
                break;
            case Constants.EXTRA_LOCATION:
                s[Constants.ARGS_POS_SHARED] = args.getString(Constants.EXTRA_LOCATION);
                s[Constants.ARGS_POS_TYPE] = Constants.EXTRA_LOCATION;
                s[Constants.ARGS_POS_LATITUDE] = args.getString(Constants.KEY_SHARED_LATITUDE, "");
                s[Constants.ARGS_POS_LONGITUDE] = args.getString(Constants.KEY_SHARED_LONGITUDE, "");
                break;
        }
        return s;
    }
}
