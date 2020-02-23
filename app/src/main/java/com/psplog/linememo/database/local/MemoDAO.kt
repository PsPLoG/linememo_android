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
        LEFT JOIN  (SELECT min(memo_image_id)as memo_image_id, memo_uri ,memo_id
                    FROM memo_image
                    group by memo_id) as memo_image
        ON memo.memo_id = memo_image.memo_id
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