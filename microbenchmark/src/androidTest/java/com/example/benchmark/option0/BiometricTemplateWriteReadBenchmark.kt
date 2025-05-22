package com.example.benchmark.option0

import android.content.Context
import android.util.Log
import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.simprints.id.dbschemas.CreateRangesUseCase
import com.simprints.id.dbschemas.TestingParams.Companion.NUMBER_OF_ITERATIONS
import com.simprints.id.dbschemas.TestingParams.Companion.NUMBER_OF_SUBJECTS
import com.simprints.id.dbschemas.option0.DataGenerator
import com.simprints.id.dbschemas.option0.DataGenerator.PROJECT_ID
import com.simprints.id.dbschemas.option0.DataGenerator.ROC_FORMAT
import com.simprints.id.dbschemas.option0.RealmConfig
import com.simprints.id.dbschemas.option0.RealmWrapper
import com.simprints.id.dbschemas.option0.models.DbSubject
import io.realm.kotlin.query.find
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import kotlin.system.measureTimeMillis
import kotlin.time.measureTimedValue

@RunWith(AndroidJUnit4::class)
class BiometricTemplateWriteReadBenchmark {

    @get:Rule
    val benchmarkRule = BenchmarkRule()
    val createRanges = CreateRangesUseCase()
    val realmWrapper = RealmWrapper(RealmConfig())
    val context: Context = InstrumentationRegistry.getInstrumentation().targetContext

    init {
        log("Benchmark option 0 with $NUMBER_OF_SUBJECTS ...")
        val insertTime = measureTimeMillis {
            runBlocking {
                DataGenerator.generateAndInsertSubjects(
                    NUMBER_OF_SUBJECTS, realmWrapper
                )
            }
        }
        println("Insert time: $insertTime ms")
        log("Insert time: $insertTime ms")
        log("DB size: ${getRealmDbSizeInKB()}KB")
    }

    fun getRealmDbSizeInKB(): Double {
        val realmFile = File(context.filesDir, "default.realm")
        return if (realmFile.exists()) {
            realmFile.length() / 1024.0
        } else {
            0.0
        }
    }

    var iterations = 0

    @Test
    fun insertAndQuery5kSubjects() = benchmarkRule.measureRepeated {
        runBlocking {
            realmWrapper.readRealm { realm ->
                val count = measureTimedValue {
                    realm.query(DbSubject::class).query("projectId == $0", PROJECT_ID)
                        .query("faceSamples.format == $0", ROC_FORMAT).count().find().toInt()
                }
                println("Count result: ${count.value}")
                val ranges = createRanges(count.value)
                val queryTime = measureTimeMillis {
                    ranges.forEach { range ->
                        // first get subject ids
                        val subjects =
                            realm.query(DbSubject::class).query("projectId == $0", PROJECT_ID)
                                .query("faceSamples.format == $0", ROC_FORMAT)
                                .find { it.subList(range.first, range.last) }
                        println("Query result size: ${subjects.size} subjects")
                    }
                }
                logPerformanceMetrics(
                    queryTime = queryTime, countTime = count.duration.inWholeMilliseconds
                )
            }
            iterations++
            if (iterations > NUMBER_OF_ITERATIONS) {
                throw Exception("Reached number of iterations")
            }
        }
    }

    @After
    fun tearDown() {
        val realmFile = File(context.filesDir, "default.realm")
        if (realmFile.exists()) {
            realmFile.delete()
        }
    }

    private fun logPerformanceMetrics(queryTime: Long, countTime: Long) {
        log("$countTime, $queryTime")
    }

    private fun log(message: String) {
        Log.i("option0", message)
    }
}