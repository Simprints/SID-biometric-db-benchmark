package com.simprints.id.dbschemas.option2

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns

@Dao
interface BiometricTemplateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(subjects: List<BiometricTemplate>)

    // count of distinct subjects that has a specific projectId and format
    @Query(
        """
            SELECT COUNT(DISTINCT subjectId) 
            FROM BiometricTemplate
            WHERE 
                projectId = :projectId
                AND format = :format
        """
    )
    fun countSubjects(projectId: String, format: String): Int

    @RewriteQueriesToDropUnusedColumns
    @Query(
        """
            SELECT * 
            FROM BiometricTemplate
            WHERE 
                subjectId IN (:subjectIds)
                AND format = :format
        """
    )
    fun getBiometricTemplatesBySubjectIds(
        format: String,
        subjectIds: List<String>
    ): List<BiometricTemplate>

    @Query(
        """
            SELECT DISTINCT subjectId 
            FROM BiometricTemplate
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