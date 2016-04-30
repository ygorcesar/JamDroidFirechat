package com.ygorcesar.jamdroidfirechat.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.BindingAdapter;
import android.preference.PreferenceManager;
import android.support.v4.widget.Space;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.ygorcesar.jamdroidfirechat.R;

public class Utils {

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

    /**
     * Verifica se a mensagem é do usuário atual que enviou ou de outro usuário
     *
     * @param context
     * @param senderEmail
     * @return
     */
    public static boolean isSender(Context context, String senderEmail) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String userEmail = preferences.getString(Constants.KEY_ENCODED_EMAIL, "");
        return userEmail.equals(senderEmail);
    }

    /**
     * Binding Adapter para exibir view como enviado ou recebida, onde
     * enviado espaçamento a esquerda, recebida espaçamento a direita
     * @param view
     * @param senderEmail
     */
    @BindingAdapter({"bind:spaceSender"})
    public static void isSender(Space view, String senderEmail) {
        if (isSender(view.getContext(), senderEmail)) {
            if (view.getId() == R.id.space_left) {
                view.setVisibility(View.VISIBLE);
            } else {
                view.setVisibility(View.GONE);
            }
        } else {
            if (view.getId() == R.id.space_left) {
                view.setVisibility(View.GONE);
            } else {
                view.setVisibility(View.VISIBLE);
            }
        }
    }
}
