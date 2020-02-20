package com.psplog.linememo.utils.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.psplog.linememo.utils.database.local.Memo
import com.psplog.linememo.utils.database.local.MemoDAO
import com.psplog.linememo.utils.database.local.MemoImage
import com.psplog.linememo.utils.database.local.MemoImageDAO

@Database(entities = arrayOf(
        Memo::class,
        MemoImage::class), version = 3)
abstract class MemoDataBase : RoomDatabase() {
    abstract fun memoDAO(): MemoDAO
    abstract fun memoImageDAO(): MemoImageDAO

    companion object {
        fun provideMemoDAO(context: Context): MemoDAO = getInstance(context).memoDAO()
        fun provideMemoImageDAO(context: Context): MemoImageDAO = getInstance(context).memoImageDAO()

        private var INSTANCE: MemoDataBase? = null

        private val lock = Any()

        private fun getInstance(context: Context): MemoDataBase {
            synchronized(lock) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context,
                            MemoDataBase::class.java, "Memo.db")
                            .build()
                }
                return INSTANCE!!
            }
        }
    }
}
