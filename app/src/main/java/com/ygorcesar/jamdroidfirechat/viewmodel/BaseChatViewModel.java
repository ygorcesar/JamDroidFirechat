package com.ygorcesar.jamdroidfirechat.viewmodel;

import android.databinding.BaseObservable;
import android.databinding.BindingAdapter;
import android.view.View;
import android.widget.ImageView;

import com.ygorcesar.jamdroidfirechat.model.MapLocation;
import com.ygorcesar.jamdroidfirechat.model.User;
import com.ygorcesar.jamdroidfirechat.utils.ConstantsFirebase;

public abstract class BaseChatViewModel extends BaseObservable {

    protected User mUser;
    protected String mLoggedUserEmail;

    public BaseChatViewModel(User user, String loggedUserEmail) {
        mUser = user;
        mLoggedUserEmail = loggedUserEmail;
    }

    public String getName() {
        return !mUser.getEmail().equals(ConstantsFirebase.FIREBASE_LOCATION_CHAT_GLOBAL)
                ? mUser.getName() : mUser.getName().replace("0", "");
    }

    public String getEmail() {
        return mUser.getEmail();
    }

    public String getPhotoUrl() {
        return mUser.getPhotoUrl();
    }

    public boolean isOnline() {
        return mUser.getEmail().equals(ConstantsFirebase.FIREBASE_LOCATION_CHAT_GLOBAL) || mUser.isOnline();
    }

    public void onItemClick(View view) {
    }

    /**
     * Verifica se a mensagem é do usuário atual que enviou ou de outro usuário
     *
     * @return int
     */
    public boolean isSender() {
        return mLoggedUserEmail.equals(mUser.getEmail());
    }

    /**
     * Binding adapter para carregar imagem por url em uma ImageView
     *
     * @param view
     * @param url
     */
    @BindingAdapter({"bind:photoUrl"})
    public static void loadImage(ImageView view, String url) {
    /*    if (url != null && !url.isEmpty()) {
            if (url.equals(ConstantsFirebase.FIREBASE_LOCATION_CHAT_GLOBAL)) {
                view.setImageDrawable(view.getContext().getResources().getDrawable(R.drawable.ic_chat_global));
            } else {
                Glide.with(view.getContext())
                        .load(url)
                        .placeholder(R.drawable.ic_person)
                        .fitCenter()
                        .dontAnimate()
                        .into(view);
            }
        } else {
            view.setImageDrawable(view.getContext().getResources().getDrawable(R.drawable.ic_person));
        }*/
    }

    @BindingAdapter({"bind:photoUrlMessage", "bind:mapLocation"})
    public static void loadMessageImage(ImageView view, String url, MapLocation mapLocation) {
      /*  if (mapLocation != null) {
            Glide.with(view.getContext())
                    .load(view.getContext().getString(R.string.map_static_url,
                            mapLocation.getLatitude(), mapLocation.getLongitude()))
                    .placeholder(R.drawable.ic_map_placeholder)
                    .crossFade()
                    .into(view);
        } else {
            if (url != null && !url.isEmpty()) {
                Glide.with(view.getContext())
                        .load(url)
                        .placeholder(R.drawable.ic_image_placeholder)
                        .centerCrop()
                        .crossFade()
                        .into(view);
            }
        }*/
    }

    public void setUser(User user) {
        mUser = user;
        notifyChange();
    }
}
