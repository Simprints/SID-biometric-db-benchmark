package com.example.benchmark.option1

import android.content.Context
import android.util.Log
import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.simprints.id.dbschemas.CreateRangesUseCase
import com.simprints.id.dbschemas.option1.BenchmarkDatabase
import com.simprints.id.dbschemas.option1.DataGenerator
import com.simprints.id.dbschemas.option1.DataGenerator.PROJECT_ID
import com.simprints.id.dbschemas.option1.DataGenerator.ROC_FORMAT
import com.simprints.id.dbschemas.option1.SubjectDao
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
    val context: Context = InstrumentationRegistry.getInstrumentation().targetContext

    var dao: SubjectDao

    init {
        try {
            System.loadLibrary("sqlcipher")
        } catch (_: UnsatisfiedLinkError) {
            println("Library not found")
        }
        val subjectsTemplatesPair =
            DataGenerator.generateSubjectsAndTemplates(10_000) // Generate 10,000 subjects = 40,000 records as each subject has 4 templates

        val passphrase: ByteArray = "passphrase".toByteArray(Charset.forName("UTF-8"))
        log("Benchmark option 1...")

        context.deleteDatabase("benchmark-option1.db")
        val db =
            Room.databaseBuilder(context, BenchmarkDatabase::class.java, "benchmark-option1.db")
                .openHelperFactory(SupportOpenHelperFactory(passphrase)).build()
        //log db size without data
        logDatabaseSize()

        dao = db.subjectDao()
        var insertTime = measureTimeMillis {
            dao.insertSubjects(subjectsTemplatesPair.first)
            dao.insertTemplates(subjectsTemplatesPair.second)
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
                val result = dao.getSubjectsFiltered(
                    projectId = PROJECT_ID,
                    format = ROC_FORMAT,
                    limit = range.last - range.first,
                    offset = range.first
                )
                println("Query result size: ${result.size}")
            }
        }
        logPerformanceMetrics(
            queryTime = queryTime, countTime = count.duration.inWholeMilliseconds
        )

    }

    private fun logPerformanceMetrics(queryTime: Long, countTime: Long) {
        log("$countTime, $queryTime")
    }

    fun logDatabaseSize() {
        val dbFile = context.getDatabasePath("benchmark-option1.db")
        val dbFileSize = dbFile.length() / 1024 // in KB
        log("Database file size: $dbFileSize KB")
    }

    private fun log(message: String) {
        Log.i("option1", message)
    }
}

