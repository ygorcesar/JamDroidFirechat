package com.ygorcesar.jamdroidfirechat.ui.latestimages

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.widget.ImageView
import com.ygorcesar.jamdroidfirechat.extensions._setGridLayoutManager
import java.io.File

class LatestImages(context: Context, attrs: AttributeSet?) : RecyclerView(context, attrs) {
    private var numberOfImages = 30
    private var onImageClickEvent: (imageTile: ImageTile) -> Unit = {}
    private lateinit var imageProvider: (imageView: ImageView, imageTile: ImageTile) -> Unit
    private var withCamera: Boolean = false
    private var withGallery: Boolean = false

    init {
//        init(attrs)
        _setGridLayoutManager(spaceCount = 3)
    }

    /* private fun init(attrs: AttributeSet?) {
         attrs?.apply {
             val packageName = "http://schemas.android.com/apk/res-auto"
             numberOfImages = getAttributeIntValue(packageName, "quantity", 2)
         }
     }*/

    fun numberOfImages(quantity: Int): LatestImages {
        numberOfImages = quantity
        return this
    }

    fun withCameraOption(withCamera: Boolean): LatestImages {
        this.withCamera = withCamera
        return this
    }

    fun withGalleryOption(withGallery: Boolean): LatestImages {
        this.withGallery = withGallery
        return this
    }

    fun setOnClickEvent(onImageSelected: (imageTile: ImageTile) -> Unit): LatestImages {
        onImageClickEvent = onImageSelected
        return this
    }

    fun build(provider: ((imageView: ImageView, imageTile: ImageTile) -> Unit)) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            throw RuntimeException("Missing required READ_EXTERNAL_STORAGE permission. Did you remember to request it first?")
        }

        imageProvider = provider
        this.apply {
            adapter = LatestImagesAdapter(onImageClickEvent, imageProvider)
            getImagesPath(context)
        }
    }

    private fun getImagesPath(context: Context) {
        val adapt = adapter
        if (adapt is LatestImagesAdapter) {
            val images = mutableListOf<ImageTile>()
            if (withCamera) images.add(ImageTile(ImageTile.CAMERA, Uri.EMPTY))
            if (withGallery) images.add(ImageTile(ImageTile.GALERY, Uri.EMPTY))

            val projection = arrayOf(MediaStore.Images.ImageColumns._ID,
                    MediaStore.Images.ImageColumns.DATA,
                    MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                    MediaStore.Images.ImageColumns.DATE_TAKEN,
                    MediaStore.Images.ImageColumns.MIME_TYPE)

            val cursor = context.contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection, null, null, "${MediaStore.Images.ImageColumns.DATE_TAKEN} DESC")

            cursor?.let {
                var count = 0
                while (it.moveToNext() && count < numberOfImages) {
                    val imageLocation = cursor.getString(1)
                    val file = File(imageLocation)
                    if (file.exists()) images.add(ImageTile(ImageTile.IMAGE, Uri.fromFile(file)))
                    count++
                }
            }
            cursor.close()
            adapt.addImages(images)
        }
    }
}