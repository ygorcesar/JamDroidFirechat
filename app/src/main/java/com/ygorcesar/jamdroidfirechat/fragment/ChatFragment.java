package com.ygorcesar.jamdroidfirechat.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.ygorcesar.jamdroidfirechat.R;
import com.ygorcesar.jamdroidfirechat.adapters.ChatItemAdapter;
import com.ygorcesar.jamdroidfirechat.model.Chat;
import com.ygorcesar.jamdroidfirechat.utils.Constants;
import com.ygorcesar.jamdroidfirechat.utils.ConstantsFirebase;

public class ChatFragment extends Fragment implements View.OnClickListener {

    private RecyclerView mRecyclerViewChat;
    private FloatingActionButton mFabSendMsg;
    private AppCompatEditText mEdtMsgContent;
    private String mEncodedMail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);

        mRecyclerViewChat = (RecyclerView) rootView.findViewById(R.id.rv_chat);
        mRecyclerViewChat.setHasFixedSize(true);
        mRecyclerViewChat.setLayoutManager(new LinearLayoutManager(getActivity()));

        mFabSendMsg = (FloatingActionButton) rootView.findViewById(R.id.btn_send_message);
        mEdtMsgContent = (AppCompatEditText) rootView.findViewById(R.id.edt_message_content);

        initializeScreen();

        return rootView;
    }

    /**
     * Initialize Listeners, adapter on recycler view...
     */
    private void initializeScreen() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mEncodedMail = prefs.getString(Constants.KEY_ENCODED_EMAIL, "");
        mFabSendMsg.setOnClickListener(this);

        ChatItemAdapter chatAdapter = new ChatItemAdapter();
        mRecyclerViewChat.setAdapter(chatAdapter);
    }

    private void sendMessage() {
        String msg = mEdtMsgContent.getText().toString();
        if (validateMsgContent(msg)) {

            Firebase firebaseRef = new Firebase(ConstantsFirebase.FIREBASE_URL_CHAT);
            Firebase chatRef = firebaseRef.push();

            Chat chat = new Chat(mEncodedMail, msg);
            chatRef.setValue(chat);
        } else {
            Toast.makeText(getActivity(), getString(R.string.notice_insert_message), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateMsgContent(String msg) {
        return !msg.isEmpty();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send_message:
                sendMessage();
                break;
        }
    }
}
