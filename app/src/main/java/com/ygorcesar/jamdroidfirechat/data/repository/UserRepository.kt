package com.ygorcesar.jamdroidfirechat.data.repository

import com.ygorcesar.jamdroidfirechat.data.entity.ChatReference
import com.ygorcesar.jamdroidfirechat.data.entity.User
import com.ygorcesar.jamdroidfirechat.data.repository.local.AppDatabase
import com.ygorcesar.jamdroidfirechat.data.repository.remote.UserRemoteDao
import com.ygorcesar.jamdroidfirechat.utils.ConstantsFirebase
import org.jetbrains.anko.doAsync


class UserRepository(private val db: AppDatabase) {

    val mUserRemoteDao: UserRemoteDao by lazy { UserRemoteDao() }

    fun fetchUser(email: String, onFetch: (user: User) -> Unit, onError: (err: Throwable) -> Unit) {
        val user = db.userDao().getUser(email)
        if (user == null) {
            mUserRemoteDao.fetchUser(email).subscribe({
                doAsync { db.userDao().insertUser(it) }
                onFetch(it)
            }, { onError(it) })
        } else {
            onFetch(user)
        }
    }

    fun fetchUsers(email: String) = mUserRemoteDao.fetchUsers(email)

    fun fetchUsersLocally(userEmail: String) = db.userDao().getUsersWithoutLoggedUser(userEmail)

    fun insertUsers(vararg users: User) = doAsync { db.userDao().insertUsers(*users) }

    fun fetchReference(userEmail: String, friendEmail: String,
                       goToChat: (chatRef: ChatReference) -> Unit, onError: (err: Throwable) -> Unit) {
        val chatReference = fetchChatReferenceLocal(friendEmail)
        if (chatReference == null) {
            fetchChatReferenceRemote(userEmail, friendEmail)
                    .subscribe({ chatKey -> goToChat(saveChatReference(friendEmail, chatKey)) },
                            { err ->
                                if (err is NullPointerException) {
                                    goToChat(createChatReference(userEmail, friendEmail))
                                } else {
                                    onError(err)
                                }
                            })
        } else {
            goToChat(chatReference)
        }
    }

    fun fetchChatReferenceLocal(friendEmail: String): ChatReference? {
        if (friendEmail == ConstantsFirebase.Location.CHAT_GLOBAL) {
            return mUserRemoteDao.chatGlobalReference()
        }
        return db.chatReferenceDao().getChatReference(friendEmail)
    }

    fun fetchChatReferenceRemote(userEmail: String, friendEmail: String) = mUserRemoteDao.fetchFriendChatReference(userEmail, friendEmail)

    fun createChatReference(userEmail: String, friendEmail: String): ChatReference {
        val chatKey = mUserRemoteDao.createChat(userEmail, friendEmail)
        return saveChatReference(friendEmail, chatKey)
    }

    fun saveChatReference(friendEmail: String, chatKey: String): ChatReference {
        val chatRef = ChatReference(friendEmail, chatKey)
        doAsync { db.chatReferenceDao().insertChatReference(chatRef) }
        return chatRef
    }
}