package com.simprints.id.dbschemas.option3

import androidx.room.Entity

@Entity(
    primaryKeys = ["subjectId", "format"],
)
data class FormatLookup(
    val subjectId: String,
    val format: String,
)
