package com.simprints.id.dbschemas.option2

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [
        Index(value = ["projectId", "format", "subjectId"]),
        Index(value = ["subjectId", "format"]),
    ]
)
@Suppress("ArrayInDataClass")
data class BiometricTemplate(
    @PrimaryKey(autoGenerate = true)
    val id: Long=0,
    val subjectId: String,
    val projectId: String,
    val moduleId: String?,
    val attendantId: String?,
    val createdAt: Long,
    val format: String,
    val fingerPosition: Int?,
    val templateData: ByteArray
)