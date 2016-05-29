package com.ygorcesar.jamdroidfirechat.view.fragment;

import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ygorcesar.jamdroidfirechat.R;
import com.ygorcesar.jamdroidfirechat.databinding.FragmentChatsBinding;
import com.ygorcesar.jamdroidfirechat.model.User;
import com.ygorcesar.jamdroidfirechat.utils.Constants;
import com.ygorcesar.jamdroidfirechat.utils.ConstantsFirebase;
import com.ygorcesar.jamdroidfirechat.utils.Utils;
import com.ygorcesar.jamdroidfirechat.view.activity.MainActivity;
import com.ygorcesar.jamdroidfirechat.view.adapter.ChatsItemAdapter;
import com.ygorcesar.jamdroidfirechat.viewmodel.ChatsViewModelContract;

import java.util.ArrayList;
import java.util.List;

public class ChatsFragment extends Fragment implements ChatsViewModelContract {
    private DatabaseReference mRefUsers;
    private List<User> mUsers;
    private ValueEventListener mValueUserListener;
    private String mEncodedMail;
    private FragmentChatsBinding mFragmentChatsBinding;
    private static final String TAG = "ChatsFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mFragmentChatsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_chats, container, false);

        if (Utils.getAdditionalData() != null) {
            moveToMessagesFragment(Utils.getAdditionalData().getCHAT_KEY(),
                    Utils.getAdditionalData().getUSER_DISPLAY_NAME(),
                    Utils.getAdditionalData().getUSER_FCM_DEVICE_ID_SENDER());
            Utils.setAdditionalData(null);
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        mEncodedMail = prefs.getString(Constants.KEY_ENCODED_EMAIL, "");

        initializeScreen(mFragmentChatsBinding.rvChats);
        return mFragmentChatsBinding.getRoot();
    }

    @Override public void onStart() {
        super.onStart();
        initializeFirebase();
        getActivity().setTitle(getString(R.string.app_name));
    }

    @Override public void onStop() {
        super.onStop();
        removeFirebaseListeners();
    }

    private void initializeScreen(RecyclerView rvUsers) {
        setupToolbar();
        ChatsItemAdapter adapter = new ChatsItemAdapter(this, mEncodedMail);
        rvUsers.setAdapter(adapter);
        rvUsers.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void initializeFirebase() {
        if (mValueUserListener == null) {
            mValueUserListener = createUserValueListener();
        }
        mRefUsers = FirebaseDatabase.getInstance().getReference(ConstantsFirebase.FIREBASE_LOCATION_USERS);
        mRefUsers.keepSynced(true);
        mRefUsers.addValueEventListener(mValueUserListener);
    }

    private void setupToolbar() {
        final MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            activity.setHomeButtonVisible(false);
        }
    }

    private ValueEventListener createUserValueListener() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUsers = new ArrayList<>();
                User userGeral = new User("", getString(R.string.chat_global),
                        ConstantsFirebase.FIREBASE_LOCATION_CHAT_GLOBAL,
                        ConstantsFirebase.FIREBASE_LOCATION_CHAT_GLOBAL, null);
                mUsers.add(userGeral);
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (!snapshot.getValue(User.class).getEmail().equals(mEncodedMail)) {
                        mUsers.add(snapshot.getValue(User.class));
                    }
                }
                ChatsItemAdapter adapter = (ChatsItemAdapter) mFragmentChatsBinding.rvChats.getAdapter();
                adapter.setUsers(mUsers);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    /**
     * Move para o fragment do chat para realizar troca de mensagens
     *
     */
    public void moveToMessagesFragment(String chatKey, String friendName, String fcmKeyId) {
        Bundle args = new Bundle();
        args.putString(Constants.KEY_CHAT_CHILD, chatKey);
        args.putString(Constants.KEY_USER_DISPLAY_NAME, friendName);
        args.putString(Constants.KEY_USER_FCM_DEVICE_ID, fcmKeyId);
        MessagesFragment fragment = new MessagesFragment();
        fragment.setArguments(args);


        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in, R.anim.slide_out, R.anim.slide_out_reverse, R.anim.slide_in_reverse);
        transaction.replace(R.id.fragment, fragment).addToBackStack(null).commit();
    }

    /**
     * Remove listeners dos objetos Firebase
     */
    private void removeFirebaseListeners() {
        if (mValueUserListener != null) {
            mRefUsers.removeEventListener(mValueUserListener);
        }
    }
}
