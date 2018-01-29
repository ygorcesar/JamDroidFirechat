package com.ygorcesar.jamdroidfirechat.extensions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.net.Uri
import android.os.Environment
import android.support.annotation.StringRes
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.app.Fragment
import android.support.v4.content.FileProvider
import android.support.v4.util.Pair
import android.view.View
import com.ygorcesar.jamdroidfirechat.BuildConfig
import com.ygorcesar.jamdroidfirechat.R
import com.ygorcesar.jamdroidfirechat.data.entity.MapLocation
import com.ygorcesar.jamdroidfirechat.utils.Constants
import org.jetbrains.anko.noButton
import org.jetbrains.anko.support.v4.act
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.yesButton
import permissions.dispatcher.PermissionRequest
import java.io.ByteArrayOutputStream
import java.io.File
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*


fun Fragment.showDialogPermission(request: PermissionRequest, @StringRes idRes: Int = R.string.msg_permission_required) {
    alert {
        messageResource = idRes
        yesButton { request.proceed() }
        noButton { request.cancel() }
    }.show()
}

fun Timestamp.lessThanOneDay() = this.time < System.currentTimeMillis() - Constants.DAY

fun Fragment.createImageFileAndGetUri(): Uri? {
    val imageName = "IMG_${SimpleDateFormat("yyyyMMdd_HHmmssSSS", Locale.getDefault()).format(Date())}"
    val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
    val imageFile = File.createTempFile(imageName, ".jpg", storageDir)
    return FileProvider.getUriForFile(context!!, "${BuildConfig.APPLICATION_ID}.provider", imageFile)
}

fun Uri.getBitmap(context: Context): Bitmap? = BitmapFactory.decodeStream(context.contentResolver.openInputStream(this))

fun Uri.getBitmapBytes(context: Context, quality: Int = 60, minQuality: Int = 45): ByteArray? {
    var bitmap = this.getBitmap(context)
    return if (bitmap != null) {
        val q = if (bitmap.width > 1280 || bitmap.height > 1280) {
            bitmap = bitmap.getScaledBitmap()
            minQuality
        } else {
            quality
        }
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.WEBP, q, baos)
        baos.toByteArray()
    } else {
        null
    }
}

fun Activity.getLocationFromAdress(locationName: String): MapLocation? {
    var location: MapLocation? = null
    val lastIndex = locationName.indexOf("\n")
    if (locationName.isNotEmpty() && lastIndex != -1) {
        Geocoder(this, Locale.getDefault())
                .getFromLocationName(locationName.substring(0, lastIndex), 5)
                .first()?.apply { location = MapLocation(latitude.toString(), longitude.toString()) }
    }
    return location
}

fun Bitmap.getScaledBitmap(maxSize: Int = 1280): Bitmap {
    val ratio = Math.min(
            maxSize.toFloat() / this.width,
            maxSize.toFloat() / this.height)
    val width = Math.round(ratio * this.width)
    val height = Math.round(ratio * this.height)
    return Bitmap.createScaledBitmap(this, width, height, false)
}

fun Activity.startActivityWithTransition(intent: Intent, vararg sharedViews: Pair<View, String>) {
    val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, *sharedViews)
    startActivity(intent, options.toBundle())
}

fun Fragment.startActivityWithTransition(intent: Intent, vararg sharedViews: Pair<View, String>) {
    act.startActivityWithTransition(intent, *sharedViews)
}