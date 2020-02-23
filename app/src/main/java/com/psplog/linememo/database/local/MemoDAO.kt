package com.psplog.linememo.database.local

import androidx.room.*
import io.reactivex.Flowable

@Dao
interface MemoDAO {
    @Query(
        """
        SELECT 
        memo_title, 
        memo_content, 
        memo_uri AS thumbnail, 
        memo.memo_id 
        FROM memo 
        LEFT JOIN memo_image 
        ON memo.memo_id = memo_image.memo_id 
        GROUP BY memo.memo_id
        """
    )
    fun getMemo(): Flowable<MutableList<Memo>>

    @Query("SELECT * from memo where memo_id = :memoId")
    fun getMemoContent(memoId: Int): Flowable<Memo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addMemo(memo: Memo): Long

    @Update
    fun updateMemo(memo: Memo)

    @Delete
    fun deleteMemo(memo: Memo)
}