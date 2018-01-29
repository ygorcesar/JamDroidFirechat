package com.ygorcesar.jamdroidfirechat.ui.latestimages

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.ygorcesar.jamdroidfirechat.R

class LatestImagesAdapter(val onImageSelected: (imageTile: ImageTile) -> Unit, val imageProvider: (imageView: ImageView, imageTile: ImageTile) -> Unit)
    : RecyclerView.Adapter<LatestImagesAdapter.LatestImagesViewHolder>() {
    var imagesTile = listOf<ImageTile>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LatestImagesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.latest_images_item, parent, false)
        return LatestImagesViewHolder(view)
    }

    override fun onBindViewHolder(holder: LatestImagesViewHolder?, position: Int) {
        holder?.bind(imagesTile[position])
    }

    override fun getItemCount() = imagesTile.size

    fun addImages(images: List<ImageTile>) {
        imagesTile = images
        notifyDataSetChanged()
    }

    inner class LatestImagesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val ivImage: ImageView = view.findViewById(R.id.iv_image)

        fun bind(imageTile: ImageTile) {
            ivImage.setOnClickListener { onImageSelected(imagesTile[adapterPosition]) }
            imageProvider(ivImage, imageTile)
        }
    }
}