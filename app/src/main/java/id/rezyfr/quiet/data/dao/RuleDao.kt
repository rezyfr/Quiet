package id.rezyfr.quiet.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import id.rezyfr.quiet.data.entity.RuleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RuleDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRule(rule: RuleEntity): Long

    @Query("SELECT * FROM rules")
    fun getRulesFlow(): Flow<List<RuleEntity>>

    @Query("SELECT * FROM rules")
    suspend fun getRules(): List<RuleEntity>

    @Query("SELECT * FROM rules ORDER BY createdAt DESC")
    suspend fun getAllRules(): List<RuleEntity>

    @Query("SELECT * FROM rules WHERE enabled = 1 ORDER BY createdAt DESC")
    suspend fun getEnabledRules(): List<RuleEntity>

    @Query("SELECT * FROM rules WHERE id = :id")
    suspend fun getRuleById(id: Long): RuleEntity?

    @Update
    suspend fun updateRule(rule: RuleEntity)

    @Delete
    suspend fun deleteRule(rule: RuleEntity)

    @Query("DELETE FROM rules")
    suspend fun deleteAll()
}
