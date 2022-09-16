package com.example.onboardingtask

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface UserDAO {
    @Query("SELECT * FROM user_table")
    fun getUsers(): LiveData<List<UserModel>>

    @Insert
    suspend fun createUser(userModel: UserModel)

    @Query("SELECT * FROM user_table WHERE email = :email")
    fun validateName(email: String): LiveData<UserModel>

    @Query("SELECT * FROM user_table WHERE email = :email AND password =:password")
    fun existingUserLogin(email: String, password: String): LiveData<UserModel>

    @Update
    suspend fun updateUser(userModel: UserModel)

    @Delete
    fun delete(userModel: UserModel): Int

    @Query("SELECT * FROM user_table WHERE email = :email")
    fun getCurrentUser(email: String): LiveData<UserModel>
}