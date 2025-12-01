package id.rezyfr.quiet.data.repository

import id.rezyfr.quiet.domain.Rule
import kotlinx.coroutines.flow.Flow

interface RuleRepository {
    suspend fun saveRule(rule: Rule)
    suspend fun getRules(packageName: String): List<Rule>
    suspend fun getAllRules(): Flow<List<Rule>>
}
