package com.simprints.id.dbschemas.option1

import java.util.*
import kotlin.random.Random

object DataGenerator {



    fun generateSubjectsAndTemplates(count: Int): Pair<List<Subject>, List<BiometricTemplate>> {
        val subjects = mutableListOf<Subject>()
        val templates = mutableListOf<BiometricTemplate>()

        repeat(count) {
            val subjectId = UUID.randomUUID().toString()
            val projectId = PROJECT_ID
            val moduleId = MODULE_ID
            val attendantId =ATTENDANT_ID
            val createdAt = System.currentTimeMillis()

            // Add subject
            subjects.add( Subject(
                subjectId = subjectId,
                projectId = projectId,
                moduleId = moduleId,
                attendantId = attendantId,
                createdAt = createdAt
            ))
            // 2 ROC templates (fingerPosition = null)
            repeat(2) {
                templates.add(
                    BiometricTemplate(
                        templateId = UUID.randomUUID().toString(),
                        subjectId = subjectId,
                        format = ROC_FORMAT,
                        fingerPosition = null,
                        templateData = Random.nextBytes(256)
                    )
                )
            }

            // 2 NEC templates with fingerPosition 1 and 2
            (1..2).forEach { position ->
                templates.add(
                    BiometricTemplate(
                        templateId = UUID.randomUUID().toString(),
                        subjectId = subjectId,
                        format = NEC_FORMAT,
                        fingerPosition = position,
                        templateData = Random.nextBytes(256)
                    )
                )
            }
        }


        return Pair(subjects, templates)
    }
    const val PROJECT_ID = "project1"
    const val MODULE_ID = "module1"
    const val ATTENDANT_ID = "attendant1"
    const val NEC_FORMAT = "NEC"
    const val ROC_FORMAT = "ROC"
}