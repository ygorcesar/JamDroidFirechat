package com.ygorcesar.jamdroidfirechat.utils;

import com.ygorcesar.jamdroidfirechat.BuildConfig;

public final class ConstantsFirebase {

    /**
     * Constants para locais Firebase
     */
    public static final String FIREBASE_LOCATION_USERS = "users";
    public static final String FIREBASE_LOCATION_USER_FRIENDS = "userFriends";
    public static final String FIREBASE_LOCATION_CHAT = "chat";
    public static final String FIREBASE_LOCATION_CHAT_GLOBAL = "chatGlobal";

    /**
     * Constants para URLs Firebase
     */
    public static final String FIREBASE_URL = BuildConfig.FIREBASE_ROOT_URL;
    public static final String FIREBASE_URL_USERS = FIREBASE_URL + "/" + FIREBASE_LOCATION_USERS;
    public static final String FIREBASE_URL_USER_FRIENDS = FIREBASE_URL + "/" + FIREBASE_LOCATION_USER_FRIENDS;
    public static final String FIREBASE_URL_CHAT = FIREBASE_URL + "/" + FIREBASE_LOCATION_CHAT;
    public static final String FIREBASE_URL_CHAT_GLOBAL = FIREBASE_URL_CHAT + "/" + FIREBASE_LOCATION_CHAT_GLOBAL;

    /**
     * Constants para propriedades dos objetos Firebase
     */
    public static final String FIREBASE_PROPERTY_TIMESTAMP_LAST_CHANGED = "timestampLastChanged";
    public static final String FIREBASE_PROPERTY_TIMESTAMP = "timestamp";
    public static final String FIREBASE_PROPERTY_EMAIL = "email";
    public static final String FIREBASE_PROPERTY_USER_HAS_LOGGED_IN_WITH_PASSWORD = "hasLoggedInWithPassword";
    public static final String FIREBASE_PROPERTY_TIMESTAMP_LAST_CHANGED_REVERSE = "timestampLastChangedReverse";

    /**
     * Constants para Login com Firebase
     */
    public static final String GOOGLE_PROVIDER = "google";
    public static final String PROVIDER_DATA_DISPLAY_NAME = "displayName";

    public static final String CHAT_GLOBAL_HELPER = "Global";
}
