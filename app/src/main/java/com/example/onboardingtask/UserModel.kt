package com.example.onboardingtask

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "user_table")
data class UserModel(
    @PrimaryKey(autoGenerate = true) var uId: Int?,
    @ColumnInfo(name = "first_name") val firstName: String?,
    @ColumnInfo(name = "last_name") val lastName: String?,
    @ColumnInfo(name = "email") val userEmail: String?,
    @ColumnInfo(name = "password") val userPassword: String?,
    @ColumnInfo(name = "mobile_number") val userMobile: String?,
    @ColumnInfo(name = "age") val age: String?,
    @ColumnInfo(name = "image") val image: String
) : Serializable

