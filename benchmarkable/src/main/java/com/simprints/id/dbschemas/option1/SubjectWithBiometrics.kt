package com.simprints.id.dbschemas.option1

import androidx.room.ColumnInfo
import androidx.room.Relation

data class SubjectWithTemplates(
    @ColumnInfo(name = "subjectId") val subjectId: String,
    @Relation(
        parentColumn = "subjectId",
        entityColumn = "subjectId"
    )
    val templates: List<BiometricTemplate>
)