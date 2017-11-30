package com.ygorcesar.jamdroidfirechat.viewmodel;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ygorcesar.jamdroidfirechat.R;
import com.ygorcesar.jamdroidfirechat.model.MapLocation;
import com.ygorcesar.jamdroidfirechat.model.Message;
import com.ygorcesar.jamdroidfirechat.utils.Constants;
import com.ygorcesar.jamdroidfirechat.utils.ConstantsFirebase;
import com.ygorcesar.jamdroidfirechat.utils.PushNotificationObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class MessageFragmViewModel {
    private static final String TAG = "MessageFragmViewModel";

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
        switch (view.getId()) {
            case R.id.btn_send_message:
                sendMessage(mMessageFragmViewModelContract.getEditTextMessage(), mChildChatKey,
                        ConstantsFirebase.MESSAGE_TYPE_TEXT, null);
                break;
            case R.id.iv_camera:
                mMessageFragmViewModelContract.showImagePicker();
                break;
            case R.id.ln_menu_item_camera:
                mMessageFragmViewModelContract.actionMenuItemCamera();
                break;
            case R.id.ln_menu_item_gallery:
                mMessageFragmViewModelContract.actionMenuItemGallery();
                break;
            case R.id.ln_menu_item_location:
                mMessageFragmViewModelContract.actionMenuItemLocation();
                break;
        }
    }

    private void sendMessage(String content, String childChatKey, int messageType, @Nullable MapLocation location) {
        if (validateMsgContent(content)) {
            DatabaseReference chatRef = FirebaseDatabase.getInstance()
                    .getReference(ConstantsFirebase.FIREBASE_LOCATION_CHAT)
                    .child(childChatKey)
                    .push();
            HashMap<String, Object> timeSended = new HashMap<>();
            timeSended.put(Constants.KEY_CHAT_TIME_SENDED, ServerValue.TIMESTAMP);

            Message message;
            if (messageType == ConstantsFirebase.MESSAGE_TYPE_LOCATION && location != null) {
                message = new Message(mLoggedUserEmail, content, messageType, timeSended, location);
            } else {
                message = new Message(mLoggedUserEmail, content, messageType, timeSended);
            }
            chatRef.setValue(message, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    databaseReference.child(ConstantsFirebase.FIREBASE_PROPERTY_MESSAGE_STATUS)
                            .setValue(ConstantsFirebase.MESSAGE_STATUS_SENDED);
                }
            });
            sendNotification(content, messageType, chatRef.getKey());
            mMessageFragmViewModelContract.setEditTextMessage("");
        } else {
            mMessageFragmViewModelContract.showToastMessage(R.string.notice_insert_message);
        }
    }

    private void sendNotification(String msg, int msgType, String msgKey) {
        String fcmSenderKey = mChildChatKey.equals(ConstantsFirebase.FIREBASE_LOCATION_CHAT_GLOBAL) ?
                ConstantsFirebase.FIREBASE_TOPIC_CHAT_GLOBAL_TO :
                FirebaseInstanceId.getInstance().getToken();

        if (msgType == ConstantsFirebase.MESSAGE_TYPE_IMAGE) {
            msg = FirebaseDatabase.getInstance().getApp()
                    .getApplicationContext().getString(R.string.notification_msg_image);
        } else if (msgType == ConstantsFirebase.MESSAGE_TYPE_LOCATION) {
            msg = FirebaseDatabase.getInstance().getApp()
                    .getApplicationContext().getString(R.string.notification_msg_location);
        }

        PushNotificationObject notificationObject = new PushNotificationObject(
                mFcmUserDeviceId, new PushNotificationObject
                .AdditionalData(mUserName, msg, mChildChatKey, msgKey, mUserName,
                mLoggedUserEmail, mFcmUserDeviceId, fcmSenderKey));
    }

    public void sendLocationMessage(String latitude, String longitude) {
        MapLocation location = new MapLocation(latitude, longitude);
        sendMessage(String.format("%s,%s", latitude, longitude),
                mChildChatKey, ConstantsFirebase.MESSAGE_TYPE_LOCATION, location);
    }

    public void uploadImageToFirebase(byte[] data) {
        String time = "";
        try {
            time = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        } catch (Exception e) {
            e.printStackTrace();
        }

        StorageReference storageRef = FirebaseStorage.getInstance()
                .getReference()
                .child(mChildChatKey)
                .child(String.format("IMG_%s.jpg", time));
        UploadTask uploadTask = storageRef.putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                if (downloadUrl != null) {
                    sendMessage(downloadUrl.toString(), mChildChatKey,
                            ConstantsFirebase.MESSAGE_TYPE_IMAGE, null);
                    mMessageFragmViewModelContract.uploadTask(true);
                }
            }
        });
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "UploadImageToFirebase onFailure: ", e);
                mMessageFragmViewModelContract.uploadTask(false);
            }
        });
    }

    private boolean validateMsgContent(String msg) {
        return !msg.isEmpty();
    }
}
