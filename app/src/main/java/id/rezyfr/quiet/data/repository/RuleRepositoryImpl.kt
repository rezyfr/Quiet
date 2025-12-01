package id.rezyfr.quiet.data.repository

import id.rezyfr.quiet.data.dao.RuleDao
import id.rezyfr.quiet.data.entity.RuleEntity
import id.rezyfr.quiet.domain.Rule
import id.rezyfr.quiet.screen.action.ActionItem

class RuleRepositoryImpl(
    private val ruleDao: RuleDao,
) : RuleRepository {
    override suspend fun saveRule(rule: Rule) {
        ruleDao.insertRule(
            RuleEntity(
                packageName = rule.packageName,
                keywords = rule.keywords,
                dayRange = rule.dayRange,
                text = rule.text,
                action = rule.action.id,
                enabled = false
            )
        )
    }

    override suspend fun getRules(packageName: String): List<Rule> {
        return ruleDao.getRules().map {
            Rule(
                packageName = it.packageName,
                keywords = it.keywords,
                dayRange = it.dayRange,
                text = it.text,
                action = ActionItem(
                    id = it.action,
                    title = it.action,
                    icon = -1,
                    description = it.action
                ),
                enabled = it.enabled
            )
        }
    }
}
