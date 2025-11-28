package id.rezyfr.quiet.domain

data class ExtraCriteria(val id: String, val label: String, val description: String) {
    companion object {
        val DEFAULT =
            listOf(
                ExtraCriteria(id = "time", label = "time", description = "at any time"),
                ExtraCriteria(
                    id = "call_status", label = "call status", description = "I'm on a call"),
            )
    }
}
