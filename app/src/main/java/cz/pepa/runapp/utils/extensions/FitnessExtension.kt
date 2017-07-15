package cz.pepa.runapp.utils.extensions

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

fun Float.formatCalories(calorireUnit: Calories = Calories.KCAL): String {
    val calories: String = if (calorireUnit == Calories.KJ) {
        this.toKJ().toInt().toString() + " kJ"
    } else {
        this.toInt().toString() + " kcal"
    }
    return calories
}

fun Float.formatDistance(distanceUnit: Distance = Distance.KM): String {
    val kilometers = this / 1000
    val distance: String = if (distanceUnit == Distance.MILES) {
        kilometers.toMiles().roundDistance() + " mi"
    } else {
        kilometers.roundDistance() + " KM"
    }
    return distance
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