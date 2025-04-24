package com.simprints.id.dbschemas.option2

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SubjectDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSubjects(subjects: List<Subject>)


    @Query(
        """
    SELECT * FROM Subject
    WHERE subjectId IN (
        SELECT distinct subjectId FROM Subject
        WHERE projectId = :projectId AND format = :format
        GROUP BY subjectId
        ORDER BY createdAt
        LIMIT :limit OFFSET :offset
    )
"""
    )
    fun getSubjectsFiltered(
        projectId: String,
        format: String,
        limit: Int,
        offset: Int
    ): List<Subject>

    // count of distinct   subjects that has a specific projectId and format
    @Query(
        """
        SELECT COUNT(DISTINCT subjectId) FROM Subject
        WHERE projectId = :projectId
          AND format = :format
    """
    )
    fun countDistinctSubjects(projectId: String, format: String): Int

    @Query(
        """
    SELECT * FROM Subject
    WHERE subjectId IN (:subjectIds)
    """
    )
    fun getSubjectsBySubjectIds(subjectIds: List<String>): List<Subject>
    @Query(
        """
    SELECT distinct subjectId 
    FROM Subject
    WHERE projectId = :projectId AND format = :format
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