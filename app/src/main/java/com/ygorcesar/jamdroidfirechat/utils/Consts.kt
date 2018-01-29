package com.ygorcesar.jamdroidfirechat.utils


object Constants {
    /**
     * Constants para bundles, extras e keys shared preferences
     */
    const val KEY_PROVIDER = "PROVIDER"
    const val KEY_ENCODED_EMAIL = "ENCODED_EMAIL"
    const val KEY_USER_FCM_DEVICE_ID = "USER_FCM_DEVICE_ID"
    const val KEY_USER_FCM_DEVICE_ID_SENDER = "USER_FCM_DEVICE_ID_SENDER"
    const val KEY_USER_EMAIL = "USER_EMAIL"
    const val KEY_USER_DISPLAY_NAME = "USER_DISPLAY_NAME"
    const val KEY_USER_PROVIDER_PHOTO_URL = "USER_PROVIDER_PHOTO_URL"
    const val KEY_CHAT_TIME_SENDED = "CHAT_TIME_SENDED"
    const val KEY_CHAT_CHILD = "CHAT_KEY_CHILD"
    const val KEY_CHAT_EMAIL = "CHAT_EMAIL"
    const val KEY_CHAT_KEY = "CHAT_KEY"
    const val KEY_MSG_TITLE = "TITLE"
    const val KEY_MSG = "MSG"
    const val KEY_MSG_KEY = "MSG_KEY"
    const val KEY_IMAGE_URL = "IMAGE_URL"

    const val KEY_PREF_NOTIFICATION_IS_ACTIVE = "PREF_KEY_NOTIFICATION"
    const val KEY_PREF_GITHUB = "PREF_KEY_GITHUB"
    const val KEY_PREF_ABOUT = "PREF_KEY_ABOUT"
    const val KEY_PREF_EXIT = "PREF_KEY_EXIT"
    const val KEY_PREF_DELETE_ACCOUNT = "PREF_KEY_DELETE_ACCOUNT"
    const val KEY_PREF_REVOKE_ACCESS = "PREF_KEY_REVOKE_ACCESS"

    const val PROJECT_GITHUB_URL = "https://github.com/ygorcesar/JamDroidFireChat"
    const val KEY_IMAGE_BYTES = "IMAGE_BYTES"

    const val KEY_SHARED_CONTENT = "SHARED_CONTENT"
    const val KEY_SHARED_LATITUDE = "SHARED_CONTENT_LATITUDE"
    const val KEY_SHARED_LONGITUDE = "SHARED_CONTENT_LONGITUDE"

    const val EXTRA_LOCATION = "intent.extra.LOCATION"

    const val ARGS_POS_SHARED = 0
    const val ARGS_POS_TYPE = 1
    const val ARGS_POS_LATITUDE = 2
    const val ARGS_POS_LONGITUDE = 3

    const val FACEBOOK_PERMISSION_PUBLIC = "public_profile"
    const val FACEBOOK_PERMISSION_EMAIL = "email"

    const val DAY = (24 * 60 * 60 * 1000).toLong()

    const val DATE_PATTERN_HOUR_MINUTE = "H:mm"
    const val DATE_PATTERN_DAY_MONTH_YEAR_HOUR_MINUTE = "H:mm dd/MM/yy"

    object Notification {
        const val MESSAGES_NOTIFY_ID = 111
        const val MESSAGES_CHANNEL_ID = "jamdroid_messages"
        const val MESSAGES_GROUP_KEY = "messages_group_key"
        const val MESSAGES_DEFAULT_TAG = "messages_default_tag"
    }

    object IntentType {
        const val TEXT_PLAIN = "text/plain"
        const val IMAGE = "image/"
        const val KEY_EXTRA = "key_type_extra"

    }
}

object ConstantsFirebase {

    /**
     * Constants para locais Firebase
     */

    object Location {
        const val USERS = "users"
        const val USER_FRIENDS = "userFriends"
        const val CHAT = "chat"
        const val CHAT_GLOBAL = "chatGlobal"

        const val STORAGE_CHAT_IMAGES_LOCATION = "images"
    }

    object Topics {
        const val CHAT_GLOBAL = "chatGlobal"
    }

    /**
     * Constants para propriedades dos objetos Firebase
     */
    const val FIREBASE_PROPERTY_TIMESTAMP_LAST_CHANGED = "timestampLastChanged"
    const val FIREBASE_PROPERTY_TIMESTAMP = "timestamp"
    const val FIREBASE_PROPERTY_EMAIL = "email"
    const val FIREBASE_PROPERTY_NAME = "name"
    const val FIREBASE_PROPERTY_ONLINE = "online"
    const val FIREBASE_PROPERTY_USER_DEVICE_ID = "fcmUserDeviceId"
    const val FIREBASE_PROPERTY_USER_HAS_LOGGED_IN_WITH_PASSWORD = "hasLoggedInWithPassword"
    const val FIREBASE_PROPERTY_TIMESTAMP_LAST_CHANGED_REVERSE = "timestampLastChangedReverse"

    const val FIREBASE_PROPERTY_MESSAGE_STATUS = "status"
    const val FIREBASE_PROPERTY_MESSAGE_TIME_SENDED = "sendedTime"
    const val FIREBASE_PROPERTY_MESSAGE_IMG = "imgUrl"

    const val PROVIDER_DATA_EMAIL = "email"
    const val PROVIDER_DATA_DISPLAY_NAME = "displayName"
    const val PROVIDER_DATA_PROFILE_IMAGE_URL = "profileImageURL"

    const val CHAT_GLOBAL_HELPER = "Global"

    /**
     * Messages Types
     * 0 - TEXT
     * 1 - IMAGE
     * 2 - MAP LOCATION
     */

    object MessageType {
        const val TEXT = 0
        const val IMAGE = 1
        const val LOCATION = 2
    }

    /**
     * Message Status
     * 0 - PENDING
     * 1 - SENDED
     * 2 - RECEIVED
     */
    const val MESSAGE_STATUS_PENDING = 0
    const val MESSAGE_STATUS_SENDED = 1
    const val MESSAGE_STATUS_RECEIVED = 2
}