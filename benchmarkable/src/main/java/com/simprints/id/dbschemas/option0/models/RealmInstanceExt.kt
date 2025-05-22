package com.simprints.id.dbschemas.option0.models

import io.realm.kotlin.types.RealmInstant
import java.util.Date

/**
 * Converting epoch milliseconds of java.util.Date to pair of (epoch seconds, nanosecond offset).
 */
fun Date.toRealmInstant() = RealmInstant.from(
    time / MILLIS_IN_SECOND,
    (time % MILLIS_IN_SECOND * NANOS_IN_MILLI).toInt(),
)


private const val MILLIS_IN_SECOND = 1000L
private const val NANOS_IN_MILLI = 1_000_000L
