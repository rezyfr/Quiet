package id.rezyfr.quiet.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import id.rezyfr.quiet.data.entity.RuleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RuleDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRule(rule: RuleEntity): Long

    @Query("SELECT * FROM RuleEntity")
    fun getRulesFlow(): Flow<List<RuleEntity>>

    @Query("SELECT * FROM RuleEntity")
    suspend fun getRules(): List<RuleEntity>
}
