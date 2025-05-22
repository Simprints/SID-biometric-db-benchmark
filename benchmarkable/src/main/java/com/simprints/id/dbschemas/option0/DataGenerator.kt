package com.simprints.id.dbschemas.option0


import com.simprints.id.dbschemas.option0.models.DbFaceSample
import com.simprints.id.dbschemas.option0.models.DbSubject
import com.simprints.id.dbschemas.option0.models.toRealmInstant
import io.realm.kotlin.types.RealmUUID
import java.util.Date
import java.util.UUID
import kotlin.random.Random

object DataGenerator {


    suspend fun generateAndInsertSubjects(count: Int, realmWrapper: RealmWrapper) {
        realmWrapper.writeRealm { realm ->
            repeat(count) {
                val dbSubject = DbSubject()
                dbSubject.subjectId = RealmUUID.random()
                dbSubject.projectId = PROJECT_ID
                dbSubject.moduleId = MODULE_ID
                dbSubject.attendantId = ATTENDANT_ID
                dbSubject.createdAt = Date().toRealmInstant()
                repeat(2) {
                    val faceSample = DbFaceSample()
                    faceSample.id = UUID.randomUUID().toString()
                    faceSample.referenceId = UUID.randomUUID().toString()
                    faceSample.template = Random.nextBytes(256)
                    faceSample.format = ROC_FORMAT
                    dbSubject.faceSamples.add(
                        faceSample
                    )
                }

                (1..2).forEach { position ->
                    val fingerprintSample = DbFaceSample()
                    fingerprintSample.id = UUID.randomUUID().toString()
                    fingerprintSample.referenceId = UUID.randomUUID().toString()
                    fingerprintSample.template = Random.nextBytes(256)
                    fingerprintSample.format = NEC_FORMAT
                    dbSubject.faceSamples.add(
                        fingerprintSample
                    )

                }
                realm.copyToRealm(dbSubject)

            }
        }

    }

    const val PROJECT_ID = "project1"
    const val MODULE_ID = "module1"
    const val ATTENDANT_ID = "attendant1"
    const val NEC_FORMAT = "NEC"
    const val ROC_FORMAT = "ROC"
}