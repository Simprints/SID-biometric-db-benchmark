package com.example.benchmark

import android.content.Context

fun getTotalDatabaseSize(context: Context, dbName: String): Long {
    val dbDir = context.getDatabasePath(dbName).parentFile ?: return 0
    val dbPrefix = dbName.removeSuffix(".db")

    var totalSizeBytes = 0L

    dbDir.listFiles()?.forEach { file ->
        if (file.name.startsWith(dbPrefix)) {
            totalSizeBytes += file.length()
        }
    }

  return totalSizeBytes / 1024

}
