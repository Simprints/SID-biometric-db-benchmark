package com.simprints.id.dbschemas.option2

import java.util.UUID
import kotlin.random.Random

object DataGenerator {
    fun generateSubjects(n: Int): List<Subject> {

        val subjects = mutableListOf<Subject>()
        repeat(n) {
            val subjectId = UUID.randomUUID().toString()
            val projectId = PROJECT_ID
            val moduleId = MODULE_ID
            val attendantId = ATTENDANT_ID
            val createdAt = System.currentTimeMillis()

            // 2 ROC subjects
            repeat(2) {
                subjects.add(
                    Subject(
                        subjectId = subjectId,
                        projectId = projectId,
                        moduleId = moduleId,
                        attendantId = attendantId,
                        createdAt = createdAt,
                        format = ROC_FORMAT,
                        fingerPosition = null,
                        templateData = Random.nextBytes(256)
                    )
                )
            }

            // 2 NEC subjects with fingerPosition 1 and 2
            (1..2).forEach { position ->
                subjects.add(
                    Subject(
                        subjectId = subjectId,
                        projectId = projectId,
                        moduleId = moduleId,
                        attendantId = attendantId,
                        createdAt = createdAt,
                        format = NEC_FORMAT,
                        fingerPosition = position,
                        templateData = Random.nextBytes(256)
                    )
                )
            }
        }
        return subjects
    }
    const val PROJECT_ID = "project1"
    const val MODULE_ID = "module1"
    const val ATTENDANT_ID = "attendant1"
    const val NEC_FORMAT = "NEC"
    const val ROC_FORMAT = "ROC"
}