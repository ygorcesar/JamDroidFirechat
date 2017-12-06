package com.ygorcesar.jamdroidfirechat.ui.users

import android.databinding.DataBindingUtil
import android.view.LayoutInflater
import android.view.ViewGroup
import com.ygorcesar.jamdroidfirechat.R
import com.ygorcesar.jamdroidfirechat.data.entity.User
import com.ygorcesar.jamdroidfirechat.databinding.UsersItemBinding
import com.ygorcesar.jamdroidfirechat.ui.BaseAdapter
import com.ygorcesar.jamdroidfirechat.ui.BaseViewHolder

class UsersAdapter(val onClick: (User) -> Unit) : BaseAdapter<UsersItemBinding, UsersAdapter.UserViewHolder, User>() {

    override fun provideViewHolder(inflater: LayoutInflater, parent: ViewGroup): UserViewHolder {
        val binding = DataBindingUtil.inflate<UsersItemBinding>(inflater, R.layout.users_item, parent, false)
        return UserViewHolder(binding)
    }

    inner class UserViewHolder(binding: UsersItemBinding) : BaseViewHolder<UsersItemBinding, User>(binding) {

        override fun onBind(entity: User) {
            binding.user = UserBinding(entity, onClick)
        }
    }
}