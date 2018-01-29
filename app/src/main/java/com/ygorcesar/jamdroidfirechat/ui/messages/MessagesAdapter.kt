package com.ygorcesar.jamdroidfirechat.ui.messages

import android.databinding.ViewDataBinding
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ygorcesar.jamdroidfirechat.data.entity.Message
import com.ygorcesar.jamdroidfirechat.data.entity.User
import com.ygorcesar.jamdroidfirechat.databinding.MessagesItemBinding
import com.ygorcesar.jamdroidfirechat.databinding.MessagesItemImageBinding
import com.ygorcesar.jamdroidfirechat.ui.BaseAdapter
import com.ygorcesar.jamdroidfirechat.ui.BaseViewHolder
import com.ygorcesar.jamdroidfirechat.utils.ConstantsFirebase
import durdinapps.rxfirebase2.RxFirebaseChildEvent
import org.jetbrains.anko.error

class MessagesAdapter(val userEmail: String, val fetchUserOfMessage: (messageEmail: String, onFetch: (user: User) -> Unit, onError: (err: Throwable) -> Unit) -> Unit,
                      private val onMessageClick: (view: View?, message: RxFirebaseChildEvent<Message>, isLongClick: Boolean) -> Unit) :
        BaseAdapter<ViewDataBinding, BaseViewHolder<ViewDataBinding, RxFirebaseChildEvent<Message>>, Message, RxFirebaseChildEvent<Message>>() {

    override fun getItemViewType(position: Int) = entities[position].value.type

    override fun provideViewHolder(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): BaseViewHolder<ViewDataBinding, RxFirebaseChildEvent<Message>> {
        return when (viewType) {
            ConstantsFirebase.MessageType.TEXT -> MessageViewHolder(MessagesItemBinding.inflate(inflater, parent, false))
            ConstantsFirebase.MessageType.LOCATION -> MessageViewHolder(MessagesItemImageBinding.inflate(inflater, parent, false))
            ConstantsFirebase.MessageType.IMAGE -> MessageViewHolder(MessagesItemImageBinding.inflate(inflater, parent, false))
            else -> throw IllegalArgumentException()
        }
    }

    inner class MessageViewHolder(binding: ViewDataBinding) : BaseViewHolder<ViewDataBinding, RxFirebaseChildEvent<Message>>(binding) {

        override fun onBind(entity: RxFirebaseChildEvent<Message>) {
            fetchUserOfMessage(entity.value.email, { user ->
                when (binding) {
                    is MessagesItemBinding -> bindMessage(binding, entity, user)
                    is MessagesItemImageBinding -> bindMessageImageOrMap(binding, entity, user)
                    else -> throw IllegalArgumentException()
                }
            }, { error("Error on fetch user!", it) })
        }
    }

    private fun bindMessage(bind: MessagesItemBinding, entity: RxFirebaseChildEvent<Message>, user: User) {
        val isSender = isSender(userEmail, entity.value.email)
        if (bind.binding == null) {
            bind.binding = MessagesBinding(user, entity, isSender, onMessageClick)
        } else {
            bind.binding?.apply {
                this.user = user
                this.message = entity
                this.isSender = isSender
            }
        }
    }

    private fun bindMessageImageOrMap(bind: MessagesItemImageBinding, entity: RxFirebaseChildEvent<Message>, user: User) {
        val isSender = isSender(userEmail, entity.value.email)
        if (bind.binding == null) {
            bind.binding = MessagesBinding(user, entity, isSender, onMessageClick)
        } else {
            bind.binding?.apply {
                this.user = user
                this.message = entity
                this.isSender = isSender
            }
        }
    }


    private fun isSender(userEmail: String, messageEmail: String) = userEmail == messageEmail
}