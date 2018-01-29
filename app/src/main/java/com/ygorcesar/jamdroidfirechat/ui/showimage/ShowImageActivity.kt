package com.ygorcesar.jamdroidfirechat.ui.showimage

import android.os.Bundle
import com.bumptech.glide.Glide
import com.ygorcesar.jamdroidfirechat.R
import com.ygorcesar.jamdroidfirechat.ui.BaseActivity
import com.ygorcesar.jamdroidfirechat.utils.Constants
import kotlinx.android.synthetic.main.show_image_activity.*

class ShowImageActivity : BaseActivity() {

    private val imgUrl: String by lazy { intent.extras.getString(Constants.KEY_IMAGE_URL, "") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.show_image_activity)

        photoview_image?.apply { Glide.with(this).load(imgUrl).into(this) }

    }
}