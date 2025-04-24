package com.simprints.id.dbschemas.option1

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Subject::class, BiometricTemplate::class], version = 1)
abstract class BenchmarkDatabase : RoomDatabase() {
    abstract fun subjectDao(): SubjectDao
}