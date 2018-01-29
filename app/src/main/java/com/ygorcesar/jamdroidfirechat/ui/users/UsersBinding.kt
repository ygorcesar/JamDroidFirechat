package com.ygorcesar.jamdroidfirechat.ui.users

import android.databinding.BindingAdapter
import android.support.v7.widget.RecyclerView
import android.view.View
import com.ygorcesar.jamdroidfirechat.data.entity.User
import com.ygorcesar.jamdroidfirechat.databinding.BaseBinding
import durdinapps.rxfirebase2.RxFirebaseChildEvent


class UserBinding(user: User, val onClick: (User, View) -> Unit) : BaseBinding(user) {

    fun onItemClick(view: View) = onClick(user, view)
}

object UsersBindingAdapter {
    @BindingAdapter("bindUsers")
    @JvmStatic
    fun bindingAdapter(recyclerView: RecyclerView, users: MutableList<User>) {
        val adapter = recyclerView.adapter
        if (adapter is UsersAdapter) {
            adapter.entities = users
                    .map { RxFirebaseChildEvent(it.email, it, RxFirebaseChildEvent.EventType.ADDED) }
                    .toMutableList()
        }
    }
}