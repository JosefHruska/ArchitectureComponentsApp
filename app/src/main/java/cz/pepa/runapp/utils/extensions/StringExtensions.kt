package io.stepuplabs.settleup.util.extensions

import io.stepuplabs.settleup.R
import io.stepuplabs.settleup.app

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