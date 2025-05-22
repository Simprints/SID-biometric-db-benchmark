package com.simprints.id.dbschemas.option0

import androidx.annotation.Keep
import com.simprints.id.dbschemas.option0.models.DbFaceSample
import com.simprints.id.dbschemas.option0.models.DbFingerprintSample
import com.simprints.id.dbschemas.option0.models.DbSubject
import io.realm.kotlin.RealmConfiguration

@Keep
class RealmConfig {
    fun get(
        key: ByteArray,
    ) = RealmConfiguration
        .Builder(
            setOf(
                DbFingerprintSample::class,
                DbFaceSample::class,
                DbSubject::class,
            ),
        )
        .schemaVersion(1).encryptionKey(key)
        .build()
}
