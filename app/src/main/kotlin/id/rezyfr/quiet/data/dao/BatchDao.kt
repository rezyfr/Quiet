package id.rezyfr.quiet.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import id.rezyfr.quiet.data.entity.BatchEntry

@Dao
interface BatchDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(batch: BatchEntry): Long

    @Query("SELECT * FROM BatchEntry WHERE ruleId = :ruleId")
    suspend fun getBatchByRuleId(ruleId: Long): List<BatchEntry>

    @Query("DELETE FROM BatchEntry WHERE ruleId = :ruleId")
    suspend fun deleteBatchByRuleId(ruleId: Long)
}