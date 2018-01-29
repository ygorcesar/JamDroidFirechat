package com.ygorcesar.jamdroidfirechat.ui.messages

interface MessagesContract {

    fun initializeCameraIntent()

    fun initializeGalleryIntent()

    fun initializeMapIntent()

    fun showImagePicker()

    fun toggleViewAttachment(forceHide: Boolean = false)
}