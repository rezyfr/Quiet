package id.rezyfr.quiet.domain.repository

import id.rezyfr.quiet.domain.model.Rule
import kotlinx.coroutines.flow.Flow

interface RuleRepository {
    suspend fun saveRule(rule: Rule): Long
    suspend fun getAllRules(): Flow<List<Rule>>
    suspend fun getRules(): List<Rule>

    suspend fun getEnabledRules(): List<Rule>

    suspend fun getRule(id: Long): Rule?

    suspend fun updateRule(rule: Rule)

    suspend fun deleteRule(rule: Rule)

    suspend fun deleteAll()
}
