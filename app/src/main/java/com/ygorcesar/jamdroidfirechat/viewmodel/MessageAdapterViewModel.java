package com.ygorcesar.jamdroidfirechat.viewmodel;

import android.view.View;

import com.ygorcesar.jamdroidfirechat.R;
import com.ygorcesar.jamdroidfirechat.model.MapLocation;
import com.ygorcesar.jamdroidfirechat.model.Message;
import com.ygorcesar.jamdroidfirechat.model.User;
import com.ygorcesar.jamdroidfirechat.utils.Constants;
import com.ygorcesar.jamdroidfirechat.utils.ConstantsFirebase;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MessageAdapterViewModel extends BaseChatViewModel {
    private MessageAdapterViewModelContract mMessageAdapterViewModelContract;
    private Message mMessage;

    public MessageAdapterViewModel(User user, String loggedUserEmail, Message message,
                                   MessageAdapterViewModelContract messageAdapterViewModelContract) {
        super(user, loggedUserEmail);
        mMessage = message;
        mMessageAdapterViewModelContract = messageAdapterViewModelContract;
    }

    public String getSenderEmail() {
        return mMessage.getEmail();
    }

    public String getMessage() {
        return mMessage.getMessage();
    }

    public int getType(){
        return mMessage.getType();
    }

    public boolean isTypeMessage(){
        return mMessage.getType() == ConstantsFirebase.MESSAGE_TYPE_TEXT;
    }

    public MapLocation getMapLocation(){
        return mMessage.getMapLocation();
    }

    /**
     * Transformando o timestamp do servidor para TimeZone padrão do Aparelho
     *
     * @return String
     */
    public String getTime() {
        Timestamp stamp = new Timestamp((long) mMessage.getTime().get(Constants.KEY_CHAT_TIME_SENDED));
        Date date = new Date(stamp.getTime());
        SimpleDateFormat sdf = new SimpleDateFormat("H:mm", new Locale("pt", "BR"));
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(date);
    }

    public void setMessage(Message message) {
        mMessage = message;
    }

    public void onItemClick(View view) {
        switch (view.getId()){
            case R.id.iv_item_user_photo_sender:
                mMessageAdapterViewModelContract.onMessageItemClick(mUser, view.getId());
                break;
            case R.id.tv_item_user_email:
                mMessageAdapterViewModelContract.onMessageItemClick(mUser, view.getId());
                break;
            case R.id.iv_image:
                switch (mMessage.getType()){
                    case ConstantsFirebase.MESSAGE_TYPE_IMAGE:
                        mMessageAdapterViewModelContract.onMessageItemClick(mMessage.getMessage(), view);
                        break;
                    case ConstantsFirebase.MESSAGE_TYPE_LOCATION:
                        mMessageAdapterViewModelContract.onMessageLocationClick(mMessage.getMapLocation());
                        break;
                }
                break;
        }
    }
}
