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

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ygorcesar.jamdroidfirechat.R;
import com.ygorcesar.jamdroidfirechat.databinding.AdapterItemChatsBinding;
import com.ygorcesar.jamdroidfirechat.databinding.FragmentChatsBinding;
import com.ygorcesar.jamdroidfirechat.model.User;
import com.ygorcesar.jamdroidfirechat.utils.Constants;
import com.ygorcesar.jamdroidfirechat.utils.ConstantsFirebase;
import com.ygorcesar.jamdroidfirechat.utils.Utils;
import com.ygorcesar.jamdroidfirechat.view.activity.MainActivity;
import com.ygorcesar.jamdroidfirechat.viewmodel.ChatsViewModel;
import com.ygorcesar.jamdroidfirechat.viewmodel.ChatsViewModelContract;

public class ChatsFragment extends Fragment implements ChatsViewModelContract {
    private static String mEncodedMail;
    private FirebaseRecyclerAdapter<User, ChatsItemHolder> mAdapter;
    private FragmentChatsBinding mFragmentChatsBinding;
    private String sharedArgs;
    private String sharedType;
    private String sharedLatitude;
    private String sharedLongitude;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mFragmentChatsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_chats, container, false);

        if (Utils.getAdditionalData() != null) {
            moveToMessagesFragment(Utils.getAdditionalData().getCHAT_KEY(),
                    Utils.getAdditionalData().getUSER_DISPLAY_NAME(),
                    Utils.getAdditionalData().getUSER_FCM_DEVICE_ID_SENDER());
            Utils.setAdditionalData(null);
        }

        if (getArguments() != null) {
            String CONTENT_TYPE = getArguments().getString(Constants.KEY_SHARED_CONTENT, "");
            if (!CONTENT_TYPE.isEmpty()) {
                String[] args = Utils.getSharedType(CONTENT_TYPE, getArguments());
                sharedArgs = args[Constants.ARGS_POS_SHARED];
                sharedType = args[Constants.ARGS_POS_TYPE];
                if (sharedType.equals(Constants.EXTRA_LOCATION)){
                    sharedLatitude = args[Constants.ARGS_POS_LATITUDE];
                    sharedLongitude = args[Constants.ARGS_POS_LONGITUDE];
                }
            }
        }

        createChatGlobal();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        mEncodedMail = prefs.getString(Constants.KEY_ENCODED_EMAIL, "");

        initializeScreen();
        return mFragmentChatsBinding.getRoot();
    }

    @Override public void onStart() {
        super.onStart();
        getActivity().setTitle(getString(R.string.app_name));
    }

    @Override public void onStop() {
        super.onStop();
        mAdapter.cleanup();
    }

    private void initializeScreen() {
        setupToolbar();
        mFragmentChatsBinding.rvChats.setHasFixedSize(true);
        mFragmentChatsBinding.rvChats.setLayoutManager(new LinearLayoutManager(getActivity()));

        initializeFirebase();
    }

    private void initializeFirebase() {
        DatabaseReference refUsers = FirebaseDatabase.getInstance()
                .getReference(ConstantsFirebase.FIREBASE_LOCATION_USERS);
        refUsers.keepSynced(true);

        mAdapter = new FirebaseRecyclerAdapter<User, ChatsItemHolder>(User.class, R.layout.adapter_item_chats,
                ChatsItemHolder.class, refUsers) {
            @Override
            public ChatsItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                AdapterItemChatsBinding adapterItemChatsBinding = DataBindingUtil.inflate(LayoutInflater
                        .from(parent.getContext()), viewType, parent, false);
                return new ChatsItemHolder(adapterItemChatsBinding);
            }

            @Override
            protected void populateViewHolder(ChatsItemHolder viewHolder, User user, int position) {
                if (user.getEmail().equals(mEncodedMail)) {
                    viewHolder.mAdapterItemChatsBinding.lnItemRow.removeAllViews();
                    viewHolder.mAdapterItemChatsBinding.lnItemRow.setPadding(0, 0, 0, 0);
                } else {
                    viewHolder.bindUser(user, ChatsFragment.this);
                }
            }
        };
        mFragmentChatsBinding.rvChats.setAdapter(mAdapter);
    }

    private void setupToolbar() {
        final MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            activity.setHomeButtonVisible(false);
        }
    }

    private void createChatGlobal() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(ConstantsFirebase.FIREBASE_LOCATION_USERS)
                .child("0" + ConstantsFirebase.FIREBASE_LOCATION_CHAT_GLOBAL);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    FirebaseDatabase.getInstance().getReference(ConstantsFirebase.FIREBASE_LOCATION_USERS)
                            .child("0" + ConstantsFirebase.FIREBASE_LOCATION_CHAT_GLOBAL)
                            .setValue(new User(ConstantsFirebase.FIREBASE_TOPIC_CHAT_GLOBAL_TO,
                                    getString(R.string.chat_global), ConstantsFirebase.FIREBASE_LOCATION_CHAT_GLOBAL,
                                    ConstantsFirebase.FIREBASE_LOCATION_CHAT_GLOBAL, null));
                }
            }

            @Override public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    /**
     * Move para o fragment do chat para realizar troca de mensagens
     */
    public void moveToMessagesFragment(String chatKey, String friendName, String fcmKeyId) {
        Bundle args = new Bundle();
        args.putString(Constants.KEY_CHAT_CHILD, chatKey);
        args.putString(Constants.KEY_USER_DISPLAY_NAME, friendName);
        args.putString(Constants.KEY_USER_FCM_DEVICE_ID, fcmKeyId);
        if (sharedArgs != null) {
            args.putString(sharedType, sharedArgs);
            args.putString(Constants.KEY_SHARED_CONTENT, sharedType);
            if (sharedType.equals(Constants.EXTRA_LOCATION)){
                args.putString(Constants.KEY_SHARED_LATITUDE, sharedLatitude);
                args.putString(Constants.KEY_SHARED_LONGITUDE, sharedLongitude);
            }
        }

        MessagesFragment fragment = new MessagesFragment();
        fragment.setArguments(args);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in, R.anim.slide_out,
                R.anim.slide_out_reverse, R.anim.slide_in_reverse);
        transaction.replace(R.id.fragment, fragment).addToBackStack(null).commit();
    }

    public static class ChatsItemHolder extends RecyclerView.ViewHolder {
        private AdapterItemChatsBinding mAdapterItemChatsBinding;

        public ChatsItemHolder(AdapterItemChatsBinding adapterItemChatsBinding) {
            super(adapterItemChatsBinding.lnItemRow);
            this.mAdapterItemChatsBinding = adapterItemChatsBinding;
        }

        public void bindUser(User user, ChatsViewModelContract contract) {
            if (mAdapterItemChatsBinding.getChatsViewModel() == null) {
                mAdapterItemChatsBinding.setChatsViewModel(new ChatsViewModel(user, mEncodedMail,
                        contract));
            } else {
                mAdapterItemChatsBinding.getChatsViewModel().setUser(user);
            }
        }
    }
}
