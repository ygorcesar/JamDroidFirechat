package com.ygorcesar.jamdroidfirechat.extensions

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Dialog
import android.support.annotation.DrawableRes
import android.support.design.widget.BottomSheetDialog
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewAnimationUtils
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ygorcesar.jamdroidfirechat.R
import com.ygorcesar.jamdroidfirechat.ui.latestimages.ImageTile
import kotlinx.android.synthetic.main.latest_images_picker.view.*

fun RecyclerView._setLinearLayoutManager(withDivider: Boolean = false, orientation: Int = LinearLayoutManager.VERTICAL) {
    this.layoutManager = LinearLayoutManager(this.context, orientation, false)
    this.setHasFixedSize(true)
    if (withDivider) {
        this.addItemDecoration(DividerItemDecoration(this.context, orientation))
    }
}

fun RecyclerView._setGridLayoutManager(spaceCount: Int = 2, orientation: Int = GridLayoutManager.VERTICAL, isReverse: Boolean = false) {
    this.setHasFixedSize(true)
    this.layoutManager = GridLayoutManager(this.context, spaceCount, orientation, isReverse)
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

fun View.isVisible() = visibility == View.VISIBLE

fun ImageView.loadImageUrl(url: String, @DrawableRes idRes: Int = R.drawable.ic_image_placeholder, withAnimate: Boolean = false) {
    val options = RequestOptions().run {
        var opt = placeholder(idRes)
        opt = opt.fitCenter()
        if (!withAnimate) opt = opt.dontAnimate()
        opt
    }

    Glide.with(this)
            .load(url)
            .apply(options)
            .into(this)
}

fun View.enterCircularReveal(centerX: Int = measuredWidth, centerY: Int = measuredHeight) {
    val finalRadius = Math.max(width, height) / 1.2F
    visible()
    ViewAnimationUtils.createCircularReveal(this, centerX, centerY, 0F, finalRadius).start()
}

fun View.exitCircularReveal(centerX: Int = measuredWidth, centerY: Int = measuredHeight) {
    val initialRadius = width / 1.2F
    val anim = ViewAnimationUtils.createCircularReveal(this, centerX, centerY, initialRadius, 0F)
    anim.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            super.onAnimationEnd(animation)
            invisible()
        }
    })
    anim.start()
}

fun Fragment.generateBottomSheetImagePicker(onClick: (ImageTile) -> Unit): Dialog? {
    activity?.let {
        val view = it.layoutInflater.inflate(R.layout.latest_images_picker, null)
        view.ls_images?.apply {
            withCameraOption(true)
            withGalleryOption(true)
            setOnClickEvent(onClick)
            build({ imageView, image ->
                val idPlaceholder = when (image.type) {
                    ImageTile.CAMERA -> R.drawable.ic_camera
                    ImageTile.GALERY -> R.drawable.ic_image_placeholder
                    else -> R.drawable.ic_image_placeholder
                }
                imageView.loadImageUrl(image.imageUri.toString(), idPlaceholder, true)
            })
        }

        val dialog = BottomSheetDialog(it)
        dialog.setContentView(view)
        return dialog
    }
    return null
}