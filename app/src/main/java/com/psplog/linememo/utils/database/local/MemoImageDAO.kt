package com.psplog.linememo.utils.database.local

import androidx.room.*
import io.reactivex.Flowable

@Dao
interface MemoImageDAO {
    @Query("SELECT * from memo_image where memo_id=:memoId")
    fun getMemoImage(memoId: Int): Flowable<List<MemoImage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addMemoImage(memoImage: MemoImage)

    @Delete
    fun deleteMemoImage(memoImage: MemoImage)
}