package com.ygorcesar.jamdroidfirechat.ui.users

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.databinding.Bindable
import android.databinding.Observable
import android.databinding.PropertyChangeRegistry
import android.support.v7.widget.SearchView
import com.ygorcesar.jamdroidfirechat.BR
import com.ygorcesar.jamdroidfirechat.data.entity.ChatReference
import com.ygorcesar.jamdroidfirechat.data.entity.User
import com.ygorcesar.jamdroidfirechat.data.repository.local.AppDatabase
import com.ygorcesar.jamdroidfirechat.data.repository.remote.UserDao
import org.jetbrains.anko.doAsync

class UsersViewModel(context: Application) : AndroidViewModel(context), Observable {

    private val db: AppDatabase by lazy { AppDatabase.getInstance(this.getApplication<Application>().applicationContext) }
    private val propertyChanged: PropertyChangeRegistry = PropertyChangeRegistry()
    private var users: List<User> = listOf()
    private var usersHelper: List<User> = listOf()
    private val userDao: UserDao by lazy { UserDao() }

    @Bindable
    fun getUsers() = users

    fun setUsers(users: List<User>, isSearch: Boolean = false) {
        if (!isSearch) usersHelper = users
        this.users = users
        propertyChanged.notifyChange(this, BR.users)
    }

    fun fetchUsers(email: String) = userDao.fetchUsers(email)

    fun searchUsers(newText: String): List<User> {
        return if (newText.isNotEmpty()) {
            val filteredUsers = users.filter { it.name.contains(newText, true) }
            filteredUsers
        } else {
            usersHelper
        }
    }

    fun getOnQueryUser() = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(text: String): Boolean {
            setUsers(searchUsers(text), true)
            return false
        }

        override fun onQueryTextChange(newText: String): Boolean {
            setUsers(searchUsers(newText), true)
            return true
        }
    }

    fun shouldFetchChatReference(email: String) = db.chatReferenceDao().getChatReference(email)

    fun saveChatReference(friendEmail: String, chatKey: String): ChatReference {
        val chatRef = ChatReference(friendEmail, chatKey)
        doAsync { db.chatReferenceDao().insertChatReference(chatRef) }
        return chatRef
    }

    fun fetchChatReference(userEmail: String, friendEmail: String) = userDao.fetchFriendChatReference(userEmail, friendEmail)

    fun createChatReference(userEmail: String, friendEmail: String): ChatReference {
        val chatKey = userDao.createChat(userEmail, friendEmail)
        return saveChatReference(friendEmail, chatKey)
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) = propertyChanged.add(callback)

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) = propertyChanged.remove(callback)

}