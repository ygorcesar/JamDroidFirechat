package com.ygorcesar.jamdroidfirechat.ui

import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

abstract class BaseAdapter<out VIEWBINDING_TYPE : ViewDataBinding, VH : BaseViewHolder<VIEWBINDING_TYPE, ENTITY>, ENTITY> : RecyclerView.Adapter<VH>() {
    var entities: List<ENTITY> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    open fun clear() {
        entities = ArrayList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH = provideViewHolder(LayoutInflater.from(parent.context), parent)

    override fun onBindViewHolder(holder: VH, position: Int) = holder.onBind(entities[position])

    override fun getItemCount() = entities.size

    abstract fun provideViewHolder(inflater: LayoutInflater, parent: ViewGroup): VH
}