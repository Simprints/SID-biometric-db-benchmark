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
import com.simprints.id.dbschemas.option2.BenchmarkDatabase
import com.simprints.id.dbschemas.option2.DataGenerator
import com.simprints.id.dbschemas.option2.DataGenerator.PROJECT_ID
import com.simprints.id.dbschemas.option2.DataGenerator.ROC_FORMAT
import com.simprints.id.dbschemas.option2.BiometricTemplateDao
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory
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
        val templates =
            DataGenerator.generateBiometricTemplates(10_000) // Generate 10,000 subjects = 40,000 records as each subject has 4 templates

        val passphrase: ByteArray = "passphrase".toByteArray(Charset.forName("UTF-8"))
        log("Benchmark option 2...")

        deleteAllDatabaseFiles(context, "benchmark-option2.db")
        val db = Room
            .databaseBuilder(context, BenchmarkDatabase::class.java, "benchmark-option2.db")
            .openHelperFactory(SupportOpenHelperFactory(passphrase))
            .build()
        log(getTotalDatabaseSize(context, "benchmark-option2.db").toString() + " KB")

        dao = db.biometricTemplateDao()
        val insertTime = measureTimeMillis {
            dao.insert(templates)
        }
        log("Insert time: $insertTime ms")
        // print db file size in kb
        log(getTotalDatabaseSize(context, "benchmark-option2.db").toString() + " KB")
    }


    @Test
    fun insertAndQuery5kSubjects() = benchmarkRule.measureRepeated {
        val count = measureTimedValue {
            dao.countSubjects(projectId = PROJECT_ID, format = ROC_FORMAT)
        }
        log("Count result: ${count.value}")
        val ranges = createRanges(count.value)

        val queryTime = measureTimeMillis {
            ranges.forEach { range ->
                // first get subject ids
                val subjectIds = dao.getPaginatedSubjectIds(
                    projectId = PROJECT_ID,
                    format = ROC_FORMAT,
                    limit = range.last - range.first,
                    offset = range.first
                )
                // then get templates for those subject ids
                val subjects = dao
                    .getBiometricTemplatesBySubjectIds(ROC_FORMAT, subjectIds)
                    .groupBy { // group by subjectId to simulate a real-world scenario
                        it.subjectId
                    }
                log(
                    "Query result size: ${subjects.size} subjects, ${
                        subjects.values.flatten().count()
                    } templates"
                )
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


    private fun log(message: String) {
        Log.i("option2", message)
    }
}

