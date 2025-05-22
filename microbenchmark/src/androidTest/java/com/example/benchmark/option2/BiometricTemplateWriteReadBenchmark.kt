package com.example.benchmark.option2

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
import com.simprints.id.dbschemas.option2.BenchmarkDatabase
import com.simprints.id.dbschemas.option2.BiometricTemplateDao
import com.simprints.id.dbschemas.option2.DataGenerator
import com.simprints.id.dbschemas.option2.DataGenerator.PROJECT_ID
import com.simprints.id.dbschemas.option2.DataGenerator.ROC_FORMAT
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
    var dao: BiometricTemplateDao
    val context: Context = InstrumentationRegistry.getInstrumentation().targetContext

    init {
        try {
            System.loadLibrary("sqlcipher")
        } catch (_: UnsatisfiedLinkError) {
            println("Library not found")
        }
        // Generate 10,000 subjects = 40,000 records as each subject has 4 templates
        val passphrase: ByteArray = "passphrase".toByteArray(Charset.forName("UTF-8"))
        log("Benchmark option 2 with $NUMBER_OF_SUBJECTS ...")
        deleteAllDatabaseFiles(context, "benchmark-option2.db")
        val db =
            Room.databaseBuilder(context, BenchmarkDatabase::class.java, "benchmark-option2.db")
                .openHelperFactory(SupportOpenHelperFactory(passphrase)).build()

        dao = db.biometricTemplateDao()
        val insertTime = measureTimeMillis {
            DataGenerator.generateAndInsertBiometricTemplates(NUMBER_OF_SUBJECTS, dao)
        }
        log("Insert time: $insertTime ms")
        // print db file size in kb
        log("DB size: ${getTotalDatabaseSize(context, "benchmark-option2.db")}KB")
    }

    @After
    fun tearDown() {
        // Delete the database file
        deleteAllDatabaseFiles(context, "benchmark-option2.db")
    }

    var iterations = 0

    @Test
    fun insertAndQuery5kSubjects() = benchmarkRule.measureRepeated {
        val count = measureTimedValue {
            dao.countSubjects(projectId = PROJECT_ID, format = ROC_FORMAT)
        }
        println("Count result: ${count.value}")
        val ranges = createRanges(count.value)

        val queryTime = measureTimeMillis {
            ranges.forEachIndexed { i, range ->
                val subjects = dao.getBiometricTemplatesPaginatedBySubjects(
                        projectId = PROJECT_ID,
                        format = ROC_FORMAT,
                        limit = range.last - range.first,
                        offset = range.first
                    ).groupBy { it.subjectId }
                println("Query result size: ${subjects.size} subjects")
            }
        }
        logPerformanceMetrics(
            queryTime = queryTime, countTime = count.duration.inWholeMilliseconds
        )
        iterations++
        if (iterations > NUMBER_OF_ITERATIONS) {
            throw Exception("Reached number of iterations")
        }
    }

    private fun logPerformanceMetrics(queryTime: Long, countTime: Long) {
        log("$countTime, $queryTime")
    }

    private fun log(message: String) {
        Log.i("option2", message)
    }
}
