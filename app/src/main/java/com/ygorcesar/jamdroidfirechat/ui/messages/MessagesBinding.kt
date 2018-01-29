package com.ygorcesar.jamdroidfirechat.ui.messages

import android.databinding.BindingAdapter
import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.ygorcesar.jamdroidfirechat.R
import com.ygorcesar.jamdroidfirechat.data.entity.Message
import com.ygorcesar.jamdroidfirechat.data.entity.User
import com.ygorcesar.jamdroidfirechat.databinding.BaseBinding
import com.ygorcesar.jamdroidfirechat.extensions.lessThanOneDay
import com.ygorcesar.jamdroidfirechat.utils.Constants
import com.ygorcesar.jamdroidfirechat.utils.ConstantsFirebase
import durdinapps.rxfirebase2.RxFirebaseChildEvent
import durdinapps.rxfirebase2.RxFirebaseChildEvent.EventType.ADDED
import java.sql.Date
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class MessagesBinding(user: User, message: RxFirebaseChildEvent<Message>, var isSender: Boolean,
                      private val onMessageClick: (view: View?, message: RxFirebaseChildEvent<Message>, isLongClick: Boolean) -> Unit) : BaseBinding(user) {
    var message: RxFirebaseChildEvent<Message> = message
        set(value) {
            field = value
            notifyChange()
        }

    fun getMessageEmail() = message.value.email

    fun getMessageText() = message.value.message

    fun getMapLocation() = message.value.mapLocation

    fun getType() = message.value.type

    fun getIsSender() = isSender

    fun getTime(): String {
        val timestamp = Timestamp(message.value.time[ConstantsFirebase.FIREBASE_PROPERTY_MESSAGE_TIME_SENDED] as Long)
        val pattern = if (timestamp.lessThanOneDay()) Constants.DATE_PATTERN_DAY_MONTH_YEAR_HOUR_MINUTE else Constants.DATE_PATTERN_HOUR_MINUTE
        val date = Date(timestamp.time)
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        sdf.timeZone = TimeZone.getDefault()
        return sdf.format(date)
    }

    fun onLongClick(): Boolean {
        if (isSender) onMessageClick(null, message, true)
        return true
    }

    fun onImageClick(view: View)  = onMessageClick(view, message, false)

    @DrawableRes
    fun getBackgroundRes() = if (isSender) R.drawable.bg_message_sender else R.drawable.bg_message

    fun getColorMessageText() = if (isSender) R.color.white else R.color.colorSecondaryText
}

@BindingAdapter("bindMessages")
fun bindingAdapter(recyclerView: RecyclerView, message: RxFirebaseChildEvent<Message>?) {
    val adapter = recyclerView.adapter
    if (adapter is MessagesAdapter) {
        message?.let {
            adapter.onItemChanged(message)
            if (message.eventType == ADDED) recyclerView.smoothScrollToPosition(adapter.entities.lastIndex)
        }
    }
}

@BindingAdapter("bindBackground")
fun bindBackground(view: View, @DrawableRes idRes: Int) {
    if (idRes != 0) view.setBackgroundResource(idRes)
}

@BindingAdapter("bindMessageColor")
fun bindMessageColor(textView: TextView, idRes: Int) {
    if (idRes != 0) textView.setTextColor(ContextCompat.getColor(textView.context, idRes))
}