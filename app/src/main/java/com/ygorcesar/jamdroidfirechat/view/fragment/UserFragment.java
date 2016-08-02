package com.ygorcesar.jamdroidfirechat.view.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ygorcesar.jamdroidfirechat.R;
import com.ygorcesar.jamdroidfirechat.databinding.DialogFragmentUserBinding;
import com.ygorcesar.jamdroidfirechat.model.User;
import com.ygorcesar.jamdroidfirechat.utils.Constants;
import com.ygorcesar.jamdroidfirechat.utils.ConstantsFirebase;
import com.ygorcesar.jamdroidfirechat.utils.Utils;
import com.ygorcesar.jamdroidfirechat.view.activity.BaseActivity;

public class UserFragment extends DialogFragment {
    private String mLoggedUserEmail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        DialogFragmentUserBinding binding = DataBindingUtil.inflate(inflater,
                R.layout.dialog_fragment_user, container, false);
        mLoggedUserEmail = ((BaseActivity) getActivity()).getEncodedEmail();

        Bundle args = getArguments();
        if (args != null) {
            String currentChatKey = args.getString(Constants.KEY_CHAT_KEY);
            final String displayName = args.getString(Constants.KEY_USER_DISPLAY_NAME);
            final String fcmKeyId = args.getString(Constants.KEY_USER_FCM_DEVICE_ID);
            final String email = args.getString(Constants.KEY_ENCODED_EMAIL, "");
            String photoUrl = args.getString(Constants.KEY_USER_PROVIDER_PHOTO_URL, "");
            binding.setUser(new User("", displayName, email, photoUrl, null));

            if (currentChatKey != null && currentChatKey.equals(ConstantsFirebase.FIREBASE_LOCATION_CHAT_GLOBAL)
                    && !mLoggedUserEmail.equals(Utils.encodeEmail(email))) {
                binding.ivChatMessage.setVisibility(View.VISIBLE);
                binding.ivChatMessage.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View view) {
                        getChatKey(displayName, email, fcmKeyId);
                    }
                });
            }
        }
        Utils.animateScaleXY(binding.ivUserPhoto, 100, 800);
        Utils.animateScaleXY(binding.tvUserName, 200, 800);
        Utils.animateScaleXY(binding.tvUserEmail, 400, 800);
        return binding.getRoot();
    }

    private void getChatKey(final String friendName, String friendEmail, final String fcmKeyId) {
        FirebaseDatabase.getInstance()
                .getReference(ConstantsFirebase.FIREBASE_LOCATION_USER_FRIENDS)
                .child(mLoggedUserEmail)
                .child(Utils.encodeEmail(friendEmail))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                            String chatKey = dataSnapshot.getValue().toString();
                            moveToMessagesFragment(chatKey, friendName, fcmKeyId);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    public void moveToMessagesFragment(String chatKey, String friendName, String fcmKeyId) {
        Bundle args = new Bundle();
        args.putString(Constants.KEY_CHAT_CHILD, chatKey);
        args.putString(Constants.KEY_USER_DISPLAY_NAME, friendName);
        args.putString(Constants.KEY_USER_FCM_DEVICE_ID, fcmKeyId);
        MessagesFragment fragment = new MessagesFragment();
        fragment.setArguments(args);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in, R.anim.slide_out,
                R.anim.slide_out_reverse, R.anim.slide_in_reverse);
        dismiss();
        transaction.replace(R.id.fragment, fragment).addToBackStack(null).commit();
    }
}