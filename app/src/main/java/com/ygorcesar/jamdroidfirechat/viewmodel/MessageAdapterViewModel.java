package com.ygorcesar.jamdroidfirechat.viewmodel;

import android.view.View;

import com.ygorcesar.jamdroidfirechat.model.Message;
import com.ygorcesar.jamdroidfirechat.model.User;
import com.ygorcesar.jamdroidfirechat.utils.Constants;

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
        mMessageAdapterViewModelContract.onMessageItemClick(mUser);
    }
}
