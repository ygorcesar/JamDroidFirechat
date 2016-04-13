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
import com.firebase.client.ValueEventListener;
import com.ygorcesar.jamdroidfirechat.R;
import com.ygorcesar.jamdroidfirechat.adapters.MessageItemAdapter;
import com.ygorcesar.jamdroidfirechat.model.Message;
import com.ygorcesar.jamdroidfirechat.model.User;
import com.ygorcesar.jamdroidfirechat.utils.Constants;
import com.ygorcesar.jamdroidfirechat.utils.ConstantsFirebase;
import com.ygorcesar.jamdroidfirechat.utils.OnRecyclerItemClickListener;
import com.ygorcesar.jamdroidfirechat.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MessagesFragment extends Fragment implements View.OnClickListener, OnRecyclerItemClickListener {

    private RecyclerView mRecyclerViewChat;
    private FloatingActionButton mFabSendMsg;
    private AppCompatEditText mEdtMsgContent;
    private String mEncodedMail;
    private List<User> mUsers;
    private List<String> mUsersEmails;
    private List<Message> mMessages;
    private List<String> mKeys;
    private Firebase mRefMessages;
    private Firebase mRefUsers;
    private ChildEventListener childMessagesListener;
    private ValueEventListener valueUserListener;
    private MessageItemAdapter mAdapter;
    private String mChildChatKey;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_messages, container, false);

        mRecyclerViewChat = (RecyclerView) rootView.findViewById(R.id.rv_message);
        mRecyclerViewChat.setHasFixedSize(true);
        mRecyclerViewChat.setLayoutManager(new LinearLayoutManager(getActivity()));

        mFabSendMsg = (FloatingActionButton) rootView.findViewById(R.id.btn_send_message);
        mEdtMsgContent = (AppCompatEditText) rootView.findViewById(R.id.edt_message_content);

        if (getArguments() != null) {
            mChildChatKey = getArguments().getString(Constants.KEY_CHAT_CHILD, "");
            getActivity().setTitle(getArguments()
                    .getString(Constants.KEY_USER_DISPLAY_NAME, getString(R.string.app_name)));
        }

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

    @Override public void onDetach() {
        super.onDetach();
        getActivity().setTitle(getString(R.string.app_name));
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
        mMessages = new ArrayList<>();
        mKeys = new ArrayList<>();

        mAdapter = new MessageItemAdapter(getActivity(), mMessages, mUsers, mUsersEmails);
        mAdapter.setOnRecyclerItemClickListener(this);
        mRecyclerViewChat.setAdapter(mAdapter);
    }

    /**
     * Inicializando Firebase e Listeners do Firebase
     */
    private void initializeFirebase() {
        if (childMessagesListener == null && valueUserListener == null) {
            valueUserListener = createFirebaseUsersListeners();
            childMessagesListener = createFirebaseChatListener();
        }
        mRefUsers = new Firebase(ConstantsFirebase.FIREBASE_URL).child(ConstantsFirebase.FIREBASE_LOCATION_USERS);
        mRefUsers.addValueEventListener(valueUserListener);


        mRefMessages = new Firebase(ConstantsFirebase.FIREBASE_URL_CHAT).child(mChildChatKey);
        mRefMessages.keepSynced(true);
        Query chatsRef = mRefMessages.orderByKey().limitToLast(50);

        chatsRef.addChildEventListener(childMessagesListener);
    }

    private void sendMessage() {
        String msg = mEdtMsgContent.getText().toString();
        if (validateMsgContent(msg)) {

            Firebase firebaseRef = new Firebase(ConstantsFirebase.FIREBASE_URL_CHAT).child(mChildChatKey);
            Firebase chatRef = firebaseRef.push();
            HashMap<String, Object> timeSended = new HashMap<>();
            timeSended.put(Constants.KEY_CHAT_TIME_SENDED, ServerValue.TIMESTAMP);
            Message message = new Message(mEncodedMail, msg, timeSended);
            chatRef.setValue(message);
            mEdtMsgContent.setText("");
        } else {
            Toast.makeText(getActivity(), getString(R.string.notice_insert_message), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateMsgContent(String msg) {
        return !msg.isEmpty();
    }

    /**
     * Criando Listener para observar nó messagens de determinado chat
     *
     * @return
     */
    private ChildEventListener createFirebaseChatListener() {
        return new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                    Message message = dataSnapshot.getValue(Message.class);
                    mMessages.add(message);
                    mKeys.add(dataSnapshot.getKey());

                    int posAdded = mMessages.size() - 1;
                    mRecyclerViewChat.scrollToPosition(posAdded);
                    mAdapter.notifyItemInserted(posAdded);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                    int index = mKeys.indexOf(dataSnapshot.getKey());
                    if (index != -1) {
                        mMessages.set(index, dataSnapshot.getValue(Message.class));
                        mAdapter.notifyItemChanged(index);
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    int index = mKeys.indexOf(dataSnapshot.getKey());
                    if (index != -1) {
                        mMessages.remove(index);
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

    private ValueEventListener createFirebaseUsersListeners() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        mUsers.add(snapshot.getValue(User.class));
                        mUsersEmails.add(snapshot.getValue(User.class).getEmail());
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        };
    }

    /**
     * Remove listeners dos objetos Firebase
     */
    private void removeFirebaseListeners() {
        if (childMessagesListener != null && valueUserListener != null) {
            mRefMessages.removeEventListener(childMessagesListener);
            mRefUsers.removeEventListener(valueUserListener);
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

    /**
     * Resgata click do adapter por meio da interface implementada para exibir usuário da mensagem
     *
     * @param view_id
     * @param position
     */
    @Override
    public void onRecycleItemClick(int view_id, int position) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        int userIndex = mUsersEmails.indexOf(mMessages.get(position).getEmail());
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
