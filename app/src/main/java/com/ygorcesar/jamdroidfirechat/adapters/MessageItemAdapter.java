package com.ygorcesar.jamdroidfirechat.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.ygorcesar.jamdroidfirechat.BR;
import com.ygorcesar.jamdroidfirechat.R;
import com.ygorcesar.jamdroidfirechat.model.Message;
import com.ygorcesar.jamdroidfirechat.model.User;
import com.ygorcesar.jamdroidfirechat.utils.OnRecyclerItemClickListener;

import java.util.List;

public class MessageItemAdapter extends RecyclerView.Adapter<MessageItemAdapter.ItemViewHolder> {
    private List<User> mUsers;
    private List<String> mUsersEmails;
    private List<Message> mMessages;
    private LayoutInflater mLayoutInflater;
    private OnRecyclerItemClickListener mOnRecyclerItemClickListener;

    public MessageItemAdapter(Context c, List<Message> messages, List<User> users, List<String> usersEmails) {
        this.mLayoutInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mUsers = users;
        this.mUsersEmails = usersEmails;
        this.mMessages = messages;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.adapter_item_message, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        final Message message = mMessages.get(position);
        final User user  = mUsers.get(mUsersEmails.indexOf(message.getEmail()));
        holder.getBinding().setVariable(BR.message, message);
        holder.getBinding().setVariable(BR.user, user);
        holder.getBinding().executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    public void setOnRecyclerItemClickListener(OnRecyclerItemClickListener listener) {
        this.mOnRecyclerItemClickListener = listener;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        private ViewDataBinding binding;
        private RelativeLayout rvMessageContainer;

        public ItemViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
            rvMessageContainer = (RelativeLayout) itemView.findViewById(R.id.rv_message_container);

            rvMessageContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnRecyclerItemClickListener.onRecycleItemClick(v.getId(), getAdapterPosition());
                }
            });
        }

        public ViewDataBinding getBinding() {
            return binding;
        }
    }
}