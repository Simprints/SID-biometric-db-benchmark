package com.simprints.id.dbschemas.option1

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SubjectDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insertSubjects(subjects: List<Subject>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insertTemplates(templates: List<BiometricTemplate>)

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
    @Query(
        """
    SELECT * FROM BiometricTemplate 
    WHERE format =:format and subjectId IN (:subjectIds)
    """
    )
    fun getSubjectsBySubjectIds( format: String,subjectIds: List<String>): List<BiometricTemplate>
    @Query(
        """
    SELECT distinct Subject.subjectId 
    FROM Subject Inner JOIN BiometricTemplate ON Subject.subjectId = BiometricTemplate.subjectId
    WHERE Subject.projectId = :projectId AND BiometricTemplate.format = :format
    ORDER BY createdAt
    LIMIT :limit OFFSET :offset
    """
    )
    fun getPaginatedSubjectIds(
        projectId: String,
        format: String,
        limit: Int,
        offset: Int
    ): List<String>
}