package id.rezyfr.quiet.data.repository

import android.R.attr.enabled
import id.rezyfr.quiet.data.dao.RuleDao
import id.rezyfr.quiet.domain.mapper.toDomain
import id.rezyfr.quiet.domain.mapper.toEntity
import id.rezyfr.quiet.domain.model.Rule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RuleRepositoryImpl(
    private val ruleDao: RuleDao,
) : RuleRepository {

    override suspend fun getAllRules(): Flow<List<Rule>> {
        return ruleDao.getRulesFlow().map { rules ->
            rules.map {
                it.toDomain()
            }
        }
    }
    override suspend fun getRules(): List<Rule> {
        return ruleDao.getAllRules().map { it.toDomain() }
    }

    override suspend fun getEnabledRules(): List<Rule> {
        return ruleDao.getEnabledRules().map { it.toDomain() }
    }

    override suspend fun getRule(id: Long): Rule? {
        return ruleDao.getRuleById(id)?.toDomain()
    }

    override suspend fun saveRule(rule: Rule): Long {
        val entity = rule.toEntity()
        return ruleDao.insertRule(entity)
    }

    override suspend fun updateRule(rule: Rule) {
        ruleDao.updateRule(rule.toEntity())
    }

    override suspend fun deleteRule(rule: Rule) {
        ruleDao.deleteRule(rule.toEntity())
    }

    override suspend fun deleteAll() {
        ruleDao.deleteAll()
    }
}
