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
                subjectId IN (
                    SELECT DISTINCT subjectId 
                    FROM BiometricTemplate
                    WHERE projectId = :projectId AND format = :format
                    ORDER BY subjectId
                    LIMIT :limit OFFSET :offset
                ) 
                AND format = :format         
        """
    )
    fun getBiometricTemplatesPaginatedBySubjects(
        projectId: String,
        format: String,
        limit: Int,
        offset: Int
    ): List<BiometricTemplate>
}