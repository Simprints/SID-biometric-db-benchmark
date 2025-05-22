package com.example.benchmark.option3

import android.content.Context
import android.util.Log
import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.benchmark.deleteAllDatabaseFiles
import com.example.benchmark.getTotalDatabaseSize
import com.simprints.id.dbschemas.CreateRangesUseCase
import com.simprints.id.dbschemas.TestingParams.Companion.NUMBER_OF_ITERATIONS
import com.simprints.id.dbschemas.TestingParams.Companion.NUMBER_OF_SUBJECTS
import com.simprints.id.dbschemas.option3.BenchmarkDatabase
import com.simprints.id.dbschemas.option3.DataGenerator
import com.simprints.id.dbschemas.option3.DataGenerator.PROJECT_ID
import com.simprints.id.dbschemas.option3.DataGenerator.ROC_FORMAT
import com.simprints.id.dbschemas.option3.SubjectDao
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.nio.charset.Charset
import kotlin.system.measureTimeMillis
import kotlin.time.measureTimedValue

@RunWith(AndroidJUnit4::class)
class BiometricTemplateWriteReadBenchmark {

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
        val passphrase: ByteArray = "passphrase".toByteArray(Charset.forName("UTF-8"))
        log("Benchmark option 3 with $NUMBER_OF_SUBJECTS ...")

        deleteAllDatabaseFiles(context, "benchmark-option3.db")
        val db = Room
            .databaseBuilder(context, BenchmarkDatabase::class.java, "benchmark-option3.db")
            .openHelperFactory(SupportOpenHelperFactory(passphrase))
            .build()
        dao = db.subjectDao()
        val insertTime = measureTimeMillis {
            DataGenerator.generateAndInsertSubjectsAndTemplates(NUMBER_OF_SUBJECTS, dao)
        }
        log("Insert time: $insertTime ms")
        log("DB size: ${getTotalDatabaseSize(context, "benchmark-option3.db")}KB")
    }

    @After
    fun tearDown() {
        deleteAllDatabaseFiles(context, "benchmark-option3.db")
    }

    var iterations = 0

    @Test
    fun insertAndQuery5kSubjects() = benchmarkRule.measureRepeated {
        val count = measureTimedValue {
            dao.countDistinctSubjects(projectId = PROJECT_ID, format = ROC_FORMAT)
        }
        println("Count result: ${count.value}")
        val ranges = createRanges(count.value)
        val queryTime = measureTimeMillis {
            ranges.forEach { range ->
                val result = dao
                    .getSubjectsFiltered(
                        projectId = PROJECT_ID,
                        format = ROC_FORMAT,
                        limit = range.last - range.first,
                        offset = range.first
                    )
                    .groupBy { // group by subjectId to simulate a real-world scenario
                        it.subjectId
                    }
                println("Query result size: ${result.size} subjects")
            }
        }
        logPerformanceMetrics(
            queryTime = queryTime,
            countTime = count.duration.inWholeMilliseconds
        )
        iterations++
        if (iterations > NUMBER_OF_ITERATIONS) {
            throw Exception("Reached number of iterations")
        }

    }

    private fun logPerformanceMetrics(queryTime: Long, countTime: Long) {
        log("$countTime, $queryTime")
    }

    fun logDatabaseSize() {
        val dbFile = context.getDatabasePath("benchmark-option3.db")
        val dbFileSize = dbFile.length() / 1024 // in KB
        log("Database file size: $dbFileSize KB")
    }

    private fun log(message: String) {
        Log.i("option3", message)
    }
}
