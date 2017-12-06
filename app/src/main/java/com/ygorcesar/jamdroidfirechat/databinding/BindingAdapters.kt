package com.ygorcesar.jamdroidfirechat.databinding

import android.databinding.BindingAdapter
import android.widget.ImageView
import com.ygorcesar.jamdroidfirechat.R
import com.ygorcesar.jamdroidfirechat.utils.loadImageUrl


@BindingAdapter("bindUserPhoto")
fun bindUserPhoto(view: ImageView, url: String) {
    if (url.isNotEmpty()) {
        view.loadImageUrl(url, R.drawable.ic_person)
    } else {
        view.setImageResource(R.drawable.ic_person)
    }
}