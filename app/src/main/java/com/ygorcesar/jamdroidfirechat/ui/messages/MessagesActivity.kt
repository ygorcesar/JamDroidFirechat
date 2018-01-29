package com.ygorcesar.jamdroidfirechat.ui.messages

import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import com.ygorcesar.jamdroidfirechat.R
import com.ygorcesar.jamdroidfirechat.data.entity.ChatReference
import com.ygorcesar.jamdroidfirechat.data.repository.remote.UserRemoteDao
import com.ygorcesar.jamdroidfirechat.extensions.loadImageUrl
import com.ygorcesar.jamdroidfirechat.ui.BaseActivity
import com.ygorcesar.jamdroidfirechat.utils.Constants
import kotlinx.android.synthetic.main.messages_activity.*
import org.jetbrains.anko.error


class MessagesActivity : BaseActivity() {
    private var userName: String = ""
    private var userPhotoUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.messages_activity)

        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        if (savedInstanceState == null) {
            intent.extras?.apply {
                val chatReference = ChatReference(getString(Constants.KEY_CHAT_EMAIL), getString(Constants.KEY_CHAT_KEY))
                cancelNotificationForChat(chatReference.userEmail)
                UserRemoteDao().fetchUser(chatReference.userEmail).subscribe({
                    userName = it.name
                    userPhotoUrl = it.photoUrl
                    setupToolbar()
                    setupFragment(chatReference, this)
                }, {
                    error("errorrrr", it)
                })
            }
        } else {
            userName = savedInstanceState.getString(Constants.KEY_USER_DISPLAY_NAME, userName)
            userPhotoUrl = savedInstanceState.getString(Constants.KEY_USER_PROVIDER_PHOTO_URL)
            setupToolbar()
        }
    }

    private fun setupToolbar() {
        tv_title_user_name.text = userName
        iv_title_user_photo.loadImageUrl(userPhotoUrl, R.drawable.ic_person)
    }

    private fun setupFragment(chatReference: ChatReference, sharedArgs: Bundle) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_content, MessagesFragment.newInstance(chatReference, sharedArgs), MessagesFragment.TAG)
                .commit()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.apply {
            putString(Constants.KEY_USER_DISPLAY_NAME, userName)
            putString(Constants.KEY_USER_PROVIDER_PHOTO_URL, userPhotoUrl)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                supportFinishAfterTransition()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun cancelNotificationForChat(userEmail: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(userEmail, Constants.Notification.MESSAGES_NOTIFY_ID)
        notificationManager.cancel(Constants.Notification.MESSAGES_DEFAULT_TAG, Constants.Notification.MESSAGES_NOTIFY_ID)
    }
}