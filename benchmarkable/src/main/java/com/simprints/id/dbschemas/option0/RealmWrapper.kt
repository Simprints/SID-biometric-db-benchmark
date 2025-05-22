package com.simprints.id.dbschemas.option0

import io.realm.kotlin.MutableRealm
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RealmWrapper(
    private val configFactory: RealmConfig,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    private lateinit var realm: Realm
    private lateinit var config: RealmConfiguration

    private fun getRealm(): Realm {
        if (!this::realm.isInitialized) {
            // byte array of 64 bytes
            val key = ByteArray(64)
            config = configFactory.get( key)
            realm = createRealm()
        }
        return realm
    }

    private fun createRealm(): Realm =
        Realm.open(config)

    suspend fun <R> readRealm(block: (Realm) -> R): R =
        withContext(dispatcher) { block(getRealm()) }

    suspend fun <R> writeRealm(block: (MutableRealm) -> R) {
        withContext(dispatcher) { getRealm().write(block) }
    }

}
