package com.simprints.id.dbschemas.option3

import java.util.UUID
import kotlin.random.Random

object DataGenerator {


    fun generateAndInsertSubjectsAndTemplates(count: Int, dao: SubjectDao) {
        val subjects = mutableListOf<Subject>()
        val templates = mutableListOf<BiometricTemplate>()
        val lookups = mutableListOf<FormatLookup>()
        repeat(count) {
            val subjectId = UUID.randomUUID().toString()
            val projectId = PROJECT_ID
            val moduleId = MODULE_ID
            val attendantId = ATTENDANT_ID
            val createdAt = System.currentTimeMillis()

            // Add subject
            subjects.add(
                Subject(
                    subjectId = subjectId,
                    projectId = projectId,
                    moduleId = moduleId,
                    attendantId = attendantId,
                    createdAt = createdAt
                )
            )
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

            lookups.add(
                FormatLookup(
                    subjectId = subjectId,
                    format = ROC_FORMAT,
                )
            )

            lookups.add(
                FormatLookup(
                    subjectId = subjectId,
                    format = NEC_FORMAT,
                )
            )
            // insert every 5000 subjects and clear the list
            if (it % 5000 == 0) {
                dao.insertSubjects(subjects)
                dao.insertTemplates(templates)
                dao.insertFormatLookup(lookups)
                templates.clear()
                lookups.clear()
                subjects.clear()
            }
        }
//        // Insert remaining items
        if (templates.isNotEmpty()) {
            dao.insertSubjects(subjects)
            dao.insertTemplates(templates)
            dao.insertFormatLookup(lookups)
        }
    }

    const val PROJECT_ID = "project1"
    const val MODULE_ID = "module1"
    const val ATTENDANT_ID = "attendant1"
    const val NEC_FORMAT = "NEC"
    const val ROC_FORMAT = "ROC"
}