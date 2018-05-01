package com.ygorcesar.jamdroidfirechat.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.preference.PreferenceManager
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ygorcesar.jamdroidfirechat.R
import com.ygorcesar.jamdroidfirechat.data.entity.UnreadNotification
import com.ygorcesar.jamdroidfirechat.data.repository.local.AppDatabase
import com.ygorcesar.jamdroidfirechat.data.repository.local.NotificationDao
import com.ygorcesar.jamdroidfirechat.ui.users.UsersActivity
import com.ygorcesar.jamdroidfirechat.utils.Constants
import com.ygorcesar.jamdroidfirechat.utils.ConstantsFirebase


class FcmNotificationService : FirebaseMessagingService() {
    private val pattern: LongArray by lazy { longArrayOf(300, 300, 300, 300, 300) }
    private val notificationsRepository: NotificationDao by lazy { AppDatabase.getInstance(this).notificationDao() }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        PreferenceManager.getDefaultSharedPreferences(this)?.let {
            if (it.getBoolean(Constants.KEY_PREF_NOTIFICATION_IS_ACTIVE, true)) {
                if (message.notification != null) {

                } else {
                    message.data?.apply {
                        val email = get("email")
                        val chatRef = get("chatRef")
                        val type = get("type")?.toInt() ?: 0
                        val isGlobal = get("isGlobal")?.toBoolean() ?: false

                        val title = getOrDefault("title", getString(R.string.notification_new_notification)) +
                                if (isGlobal) " - " + ConstantsFirebase.CHAT_GLOBAL_HELPER else ""

                        val body = when (type) {
                            ConstantsFirebase.MessageType.TEXT -> getOrDefault("body", "")
                            ConstantsFirebase.MessageType.IMAGE -> getString(R.string.notification_msg_image)
                            ConstantsFirebase.MessageType.LOCATION -> getString(R.string.notification_msg_location)
                            else -> ""
                        }

                        showNotification(title, body, email, chatRef)
                    }
                }
            }
        }
    }


    private fun showNotification(title: String, body: String, email: String?, chatRef: String?) {
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val largerIcon = BitmapFactory.decodeResource(resources, R.drawable.ic_notification_large_default)

        val newUnreadNotification = createUnreadNotification(title, body, email, chatRef)

        val unreadNotifications = getUnreadNotifications().toMutableList().apply { add(newUnreadNotification) }
        val numberOfMessages = unreadNotifications.size

        val inboxStyle = with(NotificationCompat.InboxStyle()) {
            unreadNotifications.forEach {
                addLine("${it.title}: ${it.body}")
            }
            setBigContentTitle(resources.getQuantityString(R.plurals.not_new_message, numberOfMessages, numberOfMessages))
            setSummaryText("Chat")
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(this, Constants.Notification.MESSAGES_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification_stat_default)
                .setLargeIcon(largerIcon)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setVibrate(pattern)
                .setStyle(inboxStyle)
                .setNumber(numberOfMessages)
                .setColor(ContextCompat.getColor(this, R.color.colorAccent))
                .setLights(Color.BLUE, 1, 1)
                .setSound(defaultSoundUri)
                .setContentIntent(createPendingIntent(email, chatRef))
                .setGroup(Constants.Notification.MESSAGES_GROUP_KEY)
                .setGroupSummary(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel(Constants.Notification.MESSAGES_CHANNEL_ID, "JamdroidFirechat Messages",
                    NotificationManager.IMPORTANCE_HIGH).let { channel ->
                notificationManager.createNotificationChannel(channel)
            }
        }

        notificationManager.notify(email ?: Constants.Notification.MESSAGES_DEFAULT_TAG,
                Constants.Notification.MESSAGES_NOTIFY_ID, notification.build())

        insertNotification(newUnreadNotification)
    }

    private fun createPendingIntent(userEmail: String?, chatRef: String?): PendingIntent {
        val intent = Intent(this, UsersActivity::class.java).apply {
            if (!userEmail.isNullOrEmpty() && !chatRef.isNullOrEmpty()) {
                putExtra(Constants.KEY_USER_EMAIL, userEmail)
                putExtra(Constants.KEY_CHAT_KEY, chatRef)
            }
        }
        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun getUnreadNotifications(): List<UnreadNotification> = notificationsRepository.getUnreadNotifications()

    private fun insertNotification(unreadNotification: UnreadNotification) {
        notificationsRepository.insertNotification(unreadNotification)
    }

    private fun createUnreadNotification(title: String, body: String, email: String?, chatRef: String?) =
            UnreadNotification(title = title, body = body, email = email, chatRef = chatRef)
}