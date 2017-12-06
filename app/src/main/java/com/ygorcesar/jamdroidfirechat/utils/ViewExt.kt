package com.ygorcesar.jamdroidfirechat.utils

import android.support.annotation.DrawableRes
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

fun RecyclerView._setLinearLayoutManager(withDivider: Boolean = false, orientation: Int = LinearLayoutManager.VERTICAL) {
    this.layoutManager = LinearLayoutManager(this.context, orientation, false)
    this.setHasFixedSize(true)
    if (withDivider) {
        this.addItemDecoration(DividerItemDecoration(this.context, orientation))
    }
}

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun ImageView.loadImageUrl(url: String, @DrawableRes idRes: Int) {
    val options = RequestOptions()
            .placeholder(idRes)
            .fitCenter()
            .dontAnimate()

    Glide.with(this)
            .load(url)
            .apply(options)
            .into(this)
}