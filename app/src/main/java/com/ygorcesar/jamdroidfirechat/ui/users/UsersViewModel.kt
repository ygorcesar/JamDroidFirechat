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
import com.ygorcesar.jamdroidfirechat.data.repository.UserRepository
import com.ygorcesar.jamdroidfirechat.data.repository.local.AppDatabase

class UsersViewModel(context: Application) : AndroidViewModel(context), Observable {

    private val propertyChanged: PropertyChangeRegistry by lazy { PropertyChangeRegistry() }
    private var usersHelper: List<User> = mutableListOf()
    private val repository: UserRepository by lazy { UserRepository(AppDatabase.getInstance(this.getApplication<Application>().applicationContext)) }

    var users: MutableList<User> = mutableListOf()
        @Bindable get
        set(value) {
            field = value
            propertyChanged.notifyChange(this, BR.users)
        }

    fun fetchUsers(email: String) = repository.fetchUsers(email)

    fun setUsers(users: List<User>, isSearch: Boolean = false) {
        if (!isSearch) {
            usersHelper = users
            repository.insertUsers(*users.toTypedArray())
        }
        if (users.toMutableList() != this.users) this.users = users.toMutableList()
    }

    fun searchUsers(newText: String): MutableList<User> {
        return if (newText.isNotEmpty()) {
            val filteredUsers = users.filter { it.name.contains(newText, true) }.toMutableList()
            filteredUsers
        } else {
            usersHelper.toMutableList()
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

    fun fetchReference(userEmail: String, friendEmail: String,
                       goToChat: (chatRef: ChatReference) -> Unit, onError: (err: Throwable) -> Unit) {
        repository.fetchReference(userEmail, friendEmail, goToChat, onError)
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) = propertyChanged.add(callback)

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) = propertyChanged.remove(callback)

}