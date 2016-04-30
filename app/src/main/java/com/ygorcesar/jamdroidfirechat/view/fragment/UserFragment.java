package com.ygorcesar.jamdroidfirechat.view.fragment;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ygorcesar.jamdroidfirechat.BR;
import com.ygorcesar.jamdroidfirechat.R;
import com.ygorcesar.jamdroidfirechat.model.User;
import com.ygorcesar.jamdroidfirechat.utils.Constants;

public class UserFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewDataBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user, container, false);

        Bundle args = getArguments();
        if (args != null) {
            String email = args.getString(Constants.KEY_ENCODED_EMAIL, "");
            String photoUrl = args.getString(Constants.KEY_USER_PROVIDER_PHOTO_URL, "");
            binding.setVariable(BR.user, new User("", email, photoUrl, null));

            getActivity().setTitle(args.getString(Constants.KEY_USER_DISPLAY_NAME, getString(R.string.app_name)));
        }
        return binding.getRoot();
    }
}