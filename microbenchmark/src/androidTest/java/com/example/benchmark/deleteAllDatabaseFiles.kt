package com.example.benchmark

import android.content.Context

fun deleteAllDatabaseFiles(context: Context, dbName: String) {
    val dbDir = context.getDatabasePath(dbName).parentFile ?: return
    val dbPrefix = dbName.removeSuffix(".db")

    var deletedFiles = 0
    dbDir.listFiles()?.forEach { file ->
        if (file.name.startsWith(dbPrefix) && file.delete()) {
            deletedFiles++
        }
    }
}