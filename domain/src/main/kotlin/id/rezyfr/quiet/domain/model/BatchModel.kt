package id.rezyfr.quiet.domain.model

data class BatchModel(
    val id: Long,
    val ruleId: Long,
    val title: String,
    val text: String,
    val packageName: String,
    val timestamp: Long
)
