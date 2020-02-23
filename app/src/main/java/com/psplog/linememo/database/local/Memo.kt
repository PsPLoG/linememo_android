package com.psplog.linememo.database.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "memo")
class Memo(
    @ColumnInfo(name = "memo_title") val memoTitle: String = "TEST_TITLE",
    @ColumnInfo(name = "memo_content") val memoContent: String = "TEST_CONTENT",
    @ColumnInfo(name = "thumbnail") val thumbnail: String? = "",
    @ColumnInfo(name = "memo_id") @PrimaryKey(autoGenerate = true) val memoId: Int = 0
) {
    @Ignore
    var isSelected: Boolean = false
}
