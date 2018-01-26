package cz.pepa.runapp.database


/**
 * Monitors connected state
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */
object Offline {

    private var mMonitoring = false
    private var mOfflineChanges = 0

    fun monitorConnectedState() {
        if (!mMonitoring) {
            mMonitoring = true
            DatabaseRead.connected().subscribe {
                gOnline = it
                if (gOnline) {
                    mOfflineChanges = 0
                    }
                }
            }
        }
    }

private var gOnline = false

fun isDatabaseConnected(): Boolean = gOnline