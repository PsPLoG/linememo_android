package com.psplog.linememo.utils.database

import androidx.room.*
import io.reactivex.Flowable

@Dao
interface MemoDAO{
    @Query("SELECT * from memo")
    fun getMemo() : Flowable<List<Memo>>

    @Query("SELECT * from memo where memo_id = :memoId")
    fun getMemoContent(memoId : Int) : Flowable<Memo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addMemo(memo : Memo)

    @Update
    fun updateMemo(memo : Memo)

    @Delete
    fun deleteMemo(memo : Memo)
}