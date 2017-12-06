package com.ygorcesar.jamdroidfirechat.ui

import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView

abstract class BaseViewHolder<out VIEWBINDING_TYPE : ViewDataBinding, in ENTITY>(val binding: VIEWBINDING_TYPE) : RecyclerView.ViewHolder(binding.root) {

    abstract fun onBind(entity: ENTITY)
}