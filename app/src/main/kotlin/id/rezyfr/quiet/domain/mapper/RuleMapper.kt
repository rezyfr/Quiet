package id.rezyfr.quiet.domain.mapper

import id.rezyfr.quiet.data.entity.RuleEntity
import id.rezyfr.quiet.domain.model.Rule
import id.rezyfr.quiet.util.RuleJson
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

fun RuleEntity.toDomain(): Rule = Rule(
    id = id,
    name = name,
    enabled = enabled,
    apps = RuleJson.decodeApps(appsJson),
    keywords = RuleJson.decodeKeywords(keywordsJson),
    criteria = RuleJson.decodeCriteria(criteriaJson),
    action = RuleJson.decodeAction(actionJson)
)

fun Rule.toEntity(): RuleEntity = RuleEntity(
    id = id,
    name = name,
    enabled = enabled,
    appsJson = RuleJson.encodeApps(apps),
    keywordsJson = RuleJson.encodeKeywords(keywords),
    criteriaJson = RuleJson.encodeCriteria(criteria),
    actionJson = RuleJson.encodeAction(action),
    createdAt = System.currentTimeMillis(),
    updatedAt = System.currentTimeMillis()
)

@Serializable
data class CriteriaWrapper(
    val type: String,
    val value: JsonElement
)
