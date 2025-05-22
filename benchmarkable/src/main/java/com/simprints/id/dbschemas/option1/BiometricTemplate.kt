package com.simprints.id.dbschemas.option1

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [ForeignKey(
        entity = Subject::class,
        parentColumns = ["subjectId"],
        childColumns = ["subjectId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [
        Index(value = ["format", "subjectId"]),
    ],
)
@Suppress("ArrayInDataClass")
data class BiometricTemplate(
    @PrimaryKey val templateId: String,
    val subjectId: String,
    val format: String,
    val fingerPosition: Int?,
    val templateData: ByteArray
)