package com.ygorcesar.jamdroidfirechat.data.repository.local

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.ygorcesar.jamdroidfirechat.data.entity.UnreadNotification

@Dao
interface NotificationDao {

    @Query("SELECT * FROM unread_notifications") fun getUnreadNotifications(): List<UnreadNotification>

    @Insert fun insertNotification(unreadNotification: UnreadNotification)

    @Query("DELETE FROM unread_notifications") fun deleteUnreadNotifications()
}