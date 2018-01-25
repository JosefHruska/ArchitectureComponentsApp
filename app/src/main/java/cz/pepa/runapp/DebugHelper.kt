package cz.pepa.runapp

import android.support.v4.app.FragmentActivity
import cz.pepa.runapp.api.GoogleFitReader

/**
 * // TODO: Add description
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

object DebugHelper {

    fun start(activity: FragmentActivity) {
        GoogleFitReader.initGoogleFit(activity)
        GoogleFitReader.updateAllDays()
    }
}