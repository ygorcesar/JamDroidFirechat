package com.ygorcesar.jamdroidfirechat.view.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ygorcesar.jamdroidfirechat.R;
import com.ygorcesar.jamdroidfirechat.databinding.FragmentUserBinding;
import com.ygorcesar.jamdroidfirechat.model.User;
import com.ygorcesar.jamdroidfirechat.utils.Constants;

public class UserFragment extends DialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentUserBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user, container, false);

        Bundle args = getArguments();
        if (args != null) {
            String displayName = args.getString(Constants.KEY_USER_DISPLAY_NAME);
            String email = args.getString(Constants.KEY_ENCODED_EMAIL, "");
            String photoUrl = args.getString(Constants.KEY_USER_PROVIDER_PHOTO_URL, "");
            binding.setUser(new User("", displayName, email, photoUrl, null));
        }
        return binding.getRoot();
    }
}