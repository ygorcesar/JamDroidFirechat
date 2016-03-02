package com.ygorcesar.jamdroidfirechat.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ServerValue;
import com.ygorcesar.jamdroidfirechat.R;
import com.ygorcesar.jamdroidfirechat.adapters.ChatItemAdapter;
import com.ygorcesar.jamdroidfirechat.model.Chat;
import com.ygorcesar.jamdroidfirechat.model.User;
import com.ygorcesar.jamdroidfirechat.utils.Constants;
import com.ygorcesar.jamdroidfirechat.utils.ConstantsFirebase;
import com.ygorcesar.jamdroidfirechat.utils.OnRecyclerItemClickListener;
import com.ygorcesar.jamdroidfirechat.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatFragment extends Fragment implements View.OnClickListener, OnRecyclerItemClickListener {

    private RecyclerView mRecyclerViewChat;
    private FloatingActionButton mFabSendMsg;
    private AppCompatEditText mEdtMsgContent;
    private String mEncodedMail;
    private List<User> mUsers;
    private List<String> mUsersEmails;
    private List<Chat> mChats;
    private List<String> mKeys;
    private Firebase mFirebaseRef;
    private Firebase refUsers;
    private ChildEventListener childChatListener;
    private ChildEventListener childUserListener;
    private ChatItemAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);
        getActivity().setTitle(getString(R.string.app_name));

        mRecyclerViewChat = (RecyclerView) rootView.findViewById(R.id.rv_chat);
        mRecyclerViewChat.setHasFixedSize(true);
        mRecyclerViewChat.setLayoutManager(new LinearLayoutManager(getActivity()));

        mFabSendMsg = (FloatingActionButton) rootView.findViewById(R.id.btn_send_message);
        mEdtMsgContent = (AppCompatEditText) rootView.findViewById(R.id.edt_message_content);

        initializeScreen();
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        initializeFirebase();
    }

    @Override
    public void onPause() {
        super.onPause();
        removeFirebaseListeners();
    }

    /**
     * Inicializando Adapters, RecyclerView e Listeners...
     */
    private void initializeScreen() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mEncodedMail = prefs.getString(Constants.KEY_ENCODED_EMAIL, "");
        mFabSendMsg.setOnClickListener(this);

        mUsers = new ArrayList<>();
        mUsersEmails = new ArrayList<>();
        mChats = new ArrayList<>();
        mKeys = new ArrayList<>();

        mAdapter = new ChatItemAdapter(getActivity(), mChats, mUsers, mUsersEmails, mEncodedMail);
        mAdapter.setOnRecyclerItemClickListener(this);
        mRecyclerViewChat.setAdapter(mAdapter);
    }

    /**
     * Inicializando Firebase e Listeners do Firebase
     */
    private void initializeFirebase() {
        if (childChatListener == null && childUserListener == null) {
            childUserListener = createFirebaseUsersListeners();
            childChatListener = createFirebaseChatListener();
        }
        refUsers = new Firebase(ConstantsFirebase.FIREBASE_URL).child(ConstantsFirebase.FIREBASE_LOCATION_USERS);
        refUsers.addChildEventListener(childUserListener);


        mFirebaseRef = new Firebase(ConstantsFirebase.FIREBASE_URL).child(ConstantsFirebase.FIREBASE_LOCATION_CHAT);
        mFirebaseRef.keepSynced(true);
        Query chatsRef = mFirebaseRef.orderByKey().limitToLast(50);

        chatsRef.addChildEventListener(childChatListener);
    }

    private void sendMessage() {
        String msg = mEdtMsgContent.getText().toString();
        if (validateMsgContent(msg)) {

            Firebase firebaseRef = new Firebase(ConstantsFirebase.FIREBASE_URL_CHAT);
            Firebase chatRef = firebaseRef.push();
            HashMap<String, Object> timeSended = new HashMap<>();
            timeSended.put(Constants.KEY_CHAT_TIME_SENDED, ServerValue.TIMESTAMP);
            Chat chat = new Chat(mEncodedMail, msg, timeSended);
            chatRef.setValue(chat);
            mEdtMsgContent.setText("");
        } else {
            Toast.makeText(getActivity(), getString(R.string.notice_insert_message), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateMsgContent(String msg) {
        return !msg.isEmpty();
    }

    private ChildEventListener createFirebaseChatListener() {
        return new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    mChats.add(chat);
                    mKeys.add(dataSnapshot.getKey());

                    int posAdded = mChats.size() - 1;
                    mRecyclerViewChat.scrollToPosition(posAdded);
                    mAdapter.notifyItemInserted(posAdded);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                    int index = mKeys.indexOf(dataSnapshot.getKey());
                    if (index != -1) {
                        mChats.set(index, dataSnapshot.getValue(Chat.class));
                        mAdapter.notifyItemChanged(index);
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    int index = mKeys.indexOf(dataSnapshot.getKey());
                    if (index != -1) {
                        mChats.remove(index);
                        mKeys.remove(index);
                        mAdapter.notifyItemRemoved(index);
                    }
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        };
    }

    private ChildEventListener createFirebaseUsersListeners() {
        return new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                    mUsers.add(dataSnapshot.getValue(User.class));
                    mUsersEmails.add(dataSnapshot.getValue(User.class).getEmail());
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        };
    }

    private void removeFirebaseListeners() {
        if (childChatListener != null && childUserListener != null) {
            mFirebaseRef.removeEventListener(childChatListener);
            refUsers.removeEventListener(childUserListener);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send_message:
                sendMessage();
                break;
        }
    }

    @Override
    public void onRecycleItemClick(int view_id, int position) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        int userIndex = mUsersEmails.indexOf(mChats.get(position).getEmail());
        Bundle args = new Bundle();
        args.putString(Constants.KEY_USER_DISPLAY_NAME, mUsers.get(userIndex).getName());
        args.putString(Constants.KEY_ENCODED_EMAIL, Utils.decodeEmail(mUsers.get(userIndex).getEmail()));
        args.putString(Constants.KEY_USER_PROVIDER_PHOTO_URL, mUsers.get(userIndex).getPhotoUrl());
        UserFragment fragment = new UserFragment();
        fragment.setArguments(args);

        transaction.replace(R.id.fragment, fragment)
                .addToBackStack(null)
                .commit();
    }
}
