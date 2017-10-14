package cz.pepa.runapp.data

import cz.pepa.runapp.ui.main.goal.Target

/**
 * // TODO: Add description
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

class NewGoal() {

    var type: Type = Type.DISTANCE
    var target: Target = Target(0F, FitnessUnit.KCAL )
    var recurrence: Recurrence = Recurrence.DAILY
}

enum class Type {
    CALORIES,
    DISTANCE,
    STEPS,
    ACTIVE
}

enum class FitnessUnit {
    KCAL,
    KJ,
    KM,
    MILES,
    METERS,
    DAY,
    WEEK,
    MONTH
}

enum class Recurrence {
    DAILY,
    WEEKLY,
    MONTHLY,
    YEARLY,
    OVERALL
}

enum class Rating {
    GOOD,
    DIFFICULT,
    EASY,
    INSANE
}