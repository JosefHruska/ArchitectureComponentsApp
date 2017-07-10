package cz.pepa.runapp.extensions

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import cz.pepa.runapp.app
import org.jetbrains.anko.connectivityManager
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

/**
 * Various utils related to Android system.
 *
 * @author David Vávra (david@stepuplabs.io)
 */

fun isLargerOrEqualApi(apiLevel: Int): Boolean {
    return Build.VERSION.SDK_INT >= apiLevel
}

fun isLowerOrEqualApi(apiLevel: Int): Boolean {
    return Build.VERSION.SDK_INT <= apiLevel
}

fun Context.getVersionName(): String {
    return this.packageManager.getPackageInfo(this.packageName, 0).versionName
}

fun Context.getVersionCode(): Int {
    return this.packageManager.getPackageInfo(this.packageName, 0).versionCode
}



fun Activity.isPermissionGranted(permission: String): Boolean {
    return (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED)
}

fun Activity.askForPermission(permission: String, requestCode: Int) {
    ActivityCompat.requestPermissions(this,
            arrayOf(permission),
            requestCode)
}

fun Activity.doLater(delay: Long, task: () -> (Unit)) {
    Handler(Looper.getMainLooper()).postDelayed({ task() }, delay)
}

fun Application.doLater(delay: Long, task: () -> (Unit)) {
    doAsync{
        Thread.sleep(delay)
        uiThread {
            task()
        }
    }
}

fun isDeviceOnline(): Boolean {
    val activeNetworkInfo = app().connectivityManager.activeNetworkInfo
    return activeNetworkInfo != null && activeNetworkInfo.isConnected
}

