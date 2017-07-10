package cz.pepa.runapp.database

import com.google.firebase.database.FirebaseDatabase
import cz.pepa.runapp.logic.Auth
import isDatabaseConnected


/**
 * Main access point to Firebase Realtime Database. Handles initialization.
 *
 * @author David Vávra (david@stepuplabs.io)
 */
object Database {

    private var mInitialized = false

    fun get(): FirebaseDatabase {
        val db = FirebaseDatabase.getInstance()
        if (!mInitialized) {
            db.setPersistenceEnabled(true)
            mInitialized = true
            sync()
        }
        return db
    }

    fun connect() {
        if (!isDatabaseConnected()) {
            get().goOnline()
        }
    }

    fun disconnect() {
        get().goOffline()
    }

    fun sync() {
        if (Auth.isSignedIn()) {
//            DatabaseRead.userGroups().subscribe({
//                it?.forEach {
//                    syncGroup(it.getId(), true)
//                }
//            }, { logError(it) })
//            keepSynced("exchangeRatesToUsd/latest", true)
        }
    }

    fun stopSync(groupId: String) {
        syncGroup(groupId, false)
    }

    private fun syncGroup(groupId: String, synced: Boolean) {
        keepSynced("groups/$groupId", synced)
        keepSynced("members/$groupId", synced)
        keepSynced("transactions/$groupId", synced)
        keepSynced("changes/$groupId", synced)
        keepSynced("permissions/$groupId", synced)
    }

    private fun keepSynced(path: String, synced: Boolean) {
        get().reference.child(path).keepSynced(synced)
    }
}
