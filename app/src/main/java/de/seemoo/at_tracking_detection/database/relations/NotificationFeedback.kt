package de.seemoo.at_tracking_detection.database.relations

import androidx.room.Relation
import de.seemoo.at_tracking_detection.database.models.Feedback
import java.time.LocalDateTime


data class NotificationFeedback(
    val notificationId: Int,
    val falseAlarm: Boolean,
    val dismissed: Boolean,
    val clicked: Boolean,
    val createdAt: LocalDateTime,
    @Relation(
        parentColumn = "notificationId",
        entityColumn = "notificationId"
    )
    val feedback: Feedback?
)
