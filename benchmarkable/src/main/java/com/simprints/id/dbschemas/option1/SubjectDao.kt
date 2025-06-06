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

    @Query(
        """
            SELECT COUNT(DISTINCT BiometricTemplate.subjectId)
            FROM 
            Subject INNER JOIN BiometricTemplate ON Subject.subjectId = BiometricTemplate.subjectId
            WHERE Subject.projectId = :projectId
                  AND BiometricTemplate.format = :format
        """
    )
    fun countSubjects(
        projectId: String,
        format: String,
    ): Int


    @Query(
        """ SELECT * 
            FROM BiometricTemplate 
            WHERE format =:format and subjectId IN (
                       SELECT DISTINCT s.subjectId 
            FROM 
                Subject s
                INNER JOIN BiometricTemplate t ON s.subjectId = t.subjectId
            WHERE 
                s.projectId = :projectId 
                AND t.format = :format
            ORDER BY s.subjectId
            LIMIT :limit OFFSET :offset)
    """
    )
    fun getPaginatedSubjectIds(
        projectId: String,
        format: String,
        limit: Int,
        offset: Int
    ):List<BiometricTemplate>
}