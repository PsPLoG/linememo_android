package com.psplog.linememo.database.local

import androidx.room.*
import io.reactivex.Flowable

@Dao
interface MemoImageDAO {
    @Query("SELECT * from memo_image where memo_id=:memoId")
    fun getMemoImage(memoId: Int): Flowable<List<MemoImage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addMemoImage(memoImage: List<MemoImage>)

    @Delete
    fun deleteMemoImage(memoImage: MemoImage)

    @Query("DELETE from memo_image where memo_uri=:fileName")
    fun deleteMemoImage(fileName: String)

    @Query("DELETE from memo_image where memo_id=:memoId")
    fun deleteMemoImage(memoId: Int)
}