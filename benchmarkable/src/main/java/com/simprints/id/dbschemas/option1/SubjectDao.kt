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

     // I don't think this is behaving the way you expect it to.
     // If you build and go take a look at com/simprints/id/dbschemas/option1/SubjectDao_Impl.kt,
     // you'll see that because you are using a @Relation annotation in SubjectWithTemplates, Room:
     // - First performs the main query that joins with BiometricTemplate
     // - But then ignore the BiometricTemplate.* colums, and runs one or more queries like
     //  "SELECT `templateId`,`subjectId`,`format`,`fingerPosition`,`templateData` FROM `BiometricTemplate` WHERE `subjectId` IN (xxx)"
     // This is not optimal performance wise. For a fair benchmark, I would stop using @Relation
     // in SubjectWithTemplates so that only one SQL query is performed
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