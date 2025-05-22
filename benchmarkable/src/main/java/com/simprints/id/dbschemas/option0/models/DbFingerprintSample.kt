package com.simprints.id.dbschemas.option0.models

import androidx.annotation.Keep
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

@Keep
class DbFingerprintSample : RealmObject {
    @PrimaryKey
    var id: String = ""
    var referenceId = ""
    var fingerIdentifier: Int = -1
    var template: ByteArray = byteArrayOf()
    var format: String = ""
}
