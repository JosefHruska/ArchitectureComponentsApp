package cz.pepa.runapp.utils.extensions

import android.text.Spanned
import cz.pepa.runapp.R
import cz.pepa.runapp.app
import io.stepuplabs.settleup.util.extensions.*
import java.text.DecimalFormat

/**
 * TODO: Add description
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

object FittnesHelper {

    const val KJ = 4.184
    const val MILE = 0.62
}

fun Float.formatCalories(calorireUnit: Calories = Calories.KCAL): Spanned {
    val calories: Spanned = if (calorireUnit == Calories.KJ) {
        (this.toKJ().toInt().toString().toBold() + " kJ").formatHtml()
    } else {
        (this.toInt().toString().toBold() + " kcal").formatHtml()
    }
    return calories
}

fun Float.formatDistance(distanceUnit: Distance = Distance.KM): Spanned {
    val kilometers = this / 1000
    return if (distanceUnit == Distance.MILES) {
        (kilometers.toMiles().roundDistance().toBold() + " mi").formatHtml()
    } else {
        (kilometers.roundDistance().toBold() + " Km").formatHtml()
    }
}

fun Int.formatSteps() = toString().toBold().formatHtml()

fun Int.formatReward(): Spanned {
    return (app().getString(R.string.reward) + " $this Exp".getColoredString(R.color.primary.toColorHex()).toBold()).formatHtml() // TODO: Not really working

}

private fun Float.roundDistance(): String {
    val formatter = DecimalFormat("#.##")
    return formatter.format(this)
}

private fun Float.toKJ(): Float {
    return (this * FittnesHelper.KJ).toFloat()
}

private fun Float.toMiles(): Float {
    return (this * FittnesHelper.MILE).toFloat()
}

enum class Calories {
    KCAL,
    KJ
}

enum class Distance {
    KM,
    MILES

}