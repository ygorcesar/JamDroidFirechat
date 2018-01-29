package com.ygorcesar.jamdroidfirechat.databinding

import android.databinding.BindingAdapter
import android.widget.ImageView
import com.ygorcesar.jamdroidfirechat.R
import com.ygorcesar.jamdroidfirechat.data.entity.Message
import com.ygorcesar.jamdroidfirechat.extensions.loadImageUrl
import com.ygorcesar.jamdroidfirechat.utils.ConstantsFirebase
import durdinapps.rxfirebase2.RxFirebaseChildEvent


@BindingAdapter("bindUserPhoto")
fun bindUserPhoto(view: ImageView, url: String?) {
    if (url.isNullOrEmpty()) {
        view.setImageResource(R.drawable.ic_person)
    } else {
        view.loadImageUrl(url!!, R.drawable.ic_person)
    }
}

@BindingAdapter("bindMessageType")
fun bindMessageType(view: ImageView, rxMessage: RxFirebaseChildEvent<Message>?) {
    rxMessage?.value?.let {
        when (it.type) {
            ConstantsFirebase.MessageType.IMAGE -> view.loadImageUrl(it.imgUrl, withAnimate = true)
            ConstantsFirebase.MessageType.LOCATION -> it.mapLocation?.apply {
                val url = view.context.getString(R.string.map_static_url, latitude, longitude)
                view.loadImageUrl(url, R.drawable.ic_map_placeholder, true)
            }
            else -> print("Do nothing!")
        }
    }
}