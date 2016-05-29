package com.ygorcesar.jamdroidfirechat.view.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.ygorcesar.jamdroidfirechat.R;
import com.ygorcesar.jamdroidfirechat.databinding.AdapterItemChatsBinding;
import com.ygorcesar.jamdroidfirechat.model.User;
import com.ygorcesar.jamdroidfirechat.viewmodel.ChatsViewModel;
import com.ygorcesar.jamdroidfirechat.viewmodel.ChatsViewModelContract;

import java.util.Collections;
import java.util.List;

public class ChatsItemAdapter extends RecyclerView.Adapter<ChatsItemAdapter.ItemViewHolder> {
    private List<User> mUsers;
    private ChatsViewModelContract mChatsViewModelContract;
    private String mLoggedUserEmail;

    public ChatsItemAdapter(ChatsViewModelContract chatsViewModelContract, String loggedUserEmail) {
        this.mUsers = Collections.emptyList();
        mChatsViewModelContract = chatsViewModelContract;
        mLoggedUserEmail = loggedUserEmail;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        AdapterItemChatsBinding adapterItemChatsBinding = DataBindingUtil.inflate(LayoutInflater
                .from(parent.getContext()), R.layout.adapter_item_chats, parent, false);
        return new ItemViewHolder(adapterItemChatsBinding);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        holder.bindUser(mUsers.get(position));
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }


    public void setUsers(List<User> users) {
        mUsers = users;
        notifyDataSetChanged();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        private AdapterItemChatsBinding mAdapterItemChatsBinding;

        public ItemViewHolder(AdapterItemChatsBinding adapterItemChatsBinding) {
            super(adapterItemChatsBinding.lnItemRow);
            this.mAdapterItemChatsBinding = adapterItemChatsBinding;
        }

        public void bindUser(User user) {
            if (mAdapterItemChatsBinding.getChatsViewModel() == null) {
                mAdapterItemChatsBinding.setChatsViewModel(new ChatsViewModel(user, mLoggedUserEmail, mChatsViewModelContract));
            } else {
                mAdapterItemChatsBinding.getChatsViewModel().setUser(user);
            }
        }
    }
}
