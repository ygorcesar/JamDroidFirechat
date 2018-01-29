package com.ygorcesar.jamdroidfirechat.ui.latestimages

import android.net.Uri

class ImageTile(val type: Int, val imageUri: Uri) {

    companion object {
        val GALERY = 0
        val CAMERA = 1
        val IMAGE = 2
    }
}