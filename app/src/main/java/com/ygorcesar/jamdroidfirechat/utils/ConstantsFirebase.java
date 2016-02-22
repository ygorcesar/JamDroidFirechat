package com.ygorcesar.jamdroidfirechat.utils;

import com.ygorcesar.jamdroidfirechat.BuildConfig;

public final class ConstantsFirebase {

    /**
     * Constants para locais Firebase
     */
    public static final String FIREBASE_LOCATION_USERS = "users";
    public static final String FIREBASE_LOCATION_CHAT = "chat";

    /**
     * Constants para URLs Firebase
     */
    public static final String FIREBASE_URL = BuildConfig.FIREBASE_ROOT_URL;
    public static final String FIREBASE_URL_USERS = FIREBASE_URL + "/" + FIREBASE_LOCATION_USERS;
    public static final String FIREBASE_URL_CHAT = FIREBASE_URL + "/" + FIREBASE_LOCATION_CHAT;

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
}
