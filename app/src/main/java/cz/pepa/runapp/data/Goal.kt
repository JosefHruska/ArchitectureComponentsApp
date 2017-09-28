package cz.pepa.runapp.data

import android.text.Spanned
import cz.pepa.runapp.ui.common.MatchedDay

/**
 * TODO: Add description
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

class Goal() {

    var name: Spanned? = null
    var type: GoalId = GoalId.CALORIES
    var percentage: Double = 0.0
    var currentValue: Double = 0.0
    var targetValue: Double = 0.0
    var averageValue: Double = 0.0
    var matchedDays: List<MatchedDay> = emptyList()
    var timeLeft: String = "null"
    var reward: Int = 88
}

enum class GoalId {
    CALORIES,
    WEIGHT,
    STEPS,
    DISTANCE,
    ACTIVE_TIME,
    ACTIVE_TIME_SPECIFIC
}