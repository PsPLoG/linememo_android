package com.psplog.linememo.utils.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "memo_image")
class MemoImage (
    @ColumnInfo(name = "memo_Image_id") @PrimaryKey val memoImageId: Int,
    @ColumnInfo(name = "memo_uri") val memoUri: String
)
