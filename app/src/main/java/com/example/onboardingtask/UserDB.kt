package com.example.onboardingtask

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [UserModel::class], version = 1)
abstract class UserDB : RoomDatabase() {
    abstract fun userDao(): UserDAO

    companion object {
        private var userDB: UserDB? = null
        fun getInstance(context: Context): UserDB {
            if (userDB == null) {
                userDB = Room.databaseBuilder(
                    context,
                    UserDB::class.java, "student_database"
                ).build()
            }
            return userDB!!
        }
    }


//    private fun buildDB(context: Context) =
//        Room.databaseBuilder(context.applicationContext, UserDB::class.java, "db_user").build()

}