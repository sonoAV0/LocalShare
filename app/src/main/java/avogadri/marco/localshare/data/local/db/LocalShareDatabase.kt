package avogadri.marco.localshare.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [HistoryEntity::class], version = 3, exportSchema = false)
abstract class LocalShareDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao

    companion object {
        fun build(context: Context): LocalShareDatabase =
            Room.databaseBuilder(context.applicationContext, LocalShareDatabase::class.java, "localshare.db")
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build()
    }
}
