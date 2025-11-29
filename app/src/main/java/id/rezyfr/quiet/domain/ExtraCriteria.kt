package id.rezyfr.quiet.domain

data class ExtraCriteria(val id: ExtraCriteriaType, val label: String, val description: String) {
    companion object {
        val DEFAULT =
            listOf(
                ExtraCriteria(
                    id = ExtraCriteriaType.TIME, label = "time", description = "at any time"
                ),
                ExtraCriteria(
                    id = ExtraCriteriaType.CALL_STATUS,
                    label = "call status",
                    description = "I'm on a call"
                ),
            )
    }
}

enum class ExtraCriteriaType {
    TIME,
    CALL_STATUS,
}
