package id.rezyfr.quiet.domain

data class NotificationModel(
    val sbnKey: String,
    val packageName: String,
    val title: String,
    val text: String,
    val postTime: Long,
    val saved: Boolean,
)

data class NotificationUiModel(
    val sbnKey: String,
    val packageName: String,
    val title: String,
    val text: String,
    val postTime: String,
)
