package com.ygorcesar.jamdroidfirechat.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.ygorcesar.jamdroidfirechat.R;
import com.ygorcesar.jamdroidfirechat.utils.Constants;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user, container, false);
        CircleImageView circleIvUserPhoto = (CircleImageView) rootView.findViewById(R.id.iv_user_photo);
        TextView txUserEmail = (TextView) rootView.findViewById(R.id.tv_user_email);

        Bundle args = getArguments();
        if (args != null) {
            getImageFromUrlToImageView(args.getString(Constants.KEY_USER_PROVIDER_PHOTO_URL, "")
                    , circleIvUserPhoto);
            getActivity().setTitle(args.getString(Constants.KEY_USER_DISPLAY_NAME, getString(R.string.app_name)));
            txUserEmail.setText(args.getString(Constants.KEY_ENCODED_EMAIL, ""));
        }
        return rootView;
    }

    /**
     * Usando Picasso Librarie para baixar imagem do usuário obtida pelo provider e adicionar a View
     * @param url
     * @param view
     */
    private void getImageFromUrlToImageView(String url, ImageView view){
        Picasso.with(getActivity())
                .load(url)
                .placeholder(R.drawable.ic_person)
                .into(view);
    }
}
