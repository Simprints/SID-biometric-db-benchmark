package com.simprints.id.dbschemas.option2b

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [BiometricTemplate::class],
    version = 1
)
abstract class BenchmarkDatabase : RoomDatabase() {
    abstract fun biometricTemplateDao(): BiometricTemplateDao
}