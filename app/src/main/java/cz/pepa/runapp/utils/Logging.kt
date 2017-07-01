package io.stepuplabs.settleup.util

import android.os.Looper
import android.util.Log

/**
 * Simple logging wrapper.

 * @author David Vávra (david@stepuplabs.io)
 */
fun ld(logText: String) {
    Log.d("settle-up", logText)
}

fun ld(logText: Any) {
    Log.d("settle-up", logText.toString())
}

fun li(logText: String) {
    Log.i("settle-up", logText)
}

fun lw(logText: String) {
    Log.w("settle-up", logText)
}

fun logError(error: Throwable, groupId: String? = null) {
    error.printStackTrace()
//    if (Auth.isSignedIn()) {
//        FirebaseCrash.log("User ID: ${Auth.getUserId()}")
//    }
//    if (groupId != null) {
//        FirebaseCrash.log("Group ID: $groupId")
//    }
//    FirebaseCrash.report(error)
}

var time: Long = 0

fun t(logText: String) {
    val currentTime = System.currentTimeMillis()
    ld("$logText: ${currentTime - time} ms since last measurement")
    time = currentTime
}

fun checkThread() {
    ld("MainThread=" + (Looper.myLooper() == Looper.getMainLooper()) + "( " + Thread.currentThread() + ")")
}
