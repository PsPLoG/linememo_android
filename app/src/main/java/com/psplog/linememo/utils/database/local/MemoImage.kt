package com.psplog.linememo.utils.database.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "memo_image")
class MemoImage(
        @ColumnInfo(name = "memo_uri") val memoUri: String,
        @ColumnInfo(name = "memo_id")  val memoId: Int,
        @ColumnInfo(name = "memo_image_id") @PrimaryKey(autoGenerate = true) val memoImageId: Int = 0
)
