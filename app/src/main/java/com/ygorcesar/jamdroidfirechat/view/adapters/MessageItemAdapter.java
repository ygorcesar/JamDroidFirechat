package com.ygorcesar.jamdroidfirechat.view.adapters;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.ygorcesar.jamdroidfirechat.R;
import com.ygorcesar.jamdroidfirechat.databinding.AdapterItemMessageBinding;
import com.ygorcesar.jamdroidfirechat.model.Message;
import com.ygorcesar.jamdroidfirechat.model.User;
import com.ygorcesar.jamdroidfirechat.viewmodel.MessageAdapterViewModel;
import com.ygorcesar.jamdroidfirechat.viewmodel.MessageAdapterViewModelContract;

import java.util.Collections;
import java.util.List;

public class MessageItemAdapter extends RecyclerView.Adapter<MessageItemAdapter.ItemViewHolder> {
    private MessageAdapterViewModelContract mMessageAdapterViewModelContract;
    private List<User> mUsers;
    private List<String> mUsersEmails;
    private List<Message> mMessages;
    private String mLoggedUserEmail;

    public MessageItemAdapter(MessageAdapterViewModelContract messageAdapterViewModelContract,
                              String loggedUserEmail, List<String> usersEmails) {
        mMessageAdapterViewModelContract = messageAdapterViewModelContract;
        mLoggedUserEmail = loggedUserEmail;
        mMessages = Collections.emptyList();
        mUsers = Collections.emptyList();
        mUsersEmails = usersEmails;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        AdapterItemMessageBinding adapterItemMessageBinding = DataBindingUtil.inflate(LayoutInflater
                .from(parent.getContext()), R.layout.adapter_item_message, parent, false);
        return new ItemViewHolder(adapterItemMessageBinding);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        Message message = mMessages.get(position);
        int index = mUsersEmails.indexOf(message.getEmail());
        if ( index!= -1) {
            holder.bindMessage(mUsers.get(index), message);
        }
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    public void setMessages(List<Message> messages) {
        mMessages = messages;
    }

    public void setUsers(List<User> users) {
        mUsers = users;
    }

    public void clear(){
        mMessages.clear();
        mUsers.clear();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        private AdapterItemMessageBinding mAdapterItemMessageBinding;

        public ItemViewHolder(AdapterItemMessageBinding adapterItemMessageBinding) {
            super(adapterItemMessageBinding.rvContainer);
            mAdapterItemMessageBinding = adapterItemMessageBinding;

        }

        public void bindMessage(User user, Message message) {
            if (mAdapterItemMessageBinding.getMessageViewModel() == null) {
                mAdapterItemMessageBinding.setMessageViewModel(new MessageAdapterViewModel(user,
                        mLoggedUserEmail, message, mMessageAdapterViewModelContract));
            } else {
                mAdapterItemMessageBinding.getMessageViewModel().setUser(user);
                mAdapterItemMessageBinding.getMessageViewModel().setMessage(message);
            }
        }
    }
}