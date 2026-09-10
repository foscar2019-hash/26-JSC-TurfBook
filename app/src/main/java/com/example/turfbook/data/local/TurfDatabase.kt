package com.example.turfbook.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        PitchEntity::class,
        BookingEntity::class,
        BlockedSlotEntity::class,
        TeamEntity::class,
        MatchChallengeEntity::class,
        GalleryImageEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(TurfConverters::class)
abstract class TurfDatabase : RoomDatabase() {
    abstract fun turfDao(): TurfDao

    companion object {
        @Volatile
        private var INSTANCE: TurfDatabase? = null

        fun getDatabase(context: Context): TurfDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TurfDatabase::class.java,
                    "turf_book_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
