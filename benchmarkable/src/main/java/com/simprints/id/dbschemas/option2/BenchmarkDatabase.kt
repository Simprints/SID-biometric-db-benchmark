package com.simprints.id.dbschemas.option2

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Subject::class],

    version = 1)
abstract class BenchmarkDatabase : RoomDatabase() {
    abstract fun subjectDao(): SubjectDao
}