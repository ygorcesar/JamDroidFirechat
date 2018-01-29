package com.ygorcesar.jamdroidfirechat.ui.messages

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.location.places.ui.PlacePicker
import com.ygorcesar.jamdroidfirechat.R
import com.ygorcesar.jamdroidfirechat.data.entity.ChatReference
import com.ygorcesar.jamdroidfirechat.data.entity.MapLocation
import com.ygorcesar.jamdroidfirechat.databinding.MessagesFragmentBinding
import com.ygorcesar.jamdroidfirechat.extensions.*
import com.ygorcesar.jamdroidfirechat.ui.BaseActivity
import com.ygorcesar.jamdroidfirechat.ui.latestimages.ImageTile
import com.ygorcesar.jamdroidfirechat.ui.showimage.ShowImageActivity
import com.ygorcesar.jamdroidfirechat.utils.Constants
import com.ygorcesar.jamdroidfirechat.utils.ConstantsFirebase
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.messages_fragment.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.error
import org.jetbrains.anko.noButton
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.browse
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.yesButton
import permissions.dispatcher.*

@RuntimePermissions
class MessagesFragment : Fragment(), MessagesContract, AnkoLogger {
    private val userEmail: String by lazy { (activity as BaseActivity).userEmail!! }
    private val chatReference: ChatReference by lazy { arguments!!.run { ChatReference(getString(Constants.KEY_CHAT_EMAIL), getString(Constants.KEY_CHAT_KEY)) } }
    private val viewModel: MessagesViewModel by lazy {
        provideViewModelWithFactory<MessagesViewModel>(MessagesViewModelFactory(chatReference,
                activity!!.application, userEmail))
    }
    private val binding: MessagesFragmentBinding by lazy { MessagesFragmentBinding.inflate(layoutInflater) }
    private var imageUri: Uri? = null
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_IMAGE = 2
    private val REQUEST_LOCATION = 3
    private var messagesEventDisposable: Disposable? = null
    private val bottomSheetImagePicker: Dialog? by lazy {
        generateBottomSheetImagePicker({
            when (it.type) {
                ImageTile.CAMERA -> initializeCameraIntent()
                ImageTile.GALERY -> initializeGalleryIntent()
                ImageTile.IMAGE -> viewModel.sendMessageImage(it.imageUri)
            }
            dismissImagePicker()
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewModel.messagesContract = this
        setupRecyclerView()
        fetchMessages()

        arguments?.apply {
            val extraType = getString(Constants.IntentType.KEY_EXTRA, "")
            if (extraType.isNotEmpty()) {
                when (extraType) {
                    Intent.EXTRA_TEXT -> viewModel.messageText = getString(extraType)
                    Intent.EXTRA_STREAM -> viewModel.sendMessageImage(Uri.parse((getString(extraType))))
                    Constants.EXTRA_LOCATION -> viewModel.sendMessageLocation(MapLocation(getString(Constants.KEY_SHARED_LATITUDE),
                            getString(Constants.KEY_SHARED_LONGITUDE)))
                }
            }
        }
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.invalidateLastMessage()
        messagesEventDisposable?.dispose()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            dismissImagePicker()
            fetchMessages()
            when (requestCode) {
                REQUEST_IMAGE -> viewModel.sendMessageImage(data?.data)
                REQUEST_IMAGE_CAPTURE -> viewModel.sendMessageImage(imageUri)
                REQUEST_LOCATION -> PlacePicker.getPlace(context, data)?.latLng?.apply {
                    viewModel.sendMessageLocation(MapLocation(latitude.toString(), longitude.toString()))
                }
            }
        }
    }

    @SuppressLint("NeedOnRequestPermissionsResult")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    private fun fetchMessages() {
        if (messagesEventDisposable == null || messagesEventDisposable?.isDisposed == true) {
            viewModel.apply {
                binding.viewmodel = this
                binding.executePendingBindings()
                messagesEventDisposable = fetchMessages(chatReference.chatKey).subscribe({
                    viewModel.messages = it
                    binding.executePendingBindings()
                }, { error("Error on get messages", it) })
            }
        }
    }

    private fun setupRecyclerView() {
        binding.rvMessages.apply {
            _setLinearLayoutManager()
            adapter = MessagesAdapter(userEmail, { emailOfMessage, onFetchUser, onError ->
                viewModel.fetchUserOfMessage(emailOfMessage, onFetchUser, onError)
            }, { view, message, isLongClick ->
                if (isLongClick) {
                    deleteMessage(message.key)
                } else {
                    message.value.apply {
                        when (type) {
                            ConstantsFirebase.MessageType.IMAGE -> goToShowImageActivity(view, imgUrl)
                            ConstantsFirebase.MessageType.LOCATION -> mapLocation?.apply { browse(getString(R.string.map_location_intent, latitude, longitude, latitude, longitude)) }
                        }
                    }
                }
            })
        }
    }

    private fun goToShowImageActivity(view: View?, imgUrl: String) {
        if (view != null) {
            val sharedImg = Pair(view, getString(R.string.transition_name_message_image))
            val intent = Intent(context, ShowImageActivity::class.java).apply {
                putExtra(Constants.KEY_IMAGE_URL, imgUrl)
            }
            startActivityWithTransition(intent, sharedImg)
        } else {
            startActivity<ShowImageActivity>()
        }
    }

    private fun deleteMessage(messageKeyRef: String) {
        alert(R.string.dialog_message_delete_note, R.string.dialog_message_delete_title) {
            yesButton { viewModel.deleteMessage(messageKeyRef) }
            noButton { }
        }.show()
    }

    override fun initializeGalleryIntent() {
        val selectImageIntent = Intent()
        selectImageIntent.type = "image/*"
        selectImageIntent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(selectImageIntent, REQUEST_IMAGE)
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun initializeCameraIntentWithPerm() {
        createImageFileAndGetUri()?.let {
            imageUri = it
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                putExtra(MediaStore.EXTRA_OUTPUT, it)
                startActivityForResult(this, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    fun showImagePickerWithPerm() {
        bottomSheetImagePicker?.show()
    }

    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    fun initializeMapIntentWithPerm() {
        val builder = PlacePicker.IntentBuilder()
        try {
            startActivityForResult(builder.build(activity), REQUEST_LOCATION)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun dismissImagePicker() {
        if (bottomSheetImagePicker != null && bottomSheetImagePicker?.isShowing == true) bottomSheetImagePicker?.dismiss()
    }

    override fun toggleViewAttachment(forceHide: Boolean) {
        content_attachments?.apply {
            if (forceHide || isVisible()) exitCircularReveal(iv_attachment.x.toInt()) else enterCircularReveal(iv_attachment.x.toInt())
        }
    }

    override fun initializeCameraIntent() = initializeCameraIntentWithPermWithPermissionCheck()

    override fun showImagePicker() = showImagePickerWithPermWithPermissionCheck()

    override fun initializeMapIntent() = initializeMapIntentWithPermWithPermissionCheck()

    @OnShowRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
    fun showRationaleForReadExternal(request: PermissionRequest) {
        showDialogPermission(request)
    }

    @OnPermissionDenied(Manifest.permission.READ_EXTERNAL_STORAGE)
    fun showPermissionDeniedForReadExternal() {
        toast(R.string.msg_permission_required)
    }

    @OnNeverAskAgain(Manifest.permission.READ_EXTERNAL_STORAGE)
    fun showNeverAskForReadExternal() {
        toast(R.string.msg_permission_required)
    }

    @OnShowRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun showRationaleForWriteExternal(request: PermissionRequest) {
        showDialogPermission(request)
    }

    @OnPermissionDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun showPermissionDeniedForWriteExternal() {
        toast(R.string.msg_permission_required)
    }

    @OnNeverAskAgain(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun showNeverAskForWriteExternal() {
        toast(R.string.msg_permission_required)
    }

    @OnShowRationale(Manifest.permission.ACCESS_FINE_LOCATION)
    fun showRationaleForAccessFineLocation(request: PermissionRequest) {
        showDialogPermission(request)
    }

    @OnPermissionDenied(Manifest.permission.ACCESS_FINE_LOCATION)
    fun showPermissionDeniedForAccessFineLocation() {
        toast(R.string.msg_permission_required)
    }

    @OnNeverAskAgain(Manifest.permission.ACCESS_FINE_LOCATION)
    fun showNeverAskForAccessFineLocation() {
        toast(R.string.msg_permission_required)
    }

    companion object {
        val TAG = "messages_fragment"
        fun newInstance(chatReference: ChatReference, sharedArgs: Bundle): MessagesFragment {
            val args = Bundle().apply {
                putString(Constants.KEY_CHAT_EMAIL, chatReference.userEmail)
                putString(Constants.KEY_CHAT_KEY, chatReference.chatKey)

                val extraType = sharedArgs.getString(Constants.IntentType.KEY_EXTRA, "")
                if (extraType.isNotEmpty()) {
                    putString(extraType, sharedArgs.getString(extraType))
                    putString(Constants.IntentType.KEY_EXTRA, extraType)
                    if (extraType == Constants.EXTRA_LOCATION) {
                        putString(Constants.KEY_SHARED_LATITUDE, sharedArgs.getString(Constants.KEY_SHARED_LATITUDE))
                        putString(Constants.KEY_SHARED_LONGITUDE, sharedArgs.getString(Constants.KEY_SHARED_LONGITUDE))
                    }
                }
            }
            return MessagesFragment().apply { arguments = args }
        }
    }
}