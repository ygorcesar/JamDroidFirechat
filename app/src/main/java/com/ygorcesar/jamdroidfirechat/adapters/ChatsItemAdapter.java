package com.ygorcesar.jamdroidfirechat.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.ygorcesar.jamdroidfirechat.BR;
import com.ygorcesar.jamdroidfirechat.R;
import com.ygorcesar.jamdroidfirechat.model.User;
import com.ygorcesar.jamdroidfirechat.utils.OnRecyclerItemClickListener;

import java.util.List;

public class ChatsItemAdapter extends RecyclerView.Adapter<ChatsItemAdapter.ItemViewHolder> {
    private List<User> mUsers;
    private LayoutInflater mLayoutInflater;
    private OnRecyclerItemClickListener mOnRecyclerItemClickListener;

    public ChatsItemAdapter(Context context, List<User> mUsers) {
        this.mUsers = mUsers;
        this.mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.adapter_item_chats, parent, false);
        return new ItemViewHolder(view);
    }

    @Override public void onBindViewHolder(ItemViewHolder holder, int position) {
        final User user = mUsers.get(position);
        holder.getBinding().setVariable(BR.user, user);
        holder.getBinding().executePendingBindings();
    }

    @Override public int getItemCount() {
        return mUsers.size();
    }

    public void setOnRecyclerItemClickListener(OnRecyclerItemClickListener listener) {
        this.mOnRecyclerItemClickListener = listener;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        private ViewDataBinding binding;
        private LinearLayout lnRow;

        public ItemViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
            lnRow = (LinearLayout) itemView.findViewById(R.id.ln_item_row);

            lnRow.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    mOnRecyclerItemClickListener.onRecycleItemClick(v.getId(), getAdapterPosition());
                }
            });
        }

        public ViewDataBinding getBinding() {
            return binding;
        }
    }
}
