package id.rezyfr.quiet.domain.model

data class Rule(
    val id: Long,
    val name: String,
    val enabled: Boolean,
    val apps: List<String>,
    val keywords: List<String>,
    val criteria: List<RuleCriteria>,
    val action: RuleAction
)
