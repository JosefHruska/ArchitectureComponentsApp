package io.stepuplabs.settleup.firebase.database

import android.content.SharedPreferences
import io.stepuplabs.settleup.R
import io.stepuplabs.settleup.app
import io.stepuplabs.settleup.mvp.presenter.BasePresenter
import io.stepuplabs.settleup.ui.common.TimedBlockingProgress
import io.stepuplabs.settleup.util.Preferences
import io.stepuplabs.settleup.util.extensions.doLater
import org.jetbrains.anko.defaultSharedPreferences
import org.jetbrains.anko.runOnUiThread
import rx.Emitter
import rx.Observable

/**
 * Monitors connected state and maintains a number of unsynced changes.
 *
 * @author David Vávra (david@stepuplabs.io)
 */
object Offline {

    private const val FIREBASE_CONNECT_TIMEOUT = 30 // seconds
    private var mMonitoring = false
    private var mActionWhenConnected: (() -> Unit)? = null
    private var mOfflineChanges = 0

    fun monitorConnectedState() {
        if (!mMonitoring) {
            mMonitoring = true
            mOfflineChanges = Preferences.getUnsyncedChanges()
            DatabaseRead.connected().subscribe {
                gOnline = it
                if (gOnline) {
                    mOfflineChanges = 0
                    Preferences.saveUnsyncedChanges(mOfflineChanges)
                    if (mActionWhenConnected != null) {
                        app().runOnUiThread {
                            TimedBlockingProgress.stop(R.string.connecting)
                            mActionWhenConnected?.invoke()
                            mActionWhenConnected = null
                        }
                    }
                }
            }
        }
    }

    fun addChangeIfOffline() {
        if (!gOnline) {
            mOfflineChanges++
            Preferences.saveUnsyncedChanges(mOfflineChanges)
        }
    }

    fun numberOfOfflineChanges(): Observable<Int?> {
        return Observable.create<Int>({
            it.onNext(mOfflineChanges)
            val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
                if (key == "UNSYNCED_CHANGES") {
                    it.onNext(mOfflineChanges)
                }
            }
            app().defaultSharedPreferences.registerOnSharedPreferenceChangeListener(listener)
            it.setCancellation {
                app().defaultSharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
            }
        }, Emitter.BackpressureMode.BUFFER)
    }

    fun doWhenDatabaseIsConnected(presenter: BasePresenter<*>, action: () -> Unit) {
        if (gOnline) {
            action()
        } else {
            mActionWhenConnected = action
            TimedBlockingProgress.start(R.string.connecting, presenter, FIREBASE_CONNECT_TIMEOUT)
            app().doLater((FIREBASE_CONNECT_TIMEOUT * 1000).toLong()) {
                TimedBlockingProgress.stop(R.string.connecting)
                if (!gOnline) {
                    if (presenter.isViewAttached) {
                        presenter.getView().showConnectError()
                    }
                }
                mActionWhenConnected = null
            }
        }
    }
}

private var gOnline = false

fun isDatabaseConnected(): Boolean {
    return gOnline
}