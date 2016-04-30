package com.ygorcesar.jamdroidfirechat.viewmodel;

import android.databinding.BaseObservable;
import android.databinding.BindingAdapter;
import android.view.View;
import android.widget.ImageView;

import com.ygorcesar.jamdroidfirechat.R;
import com.ygorcesar.jamdroidfirechat.model.User;
import com.ygorcesar.jamdroidfirechat.utils.ConstantsFirebase;
import com.ygorcesar.jamdroidfirechat.utils.Utils;

public abstract class BaseChatViewModel extends BaseObservable {

    protected User mUser;
    protected String mLoggedUserEmail;

    public BaseChatViewModel(User user, String loggedUserEmail) {
        mUser = user;
        mLoggedUserEmail = loggedUserEmail;
    }

    public String getName() {
        return mUser.getName();
    }

    public String getEmail() {
        return mUser.getEmail();
    }

    public String getPhotoUrl() {
        return mUser.getPhotoUrl();
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
    @BindingAdapter({"app:photoUrl"})
    public static void loadImage(ImageView view, String url) {
        if (url.equals(ConstantsFirebase.FIREBASE_LOCATION_CHAT_GLOBAL)) {
            view.setImageDrawable(view.getContext().getResources().getDrawable(R.drawable.ic_chat_global));
        } else {
            Utils.loadImageWithPicasso(view.getContext(), view, url, R.drawable.ic_person);
        }
    }

    public void setUser(User user) {
        mUser = user;
        notifyChange();
    }
}
