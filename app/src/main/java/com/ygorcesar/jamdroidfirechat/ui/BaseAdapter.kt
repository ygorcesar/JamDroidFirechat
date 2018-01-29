package com.ygorcesar.jamdroidfirechat.ui

import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import durdinapps.rxfirebase2.RxFirebaseChildEvent
import durdinapps.rxfirebase2.RxFirebaseChildEvent.EventType.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

abstract class BaseAdapter<out VIEWBINDING_TYPE : ViewDataBinding, VH : BaseViewHolder<VIEWBINDING_TYPE, CHILD>, ENTITY,
        CHILD : RxFirebaseChildEvent<ENTITY>> : RecyclerView.Adapter<VH>(), AnkoLogger {

    var entities: MutableList<CHILD> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    open fun clear() {
        entities = ArrayList()
        notifyDataSetChanged()
    }

    fun onItemChanged(entity: CHILD) {
        when (entity.eventType) {
            ADDED -> addItem(entity)
            CHANGED -> changeItem(getIndexForKey(entity), entity)
            REMOVED -> removeItem(getIndexForKey(entity))
            else -> info { "Do nothing!" }
        }
    }

    open fun addItem(entity: CHILD) {
        entities.add(entity)
        notifyItemInserted(entities.lastIndex)
    }

    open fun changeItem(index: Int, entity: CHILD) {
        if (index != -1) {
            entities[index] = entity
            notifyItemChanged(index)
        }
    }

    open fun removeItem(index: Int) {
        if (index != -1) {
            entities.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    private fun getIndexForKey(entity: CHILD): Int {
        entities.forEachIndexed { index, entity_ ->
            if (entity_.key == entity.key) return index
        }
        return -1
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH = provideViewHolder(LayoutInflater.from(parent.context), parent, viewType)

    override fun onBindViewHolder(holder: VH, position: Int) = holder.onBind(entities[position])

    override fun getItemCount() = entities.size

    abstract fun provideViewHolder(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): VH
}