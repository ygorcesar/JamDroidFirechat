package com.ygorcesar.jamdroidfirechat.ui.users

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ygorcesar.jamdroidfirechat.data.entity.User
import com.ygorcesar.jamdroidfirechat.databinding.UsersItemBinding
import com.ygorcesar.jamdroidfirechat.ui.BaseAdapter
import com.ygorcesar.jamdroidfirechat.ui.BaseViewHolder
import durdinapps.rxfirebase2.RxFirebaseChildEvent

class UsersAdapter(val onClick: (User, View) -> Unit) : BaseAdapter<UsersItemBinding, UsersAdapter.UserViewHolder, User, RxFirebaseChildEvent<User>>() {

    override fun provideViewHolder(inflater: LayoutInflater, parent: ViewGroup, viewType: Int) = UserViewHolder(UsersItemBinding.inflate(inflater, parent, false))

    inner class UserViewHolder(binding: UsersItemBinding) : BaseViewHolder<UsersItemBinding, RxFirebaseChildEvent<User>>(binding) {

        override fun onBind(entity: RxFirebaseChildEvent<User>) {
            if (binding.userBinding == null) {
                binding.userBinding = UserBinding(entity.value, onClick)
            } else {
                binding.userBinding?.user = entity.value
            }
        }
    }
}