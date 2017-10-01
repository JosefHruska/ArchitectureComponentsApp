package cz.pepa.runapp.data

import cz.pepa.runapp.ui.main.goal.Target

/**
 * // TODO: Add description
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

class NewGoal() {

    var type: Type? = null
    var target: Target = Target(0F, FitnessUnit.KCAL )
    var reoccurence: FitnessUnit = FitnessUnit.DAY
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