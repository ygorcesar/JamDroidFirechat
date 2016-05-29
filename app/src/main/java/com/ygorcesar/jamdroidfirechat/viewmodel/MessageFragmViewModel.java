package com.ygorcesar.jamdroidfirechat.viewmodel;

import android.view.View;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.iid.FirebaseInstanceId;
import com.ygorcesar.jamdroidfirechat.R;
import com.ygorcesar.jamdroidfirechat.model.Message;
import com.ygorcesar.jamdroidfirechat.utils.Constants;
import com.ygorcesar.jamdroidfirechat.utils.ConstantsFirebase;
import com.ygorcesar.jamdroidfirechat.utils.PushNotificationObject;
import com.ygorcesar.jamdroidfirechat.utils.Singleton;

import java.util.HashMap;

public class MessageFragmViewModel {

    private MessageFragmViewModelContract mMessageFragmViewModelContract;
    private String mLoggedUserEmail;
    private String mChildChatKey;
    private String mFcmUserDeviceId;
    private String mUserName;

    public MessageFragmViewModel(MessageFragmViewModelContract messageFragmViewModelContract,
                                 String loggedUserEmail, String childChatKey,
                                 String fcmUserDeviceId, String userName) {
        mMessageFragmViewModelContract = messageFragmViewModelContract;
        mLoggedUserEmail = loggedUserEmail;
        mChildChatKey = childChatKey;
        mFcmUserDeviceId = fcmUserDeviceId;
        mUserName = userName;
    }

    public void onClick(View view) {
        sendMessage(mMessageFragmViewModelContract.getEditTextMessage(), mChildChatKey);
    }

    private void sendMessage(String msg, String childChatKey) {
        if (validateMsgContent(msg)) {
            DatabaseReference chatRef = FirebaseDatabase.getInstance()
                    .getReference(ConstantsFirebase.FIREBASE_LOCATION_CHAT)
                    .child(childChatKey)
                    .push();
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
        String fcmSenderKey = mChildChatKey.equals(ConstantsFirebase.FIREBASE_LOCATION_CHAT_GLOBAL) ?
                ConstantsFirebase.FIREBASE_TOPIC_CHAT_GLOBAL_TO :
                FirebaseInstanceId.getInstance().getToken();

        PushNotificationObject notificationObject = new PushNotificationObject(
                mFcmUserDeviceId, new PushNotificationObject
                .AdditionalData(mUserName, msg, mChildChatKey, mUserName,
                mLoggedUserEmail, mFcmUserDeviceId, fcmSenderKey));
        Singleton.getInstance().sendMsgPushNotification(notificationObject);
    }

    private boolean validateMsgContent(String msg) {
        return !msg.isEmpty();
    }
}
