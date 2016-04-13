package com.ygorcesar.jamdroidfirechat.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.BindingAdapter;
import android.preference.PreferenceManager;
import android.support.v4.widget.Space;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.ygorcesar.jamdroidfirechat.R;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

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
     * Transformando o timestamp do servidor para TimeZone padrão do Aparelho
     *
     * @param time
     * @return
     */
    public static String timestampToHour(Object time) {
        Timestamp stamp = new Timestamp((long) time);
        Date date = new Date(stamp.getTime());
        SimpleDateFormat sdf = new SimpleDateFormat("H:mm", new Locale("pt", "BR"));
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(date);
    }

    public static void loadImageWithPicasso(Context context, ImageView imageView, String url) {
        Picasso.with(context).load(url)
                .placeholder(R.drawable.ic_person)
                .into(imageView);
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
     * Binding adapter para carregar imagem por url em uma ImageView
     * @param view
     * @param url
     */
    @BindingAdapter({"bind:photoUrl"})
    public static void loadImage(ImageView view, String url) {
        if (url.equals(ConstantsFirebase.FIREBASE_LOCATION_CHAT_GLOBAL)) {
            view.setImageDrawable(view.getContext().getResources().getDrawable(R.drawable.ic_chat_global));
        } else {
            loadImageWithPicasso(view.getContext(), view, url, R.drawable.ic_person);
        }
    }

    /**
     * Binding Adapter para transformar timestamp do servidor para horário local
     * @param view
     * @param time
     */
    @BindingAdapter({"bind:time"})
    public static void decodeTimestamp(TextView view, HashMap<String, Object> time) {
        Timestamp stamp = new Timestamp((long) time.get(Constants.KEY_CHAT_TIME_SENDED));
        Date date = new Date(stamp.getTime());
        SimpleDateFormat sdf = new SimpleDateFormat("H:mm", new Locale("pt", "BR"));
        sdf.setTimeZone(TimeZone.getDefault());
        view.setText(sdf.format(date));
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
