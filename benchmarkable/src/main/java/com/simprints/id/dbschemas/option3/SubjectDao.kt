package com.simprints.id.dbschemas.option3

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.MapColumn
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SubjectDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSubjects(subjects: List<Subject>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTemplates(templates: List<BiometricTemplate>)

    @Insert
    fun insertFormatLookup(formatLookup: List<FormatLookup>)

    @Query(
        """
        SELECT b.subjectId, b.*
        FROM BiometricTemplate b
        JOIN (
            SELECT s.subjectId
            FROM Subject s
            INNER JOIN FormatLookup m ON s.subjectId = m.subjectId
            WHERE m.format = :format AND s.projectId = :projectId             
            ORDER BY s.createdAt
            LIMIT :limit OFFSET :offset
        ) AS filterSubjects
        ON b.subjectId = filterSubjects.subjectId and b.format = :format
        GROUP BY b.subjectId
        """
    )
    fun getSubjectsFiltered(
        projectId: String,
        format: String,
        limit: Int,
        offset: Int
    ): Map<
            @MapColumn("subjectId") String,
            List<BiometricTemplate>,
            >

    @Query(
        """
        SELECT COUNT(*) FROM (
    SELECT  Subject.subjectId
    FROM Subject
    INNER JOIN FormatLookup ON Subject.subjectId = FormatLookup.subjectId
    WHERE Subject.projectId = :projectId
      AND FormatLookup.format = :format
) 
    """
    )
    fun countDistinctSubjects(
        projectId: String,
        format: String,

        ): Int
}