package com.voices.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [ConversationEntity::class, MessageEntity::class, MessageFtsEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class VoicesDatabase : RoomDatabase() {
    abstract fun conversationDao(): ConversationDao
    abstract fun messageDao(): MessageDao

    companion object {
        @Volatile
        private var INSTANCE: VoicesDatabase? = null

        fun get(context: Context): VoicesDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    VoicesDatabase::class.java,
                    "voices.db",
                ).build().also { INSTANCE = it }
            }
        }
    }
}
