package com.ygorcesar.jamdroidfirechat.adapters;

import android.content.Context;
import android.support.v4.widget.Space;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.ygorcesar.jamdroidfirechat.R;
import com.ygorcesar.jamdroidfirechat.model.Chat;
import com.ygorcesar.jamdroidfirechat.model.User;
import com.ygorcesar.jamdroidfirechat.utils.Constants;
import com.ygorcesar.jamdroidfirechat.utils.OnRecyclerItemClickListener;
import com.ygorcesar.jamdroidfirechat.utils.Utils;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatItemAdapter extends RecyclerView.Adapter<ChatItemAdapter.ItemViewHolder> {
    private Context mContext;
    private List<User> mUsers;
    private List<String> mUsersEmails;
    private List<Chat> mChats;
    private LayoutInflater mLayoutInflater;
    private String mEmailUser;
    private OnRecyclerItemClickListener mOnRecyclerItemClickListener;

    public ChatItemAdapter(Context c, List<Chat> chats, List<User> users,
                           List<String> usersEmails, String emailUser) {
        this.mContext = c;
        this.mLayoutInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mUsers = users;
        this.mUsersEmails = usersEmails;
        this.mChats = chats;
        this.mEmailUser = emailUser;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.adapter_item_message, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        Chat chat = mChats.get(position);
        int index = mUsersEmails.indexOf(chat.getEmail());
        if (index != -1) {
            loadImageWithPicasso(holder.ivUserPhotoSender, mUsers.get(index).getPhotoUrl());
        }
        holder.tvUserEmail.setText(Utils.decodeEmail(chat.getEmail()));
        holder.tvMessage.setText(chat.getMessage());
        holder.tvMessageTime.setText(Utils.timestampToHour(chat.getTime().get(Constants.KEY_CHAT_TIME_SENDED)));
        holder.setViewIsSender(isSender(chat.getEmail()));
    }

    @Override
    public int getItemCount() {
        return mChats.size();
    }

    private boolean isSender(String email) {
        return email.equals(mEmailUser);
    }

    private void loadImageWithPicasso(ImageView imageView, String url) {
        Picasso.with(mContext).load(url)
                .placeholder(R.drawable.ic_person)
                .into(imageView);
    }

    public void setOnRecyclerItemClickListener(OnRecyclerItemClickListener listener) {
        this.mOnRecyclerItemClickListener = listener;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout rvMessageContainer;
        private CircleImageView ivUserPhotoSender;
        private TextView tvUserEmail;
        private TextView tvMessage;
        private TextView tvMessageTime;
        private Space spaceLeft;
        private Space spaceRight;

        public ItemViewHolder(View itemView) {
            super(itemView);
            rvMessageContainer = (RelativeLayout) itemView.findViewById(R.id.rv_message_container);
            ivUserPhotoSender = (CircleImageView) itemView.findViewById(R.id.iv_item_user_photo_sender);
            tvUserEmail = (TextView) itemView.findViewById(R.id.tv_item_user_email);
            tvMessage = (TextView) itemView.findViewById(R.id.tv_item_message);
            tvMessageTime = (TextView) itemView.findViewById(R.id.tv_item_message_time);
            spaceLeft = (Space) itemView.findViewById(R.id.space_left);
            spaceRight = (Space) itemView.findViewById(R.id.space_right);

            rvMessageContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnRecyclerItemClickListener.onRecycleItemClick(v.getId(), getAdapterPosition());
                }
            });
        }

        private void setViewIsSender(boolean isSender) {
            if (isSender) {
                spaceRight.setVisibility(View.VISIBLE);
                spaceLeft.setVisibility(View.GONE);
            } else {
                spaceRight.setVisibility(View.GONE);
                spaceLeft.setVisibility(View.VISIBLE);
            }
        }
    }
}