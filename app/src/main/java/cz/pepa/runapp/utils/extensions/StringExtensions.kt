package io.stepuplabs.settleup.util.extensions

import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import cz.pepa.runapp.R
import cz.pepa.runapp.app
import cz.pepa.runapp.data.Goal
import cz.pepa.runapp.data.GoalId

/**
 * Utils related to string manipulation.
 *
 * @author David Vávra (david@stepuplabs.io)
 */

fun Int.toPlural(number: Int): String {
    return app().resources.getQuantityString(this, number, number)
}

/**
 * Shortens name like this:
 * "David Vávra" -> "David V."
 * "Davidaaaaaaa" -> "Davidaaa…"
 * "Da Vi" -> "Da Vi"
 */
fun String.shortenName(): String {
    if (this.length > 9) {
        var shortened = this
        if (this.contains(" ")) {
            val parts = this.trim().split(" ")
            shortened = parts[0] + " " + parts[1][0] + "."
        }
        if (shortened.length > 9 ) {
            if (shortened.length == 10) {
                return shortened.take(9).split(" ")[0] // Charles Babbage -> Charles instead of Charles... and Charles ...
            } else {
                return shortened.take(8) + "…"
            }
        }
        return shortened
    }
    return this
}

fun String.cropLongMemberName(): String {
    return this.take(R.integer.member_name_max_length.toInteger())
}


fun String.toBold(): String {
    return "<b>$this</b>"
}

fun String.setColor(color: Int) = SpannableString(this).setSpan(ForegroundColorSpan(color), 0, this.length - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

fun Spanned.setColor(color: Int) = SpannableString(this).setSpan(ForegroundColorSpan(color), 0, this.length - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

fun String.getColoredString(color: String) = "<font color=" + color + ">$this</font>"

fun String.formatHtml(): Spanned {
    return Html.fromHtml(this)
}



//fun Long.formatTimeLeft(): String {
//    if (this <= 3600000L /* 3 600 000 = 24h */) {
//
//    }
//}