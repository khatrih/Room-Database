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
                /** we have multiple thread so we use locking mechanisms instance using synchronized */
                synchronized(this) {
                    userDB = Room.databaseBuilder(
                        context.applicationContext,
                        UserDB::class.java, "student_database"
                    ).build()
                }
            }
            return userDB!!
        }
    }
}