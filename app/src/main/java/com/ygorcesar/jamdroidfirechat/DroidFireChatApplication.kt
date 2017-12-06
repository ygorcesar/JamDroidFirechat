package com.ygorcesar.jamdroidfirechat

import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase

class DroidFireChatApplication : android.app.Application() {

    override fun onCreate() {
        super.onCreate()

        if (!FirebaseApp.getApps(this).isEmpty()) FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }
}
