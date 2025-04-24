package com.example.benchmark.option2

import android.content.Context
import android.util.Log
import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.simprints.id.dbschemas.CreateRangesUseCase
import com.simprints.id.dbschemas.option2.BenchmarkDatabase
import com.simprints.id.dbschemas.option2.DataGenerator
import com.simprints.id.dbschemas.option2.DataGenerator.PROJECT_ID
import com.simprints.id.dbschemas.option2.DataGenerator.ROC_FORMAT
import com.simprints.id.dbschemas.option2.SubjectDao
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.nio.charset.Charset
import kotlin.system.measureTimeMillis
import kotlin.time.measureTimedValue

@RunWith(AndroidJUnit4::class)
class SubjectWriteReadBenchmark {

    @get:Rule
    val benchmarkRule = BenchmarkRule()
    val createRanges = CreateRangesUseCase()
    var dao: SubjectDao
    val context: Context = InstrumentationRegistry.getInstrumentation().targetContext

    init {
        try {
            System.loadLibrary("sqlcipher")
        } catch (_: UnsatisfiedLinkError) {
            println("Library not found")
        }
        val subjects =
            DataGenerator.generateSubjects(10_000) // Generate 10,000 subjects = 40,000 records as each subject has 4 templates

        val passphrase: ByteArray = "passphrase".toByteArray(Charset.forName("UTF-8"))
        log("Benchmark option 2...")

        context.deleteDatabase("benchmark-option2.db")
        val db = Room
            .databaseBuilder(context, BenchmarkDatabase::class.java, "benchmark-option2.db")
            .openHelperFactory(SupportOpenHelperFactory(passphrase))
            .build()
        logDatabaseSize()
        dao = db.subjectDao()
        var insertTime = measureTimeMillis {
            dao.insertSubjects(subjects)
        }
        log("Insert time: $insertTime ms")
        // print db file size in kb
        logDatabaseSize()

    }


    @Test
    fun insertAndQuery5kSubjects() = benchmarkRule.measureRepeated {

        var count = measureTimedValue {
            dao.countDistinctSubjects(projectId = PROJECT_ID, format = ROC_FORMAT)

        }
        println("Count result: ${count.value}")
        val ranges = createRanges(count.value)

        var queryTime = measureTimeMillis {
            ranges.forEach { range ->
                // first get subject ids
                val subjectIds = dao.getPaginatedSubjectIds(
                    projectId = PROJECT_ID,
                    format = ROC_FORMAT,
                    limit = range.last - range.first,
                    offset = range.first
                )
                // then get templates for those subject ids
                val result = dao.getSubjectsBySubjectIds(subjectIds)
                    .groupBy { // group by subjectId to simulate a real-world scenario
                        it.subjectId
                    }
                println("Query result size: ${result.size}")
            }
        }
        logPerformanceMetrics(
            queryTime = queryTime,
            countTime = count.duration.inWholeMilliseconds
        )
    }

    private fun logPerformanceMetrics(
        queryTime: Long,
        countTime: Long
    ) {
        log("$countTime, $queryTime")
    }

    fun logDatabaseSize() {
        val dbFile = context.getDatabasePath("benchmark-option2.db")
        val dbFileSize = dbFile.length() / 1024 // in KB
        log("Database file size: $dbFileSize KB")
    }

    private fun log(message: String) {
        Log.i("option2", message)
    }
}

