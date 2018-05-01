package com.ygorcesar.jamdroidfirechat.extensions

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.TimeInterpolator
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.AccelerateDecelerateInterpolator


fun View?.animateScaleIn(duration: Long = 300L, delay: Long = 0L, interpolator: TimeInterpolator = AccelerateDecelerateInterpolator()) {
    this?.apply {
        scaleX = 0F
        scaleY = 0F
        animate().scaleX(1F)
                .scaleY(1F)
                .setDuration(duration)
                .setInterpolator(interpolator)
                .setStartDelay(delay)
                .setListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(p0: Animator?) {}
                    override fun onAnimationEnd(p0: Animator?) {}
                    override fun onAnimationCancel(p0: Animator?) {}
                    override fun onAnimationStart(p0: Animator?) {
                        visible()
                    }
                }).start()
    }
}

fun View?.animateScaleX(duration: Long = 300L, delay: Long = 0L, interpolator: TimeInterpolator = AccelerateDecelerateInterpolator()) {
    this?.apply {
        scaleX = 0.3F
        animate().scaleX(1F)
                .setStartDelay(delay)
                .setInterpolator(interpolator)
                .setListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(p0: Animator?) {}
                    override fun onAnimationEnd(p0: Animator?) {}
                    override fun onAnimationCancel(p0: Animator?) {}
                    override fun onAnimationStart(p0: Animator?) {
                        visible()
                    }
                }).setDuration(duration).start()
    }
}

fun View?.animateFadeIn(duration: Long = 300L, delay: Long = 0L, interpolator: TimeInterpolator = AccelerateDecelerateInterpolator()) {
    this?.apply {
        alpha = 0F
        animate().alpha(1F)
                .setDuration(duration)
                .setInterpolator(interpolator)
                .setStartDelay(delay)
                .setListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(p0: Animator?) {}
                    override fun onAnimationEnd(p0: Animator?) {}
                    override fun onAnimationCancel(p0: Animator?) {}
                    override fun onAnimationStart(p0: Animator?) {
                        visible()
                    }
                }).start()
    }
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