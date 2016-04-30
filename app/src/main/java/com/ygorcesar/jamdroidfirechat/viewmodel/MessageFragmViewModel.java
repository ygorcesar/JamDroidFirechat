package com.ygorcesar.jamdroidfirechat.viewmodel;

import android.view.View;

import com.firebase.client.Firebase;
import com.firebase.client.ServerValue;
import com.ygorcesar.jamdroidfirechat.R;
import com.ygorcesar.jamdroidfirechat.model.Message;
import com.ygorcesar.jamdroidfirechat.utils.Constants;
import com.ygorcesar.jamdroidfirechat.utils.ConstantsFirebase;

import java.util.HashMap;

public class MessageFragmViewModel {

    private MessageFragmViewModelContract mMessageFragmViewModelContract;
    private String mLoggedUserEmail;
    private String mChildChatKey;

    public MessageFragmViewModel(MessageFragmViewModelContract messageFragmViewModelContract,
                                 String loggedUserEmail, String childChatKey) {
        mMessageFragmViewModelContract = messageFragmViewModelContract;
        mLoggedUserEmail = loggedUserEmail;
        mChildChatKey = childChatKey;
    }

    public void onClick(View view) {
        sendMessage(mMessageFragmViewModelContract.getEditTextMessage(), mChildChatKey);
    }

    private void sendMessage(String msg, String childChatKey) {
        if (validateMsgContent(msg)) {
            Firebase firebaseRef = new Firebase(ConstantsFirebase.FIREBASE_URL_CHAT).child(childChatKey);
            Firebase chatRef = firebaseRef.push();
            HashMap<String, Object> timeSended = new HashMap<>();
            timeSended.put(Constants.KEY_CHAT_TIME_SENDED, ServerValue.TIMESTAMP);
            Message message = new Message(mLoggedUserEmail, msg, timeSended);
            chatRef.setValue(message);
            mMessageFragmViewModelContract.setEditTextMessage("");
        } else {
            mMessageFragmViewModelContract.showToastMessage(R.string.notice_insert_message);
        }
    }

    private boolean validateMsgContent(String msg) {
        return !msg.isEmpty();
    }
}
