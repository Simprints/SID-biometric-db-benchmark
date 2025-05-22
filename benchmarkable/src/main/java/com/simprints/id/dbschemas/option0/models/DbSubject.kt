package com.simprints.id.dbschemas.option0.models

import androidx.annotation.Keep
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.RealmUUID
import io.realm.kotlin.types.annotations.PrimaryKey

@Keep
class DbSubject : RealmObject {
    @PrimaryKey
    var subjectId: RealmUUID = RealmUUID.random()
    var projectId: String = ""
    var attendantId: String = ""
    var moduleId: String = ""
    var createdAt: RealmInstant? = null

    var fingerprintSamples: RealmList<DbFingerprintSample> = realmListOf()
    var faceSamples: RealmList<DbFaceSample> = realmListOf()

}
