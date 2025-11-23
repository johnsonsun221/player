package com.decisionhelper.ai.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.decisionhelper.ai.data.model.Decision
import com.decisionhelper.ai.data.model.DecisionConverters

/**
 * Room 数据库
 */
@Database(
    entities = [Decision::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DecisionConverters::class)
abstract class DecisionDatabase : RoomDatabase() {

    abstract fun decisionDao(): DecisionDao

    companion object {
        @Volatile
        private var INSTANCE: DecisionDatabase? = null

        fun getDatabase(context: Context): DecisionDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DecisionDatabase::class.java,
                    "decision_helper_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
