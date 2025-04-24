package com.simprints.id.dbschemas.option3

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [
        Index(value = ["projectId", "subjectId"]),
        Index(value = ["projectId", "subjectId", "createdAt"])
    ]
)
data class Subject(
    @PrimaryKey val subjectId: String,
    val projectId: String,
    val moduleId: String?,
    val attendantId: String?,
    val createdAt: Long
)