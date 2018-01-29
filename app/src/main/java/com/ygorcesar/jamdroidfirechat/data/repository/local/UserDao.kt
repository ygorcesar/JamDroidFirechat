package com.ygorcesar.jamdroidfirechat.data.repository.local

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query

import com.ygorcesar.jamdroidfirechat.data.entity.User

@Dao
interface UserDao {

    @Query("SELECT * FROM USERS") fun getUsers(): List<User>

    @Query("SELECT * FROM USERS WHERE email LIKE :userEmail") fun getUser(userEmail: String): User?

    @Query("SELECT * FROM USERS WHERE email NOT LIKE :userEmail") fun getUsersWithoutLoggedUser(userEmail: String): List<User>

    @Insert(onConflict = OnConflictStrategy.REPLACE) fun insertUser(user: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE) fun insertUsers(vararg users: User)
}
