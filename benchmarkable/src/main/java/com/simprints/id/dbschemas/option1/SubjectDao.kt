package com.simprints.id.dbschemas.option1

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface SubjectDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insertSubjects(subjects: List<Subject>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insertTemplates(templates: List<BiometricTemplate>)

    @Transaction
    @Query("""
        SELECT Subject.subjectId,BiometricTemplate.*  FROM Subject
        Inner JOIN BiometricTemplate ON Subject.subjectId = BiometricTemplate.subjectId
        WHERE projectId = :projectId        
          AND format = :format
          order by Subject.subjectId
        LIMIT :limit OFFSET :offset
    """)
    fun getSubjectsFiltered(
        projectId: String,
        format: String,
        limit: Int,
        offset: Int
    ): List<SubjectWithTemplates>

    @Query("""
        SELECT COUNT(*) FROM (
    SELECT DISTINCT Subject.subjectId
    FROM Subject
    INNER JOIN BiometricTemplate ON Subject.subjectId = BiometricTemplate.subjectId
    WHERE Subject.projectId = :projectId
      AND BiometricTemplate.format = :format
) 
    """)
    fun countDistinctSubjects(
        projectId: String,
        format: String,

    ): Int
}