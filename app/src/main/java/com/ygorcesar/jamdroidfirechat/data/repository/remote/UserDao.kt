package com.ygorcesar.jamdroidfirechat.data.repository.remote

import com.google.firebase.database.FirebaseDatabase
import com.ygorcesar.jamdroidfirechat.data.entity.User
import com.ygorcesar.jamdroidfirechat.utils.*
import durdinapps.rxfirebase2.DataSnapshotMapper
import durdinapps.rxfirebase2.RxFirebaseDatabase
import io.reactivex.Observable
import org.jetbrains.anko.AnkoLogger

class UserDao : AnkoLogger {

    fun fetchUsers(email: String): Observable<List<User>> {
        val usersReference = FirebaseDatabase.getInstance().usersReference()
        return RxFirebaseDatabase.observeValueEvent(usersReference, DataSnapshotMapper.listOf(User::class.java))
                .map { it.filter { it.email != email } }
                .toObservable()
    }

    fun fetchUser(email: String?): Observable<User> {
        val userReference = FirebaseDatabase.getInstance().userReference(email)
        return RxFirebaseDatabase.observeSingleValueEvent(userReference, User::class.java).toObservable()
    }

    fun createOrUpdateUser(user: User) = RxFirebaseDatabase.setValue(
            FirebaseDatabase.getInstance().userReference(user.email), user
    )

    fun updateUserDeviceId(deviceId: String?, user: User) = RxFirebaseDatabase.setValue(
            FirebaseDatabase.getInstance().userDeviceIdReference(user.email), deviceId
    )

    fun fetchFriendChatReference(userEmail: String, friendEmail: String): Observable<String> {
        val friendReference = FirebaseDatabase.getInstance().userFriendReference(userEmail, friendEmail)
        return RxFirebaseDatabase.observeSingleValueEvent(friendReference, String::class.java).toObservable()
    }

    fun createChat(userEmail: String, friendEmail: String): String {
        val chatKey = FirebaseDatabase.getInstance().chatReference().push().key
        makeFriends(userEmail, friendEmail, chatKey)
        makeFriends(friendEmail, userEmail, chatKey)
        return chatKey
    }

    private fun makeFriends(userEmail: String, friendEmail: String, chatKey: String) {
        RxFirebaseDatabase.setValue(FirebaseDatabase.getInstance()
                .userFriendReference(userEmail, friendEmail), chatKey)
                .subscribe()
    }
}