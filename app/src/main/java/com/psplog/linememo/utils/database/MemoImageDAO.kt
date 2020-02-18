package com.psplog.linememo.utils.database

import androidx.room.*

@Dao
interface MemoImageDAO{
    @Query("SELECT * from memo")
    fun getMemoImage() : List<Memo>

    @Query("SELECT * from memo where memo_id = :memoId")
    fun getMemoContent(memoId : Int) : List<Memo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addMemo(memo : Memo)

    @Update
    fun updateMemo(memo : Memo)

    @Delete
    fun deleteMemo(memo : Memo)
}