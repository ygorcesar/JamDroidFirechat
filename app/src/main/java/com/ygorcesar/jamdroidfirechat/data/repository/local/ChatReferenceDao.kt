package com.ygorcesar.jamdroidfirechat.data.repository.local

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query

import com.ygorcesar.jamdroidfirechat.data.entity.ChatReference

@Dao
interface ChatReferenceDao {

    @Query("SELECT * FROM chat_reference") fun getReferences(): List<ChatReference>

    @Query("SELECT * FROM chat_reference WHERE userEmail = :friendEmail") fun getChatReference(friendEmail: String): ChatReference?

    @Insert(onConflict = OnConflictStrategy.REPLACE) fun insertChatReference(userFriend: ChatReference)

}
