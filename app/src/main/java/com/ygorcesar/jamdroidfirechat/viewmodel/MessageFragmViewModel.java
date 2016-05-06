package com.ygorcesar.jamdroidfirechat.viewmodel;

import android.content.Context;
import android.view.View;

import com.firebase.client.Firebase;
import com.firebase.client.ServerValue;
import com.onesignal.OneSignal;
import com.ygorcesar.jamdroidfirechat.R;
import com.ygorcesar.jamdroidfirechat.model.Message;
import com.ygorcesar.jamdroidfirechat.utils.AditionalNotificationData;
import com.ygorcesar.jamdroidfirechat.utils.Constants;
import com.ygorcesar.jamdroidfirechat.utils.ConstantsFirebase;
import com.ygorcesar.jamdroidfirechat.utils.Utils;

import java.util.HashMap;

public class MessageFragmViewModel {

    private MessageFragmViewModelContract mMessageFragmViewModelContract;
    private String mLoggedUserEmail;
    private String mChildChatKey;
    private String mUserOneSignalId;
    private String mUserName;
    private Context mContext;

    public MessageFragmViewModel(Context context, MessageFragmViewModelContract messageFragmViewModelContract,
                                 String loggedUserEmail, String childChatKey,
                                 String userOneSignalId, String userName) {
        mContext = context;
        mMessageFragmViewModelContract = messageFragmViewModelContract;
        mLoggedUserEmail = loggedUserEmail;
        mChildChatKey = childChatKey;
        mUserOneSignalId = userOneSignalId;
        mUserName = userName;
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
            sendNotification(msg);
            mMessageFragmViewModelContract.setEditTextMessage("");
        } else {
            mMessageFragmViewModelContract.showToastMessage(R.string.notice_insert_message);
        }
    }

    private void sendNotification(String msg) {
        OneSignal.postNotification(Utils.generateNotificationJson(mContext, msg,
                new AditionalNotificationData(mChildChatKey, mUserName, mUserOneSignalId)), null);
    }

    private boolean validateMsgContent(String msg) {
        return !msg.isEmpty();
    }
}
