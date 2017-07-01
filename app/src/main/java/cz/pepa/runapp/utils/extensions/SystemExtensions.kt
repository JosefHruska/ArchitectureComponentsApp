package io.stepuplabs.settleup.util.extensions

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import io.stepuplabs.settleup.app
import io.stepuplabs.settleup.firebase.Auth
import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.exception.ExceptionUtils
import org.jetbrains.anko.connectivityManager
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.File
import java.util.*

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

fun Context.getSystemDebugFile(error: Throwable? = null): Uri {
    val version = "${this.getVersionName()} (${this.getVersionCode()})"
    val locale = Locale.getDefault()
    val account = "${Auth.getUserId()} (${Auth.getEmail()})"
    val device = "${Build.MANUFACTURER} ${Build.MODEL}"
    val system = "Android ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})"
    var text = "Version: $version\nAccount: $account\nLocale: $locale\nDevice: $device\nSystem: $system"
    if (error != null) {
        text += "\n\n" + ExceptionUtils.getStackTrace(error)
        ExceptionUtils.getRootCauseStackTrace(error).forEach { text += "\n\n" + it }
    }
    val file = File(this.cacheDir, "DebuggingInfo.txt")
    FileUtils.writeStringToFile(file, text, "UTF-8")
    return FileProvider.getUriForFile(app(), "io.stepuplabs.settleup.fileprovider", file)
}

fun Context.isDeviceOnline(): Boolean {
    val activeNetworkInfo = app().connectivityManager.activeNetworkInfo
    return activeNetworkInfo != null && activeNetworkInfo.isConnected
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
