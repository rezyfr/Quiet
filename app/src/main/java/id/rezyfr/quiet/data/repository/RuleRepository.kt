package id.rezyfr.quiet.data.repository

import id.rezyfr.quiet.domain.Rule

interface RuleRepository {
    suspend fun saveRule(rule: Rule)
    suspend fun getRules(packageName: String): List<Rule>
}
