package com.skyd.anivu.model.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.skyd.anivu.model.bean.download.bt.SESSION_PARAMS_TABLE_NAME
import com.skyd.anivu.model.bean.download.bt.SessionParamsBean

@Dao
interface SessionParamsDao {
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun updateSessionParams(sessionParamsBean: SessionParamsBean)

    @Transaction
    @Delete
    suspend fun deleteSessionParams(sessionParamsBean: SessionParamsBean): Int

    @Transaction
    @Query(
        """
        DELETE FROM $SESSION_PARAMS_TABLE_NAME
        WHERE ${SessionParamsBean.LINK_COLUMN} = :link
        """
    )
    fun deleteSessionParams(
        link: String,
    ): Int

    @Transaction
    @Query(
        """
        SELECT * FROM $SESSION_PARAMS_TABLE_NAME
        WHERE ${SessionParamsBean.LINK_COLUMN} = :link
        """
    )
    fun getSessionParams(
        link: String,
    ): SessionParamsBean?
}