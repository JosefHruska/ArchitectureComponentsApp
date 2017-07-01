package io.stepuplabs.settleup.util.extensions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import io.stepuplabs.settleup.R
import io.stepuplabs.settleup.ui.common.Ids

/**
 * Intent-related extensions.
 *
 * @author David Vávra (david@stepuplabs.io)
 */

fun Context.shareLink(link: String, chooserText: Int) {
    val intent = Intent(Intent.ACTION_SEND)
    intent.type = "text/plain"
    intent.putExtra(Intent.EXTRA_TEXT, link)
    this.startActivity(Intent.createChooser(intent, getString(chooserText)))
}

fun Context.sendFeedbackEmail() {
    sendEmail("settle-up-android-feedback@stepuplabs.io", R.string.feedback_email_subject.toText(), getSystemDebugFile())
}

fun Context.sendErrorEmail(error: Throwable) {
    sendEmail("settle-up-android-error@stepuplabs.io", R.string.error_email_subject.toText(), getSystemDebugFile(error), R.string.error_email_body.toText())
}

private fun Context.sendEmail(recipient: String, subject: String, file: Uri, text: String? = null) {
    val i = Intent(Intent.ACTION_SEND)
    i.type = "message/rfc822"
    i.putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
    i.putExtra(Intent.EXTRA_SUBJECT, subject)
    if (text != null) {
        i.putExtra(Intent.EXTRA_TEXT, text)
    }
    i.putExtra(Intent.EXTRA_STREAM, file)
    this.startActivity(Intent.createChooser(i, R.string.choose_email_app.toText()))
}

fun Intent.getCurrencyCode(requestCode: Int, resultCode: Int): String? {
    if (requestCode == Ids.REQUEST_CURRENCIES && resultCode == Ids.RESULT_CURRENCY_SELECTED) {
        return this.getStringExtra("CODE")
    }
    return null
}

fun Activity.pickContact() {
    val intent = Intent(Intent.ACTION_PICK)
    intent.type = ContactsContract.Contacts.CONTENT_TYPE
    if (intent.resolveActivity(packageManager) != null) {
        startActivityForResult(intent, Ids.REQUEST_SELECT_CONTACT)
    }
}